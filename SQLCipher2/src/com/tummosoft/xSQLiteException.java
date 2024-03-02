package com.tummosoft;

public class xSQLiteException extends RuntimeException {
     public xSQLiteException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
