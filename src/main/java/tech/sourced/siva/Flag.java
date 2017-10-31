package tech.sourced.siva;

/**
 * Flags used by the index to annotate files. Siva format v1 just make use of
 * the flags to mark a file as deleted.
 * Different flags could be added in future versions.
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

    /**
     * get the flag from an integer.
     *
     * @param i integer that represents the flag id.
     * @return Flag element.
     */
    public static Flag fromInteger(final int i) {
        switch (i) {
            case 1:
                return DELETE;
            default:
                return NO_FLAG;
        }
    }
}
