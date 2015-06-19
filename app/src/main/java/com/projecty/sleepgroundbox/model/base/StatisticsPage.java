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
public abstract class StatisticsPage {
    public String nextPageToken;
    public List<StatisticsItem> items;
    public String eTag;

    public StatisticsPage(JSONArray jsonItems, String etag, String nextPageToken) throws JSONException, ParseException {
        eTag = etag;
        items = new ArrayList<StatisticsItem>(jsonItems.length());
        this.nextPageToken = nextPageToken;

        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            StatisticsItem insertItem = getStatisticsItem(item);
            if (insertItem.title != "Private video"){
                items.add(insertItem);
            }
        }
    }

    public StatisticsPage(JSONArray jsonItems, String etag) throws JSONException, ParseException {
        eTag = etag;
        items = new ArrayList<StatisticsItem>(jsonItems.length());

        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            StatisticsItem insertItem = getStatisticsItem(item);
            if (insertItem.title != "Private video"){
                items.add(insertItem);
            }
        }
    }

    protected abstract StatisticsItem getStatisticsItem(JSONObject item) throws JSONException, ParseException;


}