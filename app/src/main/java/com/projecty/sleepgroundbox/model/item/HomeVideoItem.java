package com.projecty.sleepgroundbox.model.item;

import com.projecty.sleepgroundbox.model.base.StatisticsItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class HomeVideoItem extends StatisticsItem{


    public HomeVideoItem(JSONObject jsonItem) throws JSONException, ParseException {
        final JSONObject snippet = jsonItem.getJSONObject("snippet");

        id = jsonItem.getString("id");
        title = snippet.getString("title");
        if (title != "Private video"){
            date = snippet.getString("publishedAt").substring(0, 10);
            thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
            videoId = snippet.getJSONObject("resourceId").getString("videoId");
            description = snippet.getString("description");

            duration = setDuration(snippet.getString("duration"));
            viewCount = convertCount(snippet.getString("viewCount"));
            likeCount = convertCount(snippet.getString("likeCount"));
        }else{
            date ="null";
            thumbnailUrl = "null";
            videoId = "null";
            description = "null";
            duration = "null";
            viewCount = "null";
            likeCount = "null";
        }

    }

}
