package tech.sourced.siva;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class IndexReader {
    private static final int INDEX_VERSION = 1;
    private static final int INDEX_FOOTER_SIZE = 24;
    private static final byte[] INDEX_SIGNATURE = {'I', 'B', 'A'};

    private final RandomAccessFile sivaFile;

    IndexReader(RandomAccessFile sivaFile) throws IOException {
        this.sivaFile = sivaFile;
    }

    /**
     * getFilteredIndex will return an index with all the deleted or modified elements already filtered.
     *
     * @return Index with the last entry of each file. Delete files will not appear in this index.
     * @throws IOException
     * @throws SivaException
     */
    public synchronized Index getFilteredIndex() throws IOException, SivaException {
        return this.readIndex(new FilteredIndex());
    }


    /**
     * getCompleteIndex will return all the entries into the siva file, even modified or delete files.
     *
     * @return Index with all the entries in the siva file.
     * @throws IOException
     * @throws SivaException
     */
    public synchronized Index getCompleteIndex() throws IOException, SivaException {
        return this.readIndex(new CompleteIndex());
    }

    private Index readIndex(BaseIndex index) throws IOException, SivaException {
        // go to the end of the file
        this.sivaFile.seek(this.sivaFile.length());
        while (true) {
            if (this.sivaFile.getFilePointer() == 0) {
                break;
            }

            this.sivaFile.seek(this.sivaFile.getFilePointer() - INDEX_FOOTER_SIZE);
            IndexFooter indexFooter = this.readIndexFooter();
            this.sivaFile.seek(this.sivaFile.getFilePointer() - INDEX_FOOTER_SIZE - indexFooter.getIndexSize());

            this.readSignature();
            this.readIndexVersion();

            for (int i = 0; i < indexFooter.getEntryCount(); i++) {
                index.add(this.readEntry());
            }

            // go to the next index
            this.sivaFile.seek(this.sivaFile.getFilePointer() - indexFooter.getBlockSize() + INDEX_FOOTER_SIZE);
        }

        return index;
    }

    private IndexEntry readEntry() throws IOException {
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
                FileModeUtils.posixFilePermissions(fileMode),
                modificationTime,
                fileOffset,
                fileSize,
                crc32,
                flag
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
            throw new SivaException("invalid index version");
        }
    }

    private void readSignature() throws IOException, SivaException {
        byte[] sig = new byte[3];
        this.sivaFile.readFully(sig);
        if (!Arrays.equals(sig, INDEX_SIGNATURE)) {
            throw new SivaException("invalid index signature");
        }
    }
}

