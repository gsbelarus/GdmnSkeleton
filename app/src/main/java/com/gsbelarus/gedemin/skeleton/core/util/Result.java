package com.gsbelarus.gedemin.skeleton.core.util;

public class Result<T> {

    private Exception exception;
    private T data;

    public Result() {
    }

    public Result(Exception exception, T data) {
        this.exception = exception;
        this.data = data;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
