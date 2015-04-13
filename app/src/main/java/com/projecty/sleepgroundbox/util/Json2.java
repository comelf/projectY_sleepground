package com.projecty.sleepgroundbox.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by LeeYoungNam on 3/28/15.
 */
public class Json2 {
    public static String getJson(String url) {
        StringBuilder sbUrl = new StringBuilder(url);

        String result = "start";
        String response = "";

        result = "생성자 앞  ";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(sbUrl.toString());
        result = "생성자 뒷 ";
        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();

            result = "버퍼뒷  ";
            BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
            String s = "";
            while ((s = buffer.readLine()) != null) {
                response += s;
            }
            result = "성공  ";

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }

        return result;
//                return response;
    }
}
