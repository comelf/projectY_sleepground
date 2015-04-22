package com.projecty.sleepgroundbox.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.kevinsawicki.etag.CacheRequest;
import com.github.kevinsawicki.etag.EtagCache;
import com.projecty.sleepgroundbox.util.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by byungwoo on 15. 4. 8..
 */
public abstract class GetSandboxVideoAsyncTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "GetPlaylistAsyncTask";

    private static final int YOUTUBE_PLAYLIST_MAX_RESULTS = 10;
    private static final String YOUTUBE_PLAYLISTITEMS_URL = "https://www.googleapis.com/youtube/v3/playlistItems";
    private final String YOUTUBE_PLAYLIST_FIELDS = "etag,pageInfo,nextPageToken,items(id, snippet(title,position,publishedAt, thumbnails(medium,high),resourceId/videoId))";
    private final String YOUTUBE_PLAYLIST_PART ="snippet";
    public GetSandboxVideoAsyncTask() {

        mUriBuilder = Uri.parse(YOUTUBE_PLAYLISTITEMS_URL).buildUpon();
    }



    @Override
    protected JSONObject doInBackground(String... params) {
        final String playlistId = params[0];
        if (playlistId == null || playlistId.length() == 0) {
            return null;
        }

        if (params.length == 2) {
            final String nextPageToken = params[1];
            if (nextPageToken != null) {
                mUriBuilder.appendQueryParameter("pageToken", nextPageToken);
            }
        }

        mUriBuilder.appendQueryParameter("playlistId", playlistId)
                .appendQueryParameter("part", YOUTUBE_PLAYLIST_PART)
                .appendQueryParameter("maxResults", Integer.toString(YOUTUBE_PLAYLIST_MAX_RESULTS))
                .appendQueryParameter("fields", YOUTUBE_PLAYLIST_FIELDS)
                .appendQueryParameter("key", Global.YOUTUBE_API_KEY);

        Log.i(TAG, mUriBuilder.build().toString());
        final String result = doGetUrl(mUriBuilder.build().toString());
        if (result == null) {
            return null;
        }

        JSONObject jsonObject=null;

        try {
            jsonObject = new JSONObject(result);
            JSONArray itemList = jsonObject.getJSONArray("items");
            String items = "";
            for (int i = 0; i < itemList.length() ; i++) {
                String id = itemList.getJSONObject(i).getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");
                items = items + id + ",";
//                Log.i(TAG, id);
            }
            String api = "https://www.googleapis.com/youtube/v3/videos?part=statistics,contentDetails&id=" + items + "&key=AIzaSyDrp3hVd7PBIryKmk3nBcPIoxTOX5kTPvQ";
            Uri.Builder uriBuilder = Uri.parse(api).buildUpon();
            String result2 = doGetUrl(uriBuilder.build().toString());

            if(result2==null){
                return jsonObject;
            }
            JSONObject itemInfo = new JSONObject(result2);


            for (int i = 0; i < itemList.length() ; i++) {
                JSONObject item = itemInfo.getJSONArray("items").getJSONObject(i);
                JSONObject crew = itemList.getJSONObject(i).getJSONObject("snippet");

                String duration = item.getJSONObject("contentDetails").getString("duration");
                String viewCount = String.valueOf(item.getJSONObject("statistics").getLong("viewCount"));
                String likeCount = String.valueOf(item.getJSONObject("statistics").getLong("likeCount"));

                crew.put("duration",duration);
                crew.put("viewCount", viewCount);
                crew.put("likeCount", likeCount);
            }



        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


        return jsonObject;
    }

    protected Uri.Builder mUriBuilder;
    public abstract EtagCache getEtagCache();
    public String doGetUrl(String url) {
//        Log.d(TAG, url);

        CacheRequest request = CacheRequest.get(url, getEtagCache());
//        Log.d(TAG, "Response was " + request.body());

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

        if (request.cached()) {
            Log.d(TAG, "Cache hit");
        } else {
            Log.d(TAG, "Cache miss");
        }

        return builder.toString();
    }
}
