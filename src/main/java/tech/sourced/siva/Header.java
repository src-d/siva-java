package tech.sourced.siva;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

/**
 * Set of properties an {@link IndexEntry} must have
 */
public class Header {
    private final String name;
    private final FileTime modificationTime;
    private final Set<PosixFilePermission> fileMode;
    private final Flag flag;

    /**
     * Constructor.
     *
     * @param name Entry name.
     * @param modificationTime Modification time as UNIX time in nanoseconds.
     * @param fileMode UNIX mode.
     * @param flag supported flags @see {@link Flag}
     */
    Header(String name, FileTime modificationTime, Set<PosixFilePermission> fileMode, Flag flag) {
        this.name = name;
        this.modificationTime = modificationTime;
        this.fileMode = fileMode;
        this.flag = flag;
    }

    /**
     * @return Entry name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Modification time as Unix time in nanoseconds.
     */
    public FileTime getModificationTime() {
        return modificationTime;
    }

    /**
     * @return UNIX file mode.
     */
    public Set<PosixFilePermission> getFileMode() {
        return fileMode;
    }

    /**
     * @return {@link Flag}
     */
    public Flag getFlag() {
        return flag;
    }
}
