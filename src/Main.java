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

    private static void output(String str) {
        Process runt = null;
        try {
            runt = Runtime.getRuntime().exec(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(runt.getInputStream()));
        String out = null;
        while (true) {
            try {
                if (!((out = reader.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(out);
        }
    }

    public static void main(String args[]) throws Exception {
        AppContext.getInstance();
        if (System.getProperty("os.name").toString().toLowerCase().indexOf("linux") != -1) {

            long process_id = ProcessHandle.current().pid();

            Runtime.getRuntime().exec("pacmd unload-module module-null-sink");
            Runtime.getRuntime().exec("pacmd unload-module module-remap-source");
            Runtime.getRuntime().exec("pactl load-module module-null-sink sink_name=\"WireMicSpeaker\" sink_properties=device.description=\"WireMicSpeaker\"");
            Runtime.getRuntime().exec("pactl load-module module-remap-source master=\"WireMicSpeaker.monitor\" source_name=\"WireMic\" source_properties=device.description=\"WireMic\"");
            long sinkinput = CommandService.getInstance().getProgramSinkInput(process_id);
            Runtime.getRuntime().exec("pacmd move-sink-input " + sinkinput + " WireMicSpeaker");
            System.out.println("Started on ips:");
            CommandService.getInstance().getIpAddresses();
        }


        BufferedReader _reader = new BufferedReader(new InputStreamReader(System.in));
        String str;
        while ((str = _reader.readLine()) != null) {
            CommandService.getInstance().ExecuteCommand(str);
        }
        _reader.close();
    }


}