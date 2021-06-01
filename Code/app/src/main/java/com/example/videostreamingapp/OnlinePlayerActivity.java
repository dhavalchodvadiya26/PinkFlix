package com.example.videostreamingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.HomeMovieListAdapter;
import com.example.adapter.MovieCastAdapter;
import com.example.fragment.DownloadedFragment;
import com.example.item.CastModel;
import com.example.item.ItemMovie;
import com.example.util.API;
import com.example.util.AppUtil;
import com.example.util.Constant;
import com.example.util.DemoDownloadService;
import com.example.util.DownloadTracker;
import com.example.util.ExoDownloadState;
import com.example.util.GradientTextView;
import com.example.util.NetworkUtils;
import com.example.util.PrettyDialog;
import com.example.util.ReadMoreTextView;
import com.example.util.Remember;
import com.example.util.ScreenUtils;
import com.example.util.TrackKey;
import com.example.util.TrackSelectionDialog;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadHelper;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
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
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

import static android.net.Uri.parse;
import static com.google.android.exoplayer2.offline.Download.STATE_COMPLETED;
import static com.google.android.exoplayer2.offline.Download.STATE_DOWNLOADING;
import static com.google.android.exoplayer2.offline.Download.STATE_FAILED;
import static com.google.android.exoplayer2.offline.Download.STATE_QUEUED;
import static com.google.android.exoplayer2.offline.Download.STATE_REMOVING;
import static com.google.android.exoplayer2.offline.Download.STATE_RESTARTING;
import static com.google.android.exoplayer2.offline.Download.STATE_STOPPED;


public class OnlinePlayerActivity extends AppCompatActivity implements View.OnClickListener, PlaybackPreparer, PlayerControlView.VisibilityListener, DownloadTracker.Listener {

    private static final int playerHeight = 250;
    ProgressDialog pDialog;
    protected static final CookieManager DEFAULT_COOKIE_MANAGER;
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;
    private static final int UI_ANIMATION_DELAY = 300;
    // Saved instance state keys.
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
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    int tapCount = 1;
    RelativeLayout llParentContainer;
    LinearLayout lyt_not_found;
    private RecyclerView recyclerCast;
    boolean isPurchased = false;
    String rental_plan = "";
    private String strMovieURL,strMovieURL2, strTrailerURL;
    ItemMovie itemMovie;
    ArrayList<ItemMovie> mListItemRelated;
    private LinearLayout lytRate, layoutTrailer;
    private FragmentManager fragmentManager;
    private Button btnRental;
    public boolean isMovie;
    private TextView txtRentalTime, txtExpire;
    private GradientTextView txtMovie;
    GradientTextView textTitle;
    TextView textDate, textLanguage, textGenre, textRelViewAll, textRate;
    TextView txtDescription;
    HomeMovieListAdapter homeMovieAdapter;
    LinearLayout lytRelated;
    RecyclerView rvRelated;
    StringBuilder strGenre = new StringBuilder();

    Boolean isScreenLandscape = false;
    List<TrackKey> trackKeys = new ArrayList<>();
    List<String> optionsToDownload = new ArrayList<String>();
    //    TrackKey trackKeyDownload;
    DefaultTrackSelector.Parameters qualityParams;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private PlayerView playerView;
    private DataSource.Factory dataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private TrackGroupArray lastSeenTrackGroupArray;
    private TextView tvPlaybackSpeed, tvPlaybackSpeedSymbol;
    private boolean startAutoPlay;
    private int startWindow;
    // Fields used only for ad playback. The ads loader is loaded via reflection.
    private long startPosition;
    private AdsLoader adsLoader;
    private FrameLayout frameLayoutMain;
    private ImageView imgBwd;
    private ImageView exoPlay;
    private ImageView exoPause;
    private ImageView imgFwd, imgBackPlayer;
    private TextView tvPlayerCurrentTime;
    private DefaultTimeBar exoTimebar;
    private ProgressBar exoProgressbar;
    private TextView tvPlayerEndTime;
    private ImageView imgSetting;
    private ImageView imgFullScreenEnterExit;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private DownloadTracker downloadTracker;
    private DownloadManager downloadManager;
    private DownloadHelper myDownloadHelper;
    private LinearLayout llDownloadVideo;
    private ImageView imgDownloadState;
    private TextView tvDownloadState;
    private String videoId;
    private Runnable runnableCode;
    private Handler handler;

