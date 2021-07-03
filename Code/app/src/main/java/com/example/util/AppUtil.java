package com.example.util;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.itemmodels.ItemMovie;
import com.example.streamingapp.MyApplication;
import com.google.android.exoplayer2.offline.Download;

import java.text.DecimalFormat;

/**
 * Created by Mayur Solanki (mayursolanki120@gmail.com) on 21/06/20, 10:49 PM.
 */
public class AppUtil {

    public static ItemMovie getVideoDetail(String videoUri){
        ItemMovie videoModel = null;
        for (ItemMovie videoModels : MyApplication.getInstance().videoModels) {
            videoModels.setMovieUrl(videoUri);
            System.out.println("AppUtil ==> getVideoDetail ==> video_URL = "+videoModels.getMovieUrl());
            System.out.println("AppUtil ==> getVideoDetail ==> video_URL = "+videoUri);
            if(videoModels.getMovieUrl().equalsIgnoreCase(videoUri)){
                videoModel = videoModels;
                return videoModel;
            }
        }
        return videoModel;
    }


    public static String createExoDownloadNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelId = "1017";

            CharSequence channelName = "Adaptive Exo Download";
            String channelDescription = "Adaptive Exoplayer video Download";
            int channelImportance = NotificationManager.IMPORTANCE_NONE;
            // Initializes NotificationChannel.
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            return null;
        }
    }

    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size/1024.0;
        double m = ((size/1024.0)/1024.0);
        double g = (((size/1024.0)/1024.0)/1024.0);
        double t = ((((size/1024.0)/1024.0)/1024.0)/1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( t>1 ) {
            hrSize = dec.format(t).concat(" TB");
        } else if ( g>1 ) {
            hrSize = dec.format(g).concat(" GB");
        } else if ( m>1 ) {
            hrSize = dec.format(m).concat(" MB");
        } else if ( k>1 ) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }



    public static String floatToPercentage(float n){
        return String.format("%.0f",n)+"%";
    }

    public  static String downloadStatusFromId(Download download){

        String value ="";

        switch (download.state) {
            case Download.STATE_COMPLETED:
                value = "Download Completed";
                break;
            case Download.STATE_DOWNLOADING:
                value = "Downloading...";
                break;
            case Download.STATE_FAILED:
                value = "Failed";
                break;
            case Download.STATE_QUEUED:
                value = "Added in Queue";
                break;
            case Download.STATE_REMOVING:
                value = "Removing...";
                break;
            case Download.STATE_RESTARTING:
                value = "Restarting...";
                break;
            case Download.STATE_STOPPED:
                value = "Paused";
                break;
        }
        return value;
    }
}

