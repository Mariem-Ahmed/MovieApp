package com.example.marim.movieapp.classes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marim on 23-Aug-16.
 */
public class Reviews {

    private String Author;
    private String Content;

    public Reviews() {
    }

    public Reviews(JSONObject reviews) throws JSONException {
        this.Author = reviews.getString("author");
        this.Content = reviews.getString("content");
    }

    public String getAuthorName() {
        return Author;
    }

    public String getContent() {
        return Content;
    }

}
