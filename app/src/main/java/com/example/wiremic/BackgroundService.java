package com.example.wiremic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.wiremic.events.StatusChangedEvent;

public class BackgroundService extends Service implements
//        IBackgroundListener,
        IListener<StatusChangedEvent> {

    private IModel model;
    BackgroundService.BackgroundBinder binder = new BackgroundBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

//    @Override
//    public void onStateUpdated(boolean state) {
//        createNotification();
//    }

    @Override
    public void onEvent(StatusChangedEvent event) {
        createNotification();
    }

    class BackgroundBinder extends Binder
    {
        BackgroundService getService() { return BackgroundService.this;}
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        model = ServiceProvider.getProvider().<IModel>getSingleton(IModel.class);
        model.addListener(this);

        boolean serviceRunning = false;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            for(StatusBarNotification notification : manager.getActiveNotifications())
            {
                if(notification.getNotification().getChannelId().equals("WireMicChannel") && !serviceRunning)
                {
                    serviceRunning = true;
                    createNotification();
                }
            }
        }

        if(!serviceRunning) {
            Intent notifintent = new Intent(this, BackgroundService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifintent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "WireMicChannel")
                    .setContentTitle(getText(R.string.app_name))
                    .setContentText(getText(R.string.app_name))
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent);
            Notification notification = builder.build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
                Log.d("VS", "Foreground started");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE);
            }
        }

        return START_STICKY;
    }

    private void createNotification()
    {
        Intent notifintent = new Intent(getApplicationContext(),BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifintent, 0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "WireMicChannel")
                    .setContentTitle("WireMic")
                    .setContentText("WireMic")
                    .setContentIntent(pendingIntent)
                    .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            startForeground(1,notification);
        }
    }
}
