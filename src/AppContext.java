import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Date;

public class AppContext {
    private boolean listening = false;
    private static AppContext context;

    private static AudioFormat format;
    private static final int port = 50005;
    private static final int sampleRate = 44100;
    private final DatagramSocket serverSocket;
    SourceDataLine sourceDataLine;
    DataLine.Info dataLineInfo;
    private Thread thr;
//    private static final int buffSize = 3584;
private static final int buffSize = 128;

    private AppContext()
    {
        try {
            serverSocket = new DatagramSocket(port);
            format = new AudioFormat(sampleRate, 16, 1, true, false);

            dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
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
            FloatControl volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(5f);
            sourceDataLine.start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public static AppContext getInstance()
    {
        if(context == null)
        {
            context = new AppContext();
        }
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
        byte[] receiveData = new byte[buffSize*2];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        thr = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                        try {
                            serverSocket.receive(receivePacket);
                            byte[] soundbytes = receivePacket.getData();
                            sourceDataLine.write(soundbytes, 0, soundbytes.length);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
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
