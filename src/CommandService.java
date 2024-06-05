import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CommandService {
    private final static CommandService service = new CommandService();

    private CommandService() {
    }

    public static CommandService getInstance() {
        return service;
    }

    public void ExecuteCommand(String str) {
        switch (str) {
            case "Start":
                AppContext.getInstance().setListening(true);
                break;
            case "Stop":
                AppContext.getInstance().setListening(false);
                break;
            case "Close":
                System.out.println("Closing application");
                System.exit(0);
                break;
            case "Ip":
                getIpAddresses();
                break;
            default:
                System.out.println("Invalid Command");
        }
    }

    public long getProgramSinkInput(long process_id) {
        ArrayList<String> list = getProgramOutput("pacmd list-sink-inputs");
        long currentSink = 0;
        for (var row : list) {
            String search = "index: ";
            int index = row.indexOf(search);
            if (index != -1)
            {
                row = row.substring(index, row.length());
                currentSink = Long.parseLong(row.split(" ")[1]);

            }
            search = "application.process.id";
            index = row.indexOf(search);
            if (index != -1) {
                row = row.substring(index, row.length());
                if (Integer.parseInt(row.split(" ")[2].replace("\"", "")) == process_id) {
                    return currentSink;
                }
            }
        }
        return -1;
    }

    private ArrayList<String> getProgramOutput(String command) {
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader stdout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String _read = "";
        ArrayList<String> list = new ArrayList<String>();

        while (true) {
            try {
                if (!((_read = stdout.readLine()) != null)) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            list.add(_read);
        }
        return list;
    }

    public void getIpAddresses() {
        ArrayList<String> list = getProgramOutput("ifconfig");
        ArrayList<String> ipAddresses = new ArrayList<String>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).indexOf("wl") >= 0 && (i + 1) < list.size()) {
                String info = list.get(i + 1);
                int begin = info.indexOf("inet");
                info = info.substring(begin, info.length() - begin);
                String[] cols = info.split(" ");
                ipAddresses.add(cols[1]);
            }
        }

        for (String ip : ipAddresses) {
            System.out.println(ip);
        }
    }
}