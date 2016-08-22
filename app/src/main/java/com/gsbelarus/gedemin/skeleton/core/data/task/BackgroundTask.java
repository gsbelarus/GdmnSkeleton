package com.gsbelarus.gedemin.skeleton.core.data.task;

import android.os.AsyncTask;

public abstract class BackgroundTask<ParamsType, ProgressType, ResultType> extends
        AsyncTask<ParamsType, ProgressType, BackgroundTaskResult<ParamsType, ResultType>> {

    private BackgroundTaskResult<ParamsType, ResultType> backgroundTaskResult = new BackgroundTaskResult<>();
    private BackgroundTaskListener<ParamsType, ProgressType, ResultType> taskListener;

    protected abstract ResultType doTaskInBackground(ParamsType... params) throws Exception;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (taskListener != null) {
            taskListener.onPreExecute();
        }
    }

    @Override
    protected void onProgressUpdate(ProgressType... values) {
        super.onProgressUpdate(values);

        if (taskListener != null) {
            taskListener.onProgressUpdate(values);
        }
    }

    @Override
    protected void onPostExecute(BackgroundTaskResult<ParamsType, ResultType> backgroundTaskResult) {
        super.onPostExecute(backgroundTaskResult);

        if (taskListener != null) {
            taskListener.onPostExecute(backgroundTaskResult);
        }
    }

    @Override
    protected final BackgroundTaskResult<ParamsType, ResultType> doInBackground(ParamsType... params) {
        backgroundTaskResult.setParams(params);
        try {
            backgroundTaskResult.setResult(doTaskInBackground(params));
            backgroundTaskResult.setException(null);
        } catch (Exception e) {
            backgroundTaskResult.setResult(null);
            backgroundTaskResult.setException(e);
        }
        return backgroundTaskResult;
    }

    public BackgroundTaskListener<ParamsType, ProgressType, ResultType> getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(BackgroundTaskListener<ParamsType, ProgressType, ResultType> taskListener) {
        this.taskListener = taskListener;
    }
}
