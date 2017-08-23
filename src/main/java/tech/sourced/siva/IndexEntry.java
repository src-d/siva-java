package tech.sourced.siva;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class IndexEntry extends Header {
    private final long intStart;
    private final long size;
    private final int crc32;
    private final long absStart;

    public IndexEntry(String name, FileTime modificationTime, Set<PosixFilePermission> fileMode,
                      Flag flag, long intStart, long size, int crc32, long absStart) {
        super(name, modificationTime, fileMode, flag);
        this.intStart = intStart;
        this.size = size;
        this.crc32 = crc32;
        this.absStart = absStart;
    }

    public long getAbsStart() {
        return absStart;
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

