package tech.sourced.siva;

/**
 * Specific exception thrown when a problem happens while a siva file is manipulated.
 */
public class SivaException extends Exception {
    SivaException(String message, Throwable cause) {
        super(message, cause);
    }

    SivaException(String message) {
        super(message);
    }
}
