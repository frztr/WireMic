package com.example.wiremic;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class AudioController implements IAudioController  {

    IModel model;
    public AudioController()
    {
        model = ServiceProvider.getProvider().<IModel>getSingleton(IModel.class);
    }

    @Override
    public void MicOn() {
        System.out.println("Mic is On.");
        model.setStatus(true);
        startStreaming();
    }

    @Override
    public void MicOff() {
        System.out.println("Mic is Off.");
        model.setStatus(false);
        recorder.release();
        recorder.stop();
        streamThread.stop();
    }

    byte[] buffer;
    static DatagramSocket socket;
    private int port = 50005;

    static AudioRecord recorder;

    private int sampleRate = 44100;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = 128;
    Thread streamThread;

    private void startStreaming() {

        streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket();
                    Log.d("VS", "Socket Created");

                    byte[] buffer = new byte[minBufSize*2];

                    Log.d("VS", "Buffer created of size " + minBufSize);
                    DatagramPacket packet;

                    String ip = "192.168.137.1";
                    String modelIp = model.getIp();
                    if(modelIp != "")
                    {
                        ip = modelIp;
                    }
                    final InetAddress destination = InetAddress.getByName(ip);
                    Log.d("VS", "Address retrieved");


                    if (ActivityCompat.checkSelfPermission(ServiceProvider.getProvider().getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize * 10);
                    Log.d("VS", "Recorder initialized");

                    recorder.startRecording();

                    packet = new DatagramPacket(buffer,buffer.length,destination,port);
                    while(model.getStatus()) {
                        minBufSize = recorder.read(buffer, 0, buffer.length);
                        packet.setData(buffer);
                        socket.send(packet);
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
