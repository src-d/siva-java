package tech.sourced.siva;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * {@link SivaReader} and all its outputs are thread safe. This class handles the unpacked process of a siva file.
 */
public class SivaReader {

    private final RandomAccessFile sivaFile;
    private final String sivaFileName;
    private final FileChannel channel;

    /**
     * Create a {@link SivaReader} from a siva file.
     *
     * @param sivaFile The siva file.
     * @throws FileNotFoundException If the file does not exists.
     */
    public SivaReader(File sivaFile) throws FileNotFoundException {
        this.sivaFile = new RandomAccessFile(sivaFile, "r");
        this.sivaFileName = sivaFile.getName();
        this.channel = this.sivaFile.getChannel();
    }

    /**
     * getEntry returns the file referenced by the provided {@link IndexEntry}.
     * Entries can be obtained from {@link #getIndex()} method.
     *
     * @param indexEntry entry obtained from the siva index
     * @return InputStream to be able to read the entire expected file.
     * @throws SivaException If some problem happens trying to read the siva file.
     */
    public InputStream getEntry(IndexEntry indexEntry) throws SivaException {
        try {
            MappedByteBuffer mbb = this.channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    indexEntry.getAbsStart(),
                    indexEntry.getSize()
            );

            return new ByteBufferBackedInputStream(mbb.asReadOnlyBuffer());
        } catch (IOException e) {
            throw new SivaException("Error reading index entry.", e);
        }
    }

    /**
     * getIndex provides an {@link IndexReader} that points to the index into the siva file.
     * Is recommended to get an index per thread, but anyways {@link IndexReader} is thread safe.
     *
     * @return an {@link IndexReader}
     */
    public IndexReader getIndex() {
        return new IndexReader(this.sivaFile, this.sivaFileName);
    }

    /**
     * Close closes the siva reader. When it is closed, it cannot be reused.
     *
     * @throws SivaException if some error happens when we tried to close the internal channel or the siva file reader.
     */
    public void close() throws SivaException {
        try {
            this.channel.close();
            this.sivaFile.close();
        } catch (IOException e) {
            throw new SivaException("Error closing siva reader", e);
        }
    }
}
