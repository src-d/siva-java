package tech.sourced.siva;

public class SivaException extends Exception {
    SivaException(String message, Throwable cause) {
        super(message, cause);
    }

    SivaException(String message) {
        super(message);
    }
}
