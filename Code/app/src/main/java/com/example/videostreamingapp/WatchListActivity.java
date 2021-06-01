package com.example.videostreamingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.WatchMovieListAdapter2;
import com.example.item.ItemMovie;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WatchListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvRecently;
    private MyApplication myApplication;
    private ArrayList<ItemMovie> recentList;
    private WatchMovieListAdapter2 latestMovieAdapter;
    LinearLayout lyt_not_found;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);
        IsRTL.ifSupported(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Watchlist");
        }
        myApplication = MyApplication.getInstance();
        rvRecently = findViewById(R.id.rv_recently_watched);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        recyclerViewProperty(rvRecently);
        recentList = new ArrayList<>();

        if (NetworkUtils.isConnected(WatchListActivity.this)) {
            getToWatchList();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    public void showToast(String msg) {
        Toast.makeText(WatchListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void recyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void getToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());

        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        client.post(Constant.GET_WATCHLIST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                recentList.clear();
                try {
                    JSONObject mainJson = new JSONObject(result);
                    if (mainJson.optString("status_code").equalsIgnoreCase("200")) {
                        JSONArray video_Array = mainJson.getJSONArray("VIDEO_STREAMING_APP");
                        JSONArray recentArray = video_Array.optJSONObject(0).optJSONArray("data");
                        for (int i = 0; i < recentArray.length(); i++) {
                            JSONObject jsonObject = recentArray.getJSONObject(i);
                            ItemMovie itemRecent = new ItemMovie();
                            itemRecent.setMovieId(jsonObject.getString("movie_videos_id"));
                            itemRecent.setMovieName(jsonObject.getString("movie_title"));
                            itemRecent.setMovieImage(jsonObject.getString("movie_poster"));
                            itemRecent.setMovieDuration(jsonObject.getString("duration"));
                            itemRecent.setMovieLanguage(jsonObject.getString("movie_language"));
                            itemRecent.setPremium(jsonObject.getString("movie_access").equalsIgnoreCase("Premium")
                                    || jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                    || jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip"));
                            itemRecent.setMovieAccess(jsonObject.getString(Constant.MOVIE_ACCESS));
                            recentList.add(itemRecent);
                            latestMovieAdapter = new WatchMovieListAdapter2(WatchListActivity.this, recentList, false);
                            rvRecently.setAdapter(latestMovieAdapter);
                            latestMovieAdapter.notifyItemRemoved(i);
                            latestMovieAdapter.setOnItemClickListener(position ->
                            {
                                String movieId = recentList.get(position).getMovieId();
                                Intent intent = new Intent(WatchListActivity.this, MovieDetailsActivity2.class);
                                intent.putExtra("Id", movieId);
                                startActivity(intent);
                            });
                        }
                    } else {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Transaction", error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.isConnected(WatchListActivity.this)) {
            getToWatchList();
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }
}
