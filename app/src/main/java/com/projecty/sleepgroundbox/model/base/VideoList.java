package com.projecty.sleepgroundbox.model.base;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byungwoo on 15. 4. 10..
 */
public abstract class VideoList {
    public final int totalResults;
    public final int resultsPerPage;

    public List<StatisticsPage> statisticsPages;

    public VideoList(JSONObject jsonPlaylist) throws JSONException, ParseException {
        statisticsPages = new ArrayList<StatisticsPage>();
        JSONObject pageInfo = jsonPlaylist.getJSONObject("pageInfo");
        totalResults = pageInfo.getInt("totalResults");
        resultsPerPage = pageInfo.getInt("resultsPerPage");

    }

    public int getCount() {
        int count = 0;
        for (StatisticsPage statisticsPage : statisticsPages) {
            count += statisticsPage.items.size();
        }

        return count;
    }

    public StatisticsItem getItem(int position) {
        int pageNumber = position / resultsPerPage;
        StatisticsPage statisticsPage = statisticsPages.get(pageNumber);
        return statisticsPage.items.get(position % resultsPerPage);
    }

    public String getNextPageToken(int position) {
        int pageNumber = position / resultsPerPage;
        StatisticsPage statisticsPage = statisticsPages.get(pageNumber);

        return statisticsPage.nextPageToken;
    }

}
