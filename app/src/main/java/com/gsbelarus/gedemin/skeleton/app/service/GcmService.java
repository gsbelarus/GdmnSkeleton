package com.gsbelarus.gedemin.skeleton.app.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.gsbelarus.gedemin.skeleton.core.util.Logger;

public class GcmService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String msg = data.getString("message");
        Logger.d(msg);

        if (msg != null) {
//            try {
            //TODO Parser and response action

//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

    }
}