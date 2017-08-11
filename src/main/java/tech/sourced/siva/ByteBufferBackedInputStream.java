package tech.sourced.siva;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

// http://nadeausoftware.com/articles/2008/02/java_tip_how_read_files_quickly
class ByteBufferBackedInputStream extends InputStream {

    private final ByteBuffer buf;

    ByteBufferBackedInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    public int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }
        return buf.get() & 0xFF;
    }

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
