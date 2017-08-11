package tech.sourced.siva;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class IndexEntry extends Header {
    private final long intStart;
    private final long size;
    private final int crc32;

    IndexEntry(String name, Set<PosixFilePermission> fileMode, FileTime modificationTime, long intStart, long size, int crc32, Flag flag) {
        super(name, modificationTime, fileMode, flag);
        this.intStart = intStart;
        this.size = size;
        this.crc32 = crc32;
    }

    public long getIntStart() {
        return intStart;
    }

    public long getSize() {
        return size;
    }

    public long getCrc32() {
        return (long) this.crc32 & 0xffffffffL;
    }

}

