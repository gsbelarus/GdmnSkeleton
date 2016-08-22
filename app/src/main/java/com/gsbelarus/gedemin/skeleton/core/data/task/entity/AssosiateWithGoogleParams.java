package com.gsbelarus.gedemin.skeleton.core.data.task.entity;

import java.util.LinkedHashMap;

public class AssosiateWithGoogleParams extends BaseParams {

    private String gdmnToken, googleToken;

    public AssosiateWithGoogleParams(String url, String gdmnToken, String googleToken, LinkedHashMap<String, String> params) {
        super(url, params);
        this.gdmnToken = gdmnToken;
        this.googleToken = googleToken;
    }

    public String getGdmnToken() {
        return gdmnToken;
    }

    public void setGdmnToken(String gdmnToken) {
        this.gdmnToken = gdmnToken;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }
}
