package com.gsbelarus.gedemin.skeleton.core.service;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.gsbelarus.gedemin.skeleton.core.util.GCMHelper;
import com.gsbelarus.gedemin.skeleton.core.util.LogUtil;

public class GcmInstanceIDService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        LogUtil.dtag("GcmTest","onTokenRefresh");
        GCMHelper gcmHelper = new GCMHelper(getApplicationContext());
        gcmHelper.updateRegistration();
    }
}
