package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.WatchMovieListAdapter;
import com.example.item.ItemMovie;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.videostreamingapp.MovieDetailsActivity2;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
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

public class WatchListContentFragment extends Fragment {
    private RecyclerView rvRecently;
    private MyApplication myApplication;
    private ArrayList<ItemMovie> recentList;
    private WatchMovieListAdapter latestMovieAdapter;
    TextView watchlist_text;
    LinearLayout lyt_not_found;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_watchlist, container, false);
        IsRTL.ifSupported(getActivity());

        watchlist_text=rootView.findViewById(R.id.watchlist_text);
        watchlist_text.setText("WatchList");
        myApplication = MyApplication.getInstance();
        rvRecently = rootView.findViewById(R.id.rv_recently_watched);
        lyt_not_found=rootView.findViewById(R.id.lyt_not_found);
        recyclerViewProperty(rvRecently);
        recentList = new ArrayList<>();

        if (NetworkUtils.isConnected(getActivity())) {
            getToWatchList();
        } else {
            showToast(getString(R.string.conne_msg1));
        }
        return rootView;
    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void recyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void getToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        System.out.println("GetWatchlist Called...");
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
                    System.out.println("WatchListActivity ==> Movie_RESPONSE ==> "+mainJson);
                    if (mainJson.optString("status_code").equalsIgnoreCase("200")) {
                        JSONArray video_Array = mainJson.getJSONArray("VIDEO_STREAMING_APP");
                        JSONArray recentArray = video_Array.optJSONObject(0).optJSONArray("data");
                        for (int i = 0; i < recentArray.length(); i++) {
                            JSONObject jsonObject = recentArray.getJSONObject(i);
                            ItemMovie itemRecent = new ItemMovie();
                            System.out.println("Movie_ID ==> "+jsonObject.getString("movie_videos_id"));
                            itemRecent.setMovieId(jsonObject.getString("movie_videos_id"));
                            itemRecent.setMovieName(jsonObject.getString("movie_title"));
                            itemRecent.setMovieImage(jsonObject.getString("movie_poster"));
                            itemRecent.setMovieDuration(jsonObject.getString("duration"));
//                            itemRecent.setMovieLanguage(jsonObject.getString("movie_language"));
                            itemRecent.setPremium(jsonObject.getString("movie_access").equalsIgnoreCase("Premium")
                                    || jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                    || jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip"));
                            itemRecent.setMovieAccess(jsonObject.getString(Constant.MOVIE_ACCESS));
                            /*itemRecent.setRecentType(jsonObject.getString("video_type"));*/
                            recentList.add(itemRecent);
                            latestMovieAdapter = new WatchMovieListAdapter(getActivity(), recentList, false);
                            rvRecently.setAdapter(latestMovieAdapter);
                            latestMovieAdapter.notifyItemRemoved(i);
                            latestMovieAdapter.setOnItemClickListener(position ->
                            {
                                String movieId = recentList.get(position).getMovieId();
                                Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                                intent.putExtra("Id", movieId);
                                startActivity(intent);
                            });
                        }
                        lyt_not_found.setVisibility(View.GONE);
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
    public void onResume() {
        super.onResume();
        if (NetworkUtils.isConnected(getActivity())) {
            getToWatchList();
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }
}
