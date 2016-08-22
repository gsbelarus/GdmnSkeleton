package com.gsbelarus.gedemin.skeleton.core.data.task.entity;

import java.util.LinkedHashMap;

public class GdmnSignInParams extends BaseParams {

    private String login, password;

    public GdmnSignInParams(String url, String login, String password, LinkedHashMap<String, String> params) {
        super(url, params);
        this.login = login;
        this.password = password;
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
}
