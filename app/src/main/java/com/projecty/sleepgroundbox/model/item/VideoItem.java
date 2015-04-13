package com.projecty.sleepgroundbox.model.item;

import com.projecty.sleepgroundbox.model.base.StatisticsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by byungwoo on 15. 4. 4..
 */
public class VideoItem extends StatisticsItem {

    public VideoItem(JSONObject jsonItem) throws JSONException, ParseException {
        final JSONObject snippet = jsonItem.getJSONObject("snippet");
        final JSONObject statistics = jsonItem.getJSONObject("statistics");
        final JSONObject contentDetails = jsonItem.getJSONObject("contentDetails");

        id = jsonItem.getString("id");
        title = snippet.getString("title");
        date = snippet.getString("publishedAt").substring(0, 10);
        thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
        videoId = jsonItem.getString("id");
        description = snippet.getString("description");

        duration = setDuration(contentDetails.getString("duration"));
        viewCount = convertCount(statistics.getString("viewCount"));
        likeCount = convertCount(statistics.getString("likeCount"));
    }
}
