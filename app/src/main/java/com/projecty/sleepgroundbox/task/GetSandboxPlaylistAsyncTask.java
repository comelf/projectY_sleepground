package com.projecty.sleepgroundbox.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.github.kevinsawicki.etag.CacheRequest;
import com.github.kevinsawicki.etag.EtagCache;
import com.projecty.sleepgroundbox.util.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class GetSandboxPlaylistAsyncTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "GetPlaylistAsyncTask";

    private static final int YOUTUBE_PLAYLIST_MAX_RESULTS = 10;
    private static final String YOUTUBE_PLAYLISTITEMS_URL = "https://www.googleapis.com/youtube/v3/playlists";
    private static final String YOUTUBE_PLAYLIST_PART = "snippet,contentDetails";

    public GetSandboxPlaylistAsyncTask() {

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

        mUriBuilder.appendQueryParameter("channelId", playlistId)
                .appendQueryParameter("part", YOUTUBE_PLAYLIST_PART)
                .appendQueryParameter("maxResults", Integer.toString(YOUTUBE_PLAYLIST_MAX_RESULTS))
                .appendQueryParameter("key", Global.YOUTUBE_API_KEY);

        Log.i(TAG, mUriBuilder.build().toString());
        final String result = doGetUrl(mUriBuilder.build().toString());
        if (result == null) {
            return null;
        }

        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
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

        return builder.toString();
    }
}
