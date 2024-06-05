package com.example.wiremic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button startButton, stopButton;

    private StateClass stateStore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);

        startButton.setOnClickListener(startListener);
        stopButton.setOnClickListener(stopListener);
        stateStore = StateClass.getInstance();
        stateStore.setContext(getApplicationContext());
        StartAudioService();

    }

    private final OnClickListener stopListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            stateStore.MicOff();
        }

    };

    private final OnClickListener startListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            EditText ed1 = (EditText) findViewById(R.id.ipAddress);
            stateStore.MicOn(ed1.getText().toString());
        }

    };

    public void StartAudioService()
    {
        Intent intent = new Intent(this,BackgroundService.class);

        ServiceConnection srv = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                BackgroundService service = ((BackgroundService.BackgroundBinder) iBinder).getService();
                Log.d("VS", "Service Connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("VS", "Service Disconnected");
            }
        };
        bindService(intent,srv,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("TestChannel","TestNameChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            getApplicationContext().startForegroundService(intent);
        }
    }
}