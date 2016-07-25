package com.gsbelarus.gedemin.skeleton.core.data;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.gsbelarus.gedemin.skeleton.core.util.GCMHelper;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class GcmInstanceIDService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        Logger.dtag("GcmTest","onTokenRefresh");
        GCMHelper gcmHelper = new GCMHelper(getApplicationContext());
        gcmHelper.updateRegistration();
    }
}
