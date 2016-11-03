package com.example.marim.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marim.movieapp.R;
import com.example.marim.movieapp.classes.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Marim on 23-Aug-16.
 */
public class TrailerAdapter extends ArrayAdapter<Trailer> {

    TextView textView;

    Context c;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
        super(context, 0);
        c = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailer trailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_trailer, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.trailer_item);
        textView = (TextView) convertView.findViewById(R.id.trailer_name);

        Picasso.with(c).load("http://img.youtube.com/vi/" + trailer.getTrailerKey() + "/0.jpg").into(iconView);

        textView.setText(trailer.getTrailerName());

        return convertView;
    }
}
