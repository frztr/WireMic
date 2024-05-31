package com.example.wiremic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class StateClass {
    byte[] buffer;
    static DatagramSocket socket;
    private int port = 50005;

    static AudioRecord recorder;

    private int sampleRate = 48000; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private static boolean status = true;

    static Context context;

    private static StateClass state;
    private StateClass()
    {

    }

    private ArrayList<IBackgroundListener> backgroundListeners = new ArrayList<IBackgroundListener>();

    public void addBackgroundListener(IBackgroundListener listener)
    {
        backgroundListeners.add(listener);
    }

    public void removeBackgroundListener(IBackgroundListener listener)
    {
        backgroundListeners.remove(listener);
    }

    public static void setContext(Context _context)
    {
        if(context == null)
        {
            context = _context;
        }
    }

    public static StateClass getInstance()
    {
        if(state == null)
        {
            state = new StateClass();
        }
        return state;
    }

    public void MicOn(String ip)
    {
        status = true;
        startStreaming(ip);
        for (IBackgroundListener listener: backgroundListeners)
        {
            listener.onStateUpdated(status);
        }
    }

    public void MicOff()
    {
        status = false;
        recorder.release();
        Log.d("VS", "Recorder released");
        for (IBackgroundListener listener: backgroundListeners)
        {
            listener.onStateUpdated(status);
        }
    }

    private void startStreaming(@Nullable String ipAddress) {

        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

//                    MediaPlayer mediaPlayer = new MediaPlayer();
//                    try {
//                        mediaPlayer.setDataSource("https://psv4.vkuseraudio.net/s/v1/amp2/p9wBzpsvdZAc2aZex0fE1hN5Cpz39V9O0JNAowitHCNOGgjLeuVEfsSalgktNJJtjoKt8N1owHVzc3QazkxRexmSiFuhi-IKgNlNHkty0TU4kz9obTUC8vMLHEox_eyDlkmeRG9n9zA2s2x4_aDe_mDjJV5BCGf40LjW.mp3?siren=1");
//                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mediaPlayer)
//                            {
//                                mediaPlayer.start();
//                            }
//                        });
//                        mediaPlayer.prepareAsync();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize];

                    Log.d("VS", "Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    String ip = "192.168.137.1";
                    if(ipAddress != "")
                    {
                        ip = ipAddress;
                    }
                    final InetAddress destination = InetAddress.getByName(ip);
                    Log.d("VS", "Address retrieved");


                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                    }
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize * 10);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();


                    while(status) {

                        //reading data from MIC into buffer
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        //putting buffer in the packet
                        packet = new DatagramPacket(buffer,buffer.length,destination,port);

                        socket.send(packet);
                        System.out.println("MinBufferSize: " +minBufSize);

                    }

                } catch(UnknownHostException e) {
                    Log.e("VS", "UnknownHostException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("VS", "IOException");
                }
            }

        }
        );
        streamThread.start();
    }
}
