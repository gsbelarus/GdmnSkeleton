package com.gsbelarus.gedemin.skeleton.core.util;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class IndexingHelper {

    private GoogleApiClient client;

    private Uri url;
    private String title;
    private String description;

    public IndexingHelper(Context context, Uri url, String title, String description) {
        this.client = new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName(title)
                .setDescription(description)
                .setUrl(url)
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public GoogleApiClient getClient() {
        return client;
    }
}
