package com.example.videostreamingapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.EpisodeListAdapter;
import com.example.adapter.HomeShowListAdapter;
import com.example.adapter.SeasonListAdapter;
import com.example.fragment.EmbeddedImagesFragment;
import com.example.fragment.ExoPlayerFragment;
import com.example.fragment.PremiumContentListFragment;
import com.example.item.ItemEpisode;
import com.example.item.ItemMovie;
import com.example.item.ItemSeason;
import com.example.item.ItemShow;
import com.example.util.API;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.Remember;
import com.example.util.RvOnClickListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ShowDetailsActivity extends AppCompatActivity {

    ProgressBar mProgressBar, mProgressBarEpisode;
    LinearLayout lyt_not_found;
    RelativeLayout lytParent;
    WebView webView;
    TextView textTitle, textDate,txtExpire,txtExpireDuration, textLanguage, textGenre, textRelViewAll, textDuration, textNoEpisode, textDurationLbl, textRate;
    LinearLayout lytRate;

    RecyclerView rvRelated, rvEpisode;
    ItemShow itemShow;
    ArrayList<ItemShow> mListItemRelated;
    ArrayList<ItemSeason> mListSeason;
    ArrayList<ItemEpisode> mListItemEpisode;
    HomeShowListAdapter homeShowAdapter;
    String Id;
    ItemMovie itemMovie;
    String rental_plan = "";
    StringBuilder strGenre = new StringBuilder();
    LinearLayout lytRelated, lytSeason;
    MyApplication myApplication;
    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    AppCompatSpinner spSeason;
    SeasonListAdapter seasonAdapter;
    EpisodeListAdapter episodeAdapter;
    private FragmentManager fragmentManager;
    private int playerHeight;
    FrameLayout frameLayout;
    boolean isFullScreen = false;
    boolean isFromNotification = false;
    LinearLayout mAdViewLayout;
    boolean isPurchased = false;
    private ImageView imgBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        IsRTL.ifSupported(this);
        GlobalBus.getBus().register(this);
        mAdViewLayout = findViewById(R.id.adView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        myApplication = MyApplication.getInstance();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> onBackPressed());

        Remember.putString(Constant.SHOW_ID, Id);
        Remember.putString(Constant.MOVIE_ID, "");

        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        frameLayout = findViewById(R.id.playerSection);
        int columnWidth = NetworkUtils.getScreenWidth(this);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        playerHeight = frameLayout.getLayoutParams().height;

        BannerAds.showBannerAds(this, mAdViewLayout);

        mListItemRelated = new ArrayList<>();
        mListSeason = new ArrayList<>();
        mListItemEpisode = new ArrayList<>();
        itemShow = new ItemShow();
        lytRelated = findViewById(R.id.lytRelated);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytParent = findViewById(R.id.lytParent);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        webView = findViewById(R.id.webView);
        textRelViewAll = findViewById(R.id.textRelViewAll);
        textTitle = findViewById(R.id.textTitle);
        textDate = findViewById(R.id.textDate);
        textLanguage = findViewById(R.id.txtLanguage);
        textGenre = findViewById(R.id.txtGenre);
        textDuration = findViewById(R.id.txtDuration);
        textDurationLbl = findViewById(R.id.txtDurationLbl);
        rvRelated = findViewById(R.id.rv_related);
        rvEpisode = findViewById(R.id.rv_episode);
        spSeason = findViewById(R.id.spSeason);
        lytSeason = findViewById(R.id.lytSeason);
        mProgressBarEpisode = findViewById(R.id.progressBar);
        textNoEpisode = findViewById(R.id.textNoEpisode);
        textRate = findViewById(R.id.txtIMDbRating);
        lytRate = findViewById(R.id.lytIMDB);

        rvRelated.setHasFixedSize(true);
        rvRelated.setLayoutManager(new LinearLayoutManager(ShowDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);

        rvEpisode.setHasFixedSize(true);
        rvEpisode.setLayoutManager(new LinearLayoutManager(ShowDetailsActivity.this, LinearLayoutManager.VERTICAL, false));
        rvEpisode.setFocusable(false);
        rvEpisode.setNestedScrollingEnabled(false);

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        if (NetworkUtils.isConnected(ShowDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("show_id", Id);

        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.SHOW_DETAILS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                lytParent.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject objJson = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    if (objJson.length() > 0) {
                        if (objJson.has(Constant.STATUS)) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            itemShow.setShowId(objJson.getString(Constant.SHOW_ID));
                            itemShow.setShowName(objJson.getString(Constant.SHOW_NAME));
                            itemShow.setShowDescription(objJson.getString(Constant.SHOW_DESC));
                            itemShow.setShowImage(objJson.getString(Constant.SHOW_POSTER));
                            itemShow.setShowLanguage(objJson.getString(Constant.SHOW_LANGUAGE));
                            itemShow.setShowRating(objJson.getString(Constant.IMDB_RATING));

                            JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_SHOW_ARRAY_NAME);
                            if (jsonArrayChild.length() != 0) {
                                for (int j = 0; j < jsonArrayChild.length(); j++) {
                                    JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                    ItemShow item = new ItemShow();
                                    item.setShowId(objChild.getString(Constant.SHOW_ID));
                                    item.setShowName(objChild.getString(Constant.SHOW_TITLE));
                                    item.setShowImage(objChild.getString(Constant.SHOW_POSTER));
                                    mListItemRelated.add(item);
                                }
                            }


                            JSONArray jsonArrayGenre = objJson.getJSONArray(Constant.GENRE_LIST);
                            if (jsonArrayGenre.length() != 0) {
                                String prefix = "";
                                for (int k = 0; k < jsonArrayGenre.length(); k++) {
                                    JSONObject objChild = jsonArrayGenre.getJSONObject(k);
                                    strGenre.append(prefix);
                                    prefix = " | ";
                                    strGenre.append(objChild.getString(Constant.GENRE_NAME));
                                }
                            } else {
                                textGenre.setVisibility(View.GONE);
                            }

                            JSONArray jsonArraySeason = objJson.getJSONArray(Constant.SEASON_ARRAY_NAME);
                            if (jsonArraySeason.length() != 0) {
                                for (int j = 0; j < jsonArraySeason.length(); j++) {
                                    JSONObject objSeason = jsonArraySeason.getJSONObject(j);
                                    ItemSeason item = new ItemSeason();
                                    item.setSeasonId(objSeason.getString(Constant.SEASON_ID));
                                    item.setSeasonName(objSeason.getString(Constant.SEASON_NAME));
                                    if (objSeason.getString(Constant.SEASON_NAME).equalsIgnoreCase(itemShow.getShowName()))
                                        mListSeason.add(item);
                                }
                            }

                        }
                        displayData();

                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytParent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                lytParent.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayData() {
        setTitle("");
        textTitle.setText(itemShow.getShowName());
        textLanguage.setText(itemShow.getShowLanguage());
        textGenre.setText(strGenre.toString());

        if (itemShow.getShowRating().isEmpty() || itemShow.getShowRating().equals("0")) {
            lytRate.setVisibility(View.GONE);
        } else {
            textRate.setText(itemShow.getShowRating());
        }

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = itemShow.getShowDescription();

        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";

        String text = "<html dir=" + direction + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.otf\")}body{font-family: MyFont;color: #9c9c9c;font-size:14px;margin-left:0px;line-height:1.3}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        if (!mListItemRelated.isEmpty()) {
            homeShowAdapter = new HomeShowListAdapter(ShowDetailsActivity.this, mListItemRelated, false);
            rvRelated.setAdapter(homeShowAdapter);

            homeShowAdapter.setOnItemClickListener(new RvOnClickListener() {
                @Override
                public void onItemClick(int position) {
                    String showId = mListItemRelated.get(position).getShowId();
                    Intent intent = new Intent(ShowDetailsActivity.this, ShowDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Id", showId);
                    startActivity(intent);
                }
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }

        if (!mListSeason.isEmpty()) {
            seasonAdapter = new SeasonListAdapter(ShowDetailsActivity.this,
                    android.R.layout.simple_list_item_1,
                    mListSeason);
            seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spSeason.setAdapter(seasonAdapter);

            spSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.e("seasonId", mListSeason.get(i).getSeasonName());
                    mListItemEpisode.clear();
                    changeSeason(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            lytSeason.setVisibility(View.GONE);
            setImageIfSeasonAndEpisodeNone(itemShow.getShowImage());
            textDate.setVisibility(View.GONE);
            textDuration.setVisibility(View.GONE);
            textDurationLbl.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showToast(String msg) {
        Toast.makeText(ShowDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void changeSeason(int seasonId) {
        ItemSeason itemSeason = mListSeason.get(seasonId);
        if (NetworkUtils.isConnected(ShowDetailsActivity.this)) {
            getEpisode(Id);
        } else {
            showToast(getString(R.string.conne_msg1));
        }
    }

    private void getEpisode(String seasonId) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("series_id", seasonId);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.EPISODE_ONE_LIST_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBarEpisode.setVisibility(View.VISIBLE);
                rvEpisode.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBarEpisode.setVisibility(View.GONE);
                rvEpisode.setVisibility(View.VISIBLE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);

                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    if (jsonArray.length() > 0) {
                        ArrayList<ItemEpisode.Resolution> listResolution = new ArrayList<>();
                        textNoEpisode.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject objJson = jsonArray.getJSONObject(i);
                            ItemEpisode itemEpisode = new ItemEpisode();
                            itemEpisode.setEpisodeId(objJson.getString(Constant.EPISODE_ID));
                            itemEpisode.setEpisodeName(objJson.getString(Constant.EPISODE_TITLE));
                            itemEpisode.setEpisodeImage(objJson.getString(Constant.EPISODE_IMAGE));
                            itemEpisode.setEpisodeUrl(objJson.getString(Constant.EPISODE_URL));
                            itemEpisode.setEpisodeType(objJson.getString(Constant.EPISODE_TYPE));
                            itemEpisode.setEpisodeDate(objJson.getString(Constant.EPISODE_DATE));
                            itemEpisode.setEpisodeDuration(objJson.getString(Constant.EPISODE_DURATION));
                            itemEpisode.setPremium(objJson.getString(Constant.EPISODE_ACCESS).equals("Premium"));
                            itemEpisode.setPremium(objJson.getString(Constant.EPISODE_ACCESS).equals("rental"));
                            itemEpisode.setPremium(objJson.getString(Constant.EPISODE_ACCESS));
                            itemEpisode.setDownload(objJson.getBoolean(Constant.DOWNLOAD_ENABLE));
                            itemEpisode.setDownloadUrl(objJson.getString(Constant.DOWNLOAD_URL));
                            itemEpisode.setCheck_plan(objJson.optBoolean(Constant.USER_PLAN_STATUS));
                            itemEpisode.setRental_plan(objJson.optString(Constant.RENTAL_PLAN_STATUS));
                            itemEpisode.setDescription(objJson.optString(Constant.EPISODE_DESCRIPTION));
                            listResolution.clear();
                            if (objJson.has("resolutions")) {
                                JSONArray arrayVideos = objJson.getJSONArray("resolutions");
                                for (int j = 0; j < arrayVideos.length(); j++) {
                                    JSONObject objVideo = arrayVideos.getJSONObject(j);
                                    ItemEpisode.Resolution videos = new ItemEpisode.Resolution();
                                    videos.setMovie_id(objVideo.getString("series_id"));
                                    videos.setMovie_resolution(objVideo.getString("movie_resolution"));
                                    videos.setMovie_url(objVideo.getString("movie_url"));
                                    listResolution.add(videos);
                                }
                                itemEpisode.setLstResolution(listResolution);
                            }
                            mListItemEpisode.add(itemEpisode);
                        }
                        displayEpisode();

                    } else {
                        mProgressBarEpisode.setVisibility(View.GONE);
                        rvEpisode.setVisibility(View.GONE);
                        textNoEpisode.setVisibility(View.VISIBLE);
                        setImageIfSeasonAndEpisodeNone(itemShow.getShowImage());
                        textDate.setVisibility(View.GONE);
                        textDuration.setVisibility(View.GONE);
                        textDurationLbl.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBarEpisode.setVisibility(View.GONE);
                rvEpisode.setVisibility(View.GONE);
                textNoEpisode.setVisibility(View.VISIBLE);
                textDate.setVisibility(View.GONE);
                textDuration.setVisibility(View.GONE);
                textDurationLbl.setVisibility(View.GONE);
            }
        });
    }

    private void displayEpisode() {
        episodeAdapter = new EpisodeListAdapter(ShowDetailsActivity.this, mListItemEpisode, isPurchased);
        rvEpisode.setAdapter(episodeAdapter);
        //  play 1st episode by default
        if (!mListItemEpisode.isEmpty()) {
            playEpisode(0);
            episodeAdapter.select(0);
        }

        episodeAdapter.setOnItemClickListener(position -> {
            episodeAdapter.select(position);
            playEpisode(position);
        });
    }

    private void playEpisode(int playPosition) {
        ItemEpisode itemEpisode = mListItemEpisode.get(playPosition);
        textDate.setText(itemEpisode.getEpisodeDate());
        textDuration.setText(itemEpisode.getEpisodeDuration());
        textTitle.setText(mListItemEpisode.get(playPosition).getEpisodeName());

        if (!itemEpisode.getPremium().equalsIgnoreCase("Free")) {
            if (!itemEpisode.getPremium().equalsIgnoreCase("Rental")) {
                if (mListItemEpisode.get(playPosition).isCheck_plan()) {
                    setPlayer(playPosition);
                } else {
                    PremiumContentListFragment premiumContentFragment = PremiumContentListFragment.newInstance(Id, "Shows", itemEpisode.getPremium());
                    fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
                }
            } else {
                if (mListItemEpisode.get(playPosition).getRental_plan().equalsIgnoreCase("Active")) {
                    setPlayer(playPosition);
                } else {
                    PremiumContentListFragment premiumContentFragment = PremiumContentListFragment.newInstance(Id, "Shows", itemEpisode.getPremium());
                    fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
                }
            }
        } else {
            setPlayer(playPosition);
        }

        if (NetworkUtils.isConnected(ShowDetailsActivity.this)) {
            episodeRecentlyWatched(itemEpisode.getEpisodeId());
        }
    }

    private void setPlayer(int playPosition) {
        ItemEpisode itemEpisode = mListItemEpisode.get(playPosition);
        if (itemEpisode.getEpisodeUrl().isEmpty()) {
            showToast(getString(R.string.stream_not_found));
            EmbeddedImagesFragment embeddedImageFragment = EmbeddedImagesFragment.newInstance(itemEpisode.getEpisodeUrl(), itemEpisode.getEpisodeImage(), false);
            fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
        } else {
            switch (itemEpisode.getEpisodeType()) { //URL Embed
                case "Local":
                case "URL":
                    ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(itemEpisode.getEpisodeUrl(), false);
                    exoPlayerFragment.setShowMovie(mListItemEpisode.get(playPosition));
                    fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                    Remember.putString(Constant.MOVIE_FROM, "show");
                    break;
                case "Embed":
                    EmbeddedImagesFragment embeddedImageFragment = EmbeddedImagesFragment.newInstance(itemEpisode.getEpisodeUrl(), itemEpisode.getEpisodeImage(), true);
                    fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
                    break;

            }
        }
    }

    private void episodeRecentlyWatched(String episodeId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("episode_id", episodeId);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.EPISODE_RECENTLY_URL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    private void gotoPortraitScreen() {
        nestedScrollView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        mAdViewLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        mAdViewLayout.setVisibility(View.GONE);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            Events.FullScreen fullScreen = new Events.FullScreen();
            fullScreen.setFullScreen(false);
            GlobalBus.getBus().post(fullScreen);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            FragmentManager fm = getSupportFragmentManager();
            ExoPlayerFragment fragment = (ExoPlayerFragment) fm.findFragmentById(R.id.playerSection);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            assert fragment != null;
            fragment.fullScreen();
        } else {
            if (isFromNotification) {
                Intent intent = new Intent(ShowDetailsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setImageIfSeasonAndEpisodeNone(String imageCover) {
        EmbeddedImagesFragment embeddedImageFragment = EmbeddedImagesFragment.newInstance("", imageCover, false);
        fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
    }

    public void hideShowBackButton(boolean hideShow) {
        if (hideShow)
            imgBack.setVisibility(View.VISIBLE);
        else
            imgBack.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
