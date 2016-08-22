package com.gsbelarus.gedemin.skeleton.core.data.task;

import android.text.TextUtils;

import com.gsbelarus.gedemin.skeleton.core.data.task.entity.GdmnSignInParams;

public class GdmnSignInTask extends BackgroundTask<GdmnSignInParams, Void, String> {

    @Override
    protected String doTaskInBackground(GdmnSignInParams... params) throws Exception {
        if (TextUtils.isEmpty(params[0].getLogin()) || TextUtils.isEmpty(params[0].getUrl())) {
            throw new Exception("url or login is empty");
        }
        Thread.sleep(2000);
        return "testGdmnToken";
    }
}
