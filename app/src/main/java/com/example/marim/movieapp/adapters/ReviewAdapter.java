package com.example.marim.movieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.marim.movieapp.R;
import com.example.marim.movieapp.classes.Reviews;

import java.util.ArrayList;

/**
 * Created by Marim on 23-Aug-16.
 */
public class ReviewAdapter extends ArrayAdapter<Reviews> {

    TextView textView;

    Context c;

    public ReviewAdapter(Context context, ArrayList<Reviews> Review) {
        super(context, 0);
        c = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reviews reviews = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
        }

        textView = (TextView) convertView.findViewById(R.id.review_item);

        textView.setText(reviews.getAuthorName() + "\n" + reviews.getContent());

        return convertView;
    }
}
