package com.example.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.adapter.HomeMovieLandscapListAdapter;
import com.example.adapter.HomeMovieListAdapter;
import com.example.adapter.HomeRecentListAdapter;
import com.example.adapter.HomeShowListAdapter;
import com.example.adapter.SliderListAdapter;
import com.example.item.ItemMovie;
import com.example.item.ItemRecent;
import com.example.item.ItemShow;
import com.example.item.ItemSlider;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.GradientTextView;
import com.example.util.NetworkUtils;
import com.example.util.OfflineDatabaseHelper;
import com.example.util.RvOnClickListener;
import com.example.util.multisnaprecyclerview.MultiSnapRecyclerView;
import com.example.videostreamingapp.MainActivity;
import com.example.videostreamingapp.MovieDetailsActivity2;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.SearchHorizontalActivity;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.example.videostreamingapp.SportDetailsActivity;
import com.example.videostreamingapp.TVDetailsActivity;
import com.example.videostreamingapp.UserProfileActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.viewpagerindicator.LinePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    final long DELAY_MS = 500;
    final long PERIOD_MS = 3000;
    int currentPage = 0;
    Timer timer;
    Button play;
    ImageView my_account;
    SearchView searchView;
    ItemSlider itemChannel;
    private ProgressBar mProgressBar;
    private LinearLayout lyt_not_found;
    private NestedScrollView nestedScrollView;
    private ViewPager viewPager;
    private TextView latestMovieViewAll, latestShowViewAll, popularMovieViewAll, popularShowViewAll, home3ViewAll, home4ViewAll, home5ViewAll, recentlyViewAll;
    private LinearLayout lytLatestMovie, lytLatestShow, lytPopularMovie, lytPopularShow, lytHome3, lytHome4, lytHome5, lytRecently;
    private RecyclerView rvLatestMovie, rvLatestShow, rvPopularMovie, rvPopularShow, rvHome3, rvHome4, rvHome5, rvRecently;
    private LinePageIndicator circleIndicator;
    private ArrayList<ItemMovie> latestMovieList, popularMovieList, latestShowList;
    private ArrayList<ItemShow> popularShowList;
    private HomeMovieListAdapter latestMovieAdapter, popularMovieAdapter, home3MovieAdapter, home4MovieAdapter, home5MovieAdapter, latestShowAdapter;
    private HomeMovieLandscapListAdapter homeMovieLandscapAdapter;
    private HomeShowListAdapter popularShowAdapter, home3ShowAdapter, home4ShowAdapter, home5ShowAdapter;
    private SliderListAdapter sliderAdapter;
    private HomeRecentListAdapter recentAdapter;
    private String home3Title, home4Title, home5Title, home3Id, home4Id, home5Id;
    private GradientTextView viewHome3Title, viewHome4Title, viewHome5Title;
    private boolean isHome3Movie = false, isHome4Movie = false, isHome5Movie = false;
    private ArrayList<ItemMovie> home3Movie, home4Movie, home5Movie;
    private ArrayList<ItemShow> home3Show, home4Show, home5Show;
    private ArrayList<ItemSlider> sliderList;
    private ArrayList<ItemRecent> recentList;
    private LinearLayout lytSlider;
    private MyApplication myApplication;
    private Handler mHandler;
    private Runnable mRunnable;
    private GradientTextView txtWebSeries, txtLatestMovie;
    private GradientTextView txtRecent, txtTrending, txtDrama;
    private LinearLayout layoutHome;
    private FrameLayout homeFrame;
    OfflineDatabaseHelper offlineDatabaseHelper;
    private long movieDuration;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        offlineDatabaseHelper = new OfflineDatabaseHelper(getActivity().getApplicationContext());

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        searchView = rootView.findViewById(R.id.search_home);
        my_account = rootView.findViewById(R.id.my_account);
        my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), UserProfileActivity.class));
            }
        });


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
                Intent intent = new Intent(getContext(), SearchHorizontalActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub

                return false;
            }
        });


        myApplication = MyApplication.getInstance();

        latestMovieList = new ArrayList<>();
        latestShowList = new ArrayList<>();
        popularMovieList = new ArrayList<>();
        popularShowList = new ArrayList<>();
        home3Movie = new ArrayList<>();
        home4Movie = new ArrayList<>();
        home5Movie = new ArrayList<>();
        home3Show = new ArrayList<>();
        home4Show = new ArrayList<>();
        home5Show = new ArrayList<>();
        sliderList = new ArrayList<>();
        recentList = new ArrayList<>();

        play = rootView.findViewById(R.id.play_button_home);
        mProgressBar = rootView.findViewById(R.id.progressBar1);
        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        nestedScrollView = rootView.findViewById(R.id.nestedScrollView);
        viewPager = rootView.findViewById(R.id.viewPager);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