    private ImageView btnWatchList;
    private ImageView btnWatchListselect;
    MyApplication myApplication;
    private ImageView imgTrailer;
    ProgressBar mProgressBar;
    private boolean addedWatchList = false;
    private ImageView btnShare;
    private TextView txtShare;
    private TextView txtLike;
    private TextView txtDisLike;
    private ImageView btnLike;
    private ImageView btnDisLike;



    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        dataSourceFactory = buildDataSourceFactory();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        hideStatusBar();

        setContentView(R.layout.activity_online_player);
        Intent intent=getIntent();
        videoId = intent.getStringExtra("Id");
        System.out.println("OnlinePlayerActivity ==> videoId ==> " + videoId);
        myApplication = MyApplication.getInstance();
        itemMovie=new ItemMovie();
        getDetails();

        recyclerCast = findViewById(R.id.recyclerCast);
        recyclerCast.setHasFixedSize(true);
        recyclerCast.setLayoutManager(new LinearLayoutManager(OnlinePlayerActivity.this, LinearLayoutManager.HORIZONTAL, false));
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
        prepareView();


        runnableCode = new Runnable() {
            @Override
            public void run() {
                observerVideoStatus();
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnableCode);

    }


    private void observerVideoStatus() {
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
        tvPlaybackSpeed = findViewById(R.id.tv_play_back_speed);
        tvPlaybackSpeed.setOnClickListener(this);
        tvPlaybackSpeed.setText("" + tapCount);
        tvPlaybackSpeedSymbol = findViewById(R.id.tv_play_back_speed_symbol);
        tvPlaybackSpeedSymbol.setOnClickListener(this);
        imgBwd = findViewById(R.id.img_bwd);
        exoPlay = findViewById(R.id.exo_play);
        exoPause = findViewById(R.id.exo_pause);
        imgFwd = findViewById(R.id.img_fwd);
        tvPlayerCurrentTime = findViewById(R.id.tv_player_current_time);
        exoTimebar = findViewById(R.id.exo_progress);
        exoProgressbar = findViewById(R.id.loading_exoplayer);
        tvPlayerEndTime = findViewById(R.id.tv_player_end_time);
        imgSetting = findViewById(R.id.img_setting);
        imgFullScreenEnterExit = findViewById(R.id.img_full_screen_enter_exit);
        imgFullScreenEnterExit.setOnClickListener(this);
        imgBackPlayer = findViewById(R.id.img_back_player);
        playerView = findViewById(R.id.player_view);
        imgSetting.setOnClickListener(this);
        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        playerView.requestFocus();
        llParentContainer = (RelativeLayout) findViewById(R.id.ll_parent_container);
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        frameLayoutMain = findViewById(R.id.frame_layout_main);
        findViewById(R.id.img_back_player).setOnClickListener(this);
        llDownloadVideo = (LinearLayout) findViewById(R.id.ll_download_video);
        imgDownloadState = (ImageView) findViewById(R.id.img_download_state);
        tvDownloadState = (TextView) findViewById(R.id.tv_download_state);
        llDownloadVideo.setOnClickListener(this);
        btnWatchList = findViewById(R.id.btnWatchList2);
        btnWatchListselect = findViewById(R.id.btnWatchListselect2);
        btnWatchListselect.setOnClickListener(this);
        btnWatchList.setOnClickListener(this);
        imgTrailer = findViewById(R.id.imgTrailer);
        mProgressBar = findViewById(R.id.progressBar1);
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
        txtDescription = findViewById(R.id.txtDescription);
        rvRelated = findViewById(R.id.rv_related);
        rvRelated.setHasFixedSize(true);
        lytRelated = findViewById(R.id.lytRelated);
        rvRelated.setLayoutManager(new LinearLayoutManager(OnlinePlayerActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvRelated.setFocusable(false);
        rvRelated.setNestedScrollingEnabled(false);
        textRate = findViewById(R.id.txtIMDbRating);
        lytRate = findViewById(R.id.lytIMDB);
        mListItemRelated = new ArrayList<>();
        setProgress();
    }


    public void prepareView() {
        playerView.setLayoutParams(
                new PlayerView.LayoutParams(
                        // or ViewGroup.LayoutParams.WRAP_CONTENT
                        PlayerView.LayoutParams.MATCH_PARENT,
                        // or ViewGroup.LayoutParams.WRAP_CONTENT,
                        ScreenUtils.convertDIPToPixels(OnlinePlayerActivity.this, playerHeight)));


        frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));


    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        clearStartPosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        downloadTracker.addListener(this);


        if (Util.SDK_INT > 23) {
            setProgress();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            setProgress();

            if (playerView != null) {
                playerView.onResume();
            }
        }

        FullScreencall();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();


        }

    }

    @Override
    public void onStop() {
        super.onStop();
        downloadTracker.removeListener(this);
        handler.removeCallbacks(runnableCode);


        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    // OnClickListener methods
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        updateTrackSelectorParameters();
        updateStartPosition();
        outState.putParcelable(KEY_TRACK_SELECTOR_PARAMETERS, trackSelectorParameters);
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

// PlaybackControlView.PlaybackPreparer implementation

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
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

            case R.id.img_full_screen_enter_exit:
                Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
                int orientation = display.getOrientation();

                if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


                    playerView.setLayoutParams(
                            new PlayerView.LayoutParams(
                                    // or ViewGroup.LayoutParams.WRAP_CONTENT
                                    PlayerView.LayoutParams.MATCH_PARENT,
                                    // or ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ScreenUtils.convertDIPToPixels(OnlinePlayerActivity.this, playerHeight)));


                    frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_enter);
                    isScreenLandscape = false;
                    FullScreencall();

                    hide();
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    FullScreencall();

                    playerView.setLayoutParams(
                            new PlayerView.LayoutParams(
                                    // or ViewGroup.LayoutParams.WRAP_CONTENT
                                    PlayerView.LayoutParams.MATCH_PARENT,
                                    // or ViewGroup.LayoutParams.WRAP_CONTENT,
                                    PlayerView.LayoutParams.MATCH_PARENT));


                    frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_exit);
                    isScreenLandscape = true;
                    hide();

                }

                break;

            case R.id.tv_play_back_speed:
            case R.id.tv_play_back_speed_symbol:

                if (tvPlaybackSpeed.getText().equals("1")) {
                    tapCount++;
                    PlaybackParameters param = new PlaybackParameters(1.25f);
                    player.setPlaybackParameters(param);
                    tvPlaybackSpeed.setText("" + 1.25);
                } else if (tvPlaybackSpeed.getText().equals("1.25")) {
                    tapCount++;
                    PlaybackParameters param = new PlaybackParameters(1.5f);
                    player.setPlaybackParameters(param);
                    tvPlaybackSpeed.setText("" + 1.5);

                } else if (tvPlaybackSpeed.getText().equals("1.5")) {
                    tapCount++;
                    PlaybackParameters param = new PlaybackParameters(1.75f);
                    player.setPlaybackParameters(param);
                    tvPlaybackSpeed.setText("" + 1.75);
                } else if (tvPlaybackSpeed.getText().equals("1.75")) {
                    tapCount++;
                    PlaybackParameters param = new PlaybackParameters(2f);
                    player.setPlaybackParameters(param);
                    tvPlaybackSpeed.setText("" + 2);
                } else {
                    tapCount = 0;
                    player.setPlaybackParameters(null);
                    tvPlaybackSpeed.setText("" + 1);

                }

                break;

            case R.id.img_back_player:
                onBackPressed();
                break;
            case R.id.ll_download_video:
                ExoDownloadState exoDownloadState = (ExoDownloadState) llDownloadVideo.getTag();

                exoVideoDownloadDecision(exoDownloadState);

                break;
            case R.id.btnWatchList2:
                if (NetworkUtils.isConnected(OnlinePlayerActivity.this)) {
                    if (myApplication.getIsLogin())
                        addToWatchList();
                    else {
                        PrettyDialog pDialog = new PrettyDialog(OnlinePlayerActivity.this);
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
                                            startActivity(new Intent(OnlinePlayerActivity.this, SignInActivity.class));
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
                break;

            case R.id.btnWatchListselect2:
                if (NetworkUtils.isConnected(OnlinePlayerActivity.this)) {
                    if (myApplication.getIsLogin())
                        removeToWatchList();
                    else {
                        PrettyDialog pDialog = new PrettyDialog(OnlinePlayerActivity.this);
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
                                            startActivity(new Intent(OnlinePlayerActivity.this, SignInActivity.class));
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
                break;

            case R.id.btnShare1:
            case R.id.txtShare1:
                break;

            case R.id.btnLike1:
                if (NetworkUtils.isConnected(OnlinePlayerActivity.this)) {
                    if (myApplication.getIsLogin())
                        likeMovie();
                    else {
                        PrettyDialog pDialog = new PrettyDialog(OnlinePlayerActivity.this);
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
                                            startActivity(new Intent(OnlinePlayerActivity.this, SignInActivity.class));
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
                break;

            case R.id.btnDisLike1:
                if (NetworkUtils.isConnected(OnlinePlayerActivity.this)) {
                    if (myApplication.getIsLogin())
                        DislikeMovie();
                    else{
                        PrettyDialog pDialog = new PrettyDialog(OnlinePlayerActivity.this);
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
                                            startActivity(new Intent(OnlinePlayerActivity.this, SignInActivity.class));
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
                break;

        }

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
        jsObj.addProperty("movie_id", Remember.getString("movie_id", ""));
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

                        PrettyDialog pDialog = new PrettyDialog(OnlinePlayerActivity.this);
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

    private void removeToWatchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("movie_videos_id", Remember.getString("movie_id", ""));
        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
//        params.put("data", API.toBase64(jsObj.toString()));
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
                        PrettyDialog pDialog = new PrettyDialog(OnlinePlayerActivity.this);
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

    private void exoVideoDownloadDecision(ExoDownloadState exoDownloadState) {
        if (exoDownloadState == null || itemMovie.getMovieUrl().isEmpty()) {
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


    private void fetchDownloadOptions() {
        trackKeys.clear();

        if (pDialog == null || !pDialog.isShowing()) {
            pDialog = new ProgressDialog(OnlinePlayerActivity.this);
            pDialog.setTitle(null);
            pDialog.setCancelable(false);
            pDialog.setMessage("Preparing Download Options...");
            pDialog.show();
        }


        DownloadHelper downloadHelper = DownloadHelper.forHls(OnlinePlayerActivity.this, Uri.parse(itemMovie.getMovieUrl()), dataSourceFactory, new DefaultRenderersFactory(OnlinePlayerActivity.this));


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

        AlertDialog.Builder builder = new AlertDialog.Builder(OnlinePlayerActivity.this);
        builder.setTitle("Select Download Format");
        int checkedItem = 1;


        for (int i = 0; i < trackKeyss.size(); i++) {
            TrackKey trackKey = trackKeyss.get(i);
            long bitrate = trackKey.getTrackFormat().bitrate;
            long getInBytes = (bitrate * player.getDuration()) / 8;
            String getInMb = AppUtil.formatFileSize(getInBytes);
            String videoResoultionDashSize = " " + trackKey.getTrackFormat().height + "      (" + getInMb + ")";
            optionsToDownload.add(i, videoResoultionDashSize);
        }

        // Initialize a new array adapter instance
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                OnlinePlayerActivity.this, // Context
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

        //       downloadManager.addDownload(downloadRequestt);

        if (myDownloadRequest.uri.toString().isEmpty()) {
            Toast.makeText(this, "Try Again!!", Toast.LENGTH_SHORT).show();

            return;
        } else {

            downloadManager.addDownload(myDownloadRequest);

        }


    }

    @Override
    public void preparePlayback() {
        initializePlayer();
    }


    private void initializePlayer() {


        TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();

        //    DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        RenderersFactory renderersFactory = ((MyApplication) getApplication()).buildRenderersFactory(true);

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        trackSelector.setParameters(trackSelectorParameters);
        lastSeenTrackGroupArray = null;

        DefaultAllocator defaultAllocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);

        DefaultLoadControl defaultLoadControl = new DefaultLoadControl(defaultAllocator,
                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
                DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES,
                DefaultLoadControl.DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS
        );

        player = new SimpleExoPlayer.Builder(/* context= */ this, renderersFactory).setTrackSelector(trackSelector).setLoadControl(defaultLoadControl).build();
        player.addListener(new PlayerEventListener());
        player.setPlayWhenReady(startAutoPlay);
        player.addAnalyticsListener(new EventLogger(trackSelector));
        playerView.setPlayer(player);
        playerView.setPlaybackPreparer(this);

        if (myItemMovie!=null) {
            mediaSource = buildMediaSource(Uri.parse(myItemMovie.getMovieUrl()));
            if (player != null) {
                player.prepare(mediaSource, false, true);
            }
        }else {
            getDetails();
        }

        exoButtonPrepareDecision();

        updateButtonVisibilities();
        initBwd();
        initFwd();

    }

    private boolean shouldDownload(Format track) {
        return track.height != 240 && track.sampleMimeType.equalsIgnoreCase("video/avc");
    }

    private MediaSource buildMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
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


    /**
     * Returns a new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory() {
        return ((MyApplication) getApplication()).buildDataSourceFactory();
    }


    private void updateButtonVisibilities() {
        if (player == null) {
            return;
        }

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }

        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                int label;
                switch (player.getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:
                        label = R.string.exo_track_selection_title_audio;
                        break;
                    case C.TRACK_TYPE_VIDEO:
                        label = R.string.exo_track_selection_title_video;
                        break;
                    case C.TRACK_TYPE_TEXT:
                        label = R.string.exo_track_selection_title_text;
                        break;
                    default:
                        continue;
                }
            }
        }
    }


    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private void setProgress() {


        handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (player != null) {
                    tvPlayerCurrentTime.setText(stringForTime((int) player.getCurrentPosition()));
                    tvPlayerEndTime.setText(stringForTime((int) player.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void initBwd() {
        imgBwd.requestFocus();
        imgBwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.seekTo(player.getCurrentPosition() - 10000);
            }
        });
    }

    private void initFwd() {
        imgFwd.requestFocus();
        imgFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.seekTo(player.getCurrentPosition() + 10000);
            }
        });

    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public void FullScreencall() {


        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);
        }


    }

    public void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
        startPosition = savedInstanceState.getInt(KEY_POSITION);
        trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
        startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
        startWindow = savedInstanceState.getInt(KEY_WINDOW);
        savedInstanceState.getString("");

    }


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {

        if (isScreenLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            playerView.setLayoutParams(
                    new PlayerView.LayoutParams(
                            // or ViewGroup.LayoutParams.WRAP_CONTENT
                            PlayerView.LayoutParams.MATCH_PARENT,
                            // or ViewGroup.LayoutParams.WRAP_CONTENT,
                            ScreenUtils.convertDIPToPixels(OnlinePlayerActivity.this, playerHeight)));


            frameLayoutMain.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

            imgFullScreenEnterExit.setImageResource(R.drawable.exo_controls_fullscreen_enter);
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            isScreenLandscape = false;
            hide();


        } else {
            OnlinePlayerActivity.this.finish();
            clearStartPosition();
            player.release();
            player.stop();
            super.onBackPressed();
        }

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

    private void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
        if (adsLoader != null) {
            adsLoader.setPlayer(null);
        }
    }

    private void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }


    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case ExoPlayer.STATE_READY:
                    exoProgressbar.setVisibility(View.GONE);

                    break;
                case ExoPlayer.STATE_BUFFERING:

                    exoProgressbar.setVisibility(View.VISIBLE);
                    break;
            }
            updateButtonVisibilities();
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            if (player.getPlaybackError() != null) {
                // The user has performed a seek whilst in the error state. Update the resume position so
                // that if the user then retries, playback resumes from the position to which they seeked.
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer();
            } else {
                updateStartPosition();
                updateButtonVisibilities();
//                showControls();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            updateButtonVisibilities();
            if (trackGroups != lastSeenTrackGroupArray) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_video);
                    }
                    if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                            == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                        showToast(R.string.error_unsupported_audio);
                    }
                }
                lastSeenTrackGroupArray = trackGroups;
            }
        }
    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.codecInfo);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }


    public void setCommonDownloadButton(ExoDownloadState exoDownloadState) {
        switch (exoDownloadState) {
            case DOWNLOAD_START:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                imgDownloadState.setImageDrawable(getResources().getDrawable(R.drawable.ic_download));

                break;

            case DOWNLOAD_PAUSE:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                imgDownloadState.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause));
                break;

            case DOWNLOAD_RESUME:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                imgDownloadState.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                break;

            case DOWNLOAD_RETRY:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                imgDownloadState.setImageDrawable(getResources().getDrawable(R.drawable.ic_retry));

                break;

            case DOWNLOAD_COMPLETED:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                imgDownloadState.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));

                break;

            case DOWNLOAD_QUEUE:
                llDownloadVideo.setTag(exoDownloadState);
                tvDownloadState.setText(exoDownloadState.getValue());
                imgDownloadState.setImageDrawable(getResources().getDrawable(R.drawable.ic_queue));

                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (NetworkUtils.isConnected(OnlinePlayerActivity.this)) {
                getDetails();
            } else {
                showToast(getString(R.string.conne_msg1));
            }
        }
    }

    private void getDetails() {
        Intent intent=getIntent();
        videoId=intent.getStringExtra("Id");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("movie_id", videoId);
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.MOVIE_DETAILS_URL1, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
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
                                        DownloadedFragment downloadFragment= DownloadedFragment.newInstance(objJson.optString(Constant.TRAILER_URL), false);
                                        downloadFragment.setMyItemMovie(itemMovie);
                                        setItemMovie(itemMovie);
                                        Remember.putString(Constant.MOVIE_FROM, "movie");
                                        try {
                                            textTitle.setText(objJson.getString(Constant.MOVIE_TITLE));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        txtMovie.setText(getString(R.string.trailer));
                                        isMovie = false;
                                    } else {
                                        setItemMovie(itemMovie);
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
                                Picasso.with(OnlinePlayerActivity.this).load(objJson.optString(Constant.MOVIE_IMAGE)).into(imgTrailer);
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
                        recyclerCast.setAdapter(new MovieCastAdapter(OnlinePlayerActivity.this, castModelArrayList, false));
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

        if (!mListItemRelated.isEmpty()) {
            homeMovieAdapter = new HomeMovieListAdapter(OnlinePlayerActivity.this, mListItemRelated, false);
            rvRelated.setAdapter(homeMovieAdapter);

            homeMovieAdapter.setOnItemClickListener(position -> {
                String movieId = mListItemRelated.get(position).getMovieId();
                Intent intent = new Intent(OnlinePlayerActivity.this, MovieDetailsActivity2.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Id", movieId);
                startActivity(intent);
            });

        } else {
            lytRelated.setVisibility(View.GONE);
        }
    }

    public void setItemMovie(ItemMovie itemMovie) {
        myItemMovie = itemMovie;
    }
}