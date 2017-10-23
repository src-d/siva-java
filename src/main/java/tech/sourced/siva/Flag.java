package tech.sourced.siva;

/**
 * Flags used by the index to mark a file as deleted. Other flags could be added in future versions.
 */
enum Flag {

    /**
     * No flag.
     */
    NO_FLAG,

    /**
     * Deleted file.
     */
    DELETE;

    public static Flag fromInteger(int i) {
        switch (i) {
            case 1:
                return DELETE;
            default:
                return NO_FLAG;
        }
    }
}
