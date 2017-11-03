package tech.sourced.siva;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Reader of Siva {@link Index} to retrieve {@link IndexEntry}s.
 * There are some known issues and limitations in the implementaion
 * that you can see at:
 *
 * @see <a href="https://github.com/src-d/siva-java#limitations">
 * Siva Java Implementation Limitations</a>
 *
 * @see <a href="https://github.com/src-d/go-siva/blob/master/SPEC.md">
 * Siva Format Specification</a>
 */
public class IndexReader {
    private static final byte INDEX_VERSION = 1;
    private static final long INDEX_FOOTER_SIZE = 24;
    private static final byte[] INDEX_SIGNATURE = {'I', 'B', 'A'};
    private static final long READ_UINT32_MASK = 0xFFFFFFFFL;

    private final RandomAccessFile sivaFile;
    private final String sivaFileName;

    /**
     * Constructs a Reader to read from a Siva {@link Index}.
     *
     * @param sivaFile     siva file to read the index from.
     * @param sivaFileName siva file name.
     */
    IndexReader(final RandomAccessFile sivaFile, final String sivaFileName) {
        this.sivaFile = sivaFile;
        this.sivaFileName = sivaFileName;
    }

    /**
     * getFilteredIndex will return an index with all the deleted or modified elements already
     * filtered.
     *
     * @return Index with the last entry of each file. Delete files will not appear in this index.
     * @throws SivaException If some error happens reading or validating the index.
     */
    public final synchronized Index getFilteredIndex() throws SivaException {
        return this.readIndex(new FilteredIndex());
    }

    /**
     * getCompleteIndex will return all the entries into the siva file, even modified or delete
     * files.
     *
     * @return Index with all the entries in the siva file.
     * @throws SivaException If some error happens reading or validating the index.
     */
    public final synchronized Index getCompleteIndex() throws SivaException {
        return this.readIndex(new CompleteIndex());
    }

    private Index readIndex(final BaseIndex index) throws SivaException {
        try {
            // go to the end of the file
            long offset = this.sivaFile.length();
            this.sivaFile.seek(offset);
            while (true) {
                if (this.sivaFile.getFilePointer() == 0) {
                    break;
                }

                long endOfBlock = this.sivaFile.getFilePointer();

                offset = this.sivaFile.getFilePointer() - INDEX_FOOTER_SIZE;
                this.sivaFile.seek(offset);

                IndexFooter indexFooter = this.readFooter();

                offset = this.sivaFile.getFilePointer() - INDEX_FOOTER_SIZE
                        - indexFooter.getIndexSize();
                this.sivaFile.seek(offset);

                this.readSignature();
                this.readVersion();

                for (long i = 0; i < indexFooter.getEntryCount(); i++) {
                    index.add(this.readEntry(indexFooter, endOfBlock));
                }

                index.endIndexBlock();

                // go to the next index
                offset = this.sivaFile.getFilePointer() - indexFooter.getBlockSize()
                        + INDEX_FOOTER_SIZE;
                this.sivaFile.seek(offset);
            }

            return index;
        } catch (IOException e) {
            throw new SivaException("Error reading index of " + this.sivaFileName + " file.", e);
        }
    }

    private IndexFooter readFooter() throws IOException, SivaException {
        long entryCount = castUnsignedIntToLong(this.sivaFile.readInt());

        long indexSize = this.sivaFile.readLong();
        checkUnsignedLongs(indexSize, "At Index footer, index size: ");

        long blockSize = this.sivaFile.readLong();
        checkUnsignedLongs(blockSize, "At Index footer, block size: ");

        long crc32 = castUnsignedIntToLong(this.sivaFile.readInt());

        return new IndexFooter(entryCount, indexSize, blockSize, crc32);
    }

    private void readSignature() throws IOException, SivaException {
        byte[] signature = new byte[INDEX_SIGNATURE.length];
        this.sivaFile.readFully(signature);

        if (!Arrays.equals(signature, INDEX_SIGNATURE)) {
            throw new SivaException("Invalid index signature at " + this.sivaFileName + " file.");
        }
    }

    private void readVersion() throws IOException, SivaException {
        byte version = this.sivaFile.readByte();

        if (version != INDEX_VERSION) {
            throw new SivaException("Invalid index version at " + this.sivaFileName + " file.");
        }
    }

    private IndexEntry readEntry(final IndexFooter indexFooter,
                                 final long endOfBlock) throws IOException, SivaException {

        int entryNameLength = this.sivaFile.readInt();
        if (entryNameLength < 0) {
            throw new SivaException(SivaException.FILE_NAME_LENGTH);
        }

        byte[] nameBuf = new byte[entryNameLength];
        this.sivaFile.readFully(nameBuf);
        String name = new String(nameBuf, "UTF-8");

        int rawFileMode = this.sivaFile.readInt();
        Set<PosixFilePermission> fileMode = FileModeUtils.posixFilePermissions(rawFileMode);

        long rawModTime = this.sivaFile.readLong();
        FileTime modificationTime = FileTime.from(rawModTime, TimeUnit.NANOSECONDS);

        long fileOffset = this.sivaFile.readLong();
        checkUnsignedLongs(fileOffset, "At Index Entry " + name + ", file offset: ");

        long fileSize = this.sivaFile.readLong();
        checkUnsignedLongs(fileSize, "At Index Entry " + name + ", file size: ");

        int rawCrc32 = this.sivaFile.readInt();
        long crc32 = castUnsignedIntToLong(rawCrc32);

        int rawFlag = this.sivaFile.readInt();
        Flag flag = Flag.fromInteger(rawFlag);

        long beginOfEntry = (endOfBlock - indexFooter.getBlockSize()) + fileOffset;

        return new IndexEntry(
                name,
                fileMode,
                modificationTime,
                flag,
                fileOffset,
                fileSize,
                crc32,
                beginOfEntry
        );
    }

    private long castUnsignedIntToLong(final int n) {
        return n & READ_UINT32_MASK;
    }

    private void checkUnsignedLongs(final long n, final String from) throws SivaException {
        if (n < 0) {
            throw new SivaException(from + SivaException.UNSIGNED_LONG);
        }
    }
}

