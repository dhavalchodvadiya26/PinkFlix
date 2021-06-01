package com.example.fragment;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.Events;
import com.example.util.GlobalBus;
import com.example.util.Remember;
import com.example.util.TrackSelectionDialog;
import com.example.videostreamingapp.MovieDetailsActivity2;
import com.example.videostreamingapp.R;
import com.example.videostreamingapp.ShowDetailsActivity;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;


public class ExoTVPlayerFragment extends Fragment {
    public static final String ABR_ALGORITHM_EXTRA = "abr_algorithm";
    public static final String ABR_ALGORITHM_DEFAULT = "default";
    public static final String ABR_ALGORITHM_RANDOM = "random";
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
    static ItemEpisode myItemEpisode;
    private String url;
    private boolean isTrailer;
    private SimpleExoPlayerView playerView;
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
    private boolean isShowingTrackSelectionDialog;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private PopupMenu popupMenu;
    private long currentVideoPosition = 0;
    private int checkdItem = 2;
    private String time;
    private ImageView btn_play, btn_forward, btn_backward;
    private ImageView btn_pause;
    private ImageView imgMoviePoster;
    private ImageView imgRental;

    public static ExoTVPlayerFragment newInstance(String url, boolean isTrailer,Long duration) {
        ExoTVPlayerFragment f = new ExoTVPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, url);
        args.putBoolean(trailer, isTrailer);
        args.putLong(currentPosition, duration);
        f.setArguments(args);
        return f;
    }

    public static ExoTVPlayerFragment newInstance(String url, boolean isTrailer) {
        ExoTVPlayerFragment f = new ExoTVPlayerFragment();
        Bundle args = new Bundle();
        args.putString(streamUrl, url);
        args.putBoolean(trailer, isTrailer);
        f.setArguments(args);
        return f;
    }

    private int seconds = 0;

    // Is the stopwatch running?
    private boolean running;

    private boolean wasRunning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exo_live_player, container, false);

        GlobalBus.getBus().register(this);

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
                    new DefaultTrackSelector.ParametersBuilder(getActivity());
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
        btn_backward = rootView.findViewById(R.id.btn_backward);
        btn_pause = rootView.findViewById(R.id.btn_pause);
        imgMoviePoster = rootView.findViewById(R.id.imgMoviePoster);
        imgRental = rootView.findViewById(R.id.imgRental);

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
        trackSelector = new DefaultTrackSelector(getActivity(), trackSelectionFactory);
        trackSelector.setParameters(trackSelectorParameters);

        RenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity());
        playerView = rootView.findViewById(R.id.exoPlayerView);
        player = new SimpleExoPlayer.Builder(getActivity(), renderersFactory).setTrackSelector(trackSelector).build();

        playerView.hideController();

        btn_forward.setVisibility(View.GONE);
        btn_backward.setVisibility(View.GONE);
        ibTrackChange.setVisibility(View.GONE);

        if (isTrailer) {
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            setPlayerWithUri();
        }

        btn_play.setOnClickListener(v -> {
            btn_play.setVisibility(View.GONE);
            btn_pause.setVisibility(View.VISIBLE);
            setPlayerWithUri();
        });

        btn_pause.setOnClickListener(v -> {
            btn_play.setVisibility(View.VISIBLE);
            btn_pause.setVisibility(View.GONE);
            currentVideoPosition = player.getCurrentPosition();
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        });

        ibTrackChange.setOnClickListener(view -> {
            CharSequence[] items = new String[2];
            items[0] = "Video";
            items[1] = "Language";

            CharSequence[] video = new String[1];
            video[0] = "1080p";

            CharSequence[] audio = new String[1];
            audio[0] = "hindi";

            BottomSheet.Builder builder = new BottomSheet.Builder(getActivity());
            BottomSheet.Builder builder1 = new BottomSheet.Builder(getActivity());

            builder.setTitle("Select")
                    .setTitleTextColor(getResources().getColor(R.color.colorAccent))
                    .setDarkTheme(true)
                    .setItems(items, (dialog, which) -> {
                        dialog.dismiss();
                        if (which == 0)
                            builder1.setTitle("Video")
                                    .setTitleTextColor(getResources().getColor(R.color.colorAccent))
                                    .setDarkTheme(true)
                                    .setItems(video, (dialog1, which1) -> dialog1.dismiss()).show();
                        else
                            builder1.setTitle("Language")
                                    .setTitleTextColor(getResources().getColor(R.color.colorAccent))
                                    .setDarkTheme(true)
                                    .setItems(audio, (dialog1, which1) -> dialog1.dismiss()).show();
                    }).show();


        });

        playerView.setControllerVisibilityListener(i -> {
            if (i == 0) {
                llControlRoot.setVisibility(View.VISIBLE);
                btn_backward.setVisibility(View.GONE);
                btn_forward.setVisibility(View.GONE);
                imgFull.setVisibility(View.VISIBLE);
                ibTrackChange.setVisibility(View.VISIBLE);
                if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                    ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(true);
                else
                    ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(true);

            } else {
                if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
                    ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
                else
                    ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

                llControlRoot.setVisibility(View.GONE);
                btn_backward.setVisibility(View.GONE);
                btn_forward.setVisibility(View.GONE);
                imgFull.setVisibility(View.GONE);
                ibTrackChange.setVisibility(View.GONE);
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
                System.out.println("Player Error ==> "+error);
                player.stop();
                if (myItemMovie != null) {
                    btnTryAgain.setVisibility(View.VISIBLE);
                    txtPremium.setVisibility(View.VISIBLE);
                    if (myItemMovie.getMovieAccess().equalsIgnoreCase("rental"))
                        txtPremium.setText("To watch this Movie , you have to rent it. Click here to Rent Movie.");
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
                isFullScr = false;
                Events.FullScreen fullScreen = new Events.FullScreen();
                fullScreen.setFullScreen(false);
                GlobalBus.getBus().post(fullScreen);
            } else {
                isFullScr = true;
                Events.FullScreen fullScreen = new Events.FullScreen();
                fullScreen.setFullScreen(true);
                GlobalBus.getBus().post(fullScreen);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });

        btnTryAgain.setOnClickListener(v ->
        {
            btnTryAgain.setVisibility(View.GONE);
            txtPremium.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            retryLoad();
        });
        playTv();
        return rootView;
    }

    private void playTv() {
        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        setPlayerWithUri();
    }

    private void setPlayerWithUri() {
        imgMoviePoster.setVisibility(View.GONE);
        playerView.setPlayer(player);
        playerView.setUseController(true);
        playerView.requestFocus();
        Uri uri = Uri.parse(url);
        btn_play.setVisibility(View.GONE);
        btn_pause.setVisibility(View.VISIBLE);
        if (Remember.getString(Constant.MOVIE_FROM, "").equalsIgnoreCase("movie"))
            ((MovieDetailsActivity2) Objects.requireNonNull(getActivity())).hideShowBackButton(false);
        else
            ((ShowDetailsActivity) Objects.requireNonNull(getActivity())).hideShowBackButton(false);

        MediaSource mediaSource = buildMediaSource(uri, null);
        player.prepare(mediaSource);
        currentVideoPosition=getArguments().getLong(currentPosition);
        player.seekTo(currentVideoPosition);

        player.setPlayWhenReady(true);

        playerView.showController();
        runTimer();
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
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
        }
    }

    public void setItemMovie(ItemMovie itemMovie) {
        myItemMovie = itemMovie;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (myItemMovie != null)
            watchHours();
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

                time
                        = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);



                if (running) {
                    seconds++;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    private void watchHours() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("movie_id", myItemMovie.getMovieId());
        params.put("view_duration", time);

        client.post(Constant.WATCH_HOUR, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

        });
    }
}
