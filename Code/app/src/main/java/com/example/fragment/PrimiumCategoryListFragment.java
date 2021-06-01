package com.example.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeMovieListAdapter;
import com.example.adapter.HomeShowListAdapter;
import com.example.adapter.HomeSportListAdapter;
import com.example.adapter.HomeTVListAdapter;
import com.example.adapter.SportCategoryListAdapter;
import com.example.item.ItemMovie;
import com.example.item.ItemShow;
import com.example.item.ItemSport;
import com.example.item.ItemSportCategory;
import com.example.item.ItemTV;
import com.example.util.API;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.util.RvOnClickListener;
import com.example.videostreamingapp.MovieDetailsActivity2;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.example.videostreamingapp.SportDetailsActivity;
import com.example.videostreamingapp.TVDetailsActivity;
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


public class PrimiumCategoryListFragment extends Fragment {

    private ArrayList<ItemSportCategory> mListItem;
    private RecyclerView recyclerView;
    private SportCategoryListAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtNoFound,title;
    private RelativeLayout lytRView;
    private SearchView searchView;

    String search;
    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    NestedScrollView nestedScrollView;
    TextView movieViewAll, showViewAll, sportViewAll;
    RecyclerView rvMovie, rvShow, rvSport, rvTV;
    ArrayList<ItemMovie> movieList;
    ArrayList<ItemShow> showList;
    ArrayList<ItemSport> sportList;
    ArrayList<ItemTV> tvList;

    HomeMovieListAdapter movieAdapter;
    HomeShowListAdapter showAdapter;
    HomeSportListAdapter sportAdapter;
    HomeTVListAdapter tvAdapter;

