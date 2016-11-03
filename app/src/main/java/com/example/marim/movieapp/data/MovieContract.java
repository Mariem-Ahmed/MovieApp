package com.example.marim.movieapp.data;

import android.provider.BaseColumns;

/**
 * Created by Marim on 22-Aug-16.
 */
public class MovieContract {

    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_DATE = "date";
    }
}
