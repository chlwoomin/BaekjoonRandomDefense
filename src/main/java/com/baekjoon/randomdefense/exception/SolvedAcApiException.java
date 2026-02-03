package com.baekjoon.randomdefense.exception;

public class SolvedAcApiException extends RuntimeException {

    public SolvedAcApiException(String message) {
        super(message);
    }

    public SolvedAcApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
