package History;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HistoryReader {

    final String KEY_TYPE = ":type ";
    final String KEY_F = " :f ";
    final String KEY_VALUE = " :value ";
    final String KEY_PROCESS = " :process ";
    final String KEY_TIME = " :time ";
    final String KEY_POSITION = " :position ";
    final String KEY_LINK = " :link ";
    final String KEY_INDEX = " :index ";

    String url;
    int concurrency;

    public HistoryReader(String url, int concurrency) {
        this.url = url;
        this.concurrency = concurrency;
    }

    HistoryItem getHistoryItem(String line) {
        String[] subs = line.split(",");
        String type = StringUtils.remove(subs[0], KEY_TYPE);
        if (!type.equals(":ok")) {
            return null;
        }
        String f = StringUtils.remove(subs[1], KEY_F);
        String value = StringUtils.remove(subs[2], KEY_VALUE);
        int process = Integer.parseInt(StringUtils.remove(subs[3], KEY_PROCESS));
        long time = Long.parseLong(StringUtils.remove(subs[4], KEY_TIME));
        long position = Long.parseLong(StringUtils.remove(subs[5], KEY_POSITION));
        String link = StringUtils.remove(subs[6], KEY_LINK);
        int index = Integer.parseInt(StringUtils.remove(subs[7], KEY_INDEX));
        return new HistoryItem(type, f, value, process, time, position, link, index, concurrency);
    }

    public ArrayList<HistoryItem> readHistories() throws IOException {
        ArrayList<HistoryItem> histories = new ArrayList<HistoryItem>();
        BufferedReader in = new BufferedReader(new FileReader(this.url));
        String line;
        while ((line = in.readLine()) != null) {
            line = StringUtils.strip(line, "{}");
//            System.out.println(line);
            HistoryItem history = this.getHistoryItem(line);
            if (history != null) {
                histories.add(history);
//                System.out.println(history);
//                System.out.println();
            }
        }
        return histories;
    }

    public static void main(String[] args) {
        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\history.edn";
        HistoryReader reader = new HistoryReader(url, 10);
        try {
            reader.readHistories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
