import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

class Main {



    public static void main(String args[]) throws Exception {

        System.out.println(ProcessHandle.current().pid());
//        Runtime.getRuntime().exec(
//        "pacmd unload-module module-null-sink && " +
//        "pactl load-module module-null-sink sink_name=\"WireMic\" sink_properties=device.description=\"WireMic\" && pacmd list-sink-inputs | awk '/index:/{print $0} /application.process.id/{print $0}'");

        // ( 1280 for 16 000Hz and 3584 for 44 100Hz (use AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) to get the correct size)

        System.out.println("Started on ips:");
        CommandService.getInstance().getIpAddresses();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        while ((str = reader.readLine()) != null) {
            CommandService.getInstance().ExecuteCommand(str);
        }
        reader.close();
    }


}