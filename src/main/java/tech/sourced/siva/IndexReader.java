package tech.sourced.siva;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Reader of Siva {@link Index} to retrieve {@link IndexEntry}s.
 *
 * @see <a href="https://github.com/src-d/go-siva/blob/master/SPEC.md"</a>
 */
public class IndexReader {
    private static final int INDEX_VERSION = 1;
    private static final int INDEX_FOOTER_SIZE = 24;
    private static final byte[] INDEX_SIGNATURE = {'I', 'B', 'A'};

    private final RandomAccessFile sivaFile;
    private final String sivaFileName;

    /**
     * Constructor.
     *
     * @param sivaFile siva file to read the index from.
     * @param sivaFileName siva file name.
     */
    IndexReader(RandomAccessFile sivaFile, String sivaFileName) {
        this.sivaFile = sivaFile;
        this.sivaFileName = sivaFileName;
    }

    /**
     * getFilteredIndex will return an index with all the deleted or modified elements already filtered.
     *
     * @return Index with the last entry of each file. Delete files will not appear in this index.
     * @throws SivaException If some error happens reading or validating the index.
     */
    public synchronized Index getFilteredIndex() throws SivaException {
        return this.readIndex(new FilteredIndex());
    }

    /**
     * getCompleteIndex will return all the entries into the siva file, even modified or delete files.
     *
     * @return Index with all the entries in the siva file.
     * @throws SivaException If some error happens reading or validating the index.
     */
    public synchronized Index getCompleteIndex() throws SivaException {
        return this.readIndex(new CompleteIndex());
    }

    private Index readIndex(BaseIndex index) throws SivaException {
        try {
            // go to the end of the file
            this.sivaFile.seek(this.sivaFile.length());
            while (true) {
                if (this.sivaFile.getFilePointer() == 0) {
                    break;
                }

                long block = this.sivaFile.getFilePointer();

                this.sivaFile.seek(this.sivaFile.getFilePointer() - INDEX_FOOTER_SIZE);
                IndexFooter indexFooter = this.readIndexFooter();
                this.sivaFile.seek(this.sivaFile.getFilePointer() - INDEX_FOOTER_SIZE - indexFooter.getIndexSize());

                this.readSignature();
                this.readIndexVersion();

                for (int i = 0; i < indexFooter.getEntryCount(); i++) {
                    index.add(this.readEntry(indexFooter, block));
                }

                index.endIndexBlock();

                // go to the next index
                this.sivaFile.seek(this.sivaFile.getFilePointer() - indexFooter.getBlockSize() + INDEX_FOOTER_SIZE);
            }

            return index;
        } catch (IOException e) {
            throw new SivaException("Error reading index of " + this.sivaFileName + " file.", e);
        }
    }

    private IndexEntry readEntry(IndexFooter indexFooter, long block) throws IOException {
        int entryNameLength = this.sivaFile.readInt();
        byte[] name = new byte[entryNameLength];
        this.sivaFile.readFully(name);

        int fileMode = this.sivaFile.readInt();

        FileTime modificationTime = FileTime.from(this.sivaFile.readLong(), TimeUnit.NANOSECONDS);

        long fileOffset = this.sivaFile.readLong();
        long fileSize = this.sivaFile.readLong();
        int crc32 = this.sivaFile.readInt();

        Flag flag = Flag.fromInteger(this.sivaFile.readInt());

        return new IndexEntry(
                new String(name),
                modificationTime,
                FileModeUtils.posixFilePermissions(fileMode),
                flag,
                fileOffset,
                fileSize,
                crc32,
                (block - indexFooter.getBlockSize()) + fileOffset
        );
    }

    private IndexFooter readIndexFooter() throws IOException {
        return new IndexFooter(
                this.sivaFile.readInt(),
                this.sivaFile.readLong(),
                this.sivaFile.readLong(),
                this.sivaFile.readInt()
        );
    }

    private void readIndexVersion() throws IOException, SivaException {
        int version = this.sivaFile.readByte();
        if (version != INDEX_VERSION) {
            throw new SivaException("Invalid index version at " + this.sivaFileName + " file.");
        }
    }

    private void readSignature() throws IOException, SivaException {
        byte[] sig = new byte[3];
        this.sivaFile.readFully(sig);
        if (!Arrays.equals(sig, INDEX_SIGNATURE)) {
            throw new SivaException("Invalid index signature at " + this.sivaFileName + " file.");
        }
    }
}

