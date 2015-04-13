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
 * Created by byungwoo on 15. 4. 11..
 */
public abstract class GetCommentListAsyncTask extends AsyncTask<String, Void, JSONObject> {
    protected Uri.Builder mUriBuilder;

    public abstract EtagCache getEtagCache();

    @Override
    protected JSONObject doInBackground(String... params) {

        String videoId = params[1];

        if(videoId==null || videoId.isEmpty()){
            return null;
        }

        mUriBuilder = Uri.parse(Global.SERVER+"/get_comment").buildUpon();
        mUriBuilder.appendQueryParameter("video_id", videoId);

        final String result = doGetUrl(mUriBuilder.build().toString());

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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

}
