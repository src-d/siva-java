package tech.sourced.siva;

/**
 * Specific exception thrown when a problem happens while a siva file is being manipulated.
 */
public class SivaException extends Exception {

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
