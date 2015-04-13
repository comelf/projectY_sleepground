package com.projecty.sleepgroundbox.model.list;

import com.projecty.sleepgroundbox.model.base.StatisticsItem;
import com.projecty.sleepgroundbox.model.base.StatisticsPage;
import com.projecty.sleepgroundbox.model.base.VideoList;
import com.projecty.sleepgroundbox.model.item.VideoItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by byungwoo on 15. 4. 4..
 */
public class VideoItemlist extends VideoList {

    public VideoItemlist(JSONObject jsonPlaylist) throws JSONException, ParseException {
        super(jsonPlaylist);
        addPage(jsonPlaylist);
    }

    public void addPage(JSONObject jsonPlaylist) throws JSONException, ParseException {
        statisticsPages.add(new StatisticsPage(
                jsonPlaylist.getJSONArray("items"),
                jsonPlaylist.getString("etag")) {
            @Override
            protected StatisticsItem getStatisticsItem(JSONObject item) throws JSONException, ParseException {
                return new VideoItem(item);
            }
        });
    }

}
