package com.projecty.sleepgroundbox.task;

import android.net.Uri;
import android.os.AsyncTask;

import com.github.kevinsawicki.etag.CacheRequest;
import com.github.kevinsawicki.etag.EtagCache;
import com.projecty.sleepgroundbox.util.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by byungwoo on 15. 4. 4..
 */
public abstract class GetRecommandlistAsyncTask extends AsyncTask<String, Void, JSONObject> {
    protected Uri.Builder mUriBuilder;
    private String PATH = "/get_recommend";
    private static final String YOUTUBE_VIDEOLISTITEMS_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet,statistics,contentDetails&id=";
    private static final String YOUTUBE_PLAYLISTITEMS_URL = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails&playlistId=";
    private static final String YOUTUBE_KEY = "&key=AIzaSyDrp3hVd7PBIryKmk3nBcPIoxTOX5kTPvQ";

    @Override
    protected JSONObject doInBackground(String... params) {
        Uri.Builder builder = Uri.parse(Global.SERVER +PATH).buildUpon();
        JSONObject recommend = doGetJsonFromUrl(builder.build().toString());
        String play_list = "";
        String cover_list = "";

        if(recommend==null){
            return null;
        }

        try {
            play_list = recommend.getString("video_list");
            cover_list = recommend.getString("cover_list");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        String play_list_url  = YOUTUBE_PLAYLISTITEMS_URL+play_list+YOUTUBE_KEY;
        String cover_list_url  = YOUTUBE_VIDEOLISTITEMS_URL+cover_list+YOUTUBE_KEY;

        final String result_play = doGetUrl(play_list_url);
        final String result_cover = doGetUrl(cover_list_url);

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("video_list",new JSONObject(result_play));
            jsonObject.put("cover_list",new JSONObject(result_cover));
            jsonObject.put("video_id", play_list);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }

    private JSONObject doGetJsonFromUrl(String s) {
        CacheRequest request = CacheRequest.get(Global.SERVER +PATH, getEtagCache());

        StringBuilder builder = new StringBuilder();
        InputStream is = request.stream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String res =  builder.toString();
        if (res == null) {
            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;


    }


    public String doGetUrl(String url) {

        CacheRequest request = CacheRequest.get(url, getEtagCache());

        StringBuilder builder = new StringBuilder();
        InputStream is = request.stream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public abstract EtagCache getEtagCache();


}
