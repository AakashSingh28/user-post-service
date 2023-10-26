package com.social.post.exception;

public class UserPostsFetchException extends RuntimeException {
    public UserPostsFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPostsFetchException(String message) {
        super(message);
    }
}
