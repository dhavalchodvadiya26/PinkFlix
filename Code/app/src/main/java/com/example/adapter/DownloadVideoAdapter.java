package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.DownloadedVideosFragment;
import com.example.itemmodels.ItemMovie;
import com.example.util.AppUtil;
import com.example.util.MyDiffUtilCallback;
import com.example.util.OfflineDatabaseHelper;
import com.example.streamingapp.OfflineMoviePlayerActivity;
import com.example.streamingapp.R;
import com.google.android.exoplayer2.offline.Download;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 25/02/19, 5:48 PM.
 */
public class DownloadVideoAdapter extends RecyclerView.Adapter<DownloadVideoAdapter.MyViewHolder> { //implements Filterable
    List<Download> videosList;
    Context context;
    ItemMovie itemMovie;
    DownloadedVideosFragment downloadActivity;
    Download download;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlContainer;
        ImageView imageView;
        TextView tvDownloadVideoTitle;
        TextView txtDuration;
        TextView tvDownloadVideoPercentage;
        TextView tvDownloadVideoStatus;
        ImageView imgMenuOverFlow;
        ProgressBar progressBarPercentage;

        public MyViewHolder(View view) {
            super(view);
            rlContainer = view.findViewById(R.id.rl_container);
            imageView = view.findViewById(R.id.img_download_banner);
            tvDownloadVideoTitle = view.findViewById(R.id.tv_download_vid_title);
            tvDownloadVideoPercentage = view.findViewById(R.id.tv_downloaded_percentage);
            tvDownloadVideoStatus = view.findViewById(R.id.tv_downloaded_status);
            imgMenuOverFlow = view.findViewById(R.id.img_overflow);
            progressBarPercentage = view.findViewById(R.id.progress_horizontal_percentage);
            txtDuration = view.findViewById(R.id.txtDuration);
        }
    }

    public DownloadVideoAdapter(Context context, List<Download> videosList, DownloadedVideosFragment downloadActivity) {
        this.context = context;
        this.videosList = videosList;
        this.downloadActivity = downloadActivity;
    }

    @Override
    public DownloadVideoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_downloaded_video, parent, false);
        return new DownloadVideoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DownloadVideoAdapter.MyViewHolder holder, int position, List<Object> payloads) {

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Bundle o = (Bundle) payloads.get(0);
            for (String key : o.keySet()) {
                if (key.equals("percentDownloaded")) {

                    download = (Download) videosList.get(position);

                    System.out.println("OnBindViewHolder ==> Movie URL ==> " + download.request.id);
                    System.out.println("OnBindViewHolder ==> Movie Length ==> " + download.contentLength);

                    if (download.state == Download.STATE_COMPLETED) {
                        holder.progressBarPercentage.setVisibility(View.GONE);
                    } else {
                        holder.progressBarPercentage.setVisibility(View.VISIBLE);
                        holder.progressBarPercentage.setProgress((int) download.getPercentDownloaded());
                    }
                    String percentage = AppUtil.floatToPercentage(download.getPercentDownloaded());
//                    String downloadInMb = AppUtil.getProgressDisplayLine(download.getBytesDownloaded(), downloadRequest.data.length);


                    if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_COMPLETED) {
                        holder.tvDownloadVideoPercentage.setVisibility(View.VISIBLE);
                        holder.tvDownloadVideoPercentage.setText("Size: " + AppUtil.formatFileSize(download.getBytesDownloaded()) + " | Progress: " + percentage);
                    } else {
                        holder.tvDownloadVideoPercentage.setVisibility(View.INVISIBLE);
                    }
                    holder.tvDownloadVideoStatus.setText(AppUtil.downloadStatusFromId(download));
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(final DownloadVideoAdapter.MyViewHolder holder, final int position) {
        OfflineDatabaseHelper offlineDatabaseHelper1 = new OfflineDatabaseHelper(context);
        ;
        Download download = videosList.get(position);

        for (int i = 0; i < videosList.size(); i++) {
            itemMovie = offlineDatabaseHelper1.getMovieByURL(download.request.id);
            holder.tvDownloadVideoTitle.setText(itemMovie.getMovieName());
            Picasso.with(context).load(itemMovie.getMovieImage()).into(holder.imageView);
            holder.txtDuration.setText(itemMovie.getMovieDuration());
        }
        if (download.state == Download.STATE_COMPLETED) {
            holder.progressBarPercentage.setVisibility(View.GONE);
        } else {
            holder.progressBarPercentage.setVisibility(View.VISIBLE);
            holder.progressBarPercentage.setProgress((int) download.getPercentDownloaded());
        }
        String percentage = AppUtil.floatToPercentage(download.getPercentDownloaded());
        if (download.state == Download.STATE_DOWNLOADING || download.state == Download.STATE_COMPLETED) {
            holder.tvDownloadVideoPercentage.setVisibility(View.VISIBLE);
            holder.tvDownloadVideoPercentage.setText("Size: " + AppUtil.formatFileSize(download.getBytesDownloaded()) + " | Progress: " + percentage);
        } else {
            holder.tvDownloadVideoPercentage.setVisibility(View.INVISIBLE);
        }

        holder.tvDownloadVideoStatus.setText(AppUtil.downloadStatusFromId(download));
        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (download.state == Download.STATE_COMPLETED) {
                    Bundle bundle = new Bundle();
                    bundle.putString("video_url", download.request.id);
                    Intent intent = new Intent(context, OfflineMoviePlayerActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } else {
                    downloadActivity.openBottomSheet(download);
                }
            }
        });

        holder.imgMenuOverFlow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadActivity.openBottomSheet(download);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public Download getItem(int position) {
        return videosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItems(List<Download> lst) {
        this.videosList = lst;
    }

    public void onNewData(List<Download> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffUtilCallback(newData, videosList));
        diffResult.dispatchUpdatesTo(this);
        this.videosList.clear();
        this.videosList.addAll(newData);
    }
}