package com.projecty.sleepgroundbox.model.base;

import java.util.Date;

/**
 * Created by byungwoo on 15. 4. 9..
 */
public class BjLog{
        public String content;
        public Date date;
        public String img_path;

        public BjLog(String content, String das, String img_path) {
            this.content = content;
            date= new Date(das);
            this.img_path = img_path;

        }
    }