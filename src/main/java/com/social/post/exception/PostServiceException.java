package com.social.post.exception;

public class PostServiceException extends RuntimeException{


    public PostServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostServiceException(String message) {
        super(message);
    }
}

