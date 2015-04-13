package com.projecty.sleepgroundbox.model.base;

import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Created by byungwoo on 15. 4. 10..
 */
public class StatisticsItem {
    public String id;
    public String title;
    public String date;
    public String thumbnailUrl;
    public String videoId;
    public String description;
    public String duration;
    public String likeCount;
    public String viewCount;

    public String convertCount(String num){
        String result = num;

        int length = num.length();
        if (length > 4){
            result = num.substring(0, length-4) + "만";
        }
        return result;
    }


    private static HashMap<String, String> regexMap = new HashMap<>();
    private static String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
    private static String two = "0$1";

    public String setDuration(String druation) throws ParseException {

        regexMap.put("PT(\\d\\d)S", "00:$1");
        regexMap.put("PT(\\d\\d)M", "$1:00");
        regexMap.put("PT(\\d\\d)H", "$1:00:00");
        regexMap.put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
        regexMap.put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
        regexMap.put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
        regexMap.put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");

        String d = druation.replaceAll(regex2two, two);
        String regex = getRegex(d);
        return d.replaceAll(regex, regexMap.get(regex));
    }

    public String getRegex(String date) {
        for (String r : regexMap.keySet())
            if (Pattern.matches(r, date))
                return r;
        return null;
    }
}
