package com.projecty.sleepgroundbox.model.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byungwoo on 15. 4. 10..
 */
public abstract class PlayPage {
    public  String nextPageToken;
    public  List<PlayItem> items;
    public  String eTag;

    public PlayPage(JSONArray jsonItems, String etag, String nextPageToken) throws JSONException, ParseException {
        eTag = etag;
        items = new ArrayList<PlayItem>(jsonItems.length());
        this.nextPageToken = nextPageToken;

        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            items.add(getPlayItem(item));
        }
    }

    public PlayPage(JSONArray jsonItems, String etag) throws JSONException, ParseException {
        eTag = etag;
        items = new ArrayList<>(jsonItems.length());


        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            items.add(getPlayItem(item));
        }
    }

    protected abstract PlayItem getPlayItem(JSONObject item) throws JSONException, ParseException;
}