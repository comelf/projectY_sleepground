package com.projecty.sleepgroundbox.model.list;

import com.projecty.sleepgroundbox.model.base.BjLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byungwoo on 15. 4. 9..
 */
public class Loglist {
    List<BjLog> logs;
    int totalResults;

    public Loglist(JSONObject result) throws JSONException, ParseException {
        logs = new ArrayList<BjLog>();

        JSONArray logArr = result.getJSONArray("result");

        totalResults = logArr.length();

        for (int i =0; i<logArr.length(); i++){
            JSONObject log = (JSONObject) logArr.get(i);
            addLog(log);
        }


    }

    private void addLog(JSONObject log) {
        if(log==null)
            return;

        try {
            String content = log.getString("content");
            String date = log.getString("date");
            String img_path = log.getString("img_path");
            BjLog bjLog = new BjLog(content,date,img_path);
            logs.add(bjLog);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public int getCount() {
        return totalResults;
    }

    public BjLog getItem(int i) {
        return logs.get(i);
    }


}
