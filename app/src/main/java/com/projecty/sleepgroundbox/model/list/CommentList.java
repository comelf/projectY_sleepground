package com.projecty.sleepgroundbox.model.list;

import com.projecty.sleepgroundbox.model.item.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byungwoo on 15. 4. 12..
 */
public class CommentList {
    private List<Comment> comments = new ArrayList<Comment>();

    public CommentList(JSONObject jsonlist) throws JSONException, ParseException {
        JSONArray comments = jsonlist.getJSONArray("comment");

        for(int i =0; i<comments.length(); i++){
            JSONObject comObj = (JSONObject) comments.get(i);
            addComment(comObj);
        }

    }

    private void addComment(JSONObject obj) throws JSONException {
        Comment comment = new Comment(obj);
        this.comments.add(comment);
    }

    public int getCount() {
        return comments.size();
    }

    public Comment getComment(int position) {
        return comments.get(position);
    }
}
