package tech.sourced.siva;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An {@link InputStream} backed by a {@link ByteBuffer} to improve read operations.
 *
 * @see <a href="http://nadeausoftware.com/articles/2008/02/java_tip_how_read_files_quickly">
 * Reading Files Comparation</a>
 */
class ByteBufferBackedInputStream extends InputStream {
    private static final int READ_MASK = 0xFF;

    private final ByteBuffer buf;

    /**
     * Constructs a new {@link ByteBufferBackedInputStream} backed by the given {@link ByteBuffer}.
     *
     * @param buf {@link ByteBuffer} to back the {@link InputStream}
     */
    ByteBufferBackedInputStream(final ByteBuffer buf) {
        this.buf = buf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & READ_MASK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte[] bytes, final int off, final int len)
            throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        int minLen = Math.min(len, buf.remaining());
        buf.get(bytes, off, minLen);
        return minLen;
    }
}
