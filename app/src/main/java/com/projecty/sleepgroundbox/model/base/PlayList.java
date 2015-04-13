package com.projecty.sleepgroundbox.model.base;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byungwoo on 15. 4. 11..
 */
public abstract class PlayList {
    public int totalResults;
    public int resultsPerPage;
    public List<PlayPage> pages;

    public PlayList(JSONObject jsonPlaylist) throws JSONException, ParseException {
        pages = new ArrayList<PlayPage>();

        JSONObject pageInfo = jsonPlaylist.getJSONObject("pageInfo");
        totalResults = pageInfo.getInt("totalResults");
        resultsPerPage = pageInfo.getInt("resultsPerPage");

        addPage(jsonPlaylist);
    }

    public int getCount() {
        int count = 0;
        for (PlayPage page : pages) {
            count += page.items.size();
        }

        return count;
    }

    public PlayItem getItem(int position) {
        int pageNumber = position / resultsPerPage;
        PlayPage page = pages.get(pageNumber);

        return page.items.get(position % resultsPerPage);
    }

    public String getNextPageToken(int position) {
        int pageNumber = position / resultsPerPage;
        PlayPage page = pages.get(pageNumber);

        return page.nextPageToken;
    }

    public abstract void addPage(JSONObject jsonPlaylist) throws JSONException, ParseException ;
}
