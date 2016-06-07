package com.gsbelarus.gedemin.skeleton.core.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class LogUtil {

    private static final String TAG_DEFAULT = "GEDEMIN";
    public static boolean enabled = true;

    public static void v(Object... msg) {
        if (enabled) {
            android.util.Log.v(TAG_DEFAULT, getMessages(msg) + " " + getLocation());
        }
    }

    public static void d(Object... msg) {
        if (enabled) {
            android.util.Log.d(TAG_DEFAULT, getMessages(msg) + " " + getLocation());
        }
    }

    public static void i(Object... msg) {
        if (enabled) {
            android.util.Log.i(TAG_DEFAULT, getMessages(msg) + " " + getLocation());
        }
    }

    public static void w(Object... msg) {
        if (enabled) {
            android.util.Log.w(TAG_DEFAULT, getMessages(msg) + " " + getLocation());
        }
    }

    public static void e(Object... msg) {
        if (enabled) {
            android.util.Log.e(TAG_DEFAULT, getMessages(msg) + " " + getLocation());
        }
    }

    public static void wtf(Object... msg) {
        if (enabled) {
            android.util.Log.wtf(TAG_DEFAULT, getMessages(msg) + " " + getLocation());
        }
    }

    public static void vtag(String TAG, Object... msg) {
        if (enabled) {
            android.util.Log.v(TAG, getMessages(msg) + " " + getLocation());
        }
    }

    public static void dtag(String TAG, Object... msg) {
        if (enabled) {
            android.util.Log.d(TAG, getMessages(msg) + " " + getLocation());
        }
    }

    public static void itag(String TAG, Object... msg) {
        if (enabled) {
            android.util.Log.i(TAG, getMessages(msg) + " " + getLocation());
        }
    }

    public static void wtag(String TAG, Object... msg) {
        if (enabled) {
            android.util.Log.w(TAG, getMessages(msg) + " " + getLocation());
        }
    }

    public static void etag(String TAG, Object... msg) {
        if (enabled) {
            android.util.Log.e(TAG, getMessages(msg) + " " + getLocation());
        }
    }

    public static void wtftag(String TAG, Object... msg) {
        if (enabled) {
            android.util.Log.wtf(TAG, getMessages(msg) + " " + getLocation());
        }
    }

    private static StringBuilder printfLogUtil(Object... msg) {

        SimpleDateFormat formatter = new SimpleDateFormat("(dd.MM.yyyy HH:mm:ss)", Locale.getDefault());
        StringBuilder builder = new StringBuilder();

        if (msg != null) {
            for (int i = 0; i < msg.length; i++) {

                if (msg[i] instanceof String) {
                    builder.append(msg[i]);

                } else if (msg[i] instanceof Date) {
                    builder.append(formatter.format(msg[i]));

                } else if (msg[i] instanceof Calendar) {
                    Calendar calendar = (Calendar) msg[i];
                    builder.append(formatter.format(calendar.getTime()));

                } else if (msg[i] != null) {
                    builder.append(msg[i].toString());
                } else
                    builder.append("null");

                if (i < msg.length - 1)
                    builder.append(" & ");
            }
        } else
            builder.append("null");

        return builder;
    }

    private static String getMessages(Object... msg) {
        return "[" + printfLogUtil(msg) + "]";
    }

    private static String getLocation() {
        final String className = LogUtil.class.getName();
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(className)) {
                        Class<?> clazz = Class.forName(trace.getClassName());
                        return "[" + getClassName(clazz) + "." + trace.getMethodName() + "]:" +
                                "(" + getClassName(clazz) + ".java:" + trace.getLineNumber() + ")";
                    }

                } else if (trace.getClassName().startsWith(className))
                    found = true;

            } catch (ClassNotFoundException e) {
            }
        }

        return "[]: ";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

}
