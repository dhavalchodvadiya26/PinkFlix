package com.example.streamingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeMovieVideoListAdapter;
import com.example.adapter.MovieStarCastAdapter;
import com.example.fragment.EmbeddedImageListFragment;
import com.example.fragment.ExoVideoPlayerFragment;
import com.example.fragment.ExoTvVideoPlayerFragment;
import com.example.fragment.PremiumMovieListFragment;
import com.example.itemmodels.CastModel;
import com.example.itemmodels.ItemMovie;
import com.example.util.API;
import com.example.util.AppUtil;
import com.example.util.BannerAds;
import com.example.util.Constant;
import com.example.util.DemoDownloadService;
import com.example.util.DialogUtils;
import com.example.util.DownloadTracker;
import com.example.util.Events;
import com.example.util.ExoDownloadState;
import com.example.util.GetMovieData;
import com.example.util.GlobalBus;
import com.example.util.GradientTextView;
import com.example.util.NetworkUtils;
import com.example.util.OfflineDatabaseHelper;
import com.example.util.PrettyDialog;
import com.example.util.Remember;
import com.example.util.TrackKey;
import com.example.util.TrackSelectionDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.net.Uri.parse;
import static com.google.android.exoplayer2.offline.Download.STATE_COMPLETED;
import static com.google.android.exoplayer2.offline.Download.STATE_DOWNLOADING;
import static com.google.android.exoplayer2.offline.Download.STATE_FAILED;
import static com.google.android.exoplayer2.offline.Download.STATE_QUEUED;
import static com.google.android.exoplayer2.offline.Download.STATE_REMOVING;
import static com.google.android.exoplayer2.offline.Download.STATE_RESTARTING;
import static com.google.android.exoplayer2.offline.Download.STATE_STOPPED;


public class MovieDetailActivity1 extends AppCompatActivity implements View.OnClickListener, DialogUtils.AlertDialogListener, DownloadTracker.Listener {

