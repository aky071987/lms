package com.cs.lms.exception;

public class BookAlreadyReturnedException extends Exception {
    private static final long serialVersionUID = 1L;

    public BookAlreadyReturnedException(String message) {
        super(message);
    }
}
