package tech.sourced.siva;

/**
 * Specific exception thrown when a problem happens while a siva file is being manipulated.
 */
public class SivaException extends Exception {

    /**
     * Known issue about siva specification using unsigned integers.
     *
     * @see <a href="https://github.com/src-d/siva-java#limitations">
     * Siva Java Implementation Limitations</a>
     *
     * @see <a href="https://github.com/src-d/go-siva/blob/master/SPEC.md">
     * Siva Format Specification</a>
     */
    static final String FILE_NAME_LENGTH = "Java implementation of siva doesn't support"
            + " file names lengths greater than max signed integer value " + Integer.MAX_VALUE;

    /**
     * Known issue about siva specification using unsigned integers.
     *
     * @see <a href="https://github.com/src-d/siva-java#limitations">
     * Siva Java Implementation Limitations</a>
     *
     * @see <a href="https://github.com/src-d/go-siva/blob/master/SPEC.md">
     * Siva Format Specification</a>
     */
    static final String UNSIGNED_LONG =
            "Java implementation of siva doesn't support values greater than " + Long.MAX_VALUE;

    /**
     * CRC32 Index integrity checking failed.
     */
    static final String INVALID_CRC = "Wrong Index, Integrity verification failed";

    /**
     * Generate a {@link SivaException} using a message and a cause.
     *
     * @param message The message of the exception
     * @param cause   The cause of the exception
     */
    SivaException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Generate a {@link SivaException} using a message.
     *
     * @param message The message of the exception
     */
    SivaException(final String message) {
        super(message);
    }
}
