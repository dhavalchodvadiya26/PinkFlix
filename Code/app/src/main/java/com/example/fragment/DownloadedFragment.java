package com.example.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.adapter.DownloadedVideoListAdapter;
import com.example.item.ItemMovie;
import com.example.util.AppUtil;
import com.example.videostreamingapp.MyApplication;
import com.example.videostreamingapp.R;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadedFragment extends Fragment {

    RecyclerView recyclerView;
    private List<Download> downloadedVideoList;
    private DownloadedVideoListAdapter downloadedVideoAdapter;
    private Runnable runnableCode;
    private Handler handler;
    private ItemMovie myItemMovie;
    private TextView download_text;
    private LinearLayout lyt_not_found;

    public void setMyItemMovie(ItemMovie myItemMovie) {
        this.myItemMovie = myItemMovie;
    }

    public static DownloadedFragment newInstance(String url, boolean isTrailer) {
        DownloadedFragment f = new DownloadedFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_downloads, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view_downloaded_video);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        download_text = rootView.findViewById(R.id.download_text);
        download_text.setText("Downloads");

        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);

        loadVideos();

        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                List<Download> exoVideoList = new ArrayList<>();
                for (Map.Entry<Uri, Download> entry : MyApplication.getInstance().getDownloadTracker().downloads.entrySet()) {
                    Uri keyUri = entry.getKey();
                    Download download = entry.getValue();
                    exoVideoList.add(download);
                }
                if (exoVideoList.size() != 0) {
                    downloadedVideoAdapter.onNewData(exoVideoList);
                    handler.postDelayed(this, 1000);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    lyt_not_found.setVisibility(View.VISIBLE);
                }
            }
        };
        handler.post(runnableCode);
        return rootView;
    }


    private void loadVideos() {
        downloadedVideoList = new ArrayList<>();

        for (Map.Entry<Uri, Download> entry : MyApplication.getInstance().getDownloadTracker().downloads.entrySet()) {
            Download download = entry.getValue();
            downloadedVideoList.add(download);
        }

        if (downloadedVideoList.size() != 0) {
            lyt_not_found.setVisibility(View.GONE);
            downloadedVideoAdapter = new DownloadedVideoListAdapter(getActivity(), downloadedVideoList, DownloadedFragment.this);
            recyclerView.setAdapter(downloadedVideoAdapter);
            downloadedVideoAdapter.addItems(downloadedVideoList);
        } else {
            lyt_not_found.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void openBottomSheet(Download download) {

        ItemMovie videoModel = AppUtil.getVideoDetail(download.request.id);

        String statusTitle = videoModel.getMovieName();

        View dialogView = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(dialogView);

        TextView tvVideoTitle = dialog.findViewById(R.id.tv_video_title);
        LinearLayout llDownloadStart = dialog.findViewById(R.id.ll_start_download);
        LinearLayout llDownloadResume = dialog.findViewById(R.id.ll_resume_download);
        LinearLayout llDownloadPause = dialog.findViewById(R.id.ll_pause_download);
        LinearLayout llDownloadDelete = dialog.findViewById(R.id.ll_delete_download);

        llDownloadStart.setVisibility(View.GONE);


        if (download.state == Download.STATE_DOWNLOADING) {
            llDownloadPause.setVisibility(View.VISIBLE);
            llDownloadResume.setVisibility(View.GONE);

        } else if (download.state == Download.STATE_STOPPED) {
            llDownloadPause.setVisibility(View.GONE);
            llDownloadResume.setVisibility(View.VISIBLE);

        } else if (download.state == Download.STATE_QUEUED) {
            llDownloadStart.setVisibility(View.VISIBLE);
            llDownloadPause.setVisibility(View.GONE);
            llDownloadResume.setVisibility(View.GONE);
        } else {
            llDownloadStart.setVisibility(View.GONE);
            llDownloadPause.setVisibility(View.GONE);
            llDownloadResume.setVisibility(View.GONE);
        }

        tvVideoTitle.setText(statusTitle);
        llDownloadStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().getDownloadManager().addDownload(download.request);
                dialog.dismiss();
            }
        });
        llDownloadResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().getDownloadManager().addDownload(download.request, Download.STOP_REASON_NONE);

                dialog.dismiss();
            }
        });

        llDownloadPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().getDownloadManager().addDownload(download.request, Download.STATE_STOPPED);
                dialog.dismiss();
            }
        });

        llDownloadDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment myFragment = new DownloadedFragment();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.Container, myFragment).addToBackStack(null).commit();
                MyApplication.getInstance().getDownloadManager().removeDownload(download.request.id);
                if (downloadedVideoList.size() != 0) {
                    lyt_not_found.setVisibility(View.GONE);
                    downloadedVideoAdapter = new DownloadedVideoListAdapter(getActivity(), downloadedVideoList, DownloadedFragment.this);
                    recyclerView.setAdapter(downloadedVideoAdapter);
                    downloadedVideoAdapter.addItems(downloadedVideoList);
                } else {
                    Fragment myFragment1 = new DownloadedFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.Container, myFragment1).addToBackStack(null).commit();
                    lyt_not_found.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