//        play=rootView.findViewById(R.id.play_now);
        latestMovieViewAll = rootView.findViewById(R.id.textHomeLatestMovieViewAll);
        latestShowViewAll = rootView.findViewById(R.id.textHomeLatestShowViewAll);
        popularMovieViewAll = rootView.findViewById(R.id.textHomePopularMovieViewAll);
        popularShowViewAll = rootView.findViewById(R.id.textHomePopularShowViewAll);
        home3ViewAll = rootView.findViewById(R.id.textHome3ViewAll);
        home4ViewAll = rootView.findViewById(R.id.textHome4ViewAll);
        home5ViewAll = rootView.findViewById(R.id.textHome5ViewAll);
        viewHome3Title = rootView.findViewById(R.id.textHome3Name);
        viewHome4Title = rootView.findViewById(R.id.textHome4Name);
        viewHome5Title = rootView.findViewById(R.id.textHome5Name);
        recentlyViewAll = rootView.findViewById(R.id.textHomeRecentlyWatchedViewAll);

        lytLatestMovie = rootView.findViewById(R.id.lytLatestMovie);
        lytLatestShow = rootView.findViewById(R.id.lytLatestShow);
        lytPopularMovie = rootView.findViewById(R.id.lytPopularMovie);
        lytPopularShow = rootView.findViewById(R.id.lytPopularShow);
        lytHome3 = rootView.findViewById(R.id.lytHome3);
        lytHome4 = rootView.findViewById(R.id.lytHome4);
        lytHome5 = rootView.findViewById(R.id.lytHome5);
        lytSlider = rootView.findViewById(R.id.lytSlider);
        lytRecently = rootView.findViewById(R.id.lytRecentlyWatched);

        rvLatestMovie = rootView.findViewById(R.id.rv_latest_movie);
        rvLatestShow = rootView.findViewById(R.id.rv_latest_show);
        rvPopularMovie = rootView.findViewById(R.id.rv_popular_movie);
        rvPopularShow = rootView.findViewById(R.id.rv_popular_show);
        rvHome3 = rootView.findViewById(R.id.rv_home3);
        rvHome4 = rootView.findViewById(R.id.rv_home4);
        rvHome5 = rootView.findViewById(R.id.rv_home5);
        rvRecently = rootView.findViewById(R.id.rv_recently_watched);

        txtDrama = rootView.findViewById(R.id.txtDrama);
        txtWebSeries = rootView.findViewById(R.id.txtWebSeries);
        txtLatestMovie = rootView.findViewById(R.id.txtLatestMovie);
        txtRecent = rootView.findViewById(R.id.txtRecent);
        layoutHome = rootView.findViewById(R.id.layoutHome);
        homeFrame = rootView.findViewById(R.id.frame_layout_home);
        txtTrending = rootView.findViewById(R.id.txtTrending);

        recyclerViewProperty(rvLatestMovie);
        recyclerViewProperty(rvLatestShow);
        recyclerViewProperty(rvPopularMovie);
        recyclerViewProperty(rvPopularShow);
        recyclerViewProperty(rvHome3);
        recyclerViewProperty(rvHome4);
        recyclerViewProperty(rvHome5);
        recyclerViewProperty(rvRecently);

        if (NetworkUtils.isConnected(getActivity())) {
            getHome();
        } else {
            lyt_not_found.setVisibility(View.VISIBLE);
            homeFrame.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        hideButton();
        return rootView;
    }


    private void setAutoSwipable() {
        mHandler = new Handler();
        mRunnable = () -> {
            if (currentPage == sliderList.size()) {
                currentPage = 0;
            }
            viewPager.setCurrentItem(currentPage++, true);
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(mRunnable);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private void recyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }


    private void getHome() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        System.out.println("HomeFragment ==> userId ==> " + myApplication.getUserId());
        System.out.println("HomeFragment ==> data ==> " + API.toBase64(jsObj.toString()));
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.DYNAMIC_HOME_URL, params, new AsyncHttpResponseHandler() {
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
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject liveTVJson = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray sliderArray = liveTVJson.getJSONArray("slider");
                    for (int i = 0; i < sliderArray.length(); i++) {
                        JSONObject jsonObject = sliderArray.getJSONObject(i);
                        ItemSlider itemSlider = new ItemSlider();
                        itemSlider.setId(jsonObject.getString("slider_post_id"));
                        itemSlider.setSliderTitle(jsonObject.getString("slider_title"));
                        itemSlider.setSliderImage(jsonObject.getString("slider_image"));
                        itemSlider.setSliderType(jsonObject.getString("slider_type"));
                        sliderList.add(itemSlider);
                    }

                    JSONArray recentArray = liveTVJson.getJSONArray("recently_watched");
                    System.out.println("HomeFragment ==> Recent Array ==> " + recentArray);
                    for (int i = 0; i < recentArray.length(); i++) {
                        JSONObject jsonObject = recentArray.getJSONObject(i);
                        ItemRecent itemRecent = new ItemRecent();
                        itemRecent.setRecentId(jsonObject.getString("video_id"));
                        itemRecent.setRecentImage(jsonObject.getString("video_thumb_image"));
                        itemRecent.setRecentType(jsonObject.getString("video_type"));
                        recentList.add(itemRecent);
                    }

                    layoutHome.removeAllViews();

                    JSONArray dashboardDataArray = liveTVJson.optJSONArray("dashboard_data");

                    System.out.println("Dashboard Array ==> " + dashboardDataArray);
                    if (Objects.requireNonNull(dashboardDataArray).length() > 0) {
                        for (int i = 1; i < dashboardDataArray.length(); i++) {
                            JSONObject data = dashboardDataArray.optJSONObject(i);
                            System.out.println("data ==> " + data);
                            layoutHome.addView(addMainView(i, data));
                        }
                    }

                    txtTrending.setVisibility(View.VISIBLE);
                    latestMovieViewAll.setVisibility(View.VISIBLE);

                    JSONArray latestMovieArray = liveTVJson.optJSONArray("latest_movies");
                    if (latestMovieArray != null)
                        if (latestMovieArray.length() > 0)
                            for (int i = 0; i < latestMovieArray.length(); i++) {
                                JSONObject objJson = latestMovieArray.getJSONObject(i);
                                ItemMovie objItem = new ItemMovie();
                                objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                        || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                        || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip")
                                );
                                objItem.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                                latestMovieList.add(objItem);
                            }

                    JSONArray latestShowArray = liveTVJson.optJSONArray("latest_shows");
//                    txtLatestMovie.setText("");
                    System.out.println("HomeFragment ==> LatestMovieArray ==> " + latestShowArray);


                    txtLatestMovie.setVisibility(View.VISIBLE);
                    latestShowViewAll.setVisibility(View.VISIBLE);

                    if (latestShowArray != null)
                        if (latestShowArray.length() > 0)
                            for (int i = 0; i < latestShowArray.length(); i++) {
                                JSONObject objJson = latestShowArray.getJSONObject(i);
                                ItemMovie objItem = new ItemMovie();
                                objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                        || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                        || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip")
                                );
                                objItem.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                                latestShowList.add(objItem);
                            }

                    JSONArray popularMovieArray = liveTVJson.optJSONArray("popular_movies");
                    txtWebSeries.setVisibility(View.VISIBLE);
                    popularMovieViewAll.setVisibility(View.VISIBLE);
                    if (popularMovieArray != null)
                        if (popularMovieArray.length() > 0)
                            for (int i = 0; i < popularMovieArray.length(); i++) {
                                JSONObject objJson = popularMovieArray.getJSONObject(i);
                                ItemMovie objItem = new ItemMovie();
                                objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                        || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                        || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip"));
                                objItem.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                                popularMovieList.add(objItem);
                            }

                    JSONArray popularShowArray = liveTVJson.optJSONArray("popular_shows");
                    txtDrama.setVisibility(View.VISIBLE);
                    popularShowViewAll.setVisibility(View.VISIBLE);
                    if (popularShowArray != null)
                        if (popularShowArray.length() > 0)
                            for (int i = 0; i < popularShowArray.length(); i++) {
                                JSONObject objJson = popularShowArray.getJSONObject(i);
                                ItemShow objItem = new ItemShow();
                                objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                                objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                                objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                                popularShowList.add(objItem);
                            }

                    home3Title = liveTVJson.optString("home_sections3_title");
                    home3Id = liveTVJson.optString("home_sections3_lang_id");
                    isHome3Movie = liveTVJson.optString("home_sections3_type").equals("Movie");

                    JSONArray home3Array = liveTVJson.optJSONArray("home_sections3");
                    if (home3Array != null)
                        if (home3Array.length() > 0)
                            for (int i = 0; i < home3Array.length(); i++) {
                                JSONObject objJson = home3Array.getJSONObject(i);
                                if (isHome3Movie) {
                                    ItemMovie objItem = new ItemMovie();
                                    objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                    objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                    objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                    objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                    objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip"));
                                    objItem.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                                    home3Movie.add(objItem);
                                } else {
                                    ItemShow objItem = new ItemShow();
                                    objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                                    objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                                    objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                                    home3Show.add(objItem);
                                }
                            }

                    home4Title = liveTVJson.optString("home_sections4_title");
                    home4Id = liveTVJson.optString("home_sections4_lang_id");
                    isHome4Movie = liveTVJson.optString("home_sections4_type").equals("Movie");

                    JSONArray home4Array = liveTVJson.optJSONArray("home_sections4");
                    if (home4Array != null)
                        if (home4Array.length() > 0)
                            for (int i = 0; i < home4Array.length(); i++) {
                                JSONObject objJson = home4Array.getJSONObject(i);
                                if (isHome4Movie) {
                                    ItemMovie objItem = new ItemMovie();
                                    objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                    objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                    objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                    objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                    objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip"));
                                    objItem.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                                    home4Movie.add(objItem);
                                } else {
                                    ItemShow objItem = new ItemShow();
                                    objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                                    objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                                    objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                                    home4Show.add(objItem);
                                }
                            }


                    home5Title = liveTVJson.optString("home_sections5_title");
                    home5Id = liveTVJson.optString("home_sections5_lang_id");
                    isHome5Movie = liveTVJson.optString("home_sections5_type").equals("Movie");

                    JSONArray home5Array = liveTVJson.optJSONArray("home_sections5");
                    if (home5Array != null)
                        if (home5Array.length() > 0)
                            for (int i = 0; i < home5Array.length(); i++) {
                                JSONObject objJson = home5Array.getJSONObject(i);
                                if (isHome5Movie) {
                                    ItemMovie objItem = new ItemMovie();
                                    objItem.setMovieId(objJson.getString(Constant.MOVIE_ID));
                                    objItem.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                                    objItem.setMovieImage(objJson.getString(Constant.MOVIE_POSTER));
                                    objItem.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                                    objItem.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip"));
                                    objItem.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                                    home5Movie.add(objItem);
                                } else {
                                    ItemShow objItem = new ItemShow();
                                    objItem.setShowId(objJson.getString(Constant.SHOW_ID));
                                    objItem.setShowName(objJson.getString(Constant.SHOW_TITLE));
                                    objItem.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                                    home5Show.add(objItem);
                                }
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
        if (!sliderList.isEmpty()) {
            sliderAdapter = new SliderListAdapter(Objects.requireNonNull(getActivity()), sliderList);


            viewPager.setAdapter(sliderAdapter);
            viewPager.setOffscreenPageLimit(1);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(-10, 0, -10, 0);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    itemChannel = sliderList.get(position);
                }

                @Override
                public void onPageSelected(int position) {
//                    sliderAdapter.playVideo();
                    currentPage = position;
                    itemChannel = sliderList.get(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            circleIndicator.setMinimumHeight(2);
            circleIndicator.setViewPager(viewPager);
            play.setOnClickListener(v -> {
                Class<?> aClass;
                String recentId = itemChannel.getId();
                String recentType = itemChannel.getSliderType();
                switch (recentType) {
                    case "Movies":
                        aClass = MovieDetailsActivity2.class;
                        break;
                    case "Shows":
                        aClass = ShowDetailsActivity.class;
                        break;
                    case "LiveTV":
                        aClass = TVDetailsActivity.class;
                        break;
                    default:
                        aClass = SportDetailsActivity.class;
                        break;
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), aClass);
                intent.putExtra("Id", recentId);
                getContext().startActivity(intent);
            });
            setAutoSwipable();
        } else {
            lytSlider.setVisibility(View.GONE);
        }

        if (!recentList.isEmpty()) {
            recentAdapter = new HomeRecentListAdapter(getActivity(), recentList);
            rvRecently.setAdapter(recentAdapter);

            recentAdapter.setOnItemClickListener(position -> {
                ItemRecent itemRecent = recentList.get(position);
                Class<?> aClass;
                String recentId = itemRecent.getRecentId();
                getContinueDuration(recentId);
                String recentType = itemRecent.getRecentType();
                switch (recentType) {
                    case "Movies":
                        aClass = MovieDetailsActivity2.class;
                        break;
                    case "Shows":
                        aClass = ShowDetailsActivity.class;
                        break;
                    case "LiveTV":
                        aClass = TVDetailsActivity.class;
                        break;
                    default:
                        aClass = SportDetailsActivity.class;
                        break;
                }
                long recentDuration = offlineDatabaseHelper.getMovieByID1(recentId).getMovieDuration2();
                System.out.println("RecentDuration ==> " + recentDuration);
                Intent intent = new Intent(getActivity(), aClass);
                intent.putExtra("Id", recentId);
                intent.putExtra("recentDuration", recentDuration);
                startActivity(intent);
            });
            txtRecent.setVisibility(View.VISIBLE);
        } else {
            lytRecently.setVisibility(View.GONE);
        }

        if (!latestMovieList.isEmpty()) {
            latestMovieAdapter = new HomeMovieListAdapter(getActivity(), latestMovieList, false);
            rvLatestMovie.setAdapter(latestMovieAdapter);

            latestMovieAdapter.setOnItemClickListener(position ->
            {
                String movieId = latestMovieList.get(position).getMovieId();
                Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                intent.putExtra("Id", movieId);
                startActivity(intent);
            });

        } else {
            lytLatestMovie.setVisibility(View.GONE);
        }

        if (!latestShowList.isEmpty()) {
            latestShowAdapter = new HomeMovieListAdapter(getActivity(), latestShowList, false);
            rvLatestShow.setAdapter(latestShowAdapter);

            latestShowAdapter.setOnItemClickListener(position -> {
                String showId = latestShowList.get(position).getMovieId();
                Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                intent.putExtra("Id", showId);
                startActivity(intent);
            });
        } else {
            lytLatestShow.setVisibility(View.GONE);
        }

        if (!popularMovieList.isEmpty()) {
            popularMovieAdapter = new HomeMovieListAdapter(getActivity(), popularMovieList, false);
            rvPopularMovie.setAdapter(popularMovieAdapter);

            popularMovieAdapter.setOnItemClickListener(position -> {
                String movieId = popularMovieList.get(position).getMovieId();
                Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                intent.putExtra("Id", movieId);
                startActivity(intent);
            });

        } else {
            lytPopularMovie.setVisibility(View.GONE);
        }

        if (!popularShowList.isEmpty()) {
            popularShowAdapter = new HomeShowListAdapter(getActivity(), popularShowList, false);
            rvPopularShow.setAdapter(popularShowAdapter);

            popularShowAdapter.setOnItemClickListener(position -> {
                String showId = popularShowList.get(position).getShowId();
                Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                intent.putExtra("Id", showId);
                startActivity(intent);
            });

        } else {
            lytPopularShow.setVisibility(View.GONE);
        }

        viewHome3Title.setText("Best of Moviesy");
        viewHome3Title.setVisibility(View.VISIBLE);
        home3ViewAll.setVisibility(View.VISIBLE);
        if (isHome3Movie) {
            if (!home3Movie.isEmpty()) {
                home3MovieAdapter = new HomeMovieListAdapter(getActivity(), home3Movie, false);
                rvHome3.setAdapter(home3MovieAdapter);

                home3MovieAdapter.setOnItemClickListener(position -> {
                    String movieId = home3Movie.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                });

            } else {
                lytHome3.setVisibility(View.GONE);
            }
        } else {
            if (!home3Show.isEmpty()) {
                home3ShowAdapter = new HomeShowListAdapter(getActivity(), home3Show, false);
                rvHome3.setAdapter(home3ShowAdapter);

                home3ShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String showId = home3Show.get(position).getShowId();
                        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                        intent.putExtra("Id", showId);
                        startActivity(intent);
                    }
                });

            } else {
                lytHome3.setVisibility(View.GONE);
                home3ViewAll.setVisibility(View.GONE);
            }
        }

        viewHome4Title.setText("Popular Movies");
        viewHome4Title.setVisibility(View.VISIBLE);
        home4ViewAll.setVisibility(View.VISIBLE);
        if (isHome4Movie) {
            if (!home4Movie.isEmpty()) {
                home4MovieAdapter = new HomeMovieListAdapter(getActivity(), home4Movie, false);
                rvHome4.setAdapter(home4MovieAdapter);

                home4MovieAdapter.setOnItemClickListener(position -> {
                    String movieId = home4Movie.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                });
            } else {
                lytHome4.setVisibility(View.GONE);
                home4ViewAll.setVisibility(View.GONE);
            }
        } else {
            if (!home4Show.isEmpty()) {
                home4ShowAdapter = new HomeShowListAdapter(getActivity(), home4Show, false);
                rvHome4.setAdapter(home4ShowAdapter);

                home4ShowAdapter.setOnItemClickListener(position -> {
                    String showId = home4Show.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                });
            } else {
                lytHome4.setVisibility(View.GONE);
                home4ViewAll.setVisibility(View.GONE);
            }
        }


        viewHome5Title.setText(home5Title);
        viewHome5Title.setVisibility(View.VISIBLE);
        home5ViewAll.setVisibility(View.VISIBLE);
        if (isHome5Movie) {
            if (!home5Movie.isEmpty()) {
                home5MovieAdapter = new HomeMovieListAdapter(getActivity(), home5Movie, false);
                rvHome5.setAdapter(home5MovieAdapter);

                home5MovieAdapter.setOnItemClickListener(position -> {
                    String movieId = home5Movie.get(position).getMovieId();
                    Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                    intent.putExtra("Id", movieId);
                    startActivity(intent);
                });

            } else {
                lytHome5.setVisibility(View.GONE);
                home5ViewAll.setVisibility(View.GONE);
            }
        } else {
            if (!home5Show.isEmpty()) {
                home5ShowAdapter = new HomeShowListAdapter(getActivity(), home5Show, false);
                rvHome5.setAdapter(home5ShowAdapter);

                home5ShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String showId = home5Show.get(position).getShowId();
                        Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                        intent.putExtra("Id", showId);
                        startActivity(intent);
                    }
                });
            } else {
                lytHome5.setVisibility(View.GONE);
            }
        }

        latestMovieViewAll.setOnClickListener(view -> {
            String title = getString(R.string.home_latest_movie);
            HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
            Bundle bundleShow = new Bundle();
            bundleShow.putString("Id", "");
            bundleShow.putString("movieUrl", "latest_movies");
            homeMovieMoreFragment.setArguments(bundleShow);
            changeFragment(homeMovieMoreFragment, title);
        });

        popularMovieViewAll.setOnClickListener(view -> {
            String title = getString(R.string.home_popular_show);

            HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
            Bundle bundleShow = new Bundle();
            bundleShow.putString("Id", "");
            bundleShow.putString("movieUrl", "popular_movies");
            homeMovieMoreFragment.setArguments(bundleShow);
            changeFragment(homeMovieMoreFragment, title);
        });

        latestShowViewAll.setOnClickListener(view -> {
            String title = getString(R.string.home_latest_show);
            HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
            Bundle bundleShow = new Bundle();
            bundleShow.putString("Id", "");
            bundleShow.putString("movieUrl", "latest_shows");
            homeMovieMoreFragment.setArguments(bundleShow);
            changeFragment(homeMovieMoreFragment, title);
        });


        popularShowViewAll.setOnClickListener(view -> {
            String title = getString(R.string.home_popular_movie);
            HomeShowMoreListFragment homeShowMoreFragment = new HomeShowMoreListFragment();
            Bundle bundleShow = new Bundle();
            bundleShow.putString("Id", "");
            bundleShow.putString("showUrl", "popular_shows");
            homeShowMoreFragment.setArguments(bundleShow);
            changeFragment(homeShowMoreFragment, title);
        });

        home3ViewAll.setOnClickListener(view -> {
            String title = home3Title;
            Bundle bundle = new Bundle();
            bundle.putString("Id", home3Id);
            bundle.putString("movieUrl", "best_stream");
            if (isHome3Movie) {
                HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
                homeMovieMoreFragment.setArguments(bundle);
                changeFragment(homeMovieMoreFragment, title);
            } else {
                HomeShowMoreListFragment homeShowMoreFragment = new HomeShowMoreListFragment();
                homeShowMoreFragment.setArguments(bundle);
                changeFragment(homeShowMoreFragment, title);
            }
        });


        home4ViewAll.setOnClickListener(view -> {
            String title = home4Title;
            Bundle bundle = new Bundle();
            bundle.putString("Id", home4Id);
            bundle.putString("movieUrl", "homepopular_shows");
            if (isHome4Movie) {
                HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
                homeMovieMoreFragment.setArguments(bundle);
                changeFragment(homeMovieMoreFragment, title);
            } else {
                HomeShowMoreListFragment homeShowMoreFragment = new HomeShowMoreListFragment();
                homeShowMoreFragment.setArguments(bundle);
                changeFragment(homeShowMoreFragment, title);
            }
        });


        home5ViewAll.setOnClickListener(view -> {
            String title = home5Title;
            Bundle bundle = new Bundle();
            bundle.putString("Id", home5Id);
            bundle.putString("movieUrl", "popular_documentary");
            if (isHome5Movie) {
                HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
                homeMovieMoreFragment.setArguments(bundle);
                changeFragment(homeMovieMoreFragment, title);
            } else {
                HomeShowMoreListFragment homeShowMoreFragment = new HomeShowMoreListFragment();
                homeShowMoreFragment.setArguments(bundle);
                changeFragment(homeShowMoreFragment, title);
            }
        });


    }

    private void getContinueDuration(String recentId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("movie_id", recentId);
        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        System.out.println("ExoplayerFragment ==> Params ==> "+params);
        client.post(Constant.CONTINUE_WATCHING_GET_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                System.out.println("HomeFragment ==> CONTINUE_WATCHING_GET_API_URL ==> " + Constant.CONTINUE_WATCHING_URL);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson = jsonArray.getJSONObject(0);
                    String duration = objJson.getString("duration");
                    long mainDuration = Long.parseLong(duration);
                    setMovieDuration(recentId, mainDuration);
                    System.out.println("HomeFragment ==> MainJSON ==> " + mainJson);
                    System.out.println("HomeFragment ==> objJson1 ==> " + mainDuration);
                    System.out.println("HomeFragment ==> result ==> " + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("HomeFragment ==> OnFailure ==> " + responseBody);

            }
        });
    }

    public void setMovieDuration(String recentId, long duration) {
        if (offlineDatabaseHelper.checkIfMyMovieExists(recentId)) {
            offlineDatabaseHelper.updateMovieDuration(recentId, duration);
        } else {
            offlineDatabaseHelper.addDuration(recentId, duration);
        }
    }

    public long getMovieDuration() {
        return movieDuration;
    }

    private void changeFragment(Fragment fragment, String Name) {
        FragmentManager fm = getFragmentManager();
        assert fm != null;
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(HomeFragment.this);
        ft.add(R.id.Container, fragment, Name);
        ft.addToBackStack(Name);
        ft.commit();
        ((MainActivity) requireActivity()).setToolbarTitle(Name);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public void hideButton() {
        if (getActivity() instanceof MainActivity) {
            Objects.requireNonNull(((MainActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
    }

    private View addMainView(final int pos, JSONObject jsonMovie) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.row_dynamic_home, null);
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtViewAll = view.findViewById(R.id.textHomeLatestMovieViewAll);
        txtTitle.setText(jsonMovie.optString("title_name"));

        txtViewAll.setPaintFlags(txtViewAll.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        MultiSnapRecyclerView rvLatestMovie = view.findViewById(R.id.rv_latest_movie);
        LinearLayoutManager lst = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvLatestMovie.setLayoutManager(lst);
        JSONArray jsonArrayMovie = jsonMovie.optJSONArray("movie_array");

        txtViewAll.setOnClickListener(view1 -> {
            String title = jsonMovie.optString("title_name");
            Bundle bundle = new Bundle();
            bundle.putString("Id", "");
            bundle.putString("category_id", jsonMovie.optString("category_id"));
            bundle.putString("movieUrl", "best_stream");
            bundle.putString("title",title);
            JSONObject jsonData = jsonArrayMovie.optJSONObject(0);
            if (jsonData.optString("movie_type").equalsIgnoreCase("Movie")) {
                HomeMovieMoreListFragment homeMovieMoreFragment = new HomeMovieMoreListFragment();
                homeMovieMoreFragment.setArguments(bundle);
                changeFragment(homeMovieMoreFragment, title);
            } else {
                HomeShowMoreListFragment homeShowMoreFragment = new HomeShowMoreListFragment();
                homeShowMoreFragment.setArguments(bundle);
                changeFragment(homeShowMoreFragment, title);
            }
        });


        assert jsonArrayMovie != null;
        if (Objects.requireNonNull(jsonArrayMovie).length() > 0) {
            JSONObject jsonData = jsonArrayMovie.optJSONObject(0);

            if (jsonData.optString("movie_type").equalsIgnoreCase("Movie")) {
                ArrayList<ItemMovie> movieList = new ArrayList<>();
                movieList.clear();
                for (int i = 0; i < jsonArrayMovie.length(); i++) {
                    JSONObject jsonMovieData = jsonArrayMovie.optJSONObject(i);
                    ItemMovie objItem = new ItemMovie();
                    objItem.setMovieId(jsonMovieData.optString(Constant.MOVIE_ID));
                    objItem.setMovieName(jsonMovieData.optString(Constant.MOVIE_TITLE));
                    objItem.setMovieImage(jsonMovieData.optString(Constant.MOVIE_POSTER));
                    objItem.setMovieDuration(jsonMovieData.optString(Constant.MOVIE_DURATION));
                    objItem.setPremium(jsonMovieData.optString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                            || jsonMovieData.optString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                            || jsonMovieData.optString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip")
                    );
                    objItem.setMovieAccess(jsonMovieData.optString(Constant.MOVIE_ACCESS));
                    movieList.add(objItem);
                }
                if (jsonMovie.optString("is_landscape").equalsIgnoreCase("0")) {
                    latestMovieAdapter = new HomeMovieListAdapter(getActivity(), movieList, false);
                    rvLatestMovie.setAdapter(latestMovieAdapter);
                    latestMovieAdapter.setOnItemClickListener(position ->
                    {
                        String movieId = movieList.get(position).getMovieId();
                        Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                        intent.putExtra("Id", movieId);
                        startActivity(intent);
                    });
                } else {
                    homeMovieLandscapAdapter = new HomeMovieLandscapListAdapter(getActivity(), movieList, false);
                    rvLatestMovie.setAdapter(homeMovieLandscapAdapter);
                    homeMovieLandscapAdapter.setOnItemClickListener(position ->
                    {
                        String movieId = movieList.get(position).getMovieId();
                        Intent intent = new Intent(getActivity(), MovieDetailsActivity2.class);
                        intent.putExtra("Id", movieId);
                        startActivity(intent);
                    });
                }
            } else {
                ArrayList<ItemShow> showList = new ArrayList<>();
                showList.clear();
                for (int i = 0; i < jsonArrayMovie.length(); i++) {
                    JSONObject jsonShowData = jsonArrayMovie.optJSONObject(i);
                    ItemShow objItem = new ItemShow();
                    objItem.setShowId(jsonShowData.optString(Constant.SHOW_ID));
                    objItem.setShowName(jsonShowData.optString((Constant.SHOW_TITLE)));
                    objItem.setShowImage(jsonShowData.optString((Constant.MOVIE_POSTER)));
                    showList.add(objItem);
                }
                popularShowAdapter = new HomeShowListAdapter(getActivity(), showList, false);
                rvLatestMovie.setAdapter(popularShowAdapter);

                popularShowAdapter.setOnItemClickListener(position -> {
                    String showId = showList.get(position).getShowId();
                    Intent intent = new Intent(getActivity(), ShowDetailsActivity.class);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                });
            }
        }
        return view;
    }
}
