package com.gsbelarus.gedemin.skeleton.core.util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import java.util.LinkedHashMap;

public class CoreAuthTokenLoader extends AsyncTaskLoader<Result<String>> {

    private String url, login, password;
    private LinkedHashMap<String, String> params;

    private Result<String> result = new Result<>();

    public CoreAuthTokenLoader(Context context, String url, String login, String password, LinkedHashMap<String, String> params) {
        super(context);
        this.url = url;
        this.login = login;
        this.password = password;
        this.params = params;
    }

    public static Result<String> signIn(Context context, String url, String login, String password, LinkedHashMap<String, String> params) {
        return new CoreAuthTokenLoader(context, url, login, password, params).loadInBackground();
    }

    @Override
    protected void onStartLoading() {
        if (result.getData() == null && result.getException() == null) {
            forceLoad();
        } else {
            deliverResult(result);
        }
    }

    @Override
    public void deliverResult(Result<String> data) {
        result = data;
        super.deliverResult(data);
    }

    @Override
    public Result<String> loadInBackground() {
        try {
            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(login)) {
                throw new Exception("url or login is empty");
            }
            result.setData(signIn());
        } catch (Exception e) {
            result.setException(e);
        }
        return result;
    }

    private String signIn() throws Exception {
        Logger.d();
        Thread.sleep(5000);
        return "testToken"; //TODO
    }

    public String getUrl() {
        return url;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }
}
