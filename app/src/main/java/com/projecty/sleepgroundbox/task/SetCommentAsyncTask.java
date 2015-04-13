package com.projecty.sleepgroundbox.task;

import android.net.Uri;
import android.os.AsyncTask;

import com.github.kevinsawicki.http.HttpRequest;
import com.projecty.sleepgroundbox.util.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by byungwoo on 15. 4. 12..
 */
public class SetCommentAsyncTask extends AsyncTask<String, Void, JSONObject>{

    private final String PATH = "/write_comment";
    protected Uri.Builder mUriBuilder;

    @Override
    protected JSONObject doInBackground(String... params) {
        String content = params[0];
        String vidroId = params[1];
        String userId = params[2];

        if(content.trim().isEmpty()){
            return null;
        }

        mUriBuilder = Uri.parse(Global.SERVER +PATH).buildUpon();
        mUriBuilder.appendQueryParameter("user_id", userId)
                .appendQueryParameter("video_id", vidroId)
                .appendQueryParameter("commnet", content);

        final String result = doPostUrl(mUriBuilder.build().toString());

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return jsonObject;
    }


    public String doPostUrl(String url) {
        HttpRequest request = HttpRequest.post(url);
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
