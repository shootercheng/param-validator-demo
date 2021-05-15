package com.scd.mvctest.exception;

/**
 * @author James
 */
public class StreamException extends RuntimeException {
    public StreamException(String message) {
        super(message);
    }

    public StreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamException(Throwable cause) {
        super(cause);
    }
}
