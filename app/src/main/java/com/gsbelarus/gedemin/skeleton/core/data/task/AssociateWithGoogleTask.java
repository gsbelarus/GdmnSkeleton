package com.gsbelarus.gedemin.skeleton.core.data.task;

import com.gsbelarus.gedemin.skeleton.core.data.task.entity.AssosiateWithGoogleParams;

public class AssociateWithGoogleTask extends BackgroundTask<AssosiateWithGoogleParams, Void, Boolean> {

    @Override
    protected Boolean doTaskInBackground(AssosiateWithGoogleParams... params) throws Exception {
        Thread.sleep(2000);
        return true;
    }
}
