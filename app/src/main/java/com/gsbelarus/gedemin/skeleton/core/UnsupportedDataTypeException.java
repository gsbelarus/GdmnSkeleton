package com.gsbelarus.gedemin.skeleton.core;


public class UnsupportedDataTypeException extends RuntimeException {

    public UnsupportedDataTypeException(String detailMessage) {
        super("Unsupported data type: " + detailMessage);
    }
}