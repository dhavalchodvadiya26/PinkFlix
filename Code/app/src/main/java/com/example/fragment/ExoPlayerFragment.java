package com.example.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.item.ItemEpisode;
import com.example.item.ItemMovie;
import com.example.item.ItemShow;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.OfflineDatabaseHelper;
import com.example.util.PrettyDialog;
import com.example.util.Remember;
import com.example.util.TrackSelectionDialog;
import com.example.videostreamingapp.MovieDetailsActivity2;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.example.videostreamingapp.SignInActivity;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


public class ExoPlayerFragment extends Fragment {
    private static final String TAG = "StreamPlayerActivity";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String streamUrl = "streamUrl";
    private static final String trailer = "trailer";
    private static final String currentPosition = "currentPosition";
    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";
    static ItemMovie myItemMovie;
    static ItemShow myItemShow;
    static ItemEpisode myItemEpisode;
    private static CountDownTimer countDownTimer;
    ImageButton ibLanguageChange;
    private String url;
    private String strTime;
    private boolean isTrailer;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private DataSource.Factory mediaDataSourceFactory;
    private ProgressBar progressBar;
    private ImageView imgFull;
    private boolean isFullScr = false;
    private Button btnTryAgain;
    private TextView txtPremium;
    private LinearLayout llControlRoot;
    private ImageView ibTrackChange;
    private Handler handler;
    private final boolean isPlayerView = false;
    private ImaAdsLoader adsLoader;

    private final Runnable runnable = () -> {
        if (llControlRoot.getVisibility() == View.VISIBLE) {
            llControlRoot.setVisibility(View.GONE);
            ibTrackChange.setVisibility(View.GONE);
            imgFull.setVisibility(View.GONE);
        }
    };
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private PopupMenu popupMenu;
    private long currentVideoPosition = 0;
    private int checkdItem = 2;
    private int checkdItem2 = 1;
    private String time;
    private ImageView btn_play, btn_forward, btn_backward, btn_back;
    private ImageView btn_pause;
    private ImageView imgMoviePoster;
    private ImageView imgRental;
    private ImageView imgLock;
    private SubtitleView txtMovieSubTitle;
    private boolean lock = true;
    private TextView txtDuration;
    private TextView txtMovieDuration, txtMovieTitle, txtExpire, txtExpireduration;
    private MyApplication myApplication;
    OfflineDatabaseHelper offlineDatabaseHelper;

