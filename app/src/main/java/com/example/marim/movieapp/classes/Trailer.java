package com.example.marim.movieapp.classes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marim on 23-Aug-16.
 */
public class Trailer {
    private String key;
    private String name;

    public Trailer() {

    }

    public Trailer(JSONObject trailer) throws JSONException {
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
    }


    public String getTrailerKey() {
        return key;
    }

    public String getTrailerName() {
        return name;
    }
}
