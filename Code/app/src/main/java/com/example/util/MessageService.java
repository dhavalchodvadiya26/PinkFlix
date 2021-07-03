package com.example.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.example.streamingapp.MainActivity;
import com.example.streamingapp.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> map = remoteMessage.getData();

        assert notification != null;
        System.out.println("MessageService ==> Image URL ==> "+notification.getImageUrl());
        try {
            URL url = new URL(notification.getImageUrl().toString());
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                sendNotification(notification.getTitle(), notification.getBody(), map,bigPicture);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String body, Map<String, String> map,Bitmap image) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_v);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setContentInfo(title)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_v))
                .setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(image));


        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.YELLOW, 1000, 300);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}