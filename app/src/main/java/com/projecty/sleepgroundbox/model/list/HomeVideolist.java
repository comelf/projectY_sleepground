
package com.projecty.sleepgroundbox.model.list;

import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.base.StatisticsPage;
import com.projecty.sleepgroundbox.model.base.VideoList;
import com.projecty.sleepgroundbox.model.item.HomeVideoItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class HomeVideolist extends VideoList {

    public HomeVideolist(JSONObject jsonPlaylist) throws JSONException, ParseException {
        super(jsonPlaylist);
        addPage(jsonPlaylist);
    }

    public void addPage(JSONObject jsonPlaylist) throws JSONException, ParseException {
        statisticsPages.add(new StatisticsPage(
                jsonPlaylist.getJSONArray("items"),
                jsonPlaylist.getString("etag"),
                jsonPlaylist.optString("nextPageToken", null)) {
            @Override
            protected StatisticsItem getStatisticsItem(JSONObject item) throws JSONException, ParseException {
                return new HomeVideoItem(item);
            }
        });
    }
}