    int playerHeight;
    ProgressDialog pDialog;
    protected static final CookieManager DEFAULT_COOKIE_MANAGER;
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
    private static final int UI_ANIMATION_DELAY = 300;
    OfflineDatabaseHelper offlineDatabaseHelper = new OfflineDatabaseHelper(this);
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    static ItemMovie myItemMovie;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private final Handler mHideHandler = new Handler();
    private final Runnable mShowRunnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }

        }
    };
    RelativeLayout llParentContainer;
    LinearLayout lyt_not_found;
    LinearLayout parent_LL;
    private RecyclerView recyclerCast;
    boolean isPurchased = false;
    String rental_plan = "";
    private String strMovieURL;
    ItemMovie itemMovie;
    ArrayList<ItemMovie> mListItemRelated;
    private LinearLayout lytRate, layoutTrailer;
    private FragmentManager fragmentManager;
    private Button btnRental;
    public boolean isMovie;
    private TextView txtRentalTime, txtExpire;
    private GradientTextView txtMovie;
    GradientTextView textTitle;
    TextView textDate, textLanguage, textGenre, textURL, textRate;
    TextView txtDescription;
    HomeMovieVideoListAdapter homeMovieAdapter;
    LinearLayout lytRelated;
    RecyclerView rvRelated;
    StringBuilder strGenre = new StringBuilder();

    List<TrackKey> trackKeys = new ArrayList<>();
    List<String> optionsToDownload = new ArrayList<String>();
    DefaultTrackSelector.Parameters qualityParams;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private DataSource.Factory dataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private TrackGroupArray lastSeenTrackGroupArray;
    private boolean startAutoPlay;
    private int startWindow;
    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private long startPosition;
    private FrameLayout frameLayoutMain;
    NestedScrollView nestedScrollView;
    LinearLayout mAdViewLayout;
    private GetMovieData getMovieData;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private DownloadTracker downloadTracker;
    private DownloadManager downloadManager;
    private DownloadHelper myDownloadHelper;
    private LinearLayout llDownloadVideo;
    private LinearLayout ll_watchlist;
    private LinearLayout ll_share;
    private LinearLayout ll_like;
    private LinearLayout ll_dislike;
    private ImageView btnDownloadMovie;
    private TextView tvDownloadState;
    private String Id;
    MediaPlayer mp;
    private Long currentPosition;
    private Runnable runnableCode;
    private Handler handler;
    private ImageView imgBack;

    private ImageView btnWatchList;
    private ImageView btnWatchListselect;
    MyApplication myApplication;
    private ImageView imgTrailer;
    boolean isLiked=false;
    ProgressBar mProgressBar;
    private boolean addedWatchList = false;
    private ImageView btnShare;
    private TextView txtShare;
    private TextView txtLike;
    private TextView txtDisLike;
    private ImageView btnLike;
    private ImageView btnDisLike;
    boolean isFullScreen = false;

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
         mp = MediaPlayer.create(MovieDetailActivity1.this, R.raw.btn_click);

        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        setContentView(R.layout.activity_online_player);
        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        currentPosition = intent.getLongExtra("recentDuration", 0);
        Remember.putString(Constant.MOVIE_ID, Id);
        Remember.putString(Constant.SHOW_ID, "");
        myApplication = MyApplication.getInstance();
        itemMovie = new ItemMovie();
        getDetails();


        recyclerCast = findViewById(R.id.recyclerCast);
        recyclerCast.setHasFixedSize(true);
        recyclerCast.setLayoutManager(new LinearLayoutManager(MovieDetailActivity1.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerCast.setFocusable(false);
        recyclerCast.setNestedScrollingEnabled(false);

        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        } else {
            trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
            clearStartPosition();
        }

        MyApplication application = (MyApplication) getApplication();
        downloadTracker = application.getDownloadTracker();
        downloadManager = application.getDownloadManager();

        try {
            DownloadService.start(this, DemoDownloadService.class);
        } catch (IllegalStateException e) {
            DownloadService.startForeground(this, DemoDownloadService.class);
        }

        createView();

    }


    private void observerVideoStatus() {
//        itemMovie=new ItemMovie();
        if (downloadManager.getCurrentDownloads().size() > 0) {
            for (int i = 0; i < downloadManager.getCurrentDownloads().size(); i++) {
                Download currentDownload = downloadManager.getCurrentDownloads().get(i);
                if (!itemMovie.getMovieUrl().isEmpty() && currentDownload.request.uri.equals(parse(itemMovie.getMovieUrl()))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadTracker.downloads.size() > 0) {
                                if (currentDownload.request.uri.equals(parse(itemMovie.getMovieUrl()))) {

                                    Download downloadFromTracker = downloadTracker.downloads.get(parse(itemMovie.getMovieUrl()));
                                    if (downloadFromTracker != null) {
                                        switch (downloadFromTracker.state) {
                                            case STATE_QUEUED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_QUEUE);
                                                break;

                                            case STATE_STOPPED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RESUME);
                                                break;

                                            case STATE_DOWNLOADING:

                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_PAUSE);

                                                if (downloadFromTracker.getPercentDownloaded() != -1) {
                                                }

                                                Log.d("EXO STATE_DOWNLOADING ", +downloadFromTracker.getBytesDownloaded() + " " + downloadFromTracker.contentLength);
                                                Log.d("EXO  STATE_DOWNLOADING ", "" + downloadFromTracker.getPercentDownloaded());


                                                break;
                                            case STATE_COMPLETED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_COMPLETED);
                                                Log.d("EXO STATE_COMPLETED ", +downloadFromTracker.getBytesDownloaded() + " " + downloadFromTracker.contentLength);
                                                Log.d("EXO  STATE_COMPLETED ", "" + downloadFromTracker.getPercentDownloaded());

                                                break;

                                            case STATE_FAILED:
                                                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RETRY);


                                                break;

                                            case STATE_REMOVING:


                                                break;

                                            case STATE_RESTARTING:


                                                break;
                                        }
                                    }
                                }

                            }
                        }
                    });
                }
            }
        }

    }


    protected void createView() {
        handler = new Handler();
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> onBackPressed());
        llParentContainer = (RelativeLayout) findViewById(R.id.lytParent);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        parent_LL = (LinearLayout) findViewById(R.id.parent_LL);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        mAdViewLayout = findViewById(R.id.adView);
        frameLayoutMain = findViewById(R.id.frame_layout_main);
        int columnWidth = NetworkUtils.getScreenWidth(this);
        frameLayoutMain.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        playerHeight = frameLayoutMain.getLayoutParams().height;
        BannerAds.showBannerAds(this, mAdViewLayout);
        llDownloadVideo = (LinearLayout) findViewById(R.id.ll_download_video);
        ll_watchlist = (LinearLayout) findViewById(R.id.ll_watchlist);
        ll_share = (LinearLayout) findViewById(R.id.ll_share);
        ll_like = (LinearLayout) findViewById(R.id.ll_like);
        ll_dislike = (LinearLayout) findViewById(R.id.ll_dislike);
        btnDownloadMovie = (ImageView) findViewById(R.id.img_download_state);
        tvDownloadState = (TextView) findViewById(R.id.tv_download_state);
        llDownloadVideo.setOnClickListener(this);
        ll_dislike.setOnClickListener(this);
        ll_like.setOnClickListener(this);
        ll_share.setOnClickListener(this);
        ll_watchlist.setOnClickListener(this);
        btnWatchList = findViewById(R.id.btnWatchList2);
        btnWatchListselect = findViewById(R.id.btnWatchListselect2);
        btnWatchListselect.setOnClickListener(this);
        btnWatchList.setOnClickListener(this);
        imgTrailer = findViewById(R.id.imgTrailer);
        mProgressBar = findViewById(R.id.progressBar_1);
        btnShare = findViewById(R.id.btnShare1);
        txtShare = findViewById(R.id.txtShare1);
        btnShare.setOnClickListener(this);
        txtShare.setOnClickListener(this);
        btnLike = findViewById(R.id.btnLike1);
        btnDisLike = findViewById(R.id.btnDisLike1);
        txtLike = findViewById(R.id.txtLike1);
        txtDisLike = findViewById(R.id.txtDisLike1);
        btnLike.setOnClickListener(this);
        btnDisLike.setOnClickListener(this);
        btnRental = findViewById(R.id.btnRental);
        btnRental.setOnClickListener(this);
        txtRentalTime = findViewById(R.id.txtRentalTime);
        txtExpire = findViewById(R.id.txtExpire);
        layoutTrailer = findViewById(R.id.layoutTrailer);
        txtMovie = findViewById(R.id.txtMovie);
        fragmentManager = getSupportFragmentManager();
        textTitle = findViewById(R.id.textTitle);
        textDate = findViewById(R.id.textDate);
        textLanguage = findViewById(R.id.txtLanguage);
        textGenre = findViewById(R.id.txtGenre);
        textURL = findViewById(R.id.textURL);
        txtDescription = findViewById(R.id.txtDescription1);
        rvRelated = findViewById(R.id.rv_related);
        rvRelated.setHasFixedSize(true);
        lytRelated = findViewById(R.id.lytRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(MovieDetailActivity1.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);
        textRate = findViewById(R.id.txtIMDbRating);
        lytRate = findViewById(R.id.lytIMDB);
        mListItemRelated = new ArrayList<>();
        getMovieData = new GetMovieData();
    }

    @Override
    public void onStart() {
        super.onStart();
        downloadTracker.addListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        downloadTracker.removeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void hideShowBackButton(boolean hideShow) {
        if (hideShow)
            imgBack.setVisibility(View.VISIBLE);
        else
            imgBack.setVisibility(View.GONE);
    }

    private void setPlayer() {
        if (itemMovie.getMovieUrl().isEmpty()) {
            EmbeddedImageListFragment embeddedImageFragment = EmbeddedImageListFragment.newInstance(itemMovie.getMovieUrl(), itemMovie.getMovieImage(), false);
            fragmentManager.beginTransaction().replace(R.id.frame_layout_main, embeddedImageFragment).commitAllowingStateLoss();
        } else {
            switch (itemMovie.getMovieType()) {
                case "Local":
                case "URL":
                    if (getIntent().getBooleanExtra("live", false)) {
                        ExoTvVideoPlayerFragment exoPlayerFragment = ExoTvVideoPlayerFragment.newInstance(itemMovie.getMovieUrl(), false, currentPosition);
                        exoPlayerFragment.setItemMovie(itemMovie);
                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, exoPlayerFragment).commitAllowingStateLoss();
                    } else {
                        ExoVideoPlayerFragment exoVideoPlayerFragment = ExoVideoPlayerFragment.newInstance(itemMovie.getMovieUrl(), false, currentPosition);
                        exoVideoPlayerFragment.setItemMovie(itemMovie);
                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, exoVideoPlayerFragment).commitAllowingStateLoss();
                    }
                    Remember.putString(Constant.MOVIE_FROM, "movie");
                    break;
                case "Embed":
                    EmbeddedImageListFragment embeddedImageFragment = EmbeddedImageListFragment.newInstance(itemMovie.getMovieUrl(), itemMovie.getMovieImage(), true);
                    fragmentManager.beginTransaction().replace(R.id.frame_layout_main, embeddedImageFragment).commitAllowingStateLoss();
                    break;

            }
        }
    }

    private void initPlayer() {
        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
        exoButtonPrepareDecision();

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        trackSelector.setParameters(trackSelectorParameters);
        lastSeenTrackGroupArray = null;

        if (itemMovie.isDownload()) {
            if (!itemMovie.getMovieAccess().equalsIgnoreCase("Free")) {
                if (itemMovie.getMovieAccess().equalsIgnoreCase("Rental")) {
                    if (rental_plan.equalsIgnoreCase("active")) {
                        llDownloadVideo.setVisibility(View.VISIBLE);
                        setPlayer();
                    } else {
                        llDownloadVideo.setVisibility(View.GONE);
                        PremiumMovieListFragment premiumContentFragment = PremiumMovieListFragment.newInstance(Id, "Movies", itemMovie.getMovieAccess());
                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, premiumContentFragment).commitAllowingStateLoss();
                    }
                } else {
                    if (isPurchased) {
                        llDownloadVideo.setVisibility(View.VISIBLE);
                        setPlayer();
                    } else {
                        llDownloadVideo.setVisibility(View.GONE);
                        PremiumMovieListFragment premiumContentFragment = PremiumMovieListFragment.newInstance(Id, "Movies", itemMovie.getMovieAccess());
                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, premiumContentFragment).commitAllowingStateLoss();
                    }
                }
            } else {
                llDownloadVideo.setVisibility(View.VISIBLE);
                setPlayer();
            }
        } else {
            if (!itemMovie.getMovieAccess().equalsIgnoreCase("Free")) {
                if (itemMovie.getMovieAccess().equalsIgnoreCase("Rental")) {
                    if (rental_plan.equalsIgnoreCase("active")) {
                        llDownloadVideo.setVisibility(View.GONE);
                        setPlayer();
                    } else {
                        llDownloadVideo.setVisibility(View.GONE);
                        PremiumMovieListFragment premiumContentFragment = PremiumMovieListFragment.newInstance(Id, "Movies", itemMovie.getMovieAccess());
                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, premiumContentFragment).commitAllowingStateLoss();
                    }
                } else {
                    if (isPurchased) {
                        llDownloadVideo.setVisibility(View.GONE);
                        setPlayer();
                    } else {
                        llDownloadVideo.setVisibility(View.GONE);
                        PremiumMovieListFragment premiumContentFragment = PremiumMovieListFragment.newInstance(Id, "Movies", itemMovie.getMovieAccess());
                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, premiumContentFragment).commitAllowingStateLoss();
                    }
                }
            } else {
                llDownloadVideo.setVisibility(View.GONE);
                setPlayer();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateTrackSelectorParameters();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.img_setting:

                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(trackSelector)) {
                        isShowingTrackSelectionDialog = true;
                        TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(trackSelector,/* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                        trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);
                    }
                }

                break;
            case R.id.ll_download_video:
                btnDownloadMovie.startAnimation(AnimationUtils.loadAnimation(MovieDetailActivity1.this, R.anim.image_click));
                if (NetworkUtils.isConnected(MovieDetailActivity1.this)) {
                    if (myApplication.getIsLogin()) {
                        if (offlineDatabaseHelper.checkIfMyMovieExists2(itemMovie.getMovieId())) {
                            System.out.println("llDownloadVideo.getTag() ==> " + llDownloadVideo.getTag());
                            ExoDownloadState exoDownloadState = (ExoDownloadState) llDownloadVideo.getTag();
                            exoVideoDownloadDecision(exoDownloadState, offlineDatabaseHelper.getMovieByID2(itemMovie.getMovieId()));
                        } else {
                            PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                            pDialog
                                    .setTitle(getString(R.string.app_name))
                                    .setMessage("Please, Play Movie Once to download it!!")
                                    .setIcon(R.drawable.ic_more_download)
                                    .addButton(
                                            "Okay",
                                            R.color.pdlg_color_white2,
                                            R.color.pdlg_color_green,
                                            pDialog::dismiss)
                                    .show();
                        }
                    } else {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.menu_login))
                                .setMessage("Please Login to Enjoy Endless Streaming !!")
                                .setIcon(R.drawable.reel)
                                .addButton(
                                        getString(R.string.menu_login),
                                        R.color.pdlg_color_white2,
                                        R.color.pdlg_color_green,
                                        () -> {
                                            pDialog.dismiss();
                                            startActivity(new Intent(MovieDetailActivity1.this, SignInScreenActivity.class));
                                        }).addButton(
                                getString(android.R.string.no),
                                R.color.pdlg_color_white2,
                                R.color.pdlg_color_green,
                                pDialog::dismiss)
                                .show();
                    }
                } else {
                    showToast(getString(R.string.conne_msg1));
                }

                break;
            case R.id.btnWatchList2:
                btnWatchList.startAnimation(AnimationUtils.loadAnimation(MovieDetailActivity1.this, R.anim.image_click));
                if (NetworkUtils.isConnected(MovieDetailActivity1.this)) {
                    if (myApplication.getIsLogin())
                        addToWatchList();
                    else {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.menu_login))
                                .setMessage("Please Login to Enjoy Endless Streaming !!")
                                .setIcon(R.drawable.reel)
                                .addButton(
                                        getString(R.string.menu_login),
                                        R.color.pdlg_color_white2,
                                        R.color.pdlg_color_green,
                                        () -> {
                                            pDialog.dismiss();
                                            startActivity(new Intent(MovieDetailActivity1.this, SignInScreenActivity.class));
                                        }).addButton(
                                getString(android.R.string.no),
                                R.color.pdlg_color_white2,
                                R.color.pdlg_color_green,
                                pDialog::dismiss)
                                .show();
                    }
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
                break;

            case R.id.btnWatchListselect2:
                btnWatchListselect.startAnimation(AnimationUtils.loadAnimation(MovieDetailActivity1.this, R.anim.image_click));
                if (NetworkUtils.isConnected(MovieDetailActivity1.this)) {
                    if (myApplication.getIsLogin())
                        removeToWatchList();
                    else {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.menu_login))
                                .setMessage("Please Login to Enjoy Endless Streaming !!")
                                .setIcon(R.drawable.reel)
                                .addButton(
                                        getString(R.string.menu_login),
                                        R.color.pdlg_color_white2,
                                        R.color.pdlg_color_green,
                                        () -> {
                                            pDialog.dismiss();
                                            startActivity(new Intent(MovieDetailActivity1.this, SignInScreenActivity.class));
                                        }).addButton(
                                getString(android.R.string.no),
                                R.color.pdlg_color_white2,
                                R.color.pdlg_color_green,
                                pDialog::dismiss)
                                .show();
                    }
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
                break;

            case R.id.btnShare1:
            case R.id.txtShare1:
                btnShare.startAnimation(AnimationUtils.loadAnimation(MovieDetailActivity1.this, R.anim.image_click));
                createLink(itemMovie.getMovieId());
                break;

            case R.id.ll_like:
            case R.id.txtLike1:
            case R.id.btnLike1:

                if (NetworkUtils.isConnected(MovieDetailActivity1.this)) {
                    if (myApplication.getIsLogin()) {
                        btnLike.startAnimation(AnimationUtils.loadAnimation(MovieDetailActivity1.this, R.anim.image_click));
                        mp.start();
                        likeMovie();
                    } else {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.menu_login))
                                .setMessage("Please Login to Enjoy Endless Streaming !!")
                                .setIcon(R.drawable.reel)
                                .addButton(
                                        getString(R.string.menu_login),
                                        R.color.pdlg_color_white2,
                                        R.color.pdlg_color_green,
                                        () -> {
                                            pDialog.dismiss();
                                            startActivity(new Intent(MovieDetailActivity1.this, SignInScreenActivity.class));
                                        }).addButton(
                                getString(android.R.string.no),
                                R.color.pdlg_color_white2,
                                R.color.pdlg_color_green,
                                pDialog::dismiss)
                                .show();
                    }
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
                break;

            case R.id.ll_dislike:
            case R.id.btnDisLike1:
            case R.id.txtDisLike1:
                btnDisLike.startAnimation(AnimationUtils.loadAnimation(MovieDetailActivity1.this, R.anim.image_click));
                if (NetworkUtils.isConnected(MovieDetailActivity1.this)) {
                    if (myApplication.getIsLogin())
                        DislikeMovie();
                    else {
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.menu_login))
                                .setMessage("Please Login to Enjoy Endless Streaming !!")
                                .setIcon(R.drawable.reel)
                                .addButton(
                                        getString(R.string.menu_login),
                                        R.color.pdlg_color_white2,
                                        R.color.pdlg_color_green,
                                        () -> {
                                            pDialog.dismiss();
                                            startActivity(new Intent(MovieDetailActivity1.this, SignInScreenActivity.class));
                                        }).addButton(
                                getString(android.R.string.no),
                                R.color.pdlg_color_white2,
                                R.color.pdlg_color_green,
                                pDialog::dismiss)
                                .show();
                    }
                } else {
                    showToast(getString(R.string.conne_msg1));
                }
                break;

        }

    }

    private void createLink(String movie_id) {
        Log.i("MovieDetailActivity1", "Creating Link...");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://pinkflix.in/"))
                .setDomainUriPrefix("https://pinkflixapp.page.link/")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Log.e("MovieDetailActivity1", "Long Refer Link ==> " + dynamicLinkUri);//https://pinkflix.page.link/?apn=com.appegic.cprime&ibi=com.example.ios&link=https%3A%2F%2Fpinkflix.app%2F

        //Shorten The Link

        //Manually
        String shortLink = "https://pinkflixapp.page.link/?"
                + "link=https://pinkflix.in/myrefer.php?Id=" + movie_id
                + "&apn=" + getPackageName()
                + "&ibi=com.pinkflixapp"
                + "&st=" + itemMovie.getMovieName().replaceAll(" ", "%20")
                + "&sd=" + "Click%20to%20Watch%20Movie"
                + "&si=" + itemMovie.getMovieImage();

        System.out.println("MovieDetailActivity1 ==> ShortLink ==> " + shortLink);


        //Using Firebase Tools
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(shortLink))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("MovieDetailActivity1", "ShortLink ==> " + shortLink);

                            Intent shareIntent;
                            shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            shareIntent.setType("text/plain");
                            startActivity(shareIntent);

                        } else {
                            // Error
                            // ...
                            Log.e("MovieDetailActivity1", "Error ==> " + task.getResult());

                        }
                    }
                });

    }

    private void likeMovie() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Remember.getString("movie_id", ""));
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.LIKE_MOVIE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                SetCount();
                isLiked = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    private void SetCount() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", Remember.getString("movie_id", ""));
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.LIKE_COUNT_MOVIE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

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
        jsObj.addProperty("movie_id", Remember.getString("movie_id", ""));
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.DISLIKE_MOVIE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                SetCount();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void addToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("movie_videos_id", Remember.getString("movie_id", ""));
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
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.app_name))
                                .setMessage(mainJson.getString("message"))
                                .setIcon(R.drawable.watchlist)
                                .addButton(
                                        getString(android.R.string.ok),
                                        R.color.pdlg_color_white2,
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

    private void removeToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("movie_videos_id", Remember.getString("movie_id", ""));
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
                        PrettyDialog pDialog = new PrettyDialog(MovieDetailActivity1.this);
                        pDialog
                                .setTitle(getString(R.string.app_name))
                                .setMessage(mainJson.getString("message"))
                                .setIcon(R.drawable.watchlist)
                                .addButton(
                                        getString(android.R.string.ok),
                                        R.color.pdlg_color_white2,
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

    private void exoVideoDownloadDecision(ExoDownloadState exoDownloadState, ItemMovie downloadItemMovie) {
        if (exoDownloadState == null || itemMovie.getMovieUrl().isEmpty()) {
            Toast.makeText(this, "Please, Tap Again", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (exoDownloadState) {

            case DOWNLOAD_START:
                fetchDownloadOptions(downloadItemMovie);
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

    private void exoButtonPrepareDecision() {
        if (downloadTracker.downloads.size() > 0) {
            Download download = downloadTracker.downloads.get(Uri.parse(itemMovie.getMovieUrl()));

            if (download != null) {
                if (download.getPercentDownloaded() > 99.0) {
                    setCommonDownloadButton(ExoDownloadState.DOWNLOAD_COMPLETED);

                } else {
                    //Resume Download Not 100 % Downloaded
                    //So, resume download
                    setCommonDownloadButton(ExoDownloadState.DOWNLOAD_RESUME);
                }
            } else {
                // New Download
                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_START);
            }

        } else {
            setCommonDownloadButton(ExoDownloadState.DOWNLOAD_START);
        }
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            parent_LL.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameLayoutMain.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            frameLayoutMain.setLayoutParams(params);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            parent_LL.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frameLayoutMain.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            frameLayoutMain.setLayoutParams(params);
        }
    }

    private void fetchDownloadOptions(ItemMovie downloadItemMovie) {
        trackKeys.clear();

        if (pDialog == null || !pDialog.isShowing()) {
            pDialog = new ProgressDialog(MovieDetailActivity1.this);
            pDialog.setTitle(null);
            pDialog.setCancelable(false);
            pDialog.setMessage("Preparing Download Options...");
            pDialog.show();
        }


        DownloadHelper downloadHelper = DownloadHelper.forHls(MovieDetailActivity1.this, Uri.parse(itemMovie.getMovieUrl()), dataSourceFactory, new DefaultRenderersFactory(MovieDetailActivity1.this));


        downloadHelper.prepare(new DownloadHelper.Callback() {
            @Override
            public void onPrepared(DownloadHelper helper) {
                // Preparation completes. Now other DownloadHelper methods can be called.
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
                showDownloadOptionsDialog(myDownloadHelper, trackKeys, downloadItemMovie);
            }

            @Override
            public void onPrepareError(DownloadHelper helper, IOException e) {
            }
        });
    }

    private void showDownloadOptionsDialog(DownloadHelper helper, List<TrackKey> trackKeys, ItemMovie downloadItemMovie) {

        if (helper == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity1.this);
        builder.setTitle("Select Download Format");
        for (int i = 0; i < trackKeys.size(); i++) {
            TrackKey trackKey = trackKeys.get(i);
            long bitrate = (long) trackKey.getTrackFormat().getPixelCount();
            System.out.println("MOvieDetailsActivity2 ==> bitrate ==> " + bitrate);
            System.out.println("MOvieDetailsActivity2 ==> trackKey ==> " + trackKey.getTrackFormat());
            long getInBytes = (bitrate * downloadItemMovie.getMovieDuration3()) / 8;
            System.out.println("MOvieDetailsActivity2 ==> trackKey ==> " + getInBytes);

            String getInMb = AppUtil.formatFileSize(bitrate);
            String[] videoResoultionDashSize = new String[trackKeys.size()];
            videoResoultionDashSize[i] = " " + trackKey.getTrackFormat().height + " x " + trackKey.getTrackFormat().width;
            optionsToDownload.add(i, videoResoultionDashSize[i]);
        }

        // Initialize a new array adapter instance
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                MovieDetailActivity1.this, // Context
                android.R.layout.select_dialog_singlechoice, // Layout
                optionsToDownload // List
        );

        TrackKey trackKey = trackKeys.get(0);
        qualityParams = ((DefaultTrackSelector) trackSelector).getParameters().buildUpon()
                .setMaxVideoSize(trackKey.getTrackFormat().width, trackKey.getTrackFormat().height)
                .setMaxVideoBitrate(trackKey.getTrackFormat().bitrate)
                .build();

        builder.setSingleChoiceItems(arrayAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TrackKey trackKey = trackKeys.get(i);

                qualityParams = ((DefaultTrackSelector) trackSelector).getParameters().buildUpon()
                        .setMaxVideoSize(trackKey.getTrackFormat().width, trackKey.getTrackFormat().height)
                        .setMaxVideoBitrate(trackKey.getTrackFormat().bitrate)
                        .build();

            }
        });
        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                offlineDatabaseHelper.addContacts(itemMovie);
                for (int periodIndex = 0; periodIndex < helper.getPeriodCount(); periodIndex++) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = helper.getMappedTrackInfo(/* periodIndex= */ periodIndex);
                    helper.clearTrackSelections(periodIndex);
                    for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
                        helper.addTrackSelection(
                                periodIndex,
                                qualityParams);
                    }
                }


                DownloadRequest downloadRequest = helper.getDownloadRequest(Util.getUtf8Bytes(itemMovie.getMovieUrl()));
                if (downloadRequest.streamKeys.isEmpty()) {
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

    @SuppressWarnings("unchecked")
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory() {
        return ((MyApplication) getApplication()).buildDataSourceFactory();
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.

        startPosition = savedInstanceState.getInt(KEY_POSITION);
        trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
        startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
        startWindow = savedInstanceState.getInt(KEY_WINDOW);
        savedInstanceState.getString("");

    }


    @Override
    public void onDownloadsChanged(Download download) {
        switch (download.state) {
            case STATE_QUEUED:

                break;

            case STATE_STOPPED:


                break;
            case STATE_DOWNLOADING:


                Log.d("EXO DOWNLOADING ", +download.getBytesDownloaded() + " " + download.contentLength);
                Log.d("EXO  DOWNLOADING ", "" + download.getPercentDownloaded());


                break;
            case STATE_COMPLETED:

                setCommonDownloadButton(ExoDownloadState.DOWNLOAD_COMPLETED);

                Log.d("EXO COMPLETED ", +download.getBytesDownloaded() + " " + download.contentLength);
                Log.d("EXO  COMPLETED ", "" + download.getPercentDownloaded());


                if (download.request.uri.toString().equals(itemMovie.getMovieUrl())) {

                    if (download.getPercentDownloaded() != -1) {
                    }
                }

                break;

            case STATE_FAILED:


                break;

            case STATE_REMOVING:


                break;

            case STATE_RESTARTING:

                break;

        }

    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }


    public void setCommonDownloadButton(ExoDownloadState exoDownloadState) {
        switch (exoDownloadState) {
            case DOWNLOAD_START:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                btnDownloadMovie.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));

                break;

            case DOWNLOAD_PAUSE:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                btnDownloadMovie.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                break;

            case DOWNLOAD_RESUME:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                btnDownloadMovie.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                break;

            case DOWNLOAD_RETRY:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                btnDownloadMovie.setImageDrawable(getResources().getDrawable(R.drawable.ic_retry));

                break;

            case DOWNLOAD_COMPLETED:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                btnDownloadMovie.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));

                break;

            case DOWNLOAD_QUEUE:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                btnDownloadMovie.setImageDrawable(getResources().getDrawable(R.drawable.ic_queue));

                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (NetworkUtils.isConnected(MovieDetailActivity1.this)) {
                getDetails();
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        }
    }

    private void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        System.out.println("JSON OBJect ==> " + jsObj);
        jsObj.addProperty("movie_id", Id);
        jsObj.addProperty("view_duration", "0");
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        System.out.println("data = " + jsObj + "movie_id ==> " + Id + "user_id ==> " + myApplication.getUserId());
        client.post(Constant.MOVIE_DETAILS_URL1, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                llParentContainer.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                mProgressBar.setVisibility(View.GONE);
                llParentContainer.setVisibility(View.VISIBLE);
                SetCount();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    System.out.println("MovieDetailActivity1 ==> mainJSON ==> " + mainJson);
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
                            itemMovie.setMovieSubtitle(objJson.getString("subtitle_url"));

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

                            if (TextUtils.isEmpty(objJson.optString(Constant.TRAILER_URL)))
                                layoutTrailer.setVisibility(View.GONE);
                            else {
                                layoutTrailer.setVisibility(View.VISIBLE);
                                layoutTrailer.setOnClickListener(v -> {
                                    if (isMovie) {
                                        ExoVideoPlayerFragment exoVideoPlayerFragment = ExoVideoPlayerFragment.newInstance(objJson.optString(Constant.MOVIE_URL), false);
                                        exoVideoPlayerFragment.setItemMovie(itemMovie);
                                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, exoVideoPlayerFragment).commitAllowingStateLoss();
                                        Remember.putString(Constant.MOVIE_FROM, "movie");
                                        try {
                                            textTitle.setText(objJson.getString(Constant.MOVIE_TITLE));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        txtMovie.setText(getString(R.string.trailer));
                                        isMovie = false;
                                    } else {
                                        ExoVideoPlayerFragment exoVideoPlayerFragment = ExoVideoPlayerFragment.newInstance(objJson.optString(Constant.TRAILER_URL), true);
                                        exoVideoPlayerFragment.setItemMovie(itemMovie);
                                        fragmentManager.beginTransaction().replace(R.id.frame_layout_main, exoVideoPlayerFragment).commitAllowingStateLoss();
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
                                Picasso.with(MovieDetailActivity1.this).load(objJson.optString(Constant.MOVIE_IMAGE)).into(imgTrailer);
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
                        llParentContainer.setVisibility(View.GONE);
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }

                    JSONArray arrayCast = objJson.getJSONArray("movie_cast");
                    if (arrayCast.length() > 0) {
                        ArrayList<CastModel> castModelArrayList = new ArrayList<>();
                        for (int i = 0; i < arrayCast.length(); i++) {
                            JSONObject jsonData = arrayCast.optJSONObject(i);
                            castModelArrayList.add(new CastModel(jsonData.optString("image"), jsonData.optString("name"), jsonData.optString("type")));
                        }
                        recyclerCast.setAdapter(new MovieStarCastAdapter(MovieDetailActivity1.this, castModelArrayList, false));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                mProgressBar.setVisibility(View.GONE);
                llParentContainer.setVisibility(View.GONE);
                lyt_not_found.setVisibility(View.VISIBLE);
            }
        });
    }


    private void displayData() {
        setTitle("");
        textTitle.setText(itemMovie.getMovieName());
        textDate.setText(itemMovie.getMovieDate());

        textLanguage.setText(itemMovie.getMovieLanguage());
        textGenre.setText(strGenre.toString());
        strMovieURL = itemMovie.getMovieUrl();

        System.out.println(" DisplayData ==> Movie_URL ==> " + strMovieURL);
        System.out.println(" DisplayData ==> Movie_ID ==> " + itemMovie.getMovieId());


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

        initPlayer();


        if (!mListItemRelated.isEmpty()) {
            homeMovieAdapter = new HomeMovieVideoListAdapter(MovieDetailActivity1.this, mListItemRelated, false);
            rvRelated.setAdapter(homeMovieAdapter);

            homeMovieAdapter.setOnItemClickListener(position -> {
                String movieId = mListItemRelated.get(position).getMovieId();
                Intent intent = new Intent(MovieDetailActivity1.this, MovieDetailActivity1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Id", movieId);
                startActivity(intent);
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }
        runnableCode = new Runnable() {
            @Override
            public void run() {

                observerVideoStatus();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnableCode);
    }

    @Override
    public void onPositiveClick(int from) {

    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    @Override
    public void onBackPressed() {
        if (isFullScreen) {
            Events.FullScreen fullScreen = new Events.FullScreen();
            fullScreen.setFullScreen(false);

            GlobalBus.getBus().post(fullScreen);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            FragmentManager fm = getSupportFragmentManager();
            ExoVideoPlayerFragment fragment = (ExoVideoPlayerFragment) fm.findFragmentById(R.id.frame_layout_main);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            assert fragment != null;
            fragment.fullScreen();
        } else {
            Intent intent = new Intent(MovieDetailActivity1.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void gotoFullScreen() {
        nestedScrollView.setVisibility(View.GONE);
        mAdViewLayout.setVisibility(View.GONE);
        frameLayoutMain.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private void gotoPortraitScreen() {
        nestedScrollView.setVisibility(View.VISIBLE);
        mAdViewLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        frameLayoutMain.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
    }
}


