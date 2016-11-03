package com.example.marim.movieapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marim on 12-Aug-16.
 */
public class Movie implements Parcelable {

    private int id;
    private String title;
    private String path;
    private String overview;
    private int vote;
    private String date;

    public Movie() {
    }

    public Movie(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.path = movie.getString("poster_path");
        this.overview = movie.getString("overview");
        this.vote = movie.getInt("vote_average");
        this.date = movie.getString("release_date");
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        path = in.readString();
        overview = in.readString();
        vote = in.readInt();
        date = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return path;
    }

    public String getOverview() {
        return overview;
    }

    public int getRating() {
        return vote;
    }

    public String getDate() {
        return date;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImagePath(String path) {
        this.path = path;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRating(int vote) {
        this.vote = vote;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeString(overview);
        parcel.writeInt(vote);
        parcel.writeString(date);
    }
}
