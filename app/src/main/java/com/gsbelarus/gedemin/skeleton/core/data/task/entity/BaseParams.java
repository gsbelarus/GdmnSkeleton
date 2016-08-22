package com.gsbelarus.gedemin.skeleton.core.data.task.entity;

import java.util.LinkedHashMap;

public class BaseParams {

    private String url;
    private LinkedHashMap<String, String> params;

    public BaseParams(String url, LinkedHashMap<String, String> params) {
        this.url = url;
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public void setParams(LinkedHashMap<String, String> params) {
        this.params = params;
    }
}
