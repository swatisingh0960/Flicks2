package com.nanodegree.swatisingh.popularmoviesapp2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nanodegree.swatisingh.popularmoviesapp2.Adapter.MovieAdapter;
import com.nanodegree.swatisingh.popularmoviesapp2.ContentProvider.MovieContract;
import com.nanodegree.swatisingh.popularmoviesapp2.Model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MovieActivity extends AppCompatActivity implements MovieAdapter.MovieItemClickListener {

    private RecyclerView rv_movies;
    private MovieAdapter movieAdapter;
    private GridLayoutManager layoutManager;

    private ProgressBar mProgressLoading;

    private static final String POPULAR_MOVIES_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_RATED_MOVIES_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String UPCOMING = "https://api.themoviedb.org/3/movie/upcoming";
    private static final String NOW_PLAYING = "https://api.themoviedb.org/3/movie/now_playing";

    private static final String lang = "en-US";
    private static final int page = 1;

    private final static String API_PARAM = "api_key";
    private final static String LANG_PARAM = "language";
    private final static String PAGE_PARAM = "page";

    public static final String TAG = "MovieActivity";

    //0 for popular movies; 1 for top-rated movies, 2 for Upcoming movies, 3 for Now Playing and 4 for Favourites

    private static int POP_TOPR_UPC_NOWP_MOVIES;

    private static SharedPreferences sharedPreferences;

    public static final String MY_PREFS = "popular";

    public static Movie[] movieArray;

    Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        getChoice();

        mProgressLoading = (ProgressBar) findViewById(R.id.progressBar);

        rv_movies = (RecyclerView) findViewById(R.id.rv_movies);

        layoutManager = new GridLayoutManager(this, 2);

        rv_movies.setLayoutManager(layoutManager);

        movieAdapter = new MovieAdapter(this);
        rv_movies.setAdapter(movieAdapter);

        if (savedInstanceState == null) {
            loadMoviesData();
        } else {
            int ids[] = savedInstanceState.getIntArray("id");
            String posters[] = savedInstanceState.getStringArray("poster");

            movieArray = new Movie[ids.length];
            for (int i = 0; i < ids.length; i++) {
                Log.d(TAG, "onCreate: " + ids[i] + " and " + posters[i]);
                movieArray[i] = new Movie(ids[i], posters[i]);
            }

            movieAdapter.setMovieData(movieArray);
            movieAdapter.notifyDataSetChanged();
            showMoviesData();

            mListState = savedInstanceState.getParcelable("position");
            if (mListState != null) {
                layoutManager.onRestoreInstanceState(mListState);
            }
        }
    }


    public void getChoice() {
        Context context = MovieActivity.this;
        sharedPreferences = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        POP_TOPR_UPC_NOWP_MOVIES = sharedPreferences.getInt("choice", 0);
    }

    @Override
    public void onClick(int id) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private void loadMoviesData() {
        showLoadIndicator();

        if (POP_TOPR_UPC_NOWP_MOVIES != 4) {
            URL moviesUrl = buildUrl();
            new MoviesFetchTask().execute(moviesUrl);
        } else {
            new FavouriteMoviesFetchTask().execute();
        }
    }

    public class FavouriteMoviesFetchTask extends AsyncTask<Void, Void, Void> {

        Movie[] movies;

        @Override
        protected Void doInBackground(Void... params) {
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            Log.d(TAG, "doInBackground: cursor: " + cursor.getCount());

            int count = cursor.getCount();
            movies = new Movie[cursor.getCount()];
            if (count == 0) {
                return null;
            }

            if (cursor.moveToFirst()) {
                do {
                    int movie_id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                    String name = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME));
                    String poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));

                    Log.d(TAG, "doInBackground: " + name + " and " + poster_path);

                    movies[cursor.getPosition()] = new Movie(movie_id, poster_path);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            movieAdapter.setMovieData(movies);
            movieAdapter.notifyDataSetChanged();
            showMoviesData();
        }
    }

    public void showLoadIndicator() {
        mProgressLoading.setVisibility(View.VISIBLE);
        rv_movies.setVisibility(View.INVISIBLE);
    }

    public void showMoviesData() {
        mProgressLoading.setVisibility(View.GONE);
        rv_movies.setVisibility(View.VISIBLE);
    }

    public URL buildUrl() {
        String BASE_URL;
        if (POP_TOPR_UPC_NOWP_MOVIES == 0)
            BASE_URL = POPULAR_MOVIES_URL;
        else if (POP_TOPR_UPC_NOWP_MOVIES == 1)
            BASE_URL = TOP_RATED_MOVIES_URL;
        else if (POP_TOPR_UPC_NOWP_MOVIES == 2)
            BASE_URL = UPCOMING;
        else {
            BASE_URL = NOW_PLAYING;
        }

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_PARAM, getString(R.string.api_key))
                .appendQueryParameter(LANG_PARAM, lang)
                .appendQueryParameter(PAGE_PARAM, String.valueOf(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public class MoviesFetchTask extends AsyncTask<URL, Void, Movie[]> {

        @Override
        protected Movie[] doInBackground(URL... objects) {
            if (objects.length == 0) {
                return null;
            }

            URL moviesRequestUrl = objects[0];
            Log.d(TAG, "doInBackground: moviesRequestUrl: " + moviesRequestUrl.toString());

            try {
                RequestQueue mRequestQueue = Volley.newRequestQueue(MovieActivity.this);
                mRequestQueue.start();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        moviesRequestUrl.toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    JSONArray results = response.getJSONArray("results");

                                    int id;
                                    String poster_path;

                                    Movie[] movies = new Movie[results.length()];

                                    for (int i = 0; i < results.length(); i++) {
                                        id = results.getJSONObject(i).getInt("id");
                                        poster_path = results.getJSONObject(i).getString("poster_path");

                                        movies[i] = new Movie(id, poster_path);
                                    }

                                    movieAdapter.setMovieData(movies);
                                    movieAdapter.notifyDataSetChanged();
                                    showMoviesData();

                                    rv_movies.scrollToPosition(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                            }
                        }
                );

                mRequestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                Log.d(TAG, "doInBackground: error: " + e.getMessage());
            }
            return null;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem popularMenuItem = menu.findItem(R.id.action_popular);
        MenuItem topRatedMenuItem = menu.findItem(R.id.action_top_rated);
        MenuItem upcomingMenuItem = menu.findItem(R.id.action_upcoming);
        MenuItem nowPlayingMenuItem = menu.findItem(R.id.action_now_playing);
        MenuItem favouritesMenutItem = menu.findItem(R.id.action_favourites);

        if (POP_TOPR_UPC_NOWP_MOVIES == 0) {
            popularMenuItem.setChecked(true);
        } else if (POP_TOPR_UPC_NOWP_MOVIES == 1) {
            topRatedMenuItem.setChecked(true);
        } else if (POP_TOPR_UPC_NOWP_MOVIES == 2) {
            upcomingMenuItem.setChecked(true);
        } else if (POP_TOPR_UPC_NOWP_MOVIES == 3) {
            nowPlayingMenuItem.setChecked(true);
        } else {
            favouritesMenutItem.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        int earlier_id = POP_TOPR_UPC_NOWP_MOVIES;

        if (id == R.id.action_popular) {
            item.setChecked(true);
            POP_TOPR_UPC_NOWP_MOVIES = 0;
        }

        if (id == R.id.action_top_rated) {
            item.setChecked(true);
            POP_TOPR_UPC_NOWP_MOVIES = 1;
        }

        if (id == R.id.action_upcoming) {
            item.setChecked(true);
            POP_TOPR_UPC_NOWP_MOVIES = 2;
        }
        if (id == R.id.action_now_playing) {
            item.setChecked(true);
            POP_TOPR_UPC_NOWP_MOVIES = 3;
        }

        if (id == R.id.action_favourites) {
            item.setChecked(true);
            POP_TOPR_UPC_NOWP_MOVIES = 4;
        }

        updateSharedPref();

        if (earlier_id != POP_TOPR_UPC_NOWP_MOVIES) {
            loadMoviesData();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateSharedPref() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("choice", POP_TOPR_UPC_NOWP_MOVIES);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable("position", mListState);

        movieArray = movieAdapter.getMovieData();
        int[] moviesId = new int[movieArray.length];
        String[] moviesPoster = new String[movieArray.length];

        for (int i = 0; i < movieArray.length; i++) {
            moviesId[i] = movieArray[i].getId();
            moviesPoster[i] = movieArray[i].getPoster_path();
        }

        outState.putIntArray("id", moviesId);
        outState.putStringArray("poster", moviesPoster);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state != null)
            mListState = state.getParcelable("position");
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadMoviesData();
        // make sure data has been reloaded into adapter first
        // ONLY call this part once the data items have been loaded back into the adapter
        // for example, inside a success callback from the network
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
    }
}
