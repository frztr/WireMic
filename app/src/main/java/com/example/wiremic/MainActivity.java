package com.example.wiremic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import com.example.wiremic.events.StatusChangedEvent;

public class MainActivity extends AppCompatActivity implements IListener<StatusChangedEvent> {

    private ImageButton micButton;
    private EditText editText;
    private IModel model;
    private IDataController dataController;
    private IAudioController audioController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.ipAddress);
        micButton = (ImageButton) findViewById(R.id.mic_imageButton);

        editText.addTextChangedListener(watcher);

        micButton.setOnClickListener(micButtonListener);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        },1);


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

        editText.setText(model.getIp());
        editText.setEnabled(!model.getStatus());

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

    private final OnClickListener micButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(model.getStatus()) {
                audioController.MicOff();
            }
            else
            {
                audioController.MicOn();
            }
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
        editText.setEnabled(!event.getStatus());
        if(event.getStatus())
        {
            micButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_mic_24));
        }
        else
            {
                micButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_mic_off_24));
            }
    }
}