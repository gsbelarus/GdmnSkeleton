package com.gsbelarus.gedemin.skeleton.core.data.task;

import com.gsbelarus.gedemin.skeleton.core.data.task.entity.GdmnGoogleSignInParams;
import com.gsbelarus.gedemin.skeleton.core.data.task.entity.GdmnGoogleSignInResult;

public class GdmnGoogleSignInTask extends BackgroundTask<GdmnGoogleSignInParams, Void, GdmnGoogleSignInResult> {

    @Override
    protected GdmnGoogleSignInResult doTaskInBackground(GdmnGoogleSignInParams... params) throws Exception {
        Thread.sleep(2000);
        return new GdmnGoogleSignInResult("testLogin", null, "testGdmnToken", null);
    }
}
