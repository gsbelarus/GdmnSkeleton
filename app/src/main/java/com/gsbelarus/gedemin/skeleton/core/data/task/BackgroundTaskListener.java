package com.gsbelarus.gedemin.skeleton.core.data.task;

public interface BackgroundTaskListener<ParamsType, ProgressType, ResultType> {
    void onPreExecute();

    void onProgressUpdate(ProgressType... values);

    void onPostExecute(BackgroundTaskResult<ParamsType, ResultType> backgroundTaskResult);
}
