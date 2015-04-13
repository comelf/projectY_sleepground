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
 * Created by byungwoo on 15. 4. 11..
 */
public class SetLoginIdAsyncTask extends AsyncTask<String, Void, JSONObject> {

    private final String PATH = "/user_update";
    protected Uri.Builder mUriBuilder;

    @Override
    protected JSONObject doInBackground(String... params) {
        String userid = params[0];
        String userName = params[1];

        if(userName==null || userName.isEmpty()){
            return null;
        }

        mUriBuilder = Uri.parse(Global.SERVER +PATH).buildUpon();
        mUriBuilder.appendQueryParameter("user_id", userid)
                .appendQueryParameter("name",userName);

        String result = doGetUrl(mUriBuilder.build().toString());

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
        HttpRequest request = HttpRequest.get(url);

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
