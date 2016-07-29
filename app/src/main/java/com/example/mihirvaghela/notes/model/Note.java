package com.example.mihirvaghela.notes.model;

import android.net.Uri;

import com.orm.SugarRecord;

import java.io.File;

/**
 * Created by PMH on 3/7/2016.
 */
public class Note extends SugarRecord {
    public String title = "";
    public int category = 0;
    public String content = "";
    public String imageUrls;
    public String time = "";
    public String location = "";
    public String recordUrls;

    public Note(){}

    public Note(String title, int cat, String content, String imgUrls, String recUrls, String time, String loc) {
//        super();
        this.title = title;
        this.category = cat;
        this.content = content;
        this.imageUrls = imgUrls;
        this.recordUrls = recUrls;
        this.time = time;
        this.location = loc;
    }

    public String getTitle() {
        return this.title;
    }

    public int getCategory() {
        return this.category;
    }

    public String getContent() {
        return this.content;
    }

    public String getImageURLs() {
        return this.imageUrls;
    }

    public String getImageUriPath(String imageUrl) {
        String uri = Uri.fromFile(new File(imageUrl)).toString();
        String decoded = Uri.decode(uri);
        return decoded;
    }

    public String[] getImageUriPathArray() {
        String[] arrayList = {};
        if (imageUrls != null) {
            arrayList = imageUrls.split(",");
        }
        return arrayList;
    }

    public String getTime() {
        return this.time;
    }

    public String getLocation() {
        return this.location;
    }

    public String getRecordUrls() {
        return this.recordUrls;
    }

    public String[] getRecordUriPathArray() {
        String[] arrayList = {};
        if (recordUrls != null) {
            arrayList = recordUrls.split(",");
        }
        return arrayList;
    }
}
