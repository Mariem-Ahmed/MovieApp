package com.example.marim.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.marim.movieapp.classes.Movie;
import com.example.marim.movieapp.R;
import com.example.marim.movieapp.data.MovieDBHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Marim on 01-Aug-16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    MovieDBHelper db = new MovieDBHelper(getContext());

    Context c;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0);
        c = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.image_item);

        Picasso.with(c).load("http://image.tmdb.org/t/p/w185/" + movie.getImagePath()).into(iconView);

        return convertView;
    }

}