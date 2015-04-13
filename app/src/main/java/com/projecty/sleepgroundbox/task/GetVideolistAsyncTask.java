package com.projecty.sleepgroundbox.task;

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

public abstract class GetVideolistAsyncTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "GetYouTubelistAsyncTask";

    private static final int YOUTUBE_PLAYLIST_MAX_RESULTS = 10;
    private static final String YOUTUBE_PLAYLISTITEMS_URL = "https://www.googleapis.com/youtube/v3/playlistItems";
    private static final String YOUTUBE_PLAYLIST_PART = "snippet";
    private static final String YOUTUBE_PLAYLIST_FIELDS = "etag,pageInfo,nextPageToken,items(id, snippet(title,position,publishedAt, thumbnails(medium,high),resourceId/videoId))";
//            "statistics(viewCount, videoCount))";
//    , statistics(viewCount, videoCount))";

    public GetVideolistAsyncTask() {

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


        Log.i(TAG, "Failed to get playlist");
        final String result = doGetUrl(mUriBuilder.build().toString());
        if (result == null) {
//            Log.e(TAG, "Failed to get playlist");
            return null;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            JSONArray itemList = jsonObject.getJSONArray("items");
            String items = "";
            for (int i = 0; i < itemList.length() ; i++) {
                String id = itemList.getJSONObject(i).getJSONObject("snippet").getJSONObject("resourceId").getString("videoId");
                items = items + id + ",";
//                Log.i(TAG, id);
            }
            String api = "https://www.googleapis.com/youtube/v3/videos?part=statistics,contentDetails,snippet&id=" + items + "&key=AIzaSyDrp3hVd7PBIryKmk3nBcPIoxTOX5kTPvQ";
            Uri.Builder uriBuilder = Uri.parse(api).buildUpon();
            String result2 = doGetUrl(uriBuilder.build().toString());
            JSONObject itemInfo = new JSONObject(result2);

            for (int i = 0; i < itemList.length() ; i++) {
                JSONObject item = itemInfo.getJSONArray("items").getJSONObject(i);
                JSONObject snippet = itemList.getJSONObject(i).getJSONObject("snippet");

                String duration = item.getJSONObject("contentDetails").getString("duration");
                String viewCount = String.valueOf(item.getJSONObject("statistics").getLong("viewCount"));
                String likeCount = String.valueOf(item.getJSONObject("statistics").getLong("likeCount"));
                String description = item.getJSONObject("snippet").getString("description");
                snippet.put("duration",duration);
                snippet.put("viewCount", viewCount);
                snippet.put("likeCount", likeCount);
                snippet.put("description",description);
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
