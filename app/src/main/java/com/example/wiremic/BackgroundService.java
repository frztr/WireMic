package com.example.wiremic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BackgroundService extends Service implements IBackgroundListener {

    BackgroundService.BackgroundBinder binder = new BackgroundBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onStateUpdated(boolean state) {
        createNotification();
    }

    class BackgroundBinder extends Binder
    {
        BackgroundService getService() { return BackgroundService.this;}
    }
    private StateClass state;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StateClass.getInstance().addBackgroundListener(this);
        Intent notifintent = new Intent(this,BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifintent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TestChannel")
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.app_name))
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1,notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
            Log.d("VS", "Foreground started");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE);
        }

        return START_NOT_STICKY;
    }

    private void createNotification()
    {
        Intent notifintent = new Intent(getApplicationContext(),BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifintent, 0);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "TestChannel")
                    .setContentTitle("WireMic")
                    .setContentText("WireMic")
                    .setContentIntent(pendingIntent)
                    .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            startForeground(1,notification);
        }
    }
}
