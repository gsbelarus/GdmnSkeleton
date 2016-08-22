package com.gsbelarus.gedemin.skeleton.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gsbelarus.gedemin.skeleton.R;

public class CoreNetworkInfo {

    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void runIfNetworkAvailable(@NonNull final View view, @NonNull final Runnable runnable) {
        CoreUtils.runWithRetry(view, view.getContext().getString(R.string.network_unavailable), new CoreUtils.Callback() {
            @Override
            public boolean run() {
                if (isNetworkAvailable(view.getContext())) {
                    runnable.run();
                    return true;
                }
                return false;
            }
        });
    }
}
