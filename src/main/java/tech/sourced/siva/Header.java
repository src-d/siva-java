package tech.sourced.siva;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class Header {
    private final String name;
    private final FileTime modificationTime;
    private final Set<PosixFilePermission> fileMode;
    private final Flag flag;

    Header(String name, FileTime modificationTime, Set<PosixFilePermission> fileMode, Flag flag) {
        this.name = name;
        this.modificationTime = modificationTime;
        this.fileMode = fileMode;
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public FileTime getModificationTime() {
        return modificationTime;
    }

    public Set<PosixFilePermission> getFileMode() {
        return fileMode;
    }

    public Flag getFlag() {
        return flag;
    }
}
