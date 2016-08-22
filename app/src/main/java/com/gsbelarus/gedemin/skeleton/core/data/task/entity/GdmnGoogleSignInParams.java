package com.gsbelarus.gedemin.skeleton.core.data.task.entity;

import java.util.LinkedHashMap;

public class GdmnGoogleSignInParams extends BaseParams {

    private String googleToken;

    public GdmnGoogleSignInParams(String url, String googleToken, LinkedHashMap<String, String> params) {
        super(url, params);
        this.googleToken = googleToken;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }
}
