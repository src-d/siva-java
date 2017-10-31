package tech.sourced.siva;

import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.GROUP_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

/**
 * FileModeUtils maps siva files modes to UNIX files mode since in Siva Format v1
 * they are represented as Go os.FileMod.
 *
 * @see <a href="https://github.com/src-d/go-siva/issues/11">Siva Format File Mode Issue</a>
 */
final class FileModeUtils {
    private FileModeUtils() {
    }

    private static final PosixFilePermission[] DECODE_MAP = {
            OTHERS_EXECUTE,
            OTHERS_WRITE,
            OTHERS_READ,
            GROUP_EXECUTE,
            GROUP_WRITE,
            GROUP_READ,
            OWNER_EXECUTE,
            OWNER_WRITE,
            OWNER_READ
    };

    /**
     * Obtain file permissions depending of the mode.
     *
     * @param mode mode of the file
     * @return Set of {@link java.nio.file.attribute.PosixFilePermissions} included on the mode.
     */
    static Set<PosixFilePermission> posixFilePermissions(final int mode) {
        int mask = 1;
        Set<PosixFilePermission> perms = EnumSet.noneOf(PosixFilePermission.class);
        for (PosixFilePermission flag : DECODE_MAP) {
            if (flag != null && (mask & mode) != 0) {
                perms.add(flag);
            }
            mask = mask << 1;
        }
        return perms;
    }
}
