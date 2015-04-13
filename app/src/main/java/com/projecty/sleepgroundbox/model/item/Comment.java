package com.projecty.sleepgroundbox.model.item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by byungwoo on 15. 4. 12..
 */
public class Comment {
    public String content;
    public int comment_id;
    public Date comment_time;
    public String user_name;
    public int like_count;

    public Comment(JSONObject obj) throws JSONException {
         content = obj.getString("comment");
         comment_id = obj.getInt("comment_id");
         comment_time = new Date(obj.getString("comment_time"));
         user_name = obj.getString("user_name");
         like_count = obj.getInt("count");
    }
}
