package com.gsbelarus.gedemin.skeleton.core.data.task;

public class BackgroundTaskResult<Params, Result> {

    private Exception exception;
    private Params[] params;
    private Result result;

    public BackgroundTaskResult() {
    }

    public BackgroundTaskResult(Params[] params, Exception exception) {
        this.exception = exception;
        this.params = params;
        this.result = null;
    }

    public BackgroundTaskResult(Params[] params, Result result) {
        this.exception = null;
        this.params = params;
        this.result = result;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Params[] getParams() {
        return params;
    }

    public void setParams(Params[] params) {
        this.params = params;
    }
}
