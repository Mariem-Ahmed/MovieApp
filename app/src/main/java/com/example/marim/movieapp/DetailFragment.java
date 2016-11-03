package com.example.marim.movieapp;

/**
 * Created by Marim on 28-Aug-16.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marim.movieapp.adapters.ReviewAdapter;
import com.example.marim.movieapp.adapters.TrailerAdapter;
import com.example.marim.movieapp.classes.Movie;
import com.example.marim.movieapp.classes.Reviews;
import com.example.marim.movieapp.classes.Trailer;

import com.example.marim.movieapp.data.MovieDBHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailFragment extends Fragment {

    static final String DETAIL_URI = "URI";
    private Movie movie;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private ArrayList<Trailer> trailers = null;
    private ArrayList<Reviews> reviews = null;

    private ScrollView DetailLayout;

    ImageView Image;
    TextView Title;
    TextView Overview;
    TextView Rating;
    TextView Date;
    RatingBar vote;
    CheckBox favorite;
    ListView trailerView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        final MovieDBHelper db = new MovieDBHelper(getActivity());

        Image = (ImageView) rootView.findViewById(R.id.path);
        Title = (TextView) rootView.findViewById(R.id.title);
        Overview = (TextView) rootView.findViewById(R.id.overview);
        Rating = (TextView) rootView.findViewById(R.id.rating);
        Date = (TextView) rootView.findViewById(R.id.date);
        vote = (RatingBar) rootView.findViewById(R.id.ratingBar);
        favorite = (CheckBox) rootView.findViewById(R.id.checkBox);
        DetailLayout = (ScrollView) rootView.findViewById(R.id.detail_layout);

        if (movie != null) {
            DetailLayout.setVisibility(View.VISIBLE);
        } else {
            DetailLayout.setVisibility(View.INVISIBLE);
        }

        trailerView = (ListView) rootView.findViewById(R.id.trailers);
        trailerAdapter = new TrailerAdapter(getActivity(), trailers);
        trailerView.setAdapter(trailerAdapter);

        ListView reviewView = (ListView) rootView.findViewById(R.id.reviews);
        reviewAdapter = new ReviewAdapter(getActivity(), reviews);
        reviewView.setAdapter(reviewAdapter);

        if (movie != null) {

            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + movie.getImagePath()).into(Image);
            Title.setText(movie.getTitle());
            Overview.setText(movie.getOverview());
            Rating.setText(String.valueOf(movie.getRating()) + "/10");
            Date.setText(movie.getDate());

            vote.setNumStars(3);
            vote.setRating((float) (movie.getRating() * 0.3));
            vote.setIsIndicator(true);

            trailerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Trailer trailer = trailerAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getTrailerKey()));
                    startActivity(intent);
                }
            });

            Cursor cursor = db.searchMovies(String.valueOf(movie.getId()));
            int num = cursor.getCount();

            if (num == 1) {
                favorite.setChecked(true);
            } else {
                favorite.setChecked(false);
            }

            favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (favorite.isChecked()) {
                        Toast.makeText(getActivity(), "Favorite!", Toast.LENGTH_SHORT).show();
                        db.insertNewMovie(
                                movie.getId(),
                                movie.getTitle(),
                                movie.getImagePath(),
                                movie.getOverview(),
                                movie.getRating(),
                                movie.getDate()
                        );
                    } else {
                        Toast.makeText(getActivity(), "Unfavorite!", Toast.LENGTH_SHORT).show();
                        db.deleteMovie(movie.getId());
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (movie != null) {
            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask();
            fetchTrailerTask.execute();
            fetchReviewsTask.execute();
        }
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, List<Trailer>> {

        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private List<Trailer> getDataFromJson(String Key) throws JSONException {

            JSONObject trailerJson = new JSONObject(Key);
            JSONArray trailerArray = trailerJson.getJSONArray("results");

            List<Trailer> TRAILERS_LIST = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);

                Trailer trailerModel = new Trailer(trailer);
                TRAILERS_LIST.add(trailerModel);
            }

            Log.e("task output : ", TRAILERS_LIST.size() + "");

            return TRAILERS_LIST;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                //return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailerJsonStr = null;

            String API = "b5fcf1c2e2251b1ce73bd87f9cd4131f";

            try {
                final String API_KEY_PARAM = "api_key";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(movie.getId()))
                        .appendPath("videos")
                        .appendQueryParameter(API_KEY_PARAM, API)
                        .build();

                String myUrl = builder.build().toString();

                Log.e("my task", myUrl);

                URL url = new URL(myUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                trailerJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Log.e("task output2 : ", trailerJsonStr);
                return getDataFromJson(trailerJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Trailer> result) {
            if (result != null) {
                trailerAdapter.clear();
                for (Trailer trailerJsonStr : result) {
                    trailerAdapter.add(trailerJsonStr);
                }
                Log.e("task output3 : ", trailerAdapter.getCount() + "");
            }
            trailerAdapter.notifyDataSetChanged();
            Utility.setListViewHeightBasedOnItems(trailerView);
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, List<Reviews>> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private List<Reviews> getDataFromJson(String Key) throws JSONException {

            JSONObject reviewJson = new JSONObject(Key);
            JSONArray reviewArray = reviewJson.getJSONArray("results");

            List<Reviews> REVIEWS_LIST = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);

                Reviews reviewModel = new Reviews(review);
                REVIEWS_LIST.add(reviewModel);
            }

            Log.e("task output : ", REVIEWS_LIST.size() + "");

            return REVIEWS_LIST;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<Reviews> doInBackground(String... params) {

            if (params.length == 0) {
                //return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            String API = "b5fcf1c2e2251b1ce73bd87f9cd4131f";

            try {
                final String API_KEY_PARAM = "api_key";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(String.valueOf(movie.getId()))
                        .appendPath("reviews")
                        .appendQueryParameter(API_KEY_PARAM, API)
                        .build();

                String myUrl = builder.build().toString();

                Log.e("my task", myUrl);

                URL url = new URL(myUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                reviewJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Log.e("task output2 : ", reviewJsonStr);
                return getDataFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Reviews> result) {
            if (result != null) {
                reviewAdapter.clear();
                for (Reviews reviewJsonStr : result) {
                    reviewAdapter.add(reviewJsonStr);
                }
                Log.e("task output3 : ", reviewAdapter.getCount() + "");
            }
            reviewAdapter.notifyDataSetChanged();
        }
    }
}
