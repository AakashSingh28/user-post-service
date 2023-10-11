package com.social.post.exception;

public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException(String message) {
        super(message);
    }

    public UserProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
