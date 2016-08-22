package com.gsbelarus.gedemin.skeleton.core.data.task.entity;

import java.util.LinkedHashMap;

public class GdmnGoogleSignInResult {

    private String login, password, gdmnToken;
    private LinkedHashMap<String, String> params;

    public GdmnGoogleSignInResult() {
    }

    public GdmnGoogleSignInResult(String login, String password, String gdmnToken, LinkedHashMap<String, String> params) {
        this.login = login;
        this.password = password;
        this.gdmnToken = gdmnToken;
        this.params = params;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, String> params) {
        this.params = params;
    }

    public String getGdmnToken() {
        return gdmnToken;
    }

    public void setGdmnToken(String gdmnToken) {
        this.gdmnToken = gdmnToken;
    }
}
