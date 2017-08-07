package tech.sourced.siva;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class SivaReader {

    private final RandomAccessFile sivaFile;
    private final FileChannel channel;

    public SivaReader(File sivaFile) throws FileNotFoundException {
        this.sivaFile = new RandomAccessFile(sivaFile, "r");
        this.channel = this.sivaFile.getChannel();
    }

    public InputStream getEntry(IndexEntry indexEntry) throws SivaException {
        try {
            FileChannel fileChannel = this.sivaFile.getChannel();
            MappedByteBuffer mbb = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    indexEntry.getIntStart(),
                    indexEntry.getSize()
            );

            return new ByteBufferBackedInputStream(mbb.asReadOnlyBuffer());
        } catch (IOException e) {
            throw new SivaException("Error reading index entry.", e);
        }
    }

    public IndexReader getIndex() throws SivaException {
        try {
            return new IndexReader(this.sivaFile);
        } catch (IOException e) {
            throw new SivaException("Error obtaining index reader", e);
        }
    }

    public void close() throws SivaException {
        try {
            this.channel.close();
            this.sivaFile.close();
        } catch (IOException e) {
            throw new SivaException("Error closing siva reader", e);
        }
    }
}