    LinearLayout lytMovie, lytShow, lytSport, lytTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_premium_new, container, false);

        searchView=rootView.findViewById(R.id.searchView);

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            // TODO Auto-generated method stub
            if (!hasFocus) {
                searchView.setQuery("", false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                search = arg0;
                System.out.println("PremiumFeagment ==> search query ==> "+search);
                if (NetworkUtils.isConnected(getContext())) {
                    getSearchAll(search);
                } else {
                    Toast.makeText(getContext(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub

                return false;
            }
        });

        LinearLayout mAdViewLayout = rootView.findViewById(R.id.adView);
        BannerAds.showBannerAds(getContext(), mAdViewLayout);

        movieList = new ArrayList<>();
        showList = new ArrayList<>();
        sportList = new ArrayList<>();
        tvList = new ArrayList<>();

        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);

        movieViewAll = rootView.findViewById(R.id.textLatestMovieViewAll);
        showViewAll = rootView.findViewById(R.id.textTVSeriesViewAll);
        sportViewAll = rootView.findViewById(R.id.textLatestChannelViewAll);

        lytMovie = rootView.findViewById(R.id.lytMovie);
        lytShow = rootView.findViewById(R.id.lytHomeTVSeries);
        lytSport = rootView.findViewById(R.id.lytHomeLatestChannel);
        lytTV = rootView.findViewById(R.id.lytSearchTV);


        rvMovie = rootView.findViewById(R.id.rv_latest_movie);
        rvShow = rootView.findViewById(R.id.rv_tv_series);
        rvSport = rootView.findViewById(R.id.rv_latest_channel);
        rvTV = rootView.findViewById(R.id.rv_tv);


        rvMovie.setHasFixedSize(true);
        rvMovie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvMovie.setFocusable(false);
        rvMovie.setNestedScrollingEnabled(false);

        rvShow.setHasFixedSize(true);
        rvShow.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvShow.setFocusable(false);
        rvShow.setNestedScrollingEnabled(false);

        rvSport.setHasFixedSize(true);
        rvSport.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSport.setFocusable(false);
        rvSport.setNestedScrollingEnabled(false);


        rvTV.setHasFixedSize(true);
        rvTV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTV.setFocusable(false);
        rvTV.setNestedScrollingEnabled(false);

        if (NetworkUtils.isConnected(getContext())) {
            getSearchAll(search);
        } else {
            Toast.makeText(getContext(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }



    private void getSearchAll(String search1) {
        System.out.println("PremiumFeagment ==> getSearchAll() ==> search query ==> "+search1);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("search_text", search1);
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.SEARCH_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                nestedScrollView.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                System.out.println("OnSuccess ==> result ==> "+result);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray showArray = liveTVJson.getJSONArray("shows");
                    for (int i = 0; i < showArray.length(); i++) {
                        JSONObject jsonObject = showArray.getJSONObject(i);

                        ItemShow itemShow = new ItemShow();
                        itemShow.setShowId(jsonObject.getString(Constant.SHOW_ID));
                        itemShow.setShowName(jsonObject.getString(Constant.SHOW_TITLE));
                        itemShow.setShowImage(jsonObject.getString(Constant.SHOW_POSTER));
                        showList.add(itemShow);
                    }

                    JSONArray movieArray = liveTVJson.getJSONArray("movies");
                    if (movieArray.length()>0){
                        movieList.clear();
                    }
                    for (int i = 0; i < movieArray.length(); i++) {
                        JSONObject jsonObject = movieArray.getJSONObject(i);

                        ItemMovie itemMovie = new ItemMovie();
                        itemMovie.setMovieId(jsonObject.getString(Constant.MOVIE_ID));
                        itemMovie.setMovieName(jsonObject.getString(Constant.MOVIE_TITLE));
                        itemMovie.setMovieImage(jsonObject.getString(Constant.MOVIE_POSTER));
                        itemMovie.setMovieDuration(jsonObject.getString(Constant.MOVIE_DURATION));
                        itemMovie.setPremium(jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                || jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                || jsonObject.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip")
                        );
                        itemMovie.setMovieAccess(jsonObject.getString(Constant.MOVIE_ACCESS));
                        movieList.add(itemMovie);

                    }

                    JSONArray sportArray = liveTVJson.getJSONArray("sports");
                    for (int i = 0; i < sportArray.length(); i++) {
                        JSONObject jsonObject = sportArray.getJSONObject(i);

                        ItemSport objItem = new ItemSport();
                        objItem.setSportId(jsonObject.getString(Constant.SPORT_ID));
                        objItem.setSportName(jsonObject.getString(Constant.SPORT_TITLE));
                        objItem.setSportImage(jsonObject.getString(Constant.SPORT_IMAGE));
                        objItem.setPremium(jsonObject.getString(Constant.SPORT_ACCESS).equals("Paid"));
                        sportList.add(objItem);
                    }


                    JSONArray tvArray = liveTVJson.getJSONArray("live_tv");
                    for (int i = 0; i < tvArray.length(); i++) {
                        JSONObject jsonObject = tvArray.getJSONObject(i);
                        ItemTV objItem = new ItemTV();
                        objItem.setTvId(jsonObject.getString(Constant.TV_ID));
                        objItem.setTvName(jsonObject.getString(Constant.TV_TITLE));
                        objItem.setTvImage(jsonObject.getString(Constant.TV_IMAGE));
                        objItem.setPremium(jsonObject.getString(Constant.TV_ACCESS).equals("Paid"));
                        tvList.add(objItem);
                    }

                    displayData();

                } catch (JSONException e) {
                    e.printStackTrace();
                    nestedScrollView.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {

        if (!movieList.isEmpty()) {
            movieAdapter = new HomeMovieListAdapter(getContext(), movieList, false);
            rvMovie.setAdapter(movieAdapter);

            movieAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String movieId = movieList.get(position).getMovieId();
                    Intent intent = new Intent(getContext(), MovieDetailsActivity2.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                }
            });

        } else {
            lytMovie.setVisibility(View.GONE);
        }

        if (!showList.isEmpty()) {
            showAdapter = new HomeShowListAdapter(getContext(), showList, false);
            rvShow.setAdapter(showAdapter);

            showAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String seriesId = showList.get(position).getShowId();
                    Intent intent = new Intent(getContext(), ShowDetailsActivity.class);
                    intent.putExtra("Id", seriesId);
                    startActivity(intent);
                }
            });

        } else {
            lytShow.setVisibility(View.GONE);
        }

        if (!sportList.isEmpty()) {
            sportAdapter = new HomeSportListAdapter(getContext(), sportList);
            rvSport.setAdapter(sportAdapter);

            sportAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = sportList.get(position).getSportId();
                    Intent intent = new Intent(getContext(), SportDetailsActivity.class);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });


        } else {
            lytSport.setVisibility(View.GONE);
        }


        if (!tvList.isEmpty()) {
            tvAdapter = new HomeTVListAdapter(getContext(), tvList);
            rvTV.setAdapter(tvAdapter);

            tvAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String tvId = tvList.get(position).getTvId();
                    Intent intent = new Intent(getContext(), TVDetailsActivity.class);
                    intent.putExtra("Id", tvId);
                    startActivity(intent);
                }
            });


        } else {
            lytTV.setVisibility(View.GONE);
        }
    }
}
