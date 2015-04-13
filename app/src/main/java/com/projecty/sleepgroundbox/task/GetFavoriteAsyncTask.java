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
 * Created by byungwoo on 15. 4. 8..
 */
public abstract class GetFavoriteAsyncTask extends AsyncTask<String, Void, JSONObject> {
    String path;
    int user_id;
    protected Uri.Builder mUriBuilder;
    String param;

    public GetFavoriteAsyncTask(String path,String param, int id) {
        this.path = path;
        this.user_id = id;
        this.param = param;
        mUriBuilder = Uri.parse(Global.SERVER + path).buildUpon();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        mUriBuilder.appendQueryParameter("user_id", String.valueOf(user_id));
        String result = doGetUrl(mUriBuilder.build().toString());
        String list;
        if (result == null) {
            return null;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            list = jsonObject.getString(param);
            if(list!=null){
                String req = "";
                if(param.equals("video_list")){
                    req = "https://www.googleapis.com/youtube/v3/videos?part=statistics,contentDetails,snippet&id="+ list +"&key=AIzaSyDrp3hVd7PBIryKmk3nBcPIoxTOX5kTPvQ";
                }else if(param.equals("play_list")){
                    req = "https://www.googleapis.com/youtube/v3/playlists?part=contentDetails,snippet&id="+ list +"&key=AIzaSyDrp3hVd7PBIryKmk3nBcPIoxTOX5kTPvQ";
                }else{
                    return null;
                }
                result = doGetUrl(req);
            }
            if(result!= null){
                jsonObject = new JSONObject(result);
            }

        }catch (JSONException e) {
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

    public abstract EtagCache getEtagCache();
}
