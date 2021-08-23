package com.cs.lms.exception;

public class NoBookAvailableForBorrowException extends Exception{
    private static final long serialVersionUID = 1L;

    public NoBookAvailableForBorrowException(String message) {
        super(message);
    }
}
