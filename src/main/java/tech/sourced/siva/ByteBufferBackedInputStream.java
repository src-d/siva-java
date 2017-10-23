package tech.sourced.siva;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An {@link InputStream} backed by a {@link ByteBuffer} to improve read operations.
 *
 * @see <a href="http://nadeausoftware.com/articles/2008/02/java_tip_how_read_files_quickly">Reading Files Comparation</a></a>
 */
class ByteBufferBackedInputStream extends InputStream {

    private final ByteBuffer buf;

    /**
     * Constructs a new {@link ByteBufferBackedInputStream} backed by the given {@link ByteBuffer}.
     *
     * @param buf {@link ByteBuffer} to back the {@link InputStream}
     */
    ByteBufferBackedInputStream(ByteBuffer buf) {
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
        return buf.get() & 0xFF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] bytes, int off, int len)
            throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());
        buf.get(bytes, off, len);
        return len;
    }
}