    public static ExoPlayerFragment newInstance(String url, boolean isTrailer) {
        ExoPlayerFragment f = new ExoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, url);
        args.putBoolean(trailer, isTrailer);
        f.setArguments(args);
        return f;
    }

    public static ExoPlayerFragment newInstance(String url, boolean isTrailer, Long duration) {
        ExoPlayerFragment f = new ExoPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, url);
        args.putBoolean(trailer, isTrailer);
        args.putLong(currentPosition, duration);
        f.setArguments(args);
        return f;
    }

    private int seconds = 0;


    private boolean running;

    private boolean wasRunning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        offlineDatabaseHelper = new OfflineDatabaseHelper(getActivity().getApplicationContext());

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);


        View rootView = inflater.inflate(R.layout.fragment_exo_player, container, false);

        adsLoader = new ImaAdsLoader(getActivity(), Uri.parse(String.valueOf(R.string.ad_tag_url)));

        GlobalBus.getBus().register(this);
        myApplication = MyApplication.getInstance();
        if (getArguments() != null) {
            url = getArguments().getString(streamUrl);
            isTrailer = getArguments().getBoolean(trailer);
        }
        handler = new Handler();


        if (savedInstanceState != null) {
            trackSelectorParameters = savedInstanceState.getParcelable(KEY_TRACK_SELECTOR_PARAMETERS);
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
            seconds
                    = savedInstanceState
                    .getInt("seconds");
            running
                    = savedInstanceState
                    .getBoolean("running");
            wasRunning
                    = savedInstanceState
                    .getBoolean("wasRunning");
        } else {
            DefaultTrackSelector.ParametersBuilder builder =
                    new DefaultTrackSelector.ParametersBuilder(Objects.requireNonNull(getActivity()));


            trackSelectorParameters = builder.build();

            clearStartPosition();
        }

        txtPremium = rootView.findViewById(R.id.txtPremium);
        progressBar = rootView.findViewById(R.id.progressBar);
        imgFull = rootView.findViewById(R.id.img_full_scr);
        btnTryAgain = rootView.findViewById(R.id.btn_try_again);
        llControlRoot = rootView.findViewById(R.id.controls_root);
        ibTrackChange = rootView.findViewById(R.id.ib_track_change);
        btn_play = rootView.findViewById(R.id.btn_play);
        btn_forward = rootView.findViewById(R.id.btn_forward);
        btn_back = rootView.findViewById(R.id.imgBack);
        btn_backward = rootView.findViewById(R.id.btn_backward);
        btn_pause = rootView.findViewById(R.id.btn_pause);
        imgMoviePoster = rootView.findViewById(R.id.imgMoviePoster);
        imgRental = rootView.findViewById(R.id.imgRental);
        txtDuration = rootView.findViewById(R.id.txtDuration);
        txtMovieDuration = rootView.findViewById(R.id.txtMovieDuration);
        txtExpire = rootView.findViewById(R.id.txtExpire);
        txtExpireduration = rootView.findViewById(R.id.txtExpireduration);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(600);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txtExpireduration.startAnimation(anim);


        txtMovieTitle = rootView.findViewById(R.id.txtMovieTitle);
        txtMovieSubTitle = rootView.findViewById(R.id.exo_subtitles);
        imgLock = rootView.findViewById(R.id.imgLock);
        imgLock.setOnClickListener(v -> {
            if (lock) {
                lock = false;
                imgLock.setImageResource(R.drawable.ic_lock);
                visibleIcon(View.GONE);
            } else {
                lock = true;
                imgLock.setImageResource(R.drawable.ic_unlock);
                visibleIcon(View.VISIBLE);
            }
        });
        imgMoviePoster.setVisibility(View.VISIBLE);
        if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
            Picasso.with(getActivity()).load(myItemMovie.getMovieImage()).into(imgMoviePoster);
        else
            Picasso.with(getActivity()).load(myItemEpisode.getEpisodeImage()).into(imgMoviePoster);

        btn_forward.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() + 10000));
        btn_backward.setOnClickListener(v -> player.seekTo(player.getCurrentPosition() - 10000));


        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (myItemMovie != null)
            makePopUpMenu();

        TrackSelection.Factory trackSelectionFactory = null;
        trackSelectionFactory = new RandomTrackSelection.Factory();


        trackSelector = new DefaultTrackSelector(Objects.requireNonNull(getActivity()), trackSelectionFactory);
        trackSelector.setParameters(trackSelectorParameters);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();


        player = ExoPlayerFactory.newSimpleInstance(getActivity(), renderersFactory, trackSelector, loadControl);
        playerView = rootView.findViewById(R.id.exoPlayerView);
        player = new SimpleExoPlayer.Builder(getActivity(), renderersFactory).setTrackSelector(trackSelector).build();

        player.setPlayWhenReady(true);
        playerView.hideController();

        btn_forward.setVisibility(View.GONE);
        btn_backward.setVisibility(View.GONE);
        ibTrackChange.setVisibility(View.GONE);

        if (isTrailer) {
            btn_play.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
            setPlayerWithTrailerUri();
        }

        btn_play.setOnClickListener(v ->
        {
            if (myApplication.getIsLogin()) {
                if (player.getCurrentPosition()>0) {
                    onResume();
                }else{
                    setPlayerWithUri();
                }
            } else {
                if (!isTrailer) {
                    PrettyDialog pDialog = new PrettyDialog(getActivity());
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
                                        startActivity(new Intent(getActivity(), SignInActivity.class));
                                    }).addButton(
                            getString(android.R.string.no),
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_green,
                            pDialog::dismiss)
                            .show();
                } else
                    setPlayerWithUri();
            }

        });

        btn_pause.setOnClickListener(v -> pauseVideo());

        ibTrackChange.setOnClickListener(view -> {

            openBottomSheet();


        });

        playerView.setControllerVisibilityListener(i -> {
            if (i == 0) {
                if (lock) {
                    visibleIcon(View.VISIBLE);
                }
                if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                    ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(true);
                else
                    ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(true);

            } else {
                if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                    ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
                else
                    ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

                visibleIcon(View.GONE);

            }
        });
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                Log.d(TAG, "onTimelineChanged: ");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG, "onTracksChanged: " + trackGroups.length);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged: " + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "onPlayerStateChanged: " + playWhenReady);
                if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
                    progressBar.setVisibility(View.GONE);
                }
                if (playWhenReady) {
                    running = true;
                } else {
                    wasRunning = running;
                    running = false;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError: ", error);
                player.stop();
                if (myItemMovie != null) {
                    btnTryAgain.setVisibility(View.VISIBLE);
                    txtPremium.setVisibility(View.VISIBLE);
                    if (myItemMovie.getMovieAccess().equalsIgnoreCase("rental"))
                        txtPremium.setText("given S.");
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d(TAG, "onPositionDiscontinuity: true");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        imgFull.setOnClickListener(v ->
        {
            if (isFullScr) {
                showNavigationBar();
            } else {
                hideNavigationBar();
            }
        });

        btnTryAgain.setOnClickListener(v ->

        {
            btnTryAgain.setVisibility(View.GONE);
            txtPremium.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            retryLoad();
        });

        if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie")) {
            txtDuration.setText(myItemMovie.getMovieDuration());
            txtMovieDuration.setText(myItemMovie.getMovieDuration());
        } else {
            txtDuration.setText(myItemEpisode.getEpisodeDuration());
            txtMovieDuration.setText(myItemEpisode.getEpisodeDuration());
        }


        if (myItemMovie != null) {
            strTime = myItemMovie.getStrTime();
            System.out.println("StringExpiryTime = " + strTime);
        }

        if (strTime != null) {

            int minute = 0;
            txtExpire.setVisibility(View.VISIBLE);
            if (strTime.contains(":")) {
                String[] strHourMinute = strTime.split(":");
                if (!TextUtils.isEmpty(strHourMinute[0])) {
                    minute = Integer.parseInt(strHourMinute[0]) * 60;
                    if (!TextUtils.isEmpty(strHourMinute[1]) && !strHourMinute[1].equalsIgnoreCase("00"))
                        minute = minute + Integer.parseInt(strHourMinute[1]);
                } else {
                    minute = Integer.parseInt(strHourMinute[0]) * 60;
                }
            }
            if (!TextUtils.isEmpty(strTime))
                startTimer(minute);
            else {
                txtExpire.setVisibility(View.GONE);
                txtExpireduration.setVisibility(View.GONE);

            }
        } else {
            return rootView;
        }


        return rootView;
    }

    private void pauseVideo() {
        btn_play.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
        currentVideoPosition = player.getCurrentPosition();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    private void hideNavigationBar() {
        isFullScr = true;
        Events.FullScreen fullScreen = new Events.FullScreen();
        fullScreen.setFullScreen(true);
        GlobalBus.getBus().post(fullScreen);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(flags);


            final View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

    }


    public void openBottomSheet() {

        View dialogView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_subtitle, null);
        BottomSheetDialog dialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
        dialog.setContentView(dialogView);

        View parent = (View) dialogView.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        dialogView.measure(0, 0);
        bottomSheetBehavior.setPeekHeight(dialogView.getMeasuredHeight());

        TextView tv_video_setting = dialog.findViewById(R.id.tv_video_setting);
        LinearLayout ll_video_quality = dialog.findViewById(R.id.ll_video_quality);
        LinearLayout ll_video_subtitle = dialog.findViewById(R.id.ll_video_subtitle);


        ll_video_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myItemMovie != null) {
                    if (myItemMovie.getLstResolution().size() > 0)
                        showTrackDialog(true, myItemMovie.getLstResolution());
                    else
                        showTrackDialog(false, new ArrayList<>());
                } else if (myItemEpisode != null) {
                    if (myItemEpisode.getLstResolution().size() > 0)
                        showTrackDialogSeries(true, myItemEpisode.getLstResolution());
                    else
                        showTrackDialogSeries(false, new ArrayList<>());
                }
            }
        });


        ll_video_subtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


        dialog.show();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Set Subtitle: ");
        String[] items = {"ON", "OFF"};
        alertDialog.setSingleChoiceItems(items, checkdItem2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        showSubtitle(true);
                        checkdItem2 = which;
                        break;
                    case 1:
                        showSubtitle(false);
                        checkdItem2 = which;
                        break;
                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    public void showNavigationBar() {
        isFullScr = false;
        Events.FullScreen fullScreen = new Events.FullScreen();
        fullScreen.setFullScreen(false);
        GlobalBus.getBus().post(fullScreen);
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);


            final View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);


                    }
                }
            });
        }

    }

    private void visibleIcon(int visible) {
        llControlRoot.setVisibility(visible);
        btn_backward.setVisibility(visible);
        btn_forward.setVisibility(visible);
        ibTrackChange.setVisibility(visible);
        imgFull.setVisibility(visible);
    }

    private void setPlayerWithTrailerUri() {
        txtMovieDuration.setVisibility(View.GONE);
        txtDuration.setVisibility(View.GONE);
        txtMovieTitle.setVisibility(View.GONE);
        imgMoviePoster.setVisibility(View.GONE);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();
        Uri uri = Uri.parse(url);
        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        imgLock.setVisibility(View.VISIBLE);
        txtExpire.setVisibility(View.GONE);
        txtExpireduration.setVisibility(View.GONE);
        if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
            ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
        else
            ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

        Format format = Format.createTextSampleFormat(
                null,
                MimeTypes.APPLICATION_SUBRIP,
                Format.NO_VALUE,
                null);
        MediaSource mediaSource = buildMediaSource(uri, null);

        player.prepare(mediaSource);
//        player.prepare(createMediaSourceWithAds(uri,playerView));
        player.seekTo(currentVideoPosition);
        player.setPlayWhenReady(true);

        hideNavigationBar();
        playerView.showController();
        runTimer();
    }

    private void setPlayerWithUri() {
        currentVideoPosition = getArguments().getLong(currentPosition);

        if (myItemMovie.getMovieSubtitle().equals("null")) {
            txtMovieDuration.setVisibility(View.GONE);
            txtMovieTitle.setVisibility(View.GONE);
            imgMoviePoster.setVisibility(View.GONE);
            playerView.setPlayer(player);
            playerView.setUseController(true);
            playerView.requestFocus();
            Uri uri = Uri.parse(url);
            Uri uri2 = Uri.parse(myItemMovie.getMovieSubtitle());
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            imgLock.setVisibility(View.VISIBLE);
            txtExpire.setVisibility(View.GONE);
            txtExpireduration.setVisibility(View.GONE);
            if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
            else
                ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

            Format format = Format.createTextSampleFormat(
                    null,
                    MimeTypes.APPLICATION_SUBRIP,
                    Format.NO_VALUE,
                    null);
            MediaSource mediaSource = buildMediaSource(uri, null);
            player.prepare(mediaSource);
//            player.prepare(createMediaSourceWithAds(uri,playerView));
            player.seekTo(currentVideoPosition);
            player.setPlayWhenReady(true);
            hideNavigationBar();
            playerView.showController();
            runTimer();
        } else {
            txtMovieDuration.setVisibility(View.GONE);
            txtMovieTitle.setVisibility(View.GONE);
            imgMoviePoster.setVisibility(View.GONE);
            playerView.setPlayer(player);
            playerView.setUseController(true);
            playerView.requestFocus();
            Uri uri = Uri.parse(url);
            Uri uri2 = Uri.parse(myItemMovie.getMovieSubtitle());
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            imgLock.setVisibility(View.VISIBLE);
            txtExpire.setVisibility(View.GONE);
            txtExpireduration.setVisibility(View.GONE);
            if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
            else
                ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

            Format format = Format.createTextSampleFormat(
                    null,
                    MimeTypes.APPLICATION_SUBRIP,
                    Format.NO_VALUE,
                    null);
            System.out.println("SetPlayerWithURI ==> Movie_Subtitle ==> " + myItemMovie.getMovieSubtitle());
            MediaSource mediaSource = buildMediaSource(uri, null);
            MediaSource subtitleSourceEng = new SingleSampleMediaSource(Uri.parse(myItemMovie.getMovieSubtitle()),
                    mediaDataSourceFactory, format, C.TIME_UNSET);
            MergingMediaSource mergedSource = new MergingMediaSource(mediaSource, subtitleSourceEng);
            player.prepare(mediaSource);
//            player.prepare(createMediaSourceWithAds(uri,playerView));
            player.seekTo(currentVideoPosition);
            player.setPlayWhenReady(true);
            showSubtitle(false);

            hideNavigationBar();
            playerView.showController();
            runTimer();
        }
    }

    private void setPlayerWithUri2(long position) {

        if (myItemMovie.getMovieSubtitle().equals("null")) {
            txtMovieDuration.setVisibility(View.GONE);
            txtMovieTitle.setVisibility(View.GONE);
            imgMoviePoster.setVisibility(View.GONE);
            playerView.setPlayer(player);
            playerView.setUseController(true);
            playerView.requestFocus();
            Uri uri = Uri.parse(url);
            Uri uri2 = Uri.parse(myItemMovie.getMovieSubtitle());
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            imgLock.setVisibility(View.VISIBLE);
            txtExpire.setVisibility(View.GONE);
            txtExpireduration.setVisibility(View.GONE);
            if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
            else
                ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

            Format format = Format.createTextSampleFormat(
                    null,
                    MimeTypes.APPLICATION_SUBRIP,
                    Format.NO_VALUE,
                    null);
            MediaSource mediaSource = buildMediaSource(uri, null);
            player.prepare(mediaSource);
//            player.prepare(createMediaSourceWithAds(uri,playerView));
            player.seekTo(position);
            player.setPlayWhenReady(true);
            hideNavigationBar();
            playerView.showController();
            runTimer();
        } else {
            txtMovieDuration.setVisibility(View.GONE);
            txtMovieTitle.setVisibility(View.GONE);
            imgMoviePoster.setVisibility(View.GONE);
            playerView.setPlayer(player);
            playerView.setUseController(true);
            playerView.requestFocus();
            Uri uri = Uri.parse(url);
            Uri uri2 = Uri.parse(myItemMovie.getMovieSubtitle());
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            imgLock.setVisibility(View.VISIBLE);
            txtExpire.setVisibility(View.GONE);
            txtExpireduration.setVisibility(View.GONE);
            if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
            else
                ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

            Format format = Format.createTextSampleFormat(
                    null,
                    MimeTypes.APPLICATION_SUBRIP,
                    Format.NO_VALUE,
                    null);
            System.out.println("SetPlayerWithURI ==> Movie_Subtitle ==> " + myItemMovie.getMovieSubtitle());
            MediaSource mediaSource = buildMediaSource(uri, null);
            MediaSource subtitleSourceEng = new SingleSampleMediaSource(Uri.parse(myItemMovie.getMovieSubtitle()),
                    mediaDataSourceFactory, format, C.TIME_UNSET);
            MergingMediaSource mergedSource = new MergingMediaSource(mediaSource, subtitleSourceEng);
            player.prepare(mediaSource);
//            player.prepare(createMediaSourceWithAds(uri,playerView));
            player.seekTo(position);
            player.setPlayWhenReady(true);
            showSubtitle(false);

            hideNavigationBar();
            playerView.showController();
            runTimer();
        }
    }

    private AdsMediaSource createMediaSourceWithAds(Uri videoUrl, PlayerView exoPlayerView) {
        //Getting UserAgent
        String UserAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
        // Creating  Video Content Media Source
        MediaSource contentMediaSource = new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(UserAgent))
                .createMediaSource(videoUrl);
        //Creating  Ima Ads Loader
        ImaAdsLoader imaAdsLoader = new ImaAdsLoader(getContext(), Uri.parse(getResources().getString(R.string.ad_tag_url)));

        imaAdsLoader.setPlayer(player);
        //Creating Video Content Media Source With Ads
        AdsMediaSource contentMediaSourceWithAds = new AdsMediaSource(
                contentMediaSource,//Video Content Media Source
                new DefaultDataSourceFactory(getContext(), UserAgent),
                imaAdsLoader,
                exoPlayerView);//Overlay During Ads Playback
        //return media source with ads
        return contentMediaSourceWithAds;
    }

    public void showSubtitle(boolean show) {
        if (playerView != null && playerView.getSubtitleView() != null)
            if (show) {
                playerView.getSubtitleView().setBackgroundColor(Color.TRANSPARENT);
                playerView.getSubtitleView().setVisibility(View.VISIBLE);

            } else {
                playerView.getSubtitleView().setVisibility(View.GONE);

            }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private void makePopUpMenu() {
        if (myItemMovie.getListVideo() != null && myItemMovie.getListVideo().size() > 0) {
            popupMenu = new PopupMenu(getActivity(), ibTrackChange);
            for (int i = 0; i < myItemMovie.getListVideo().size(); i++) {
                popupMenu.getMenu().add(1, i, i, myItemMovie.getListVideo().get(i).getLanguage());
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                if (!url.equalsIgnoreCase(myItemMovie.getListVideo().get(item.getItemId()).getUrl())) {
                    url = myItemMovie.getListVideo().get(item.getItemId()).getUrl();
                    currentVideoPosition = player.getCurrentPosition();
                    setPlayerWithUri();
                }
                return true;
            });
        } else {
            Toast.makeText(getActivity(), "No languages found for a video", Toast.LENGTH_SHORT).show();
        }
    }


    private void retryLoad() {
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri, null);
        player.prepare(mediaSource);
