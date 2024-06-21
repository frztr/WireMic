package com.example.wiremic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.wiremic.events.Event;
import com.example.wiremic.events.StatusChangedEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements IListener<StatusChangedEvent> {

    private Button startButton, stopButton;
    private EditText editText;
    //private StateClass stateStore;
    private IModel model;
    private IDataController dataController;
    private IAudioController audioController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.start_button);
        stopButton = (Button) findViewById(R.id.stop_button);
        editText = (EditText) findViewById(R.id.ipAddress);

        editText.addTextChangedListener(watcher);
        startButton.setOnClickListener(startListener);
        stopButton.setOnClickListener(stopListener);


        //stateStore = StateClass.getInstance();
        //stateStore.setContext(getApplicationContext());


        ServiceProvider provider = ServiceProvider.getProvider();
        provider
                .AddService(IAudioController.class,AudioController.class)
                .AddService(IDataController.class,DataController.class)
                .AddService(IModel.class, Store.class)
                .AddContext(getApplicationContext());

        dataController = provider.<IDataController>getSingleton(IDataController.class);
        audioController = provider.<IAudioController>getSingleton(IAudioController.class);

        model = provider.<IModel>getSingleton(IModel.class);
        model.<StatusChangedEvent>addListener(this);


        StartAudioService();
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {

            dataController.SetIpAddress(editable.toString());
        }
    };

    private final OnClickListener stopListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            audioController.MicOff();
//            stateStore.MicOff();
        }

    };

    private final OnClickListener startListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
//            EditText ed1 = editText;
//            stateStore.MicOn(ed1.getText().toString());
            audioController.MicOn();
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
            NotificationChannel channel = new NotificationChannel("WireMicChannel","WireMicChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            getApplicationContext().startForegroundService(intent);
        }
    }

    @Override
    public void onEvent(StatusChangedEvent event) {
        System.out.println("Status changed:"+ event.getStatus());
    }
}