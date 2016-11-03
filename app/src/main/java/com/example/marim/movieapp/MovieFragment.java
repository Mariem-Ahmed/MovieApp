package com.example.marim.movieapp;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.GridView;

import com.example.marim.movieapp.adapters.MovieAdapter;
import com.example.marim.movieapp.classes.Movie;
import com.example.marim.movieapp.data.MovieDBHelper;

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

public class MovieFragment extends Fragment {

    private MovieAdapter movieAdapter;
    GridView gridview;
    private ArrayList<Movie> movies = null;

    private String sort_by;

    public interface Callback {
        public void onItemSelected(Movie movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            onStart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridview = (GridView) rootView.findViewById(R.id.gridview);
        movieAdapter = new MovieAdapter(getActivity(), movies);
        gridview.setAdapter(movieAdapter);

        Log.e("task output0", movieAdapter.getCount() + "");

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie);
            }
        });
        return rootView;
    }

    private void updatePosters() {
        final MovieDBHelper db = new MovieDBHelper(getActivity());
        FetchPosterTask fetchPosterTask = new FetchPosterTask();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sort_by = sharedPrefs.getString(getString(R.string.pref_key), getString(R.string.pref_popular));
        if (sort_by.equals("favorite")) {
            List<Movie> fav = db.getAllMovies();
            if (fav != null) {
                movieAdapter.clear();
                for (Movie movieJsonStr : fav) {
                    movieAdapter.add(movieJsonStr);
                }
                Log.e("task output3 : ", movieAdapter.getCount() + "");
                movieAdapter.notifyDataSetChanged();
            }
        } else {
            fetchPosterTask.execute(sort_by);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePosters();
        Log.e("task output1", movieAdapter.getCount() + "");
    }

    public class FetchPosterTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        private List<Movie> getDataFromJson(String Images) throws JSONException {

            JSONObject movieJson = new JSONObject(Images);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movie> IMAGES_LIST = new ArrayList<>();

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);

                Movie movieModel = new Movie(movie);
                IMAGES_LIST.add(movieModel);
            }

            Log.e("task output : ", IMAGES_LIST.size() + "");

            return IMAGES_LIST;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                //return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            String API = "b5fcf1c2e2251b1ce73bd87f9cd4131f";

            if (sort_by.equals("popular")) {
                sort_by = "popular";
            } else {
                sort_by = "top_rated";
            }

            try {
                final String API_KEY_PARAM = "api_key";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sort_by)
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
                movieJsonStr = buffer.toString();
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
                Log.e("task output2 : ", movieJsonStr);
                return getDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            if (result != null) {
                movieAdapter.clear();
                for (Movie movieJsonStr : result) {
                    movieAdapter.add(movieJsonStr);
                }
                Log.e("task output3 : ", movieAdapter.getCount() + "");
            }
            movieAdapter.notifyDataSetChanged();
        }
    }
}
