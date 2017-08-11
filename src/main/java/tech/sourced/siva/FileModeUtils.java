package tech.sourced.siva;

import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;

import static java.nio.file.attribute.PosixFilePermission.*;

public class FileModeUtils {
    private static final PosixFilePermission[] decodeMap = {
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

    static Set<PosixFilePermission> posixFilePermissions(int mode) {
        int mask = 1;
        Set<PosixFilePermission> perms = EnumSet.noneOf(PosixFilePermission.class);
        for (PosixFilePermission flag : decodeMap) {
            if (flag != null && (mask & mode) != 0) {
                perms.add(flag);
            }
            mask = mask << 1;
        }
        return perms;
    }
}
