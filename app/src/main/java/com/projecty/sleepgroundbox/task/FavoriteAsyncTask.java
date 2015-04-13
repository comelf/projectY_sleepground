package com.projecty.sleepgroundbox.task;

import android.net.Uri;
import android.os.AsyncTask;

import com.github.kevinsawicki.http.HttpRequest;
import com.projecty.sleepgroundbox.util.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by byungwoo on 15. 4. 11..
 */
public class FavoriteAsyncTask extends AsyncTask<String, Void, String> {
    protected Uri.Builder mUriBuilder;

    @Override
    protected String doInBackground(String... params) {
        String path = params[0];
        String videoId = params[1];
        String userId = params[2];

        if(path==null || path.isEmpty()){
            return null;
        }

        mUriBuilder = Uri.parse(Global.SERVER+path).buildUpon();
        mUriBuilder.appendQueryParameter("user_id", userId)
                .appendQueryParameter("video_id",videoId);

        String result = doGetUrl(mUriBuilder.build().toString());


        return result;
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
