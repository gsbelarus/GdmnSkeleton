package com.gsbelarus.gedemin.skeleton.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.gsbelarus.gedemin.skeleton.base.BaseApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GCMHelper {

    private static final String SHARED_PREF_NAME = "preferences";
    private static final String PREF_SENDER_ID = "sender_id";
    private static final String PREF_DEVICE_TOKEN = "device_token";
    private static final String PREF_DEVICE_AUTH_KEY = "auth_key";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Context context;

    public GCMHelper(Context context) {
        this.context = context;

        //TODO adapt to database
        savePrefValue(PREF_SENDER_ID, "303114121834");
        savePrefValue(PREF_DEVICE_AUTH_KEY, "");
    }

    public void checkRegistration() {
        if (loadPrefValue(PREF_DEVICE_AUTH_KEY).isEmpty() || !checkPlayServices()) return;

        if (loadPrefValue(PREF_DEVICE_TOKEN).isEmpty())
            updateRegistration();

    }

    public void deleteRegistration() {
        executor.execute(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            InstanceID instanceID = InstanceID.getInstance(context);
                            instanceID.deleteToken(loadPrefValue(PREF_SENDER_ID), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                            savePrefValue(PREF_SENDER_ID, "");
                            LogUtil.i("GCM Registration ID deleted");

                        } catch (Exception ex) {
                            LogUtil.i("Error : " + ex.getMessage());

                            // Tracking exception
                            BaseApplication.getInstance().trackException(ex);
                        }
                    }
                }
        );

    }

    public void updateRegistration() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //TODO execute in basic method
                try {
                    InstanceID instanceID = InstanceID.getInstance(context);
                    String token = instanceID.getToken(loadPrefValue(PREF_SENDER_ID),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    LogUtil.i("GCM Registration ID: " + token);

                    if (!loadPrefValue(PREF_DEVICE_TOKEN).equals(token)) {
                        savePrefValue(PREF_DEVICE_TOKEN, "");
                        if (sendRegistrationIdToBackend(token)) {
                            savePrefValue(PREF_DEVICE_TOKEN, token);
                            LogUtil.i("GCM Registration ID sent to the server");
                        }
                    }

                } catch (Exception ex) {
                    LogUtil.i("Error : " + ex.getMessage());

                    // Tracking exception
                    BaseApplication.getInstance().trackException(ex);
                }
            }
        });
    }

    private boolean sendRegistrationIdToBackend(String regid) {
        if (regid.isEmpty() || loadPrefValue(PREF_DEVICE_AUTH_KEY).isEmpty())
            return false;

        //TODO field from HashMap
        Map<String, String> requestParams = new HashMap<>();

        //TODO webServiceManager

        return true;
    }

    private boolean isSyncStatusOK(InputStream inputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();

        String buf;
        while ((buf = reader.readLine()) != null) {
            lines.add(buf);
        }

//        TODO replace the Parser
//        HashMap<String, String> headerParams = new Parser().parsingHeader(lines);
//        if (headerParams.containsKey(SyncService.RESPONSE_PARAM_ERROR_CODE)) {
//            LogUtil.d(Extractor.ResponseError.getErrorByCode(headerParams.get(SyncService.RESPONSE_PARAM_ERROR_CODE)));
//            return false;
//        }
        return true;
    }

    public boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result, 9000).show();
            } else {
                LogUtil.i("This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }


    public boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        return result == ConnectionResult.SUCCESS;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void savePrefValue(String field, String value) {
        SharedPreferences sp = getSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(field, value);
        editor.commit();
    }

    public String loadPrefValue(String field) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getString(field, null);
    }

}
