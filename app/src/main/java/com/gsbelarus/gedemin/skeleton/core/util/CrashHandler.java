package com.gsbelarus.gedemin.skeleton.core.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

public final class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context context;

    public CrashHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, final Throwable exception) {
        Log.e("CrashHandler", "Thread :" + thread + "\nThrowable :" + exception);

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                new AlertDialog.Builder(context)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();

                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        })
                        .setMessage(exception.toString()).show();
                Looper.loop();
            }
        }.start();
    }
}
