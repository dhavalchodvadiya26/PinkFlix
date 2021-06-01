package com.example.videostreamingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeMovieListAdapter;
import com.example.adapter.MovieCastAdapter;
import com.example.fragment.EmbeddedImagesFragment;
import com.example.fragment.ExoPlayerFragment;
import com.example.fragment.ExoTVPlayerFragment;
import com.example.fragment.PremiumContentListFragment;
import com.example.item.CastModel;
import com.example.item.ItemMovie;
import com.example.util.API;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.DialogUtils;
import com.example.util.DownloadTracker;
import com.example.util.Events;
import com.example.util.ExoDownloadState;
import com.example.util.GlobalBus;
import com.example.util.GradientTextView;
import com.example.util.IsRTL;
import com.example.util.NetworkUtils;
import com.example.util.PrettyDialog;
import com.example.util.ReadMoreTextView;
import com.example.util.Remember;
import com.example.util.TrackKey;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener, DialogUtils.AlertDialogListener {

    ProgressBar mProgressBar;
    LinearLayout lyt_not_found;
    RelativeLayout lytParent;
    private DownloadTracker downloadTracker;
    private DownloadManager downloadManager;
    List<String> optionsToDownload = new ArrayList<String>();
    TextView txtDescription;
    GradientTextView textTitle;
    TextView textDate, textLanguage, textGenre, textRelViewAll, textRate;
    private LinearLayout lytRate, layoutTrailer;
    RecyclerView rvRelated;
    ItemMovie itemMovie;
    private DataSource.Factory dataSourceFactory;
    List<TrackKey> trackKeys = new ArrayList<>();
    ProgressDialog pDialog;
    private DownloadHelper myDownloadHelper;
    ArrayList<ItemMovie> mListItemRelated;
    HomeMovieListAdapter homeMovieAdapter;
    String Id;
    StringBuilder strGenre = new StringBuilder();
    LinearLayout lytRelated;
    MyApplication myApplication;
    NestedScrollView nestedScrollView;
    Toolbar toolbar;
    FrameLayout frameLayout;
    boolean isFullScreen = false;
    boolean isFromNotification = false;
    LinearLayout mAdViewLayout;
    boolean isPurchased = false;
    String rental_plan = "";
    private Button btnDownload;
    private FragmentManager fragmentManager;
    private int playerHeight;
    private ImageView btnWatchList;
    private ImageView btnWatchListselect;
    private ImageView btnShare;
    private String strMovieURL, strTrailerURL;
    private boolean addedWatchList = false;
    private ImageView imgTrailer;
    private Button btnRental;
    private TextView txtRentalTime, txtExpire, txtLike, txtDisLike;
    private MenuItem mediaRouteMenuItem;
    private CastStateListener mCastStateListener;
    private CastContext mCastContext;
    private IntroductoryOverlay mIntroductoryOverlay;
    private ImageView imgBack;
    private ImageView btnDownloadMovie;
    private LinearLayout lldownloadmovie;
    DefaultTrackSelector.Parameters qualityParams;
    private DefaultTrackSelector trackSelector;
    private ImageView btnLike;
    private ImageView btnDisLike;
    private GradientTextView txtMovie;
    public boolean isMovie;
    private TextView txtShare;
    private TextView txtDownload;
    private RecyclerView recyclerCast;
    private static CountDownTimer countDownTimer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_movie_details);
        IsRTL.ifSupported(this);
        dataSourceFactory = buildDataSourceFactory();
        GlobalBus.getBus().register(this);
        txtExpire = findViewById(R.id.txtExpire);
        recyclerCast = findViewById(R.id.recyclerCast);
        recyclerCast.setHasFixedSize(true);
        recyclerCast.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCast.setFocusable(false);
        recyclerCast.setNestedScrollingEnabled(false);

        mAdViewLayout = findViewById(R.id.adView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        txtMovie = findViewById(R.id.txtMovie);
        ImageView imgRental = findViewById(R.id.imgRental);
        btnRental = findViewById(R.id.btnRental);
        txtRentalTime = findViewById(R.id.txtRentalTime);
        btnRental.setOnClickListener(this);
        layoutTrailer = findViewById(R.id.layoutTrailer);
        btnShare = findViewById(R.id.btnShare);
        lldownloadmovie = findViewById(R.id.lldownloadmovie);
        btnDownloadMovie = findViewById(R.id.btnDownloadMovie);
        btnDownloadMovie.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        txtDescription = findViewById(R.id.txtDescription);
        btnLike = findViewById(R.id.btnLike);
        btnLike.setOnClickListener(this);
        btnDisLike = findViewById(R.id.btnDisLike);
        btnDisLike.setOnClickListener(this);
        txtLike = findViewById(R.id.txtLike);
        txtDisLike = findViewById(R.id.txtDisLike);
        myApplication = MyApplication.getInstance();
        fragmentManager = getSupportFragmentManager();
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        Remember.putString(Constant.MOVIE_ID, Id);
        Remember.putString(Constant.SHOW_ID, "");
        if (intent.hasExtra("isNotification")) {
            isFromNotification = true;
        }

        frameLayout = findViewById(R.id.playerSection);
        int columnWidth = NetworkUtils.getScreenWidth(this);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        playerHeight = frameLayout.getLayoutParams().height;

        BannerAds.showBannerAds(this, mAdViewLayout);

        mListItemRelated = new ArrayList<>();
        itemMovie = new ItemMovie();
        lytRelated = findViewById(R.id.lytRelated);
        mProgressBar = findViewById(R.id.progressBar1);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        lytParent = findViewById(R.id.lytParent);
        nestedScrollView = findViewById(R.id.nestedScrollView);

        textRelViewAll = findViewById(R.id.textRelViewAll);
        textTitle = findViewById(R.id.textTitle);
        textDate = findViewById(R.id.textDate);
        textLanguage = findViewById(R.id.txtLanguage);
        textGenre = findViewById(R.id.txtGenre);

        rvRelated = findViewById(R.id.rv_related);
        btnDownload = findViewById(R.id.btnDownload);
        textRate = findViewById(R.id.txtIMDbRating);
        lytRate = findViewById(R.id.lytIMDB);
        btnWatchList = findViewById(R.id.btnWatchList);
        btnWatchListselect = findViewById(R.id.btnWatchListselect);
        btnWatchListselect.setOnClickListener(this);
        btnWatchList.setOnClickListener(this);
        rvRelated.setHasFixedSize(true);
        rvRelated.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);
        imgTrailer = findViewById(R.id.imgTrailer);
        imgBack = findViewById(R.id.imgBack);
        LinearLayout layoutMovieControl = findViewById(R.id.layoutMovieControl);
        imgBack.setOnClickListener(v -> onBackPressed());
        if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
            getDetails();
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        mCastStateListener = newState -> {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                showIntroductoryOverlay();
            }
        };

        mCastContext = CastContext.getSharedInstance(this);

        if (getIntent().getBooleanExtra("live", false))
            layoutMovieControl.setVisibility(View.GONE);
        else
            layoutMovieControl.setVisibility(View.VISIBLE);

        TextView txtWatchList = findViewById(R.id.txtWatchList);
        txtWatchList.setOnClickListener(this);
        txtDownload = findViewById(R.id.txtDownload);
        txtDownload.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        mCastContext.addCastStateListener(mCastStateListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
    }

    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(() -> {
                mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                        MovieDetailsActivity.this, mediaRouteMenuItem)
                        .setTitleText(getString(R.string.app_name))
                        .setSingleTime()
                        .setOnOverlayDismissedListener(
                                new IntroductoryOverlay.OnOverlayDismissedListener() {
                                    @Override
                                    public void onOverlayDismissed() {
                                        mIntroductoryOverlay = null;
                                    }
                                })
                        .build();
                mIntroductoryOverlay.show();
            });
        }
    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Id);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.MOVIE_DETAILS_URL1, params, new AsyncHttpResponseHandler() {
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
                SetCount();

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    isPurchased = mainJson.getBoolean(Constant.USER_PLAN_STATUS);
                    JSONObject objJson = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    rental_plan = objJson.optString(Constant.RENTAL_PLAN_STATUS);
                    if (objJson.length() > 0) {
                        if (objJson.has(Constant.STATUS)) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            itemMovie.setMovieId(objJson.getString(Constant.MOVIE_ID));
                            itemMovie.setMovieName(objJson.getString(Constant.MOVIE_TITLE));
                            itemMovie.setMovieDescription(objJson.getString(Constant.MOVIE_DESC));
                            itemMovie.setMovieImage(objJson.getString(Constant.MOVIE_IMAGE));
                            itemMovie.setMovieLanguage(objJson.getString(Constant.MOVIE_LANGUAGE));
                            itemMovie.setMovieUrl(objJson.getString(Constant.MOVIE_URL));

                            Remember.putString("movie_url", objJson.getString(Constant.MOVIE_URL));
                            itemMovie.setMovieType(objJson.getString(Constant.MOVIE_TYPE));
                            itemMovie.setMovieDuration(objJson.getString(Constant.MOVIE_DURATION));
                            itemMovie.setMovieDate(objJson.getString(Constant.MOVIE_DATE));
                            itemMovie.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equals("Paid"));
                            itemMovie.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
                            if (objJson.getString(Constant.MOVIE_ACCESS).equals("Rental")) {
                                btnRental.setVisibility(View.GONE);
                                txtRentalTime.setVisibility(View.GONE);
                                txtExpire.setVisibility(View.GONE);
                                String strTime = mainJson.optString("time_remaining");
                                itemMovie.setStrTime(strTime);
                            }
                            itemMovie.setDownload(objJson.getBoolean(Constant.DOWNLOAD_ENABLE));
                            itemMovie.setDownloadUrl(objJson.getString(Constant.DOWNLOAD_URL));
                            itemMovie.setMovieRating(objJson.getString(Constant.IMDB_RATING));
                            strMovieURL = objJson.getString(Constant.MOVIE_SHARE_URL);
                            strTrailerURL = objJson.getString(Constant.TRAILER_URL);

                            if (TextUtils.isEmpty(objJson.optString(Constant.TRAILER_URL)))
                                layoutTrailer.setVisibility(View.GONE);
                            else {
                                layoutTrailer.setVisibility(View.VISIBLE);
                                layoutTrailer.setOnClickListener(v -> {
                                    if (isMovie) {
                                        ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(objJson.optString(Constant.MOVIE_URL), false);
                                        exoPlayerFragment.setItemMovie(itemMovie);
                                        fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                                        Remember.putString(Constant.MOVIE_FROM, "movie");
                                        try {
                                            textTitle.setText(objJson.getString(Constant.MOVIE_TITLE));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        txtMovie.setText(getString(R.string.trailer));
                                        isMovie = false;
                                    } else {
                                        ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(objJson.optString(Constant.TRAILER_URL), true);
                                        exoPlayerFragment.setItemMovie(itemMovie);
                                        fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                                        Remember.putString(Constant.MOVIE_FROM, "movie");
                                        try {
                                            textTitle.setText(objJson.getString(Constant.MOVIE_TITLE) + " Trailer");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        txtMovie.setText("Play Movie");
                                        isMovie = true;
                                    }
                                });
                                Picasso.with(MovieDetailsActivity.this).load(objJson.optString(Constant.MOVIE_IMAGE)).into(imgTrailer);
                            }


                            if (mainJson.getString(Constant.IS_AVAILABLE).equalsIgnoreCase("1")) {
                                btnWatchListselect.setVisibility(View.VISIBLE);
                                btnWatchList.setVisibility(View.GONE);
                                addedWatchList = true;
                            }
                            ArrayList<ItemMovie.Videos> listVideo = new ArrayList<>();
                            if (objJson.has("videos")) {
                                JSONArray arrayVideos = objJson.getJSONArray("videos");
                                for (int i = 0; i < arrayVideos.length(); i++) {
                                    JSONObject objVideo = arrayVideos.getJSONObject(i);
                                    ItemMovie.Videos videos = new ItemMovie.Videos();
                                    videos.setLanguage(objVideo.getString("language"));
                                    videos.setUrl(objVideo.getString("url"));
                                    listVideo.add(videos);
                                }
                            }
                            itemMovie.setListVideo(listVideo);

                            ArrayList<ItemMovie.Resolution> listResolution = new ArrayList<>();
                            if (objJson.has("move_resolution")) {
                                JSONArray arrayVideos = objJson.getJSONArray("move_resolution");
                                for (int i = 0; i < arrayVideos.length(); i++) {
                                    JSONObject objVideo = arrayVideos.getJSONObject(i);
                                    ItemMovie.Resolution videos = new ItemMovie.Resolution();
                                    videos.setMovie_id(objVideo.getString("movie_id"));
                                    videos.setMovie_resolution(objVideo.getString("movie_resolution"));
                                    videos.setMovie_url(objVideo.getString("movie_url"));
                                    listResolution.add(videos);
                                }
                            }
                            itemMovie.setLstResolution(listResolution);


                            JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_MOVIE_ARRAY_NAME);
                            if (jsonArrayChild.length() != 0) {
                                for (int j = 0; j < jsonArrayChild.length(); j++) {
                                    JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                    ItemMovie item = new ItemMovie();
                                    item.setMovieId(objChild.getString(Constant.MOVIE_ID));
                                    item.setMovieName(objChild.getString(Constant.MOVIE_TITLE));
                                    item.setMovieImage(objChild.getString(Constant.MOVIE_POSTER));
                                    item.setMovieDuration(objChild.getString(Constant.MOVIE_DURATION));
                                    item.setPremium(objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Premium")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Rental")
                                            || objJson.getString(Constant.MOVIE_ACCESS).equalsIgnoreCase("Vip")
                                    );
                                    item.setMovieAccess(objJson.getString(Constant.MOVIE_ACCESS));
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

                        }
                        displayData();

                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        lytParent.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                    JSONArray arrayCast = objJson.getJSONArray("movie_cast");
                    if (arrayCast.length() > 0) {
                        ArrayList<CastModel> castModelArrayList = new ArrayList<>();
                        for (int i = 0; i < arrayCast.length(); i++) {
                            JSONObject jsonData = arrayCast.optJSONObject(i);
                            castModelArrayList.add(new CastModel(jsonData.optString("image"), jsonData.optString("name"), jsonData.optString("type")));
                        }
                        recyclerCast.setAdapter(new MovieCastAdapter(MovieDetailsActivity.this, castModelArrayList, false));
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

    private DataSource.Factory buildDataSourceFactory() {
        return ((MyApplication) getApplication()).buildDataSourceFactory();
    }

    private void addToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("movie_videos_id", Id);
        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        client.post(Constant.SAVED_TO_WATCHLIST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);


                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    if (mainJson.optString("code").equalsIgnoreCase("200")) {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                        pDialog
                                .setTitle(getString(R.string.app_name))
                                .setMessage(mainJson.getString("message"))
                                .setIcon(R.drawable.watchlist)
                                .addButton(
                                        getString(android.R.string.ok),
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_red,
                                        pDialog::dismiss)
                                .show();
                        btnWatchListselect.setVisibility(View.VISIBLE);
                        btnWatchList.setVisibility(View.GONE);
                        addedWatchList = true;
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void SetCount() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Id);
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.LIKE_COUNT_MOVIE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    System.out.println("LikeMovie ==> JSON_ARRAY ==> " + jsonArray);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.getString(Constant.LIKE).equals("0")) {
                            txtLike.setText(Constant.LIKE);
                        } else {
                            txtLike.setText(objJson.getString(Constant.LIKE));
                        }
                        if (objJson.getString(Constant.DISLIKE).equals("0")) {
                            txtDisLike.setText(Constant.DISLIKE);
                        } else {
                            txtDisLike.setText(objJson.getString(Constant.DISLIKE));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void DislikeMovie() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Id);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.DISLIKE_MOVIE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                SetCount();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void likeMovie() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Id);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.LIKE_MOVIE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                SetCount();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void removeToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("movie_videos_id", Id);
        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        client.post(Constant.REMOVE_TO_WATCHLIST, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);


                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    if (mainJson.optString("code").equalsIgnoreCase("200")) {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                        pDialog
                                .setTitle(getString(R.string.app_name))
                                .setMessage(mainJson.getString("message"))
                                .setIcon(R.drawable.watchlist)
                                .addButton(
                                        getString(android.R.string.ok),
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_red,
                                        pDialog::dismiss)
                                .show();
                        btnWatchListselect.setVisibility(View.GONE);
                        btnWatchList.setVisibility(View.VISIBLE);
                        addedWatchList = false;
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void displayData() {
        setTitle("");
        textTitle.setText(itemMovie.getMovieName());
        textDate.setText(itemMovie.getMovieDate());

        textLanguage.setText(itemMovie.getMovieLanguage());
        textGenre.setText(strGenre.toString());

        if (itemMovie.getMovieRating().isEmpty() || itemMovie.getMovieRating().equals("0")) {
            lytRate.setVisibility(View.GONE);
        } else {
            textRate.setText(itemMovie.getMovieRating());
        }

        String htmlText = itemMovie.getMovieDescription();

        boolean isRTL = Boolean.parseBoolean(getResources().getString(R.string.isRTL));
        String direction = isRTL ? "rtl" : "ltr";

        String text = "<html dir=" + direction + "><head>"
                + "<style></style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtDescription.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtDescription.setText(Html.fromHtml(text));
        }

        new ReadMoreTextView(txtDescription, 5, "Read More", "Read less", "");
        initPlayer();
        initDownload();

        if (!mListItemRelated.isEmpty()) {
            homeMovieAdapter = new HomeMovieListAdapter(MovieDetailsActivity.this, mListItemRelated, false);
            rvRelated.setAdapter(homeMovieAdapter);

            homeMovieAdapter.setOnItemClickListener(position -> {
                String movieId = mListItemRelated.get(position).getMovieId();
                Intent intent = new Intent(MovieDetailsActivity.this, MovieDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Id", movieId);
                startActivity(intent);
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }
    }

    private void initPlayer() {
        if (!itemMovie.getMovieAccess().equalsIgnoreCase("Free")) {
            if (itemMovie.getMovieAccess().equalsIgnoreCase("Rental")) {
                if (rental_plan.equalsIgnoreCase("active")) {
                    if (itemMovie.isDownload()) {
                        lldownloadmovie.setVisibility(View.VISIBLE);
                    } else {
                        lldownloadmovie.setVisibility(View.GONE);
                    }
                    setPlayer();
                } else {
                    if (itemMovie.isDownload()) {
                        lldownloadmovie.setVisibility(View.VISIBLE);
                    } else {
                        lldownloadmovie.setVisibility(View.GONE);
                    }
                    PremiumContentListFragment premiumContentFragment = PremiumContentListFragment.newInstance(Id, "Movies", itemMovie.getMovieAccess());
                    fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
                }
            } else {
                if (isPurchased) {
                    if (itemMovie.isDownload()) {
                        lldownloadmovie.setVisibility(View.VISIBLE);
                    } else {
                        lldownloadmovie.setVisibility(View.GONE);
                    }
                    setPlayer();
                } else {
                    if (itemMovie.isDownload()) {
                        lldownloadmovie.setVisibility(View.VISIBLE);
                    } else {
                        lldownloadmovie.setVisibility(View.GONE);
                    }
                    PremiumContentListFragment premiumContentFragment = PremiumContentListFragment.newInstance(Id, "Movies", itemMovie.getMovieAccess());
                    fragmentManager.beginTransaction().replace(R.id.playerSection, premiumContentFragment).commitAllowingStateLoss();
                }
            }
        } else {
            if (itemMovie.isDownload()) {
                lldownloadmovie.setVisibility(View.VISIBLE);
            } else {
                lldownloadmovie.setVisibility(View.GONE);
            }
            setPlayer();
        }

    }

    private void initDownload() {
        if (itemMovie.isDownload()) {
            if (itemMovie.isPremium()) {
                if (isPurchased) {
                    btnDownload.setVisibility(View.VISIBLE);
                } else
                    btnDownload.setVisibility(View.GONE);
            } else
                btnDownload.setVisibility(View.VISIBLE);
        } else
            btnDownload.setVisibility(View.GONE);
    }


    private void setPlayer() {
        if (itemMovie.getMovieUrl().isEmpty()) {
            EmbeddedImagesFragment embeddedImageFragment = EmbeddedImagesFragment.newInstance(itemMovie.getMovieUrl(), itemMovie.getMovieImage(), false);
            fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
        } else {
            switch (itemMovie.getMovieType()) {
                case "Local":
                case "URL":
                    if (getIntent().getBooleanExtra("live", false)) {
                        ExoTVPlayerFragment exoPlayerFragment = ExoTVPlayerFragment.newInstance(itemMovie.getMovieUrl(), false);
                        exoPlayerFragment.setItemMovie(itemMovie);
                        fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                    } else {
                        ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(itemMovie.getMovieUrl(), false);
                        exoPlayerFragment.setItemMovie(itemMovie);
                        fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
                    }
                    Remember.putString(Constant.MOVIE_FROM, "movie");
                    break;
                case "Embed":
                    EmbeddedImagesFragment embeddedImageFragment = EmbeddedImagesFragment.newInstance(itemMovie.getMovieUrl(), itemMovie.getMovieImage(), true);
                    fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
                    break;

            }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(MovieDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            gotoFullScreen();
        } else {
            gotoPortraitScreen();
        }
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            assert fragment != null;
            fragment.fullScreen();

        } else {
            if (isFromNotification) {
                Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
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

    @Override
    public void onClick(View v) {
        if (v == btnWatchList) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                if (myApplication.getIsLogin())
                    addToWatchList();
                else {
                    PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                    pDialog
                            .setTitle(getString(R.string.menu_login))
                            .setMessage("Please Login to Enjoy Endless Streaming !!")
                            .setIcon(R.drawable.reel)
                            .addButton(
                                    getString(R.string.menu_login),
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    () -> {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                }
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        }
        if (v == btnLike) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                if (myApplication.getIsLogin())
                    likeMovie();
                else {
                    PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                    pDialog
                            .setTitle(getString(R.string.menu_login))
                            .setMessage("Please Login to Enjoy Endless Streaming !!")
                            .setIcon(R.drawable.reel)
                            .addButton(
                                    getString(R.string.menu_login),
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    () -> {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                }
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        }
        if (v == btnDisLike) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                if (myApplication.getIsLogin())
                    DislikeMovie();
                else {
                    PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                    pDialog
                            .setTitle(getString(R.string.menu_login))
                            .setMessage("Please Login to Enjoy Endless Streaming !!")
                            .setIcon(R.drawable.reel)
                            .addButton(
                                    getString(R.string.menu_login),
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    () -> {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                }
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        } else if (v == btnWatchListselect) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                if (myApplication.getIsLogin())
                    removeToWatchList();
                else {
                    PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                    pDialog
                            .setTitle(getString(R.string.menu_login))
                            .setMessage("Please Login to Enjoy Endless Streaming !!")
                            .setIcon(R.drawable.reel)
                            .addButton(
                                    getString(R.string.menu_login),
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    () -> {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                }
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        } else if (v == btnShare) {
        } else if (v == btnRental) {
            startActivity(new Intent(getApplicationContext(), PlanActivity.class).putExtra("rental", "true"));
        } else if (v == btnDownloadMovie) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                if (myApplication.getIsLogin()) {
                    ExoDownloadState exoDownloadState = (ExoDownloadState) ExoDownloadState.DOWNLOAD_START;
                    exoVideoDownloadDecision(exoDownloadState);
                } else {
                    PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                    pDialog
                            .setTitle(getString(R.string.menu_login))
                            .setMessage("Please Login to Enjoy Endless Streaming !!")
                            .setIcon(R.drawable.reel)
                            .addButton(
                                    getString(R.string.menu_login),
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    () -> {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                }
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        } else if (v == txtDownload) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                if (myApplication.getIsLogin()) {
                    ExoDownloadState exoDownloadState = (ExoDownloadState) ExoDownloadState.DOWNLOAD_START;
                    exoVideoDownloadDecision(exoDownloadState);
                } else {
                    PrettyDialog pDialog = new PrettyDialog(MovieDetailsActivity.this);
                    pDialog
                            .setTitle(getString(R.string.menu_login))
                            .setMessage("Please Login to Enjoy Endless Streaming !!")
                            .setIcon(R.drawable.reel)
                            .addButton(
                                    getString(R.string.menu_login),
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_green,
                                    () -> {
                                        pDialog.dismiss();
                                        startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                }
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        } else if (v == txtShare) {
        }
    }

    private void exoVideoDownloadDecision(ExoDownloadState exoDownloadState) {
        System.out.println("MovieDetailsActivity ==> Movie URL ==> " + itemMovie.getMovieUrl());
        System.out.println("MovieDetailsActivity ==> Movie URL2 ==> " + Remember.getString("movie_url", "0"));
        if (exoDownloadState == null || Remember.getString("movie_url", "0").isEmpty()) {
            Toast.makeText(this, "Please, Tap Again", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (exoDownloadState) {

            case DOWNLOAD_START:
                fetchDownloadOptions();

                break;

            case DOWNLOAD_PAUSE:

                downloadManager.addDownload(downloadTracker.getDownloadRequest(Uri.parse(itemMovie.getMovieUrl())), Download.STATE_STOPPED);
                break;

            case DOWNLOAD_RESUME:

                downloadManager.addDownload(downloadTracker.getDownloadRequest(Uri.parse(itemMovie.getMovieUrl())), Download.STOP_REASON_NONE);
                break;

            case DOWNLOAD_RETRY:

                break;

            case DOWNLOAD_COMPLETED:
                Toast.makeText(this, "Already Downloaded, Delete from Downloaded video ", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void fetchDownloadOptions() {
        trackKeys.clear();

        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        if (pDialog == null || !pDialog.isShowing()) {
            pDialog = new ProgressDialog(MovieDetailsActivity.this);
            pDialog.setTitle(null);
            pDialog.setCancelable(false);
            pDialog.setMessage("Preparing Download Options...");
            pDialog.show();
        }


        DownloadHelper downloadHelper = DownloadHelper.forHls(MovieDetailsActivity.this, Uri.parse(itemMovie.getMovieUrl()), dataSourceFactory, new DefaultRenderersFactory(MovieDetailsActivity.this));


        downloadHelper.prepare(new DownloadHelper.Callback() {
            @Override
            public void onPrepared(DownloadHelper helper) {
                myDownloadHelper = helper;
                for (int i = 0; i < helper.getPeriodCount(); i++) {
                    TrackGroupArray trackGroups = helper.getTrackGroups(i);
                    for (int j = 0; j < trackGroups.length; j++) {
                        TrackGroup trackGroup = trackGroups.get(j);
                        for (int k = 0; k < trackGroup.length; k++) {
                            Format track = trackGroup.getFormat(k);
                            if (shouldDownload(track)) {
                                trackKeys.add(new TrackKey(trackGroups, trackGroup, track));
                            }
                        }
                    }
                }

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }


                optionsToDownload.clear();
                showDownloadOptionsDialog(myDownloadHelper, trackKeys);
            }

            @Override
            public void onPrepareError(DownloadHelper helper, IOException e) {

            }
        });
    }

    private void showDownloadOptionsDialog(DownloadHelper helper, List<TrackKey> trackKeyss) {

        if (helper == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailsActivity.this);
        builder.setTitle("Select Download Format");
        int checkedItem = 1;


        for (int i = 0; i < trackKeyss.size(); i++) {
            TrackKey trackKey = trackKeyss.get(i);
            String videoResoultionDashSize = " " + trackKey.getTrackFormat().height + "      (" + "MB" + ")";
            optionsToDownload.add(i, videoResoultionDashSize);
        }

        // Initialize a new array adapter instance
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                MovieDetailsActivity.this, // Context
                android.R.layout.simple_list_item_single_choice, // Layout
                optionsToDownload // List
        );

        TrackKey trackKey = trackKeyss.get(0);
        qualityParams = ((DefaultTrackSelector) trackSelector).getParameters().buildUpon()
                .setMaxVideoSize(trackKey.getTrackFormat().width, trackKey.getTrackFormat().height)
                .setMaxVideoBitrate(trackKey.getTrackFormat().bitrate)
                .build();

        builder.setSingleChoiceItems(arrayAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TrackKey trackKey = trackKeyss.get(i);

                qualityParams = ((DefaultTrackSelector) trackSelector).getParameters().buildUpon()
                        .setMaxVideoSize(trackKey.getTrackFormat().width, trackKey.getTrackFormat().height)
                        .setMaxVideoBitrate(trackKey.getTrackFormat().bitrate)
                        .build();


            }
        });
        // Set the a;ert dialog positive button
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {


                for (int periodIndex = 0; periodIndex < helper.getPeriodCount(); periodIndex++) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = helper.getMappedTrackInfo(/* periodIndex= */ periodIndex);
                    helper.clearTrackSelections(periodIndex);
                    for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
//                        TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                        helper.addTrackSelection(
                                periodIndex,
                                qualityParams);
                    }

                }


                DownloadRequest downloadRequest = helper.getDownloadRequest(Util.getUtf8Bytes(itemMovie.getMovieUrl()));
                if (downloadRequest.streamKeys.isEmpty()) {
                    // All tracks were deselected in the dialog. Don't start the download.
                    return;
                }


                startDownload(downloadRequest);

                dialogInterface.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void startDownload(DownloadRequest downloadRequestt) {

        DownloadRequest myDownloadRequest = downloadRequestt;

        if (myDownloadRequest.uri.toString().isEmpty()) {
            Toast.makeText(this, "Try Again!!", Toast.LENGTH_SHORT).show();

            return;
        } else {
            downloadManager.addDownload(myDownloadRequest);
        }
    }

    private boolean shouldDownload(Format track) {
        return track.height != 240 && track.sampleMimeType.equalsIgnoreCase("video/avc");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (NetworkUtils.isConnected(MovieDetailsActivity.this)) {
                getDetails();
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        }
    }


    @Override
    public void onPositiveClick(int from) {
        if (from == 101) {
            startActivity(new Intent(MovieDetailsActivity.this, SignInActivity.class));
        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }
}
