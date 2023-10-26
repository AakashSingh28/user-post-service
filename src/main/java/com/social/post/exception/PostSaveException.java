package com.social.post.exception;

public class PostSaveException extends RuntimeException {

    public PostSaveException(String message, Throwable cause) {
        super(message, cause);
    }
    public PostSaveException(String message) {
        super(message);
    }
}
