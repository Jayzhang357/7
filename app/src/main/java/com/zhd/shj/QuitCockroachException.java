package com.zhd.shj;

final class QuitCockroachException extends RuntimeException {
    public QuitCockroachException(String message) {
        super(message);
    }
}