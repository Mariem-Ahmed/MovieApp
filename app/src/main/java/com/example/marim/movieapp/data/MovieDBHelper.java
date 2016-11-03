package com.example.marim.movieapp.data;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.example.marim.movieapp.DetailFragment;
import com.example.marim.movieapp.MovieFragment;
import com.example.marim.movieapp.adapters.MovieAdapter;
import com.example.marim.movieapp.classes.Movie;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Marim on 22-Aug-16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.FavoritesEntry.TABLE_NAME + " (" +
                MovieContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoritesEntry.COLUMN_IMAGE + " TEXT, " +
                MovieContract.FavoritesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.FavoritesEntry.COLUMN_RATING + " INTEGER, " +
                MovieContract.FavoritesEntry.COLUMN_DATE + " TEXT);";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insertNewMovie(int id, String title, String image, String overview, int rating, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MovieContract.FavoritesEntry.COLUMN_MOVIE_ID, id);
        initialValues.put(MovieContract.FavoritesEntry.COLUMN_TITLE, title);
        initialValues.put(MovieContract.FavoritesEntry.COLUMN_IMAGE, image);
        initialValues.put(MovieContract.FavoritesEntry.COLUMN_OVERVIEW, overview);
        initialValues.put(MovieContract.FavoritesEntry.COLUMN_RATING, rating);
        initialValues.put(MovieContract.FavoritesEntry.COLUMN_DATE, date);

        db = getWritableDatabase();

        return db.insert(MovieContract.FavoritesEntry.TABLE_NAME, null, initialValues);
    }

    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        db = getReadableDatabase();
        Cursor cursor = db.query(true, MovieContract.FavoritesEntry.TABLE_NAME,
                new String[]{
                        MovieContract.FavoritesEntry.COLUMN_MOVIE_ID,
                        MovieContract.FavoritesEntry.COLUMN_TITLE,
                        MovieContract.FavoritesEntry.COLUMN_IMAGE,
                        MovieContract.FavoritesEntry.COLUMN_OVERVIEW,
                        MovieContract.FavoritesEntry.COLUMN_RATING,
                        MovieContract.FavoritesEntry.COLUMN_DATE
                },

                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setId(cursor.getInt(0));
                movie.setTitle(cursor.getString(1));
                movie.setImagePath(cursor.getString(2));
                movie.setOverview(cursor.getString(3));
                movie.setRating(cursor.getInt(4));
                movie.setDate(cursor.getString(5));
                movieList.add(movie);
            } while (cursor.moveToNext());
        }

        return movieList;
    }

    public boolean deleteMovie(long rowId) {
        return db.delete(MovieContract.FavoritesEntry.TABLE_NAME, MovieContract.FavoritesEntry.COLUMN_MOVIE_ID + "=" + rowId, null) > 0;
    }

    public Cursor searchMovies(String rowId) throws SQLException {
        db = getReadableDatabase();

        Cursor cursor = db.query(true, MovieContract.FavoritesEntry.TABLE_NAME,
                new String[]{
                        MovieContract.FavoritesEntry.COLUMN_IMAGE},
                MovieContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{rowId},
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
