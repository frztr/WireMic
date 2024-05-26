import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AppContext {
    private boolean listening = false;
    private static final AppContext context = new AppContext();

    private static AudioFormat format;
    private static final int port = 50005;
    private static final int sampleRate = 48000;
    private final DatagramSocket serverSocket;

    private Thread thr;

    private AppContext()
    {
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static AppContext getInstance()
    {
        return context;
    }

    public void setListening(boolean listening) {
        context.listening = listening;
        System.out.println("Mic: " + (listening ?"On":"Off"));

        if(listening) {
            startMic();
        }
        else
        {
            killMic();
        }
    }
    private void killMic()
    {
        thr.interrupt();
    }

    private void startMic()
    {
        byte[] receiveData = new byte[3840];
        format = new AudioFormat(sampleRate, 16, 1, true, false);
        DataLine.Info dataLineInfo;
        dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine sourceDataLine;
        try {
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        try {
            sourceDataLine.open(format);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        sourceDataLine.start();
        FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        volumeControl.setValue(5f);

        thr = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (AppContext.getInstance().getListening()) {
                        try {
                            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                            serverSocket.receive(receivePacket);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        byte[] soundbytes = receivePacket.getData();
                                        sourceDataLine.write(soundbytes, 0, soundbytes.length);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        thr.start();
    }

    public boolean getListening()
    {
        return context.listening;
    }
}
