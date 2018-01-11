package tech.sourced.siva;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class RangeInputStream extends InputStream {
    private static final int READ_MASK = 0xFF;

    private final InputStream parent;
    private long remaining;

    /**
     * InputStream that will provide only the specified size of the provided InputStream
     * from the position already provided on the parent stream.
     *
     * @param parent The parent InputStream where the data is coming from.
     * @param size   Size of bytes to read from.
     * @throws IOException If is not possible to skip the amount
     *                     of bytes provided to skip or in any other standard InputStream case.
     */
    public RangeInputStream(
            final InputStream parent,
            final long size) throws IOException {
        this.parent = parent;
        remaining = size;
    }

    @Override
    public final int read() throws IOException {
        if (--remaining >= 0) {
            return parent.read() & READ_MASK;
        } else {
            return -1;
        }
    }

    @Override
    public final int read(final byte[] b, final int off, final int len) throws IOException {
        if (remaining <= 0) {
            // we already read everything
            return -1;
        } else if (remaining - len <= 0) {
            // we need to change the len that we are going to read
            int bytesRead = parent.read(b, off, (int) remaining);
            remaining = 0;
            return bytesRead;
        } else {
            // we can read all the len
            remaining = remaining - len;
            return parent.read(b, off, len);
        }
    }
}