//        player.prepare(createMediaSourceWithAds(uri,playerView));
        player.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource.Factory(new DefaultSsChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(new DefaultDashChunkSource.Factory(mediaDataSourceFactory), buildDataSourceFactory(false)).createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(getActivity(), bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter
                                                                      bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(getActivity(), "ExoPlayerDemo"), bandwidthMeter);
    }


    private void showTrackDialog(boolean show, ArrayList<ItemMovie.Resolution> resolution) {
        if (show) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builderSingle.setTitle("Video");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
            if (resolution.size() > 0) {
                for (int i = 0; i < resolution.size(); i++) {
                    arrayAdapter.add(resolution.get(i).getMovie_resolution());
                }
            }

            builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                checkdItem = which;
                url = resolution.get(which).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);

            });
            builderSingle.setSingleChoiceItems(arrayAdapter, checkdItem, (dialog, item) -> {
                checkdItem = item;
                url = resolution.get(item).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);
            });
            builderSingle.show();
        } else {
            if (!isShowingTrackSelectionDialog
                    && TrackSelectionDialog.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector, dismissedDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getChildFragmentManager(), null);
            }
        }

    }

    private void showTrackDialogSeries(boolean show, ArrayList<ItemEpisode.Resolution> resolution) {
        if (show) {
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
            builderSingle.setTitle("Video");
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_singlechoice);
            if (resolution.size() > 0) {
                for (int i = 0; i < resolution.size(); i++) {
                    arrayAdapter.add(resolution.get(i).getMovie_resolution());
                }
            }


            builderSingle.setNegativeButton("cancel", (dialog, which) -> dialog.dismiss());

            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                checkdItem = which;
                url = resolution.get(which).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);

            });
            builderSingle.setSingleChoiceItems(arrayAdapter, checkdItem, (dialog, item) -> {
                checkdItem = item;
                url = resolution.get(item).getMovie_url();
                dialog.dismiss();
                currentVideoPosition = player.getCurrentPosition();
                setPlayerWithUri2(currentVideoPosition);
                btnTryAgain.setVisibility(View.GONE);
                txtPremium.setVisibility(View.GONE);
            });

            builderSingle.show();
        } else {
            if (!isShowingTrackSelectionDialog
                    && TrackSelectionDialog.willHaveContent(trackSelector)) {
                isShowingTrackSelectionDialog = true;
                TrackSelectionDialog trackSelectionDialog =
                        TrackSelectionDialog.createForTrackSelector(
                                trackSelector, dismissedDialog -> isShowingTrackSelectionDialog = false);
                trackSelectionDialog.show(getChildFragmentManager(), null);
            }
        }

    }

    @Subscribe
    public void getFullScreen(Events.FullScreen fullScreen) {
        isFullScr = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            imgFull.setImageResource(R.drawable.ic_zoom);
        } else {
            imgFull.setImageResource(R.drawable.ic_zoom);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
            System.out.println("OnStop Called....");


        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
            watchHours();
            if (offlineDatabaseHelper.checkIfMyMovieExists(myItemMovie.getMovieId())) {
                offlineDatabaseHelper.updateMovieDuration(myItemMovie.getMovieId(), player.getCurrentPosition());
            } else {
                offlineDatabaseHelper.addDuration(myItemMovie.getMovieId(), player.getCurrentPosition());
            }
            offlineDatabaseHelper.addDuration2(myItemMovie.getMovieId(), player.getDuration());

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
            if (player.getCurrentPosition()>0){
                btn_pause.setVisibility(View.VISIBLE);
                btn_play.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            System.out.println("OnDestroyCalled");
            watchHours();
            if (offlineDatabaseHelper.checkIfMyMovieExists(myItemMovie.getMovieId())) {
                offlineDatabaseHelper.updateMovieDuration(myItemMovie.getMovieId(), player.getCurrentPosition());
            } else {
                offlineDatabaseHelper.addDuration(myItemMovie.getMovieId(), player.getCurrentPosition());
            }
            offlineDatabaseHelper.addDuration2(myItemMovie.getMovieId(), player.getDuration());
        }
    }

    public void setItemMovie(ItemMovie itemMovie) {
        myItemMovie = itemMovie;
    }

    public void setShowMovie(ItemEpisode itemEpisode) {
        myItemEpisode = itemEpisode;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState
                .putInt("seconds", seconds);
        outState
                .putBoolean("running", running);
        outState
                .putBoolean("wasRunning", wasRunning);
    }

    private void runTimer() {
        final Handler handler
                = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                time = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, secs);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void watchHours() {
        Log.d("TAG", "WatchHour Called");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("movie_id", myItemMovie.getMovieId());
        params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("duration", player.getCurrentPosition());
        System.out.println("ExoplayerFragment ==> params ==> " + params);

        client.post(Constant.CONTINUE_WATCHING_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                System.out.println("ExoplayerFragment ==> CONTINUE_WATCHING_API_URL ==> " + Constant.CONTINUE_WATCHING_URL);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    System.out.println("ExoplayerFragment ==> MainJSON ==> " + mainJson);
                    System.out.println("ExoplayerFragment ==> result ==> " + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("ExoplayerFragment ==> OnFailure ==> " + responseBody);

            }
        });
    }

    public void fullScreen() {
        pauseVideo();
    }

    private void startTimer(int noOfMinutes) {
        countDownTimer = new CountDownTimer(noOfMinutes * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {

                @SuppressLint("DefaultLocale") String hms = String.format("%02dh : %02dm", TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                txtExpireduration.setText(hms);
            }

            public void onFinish() {
                txtExpireduration.setText("TIME'S UP!!");
                countDownTimer = null;
                getActivity().finish();
            }
        }.start();
    }
}
