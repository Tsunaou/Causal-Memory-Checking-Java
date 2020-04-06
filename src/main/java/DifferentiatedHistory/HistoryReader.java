package DifferentiatedHistory;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

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
    int idx;

    public HistoryReader(String url, int concurrency, boolean file) {
        this.url = url;
        this.concurrency = concurrency;
        this.idx = 0; // remove the index of :invoke and :failed
    }

    public HistoryReader(String url, int concurrency) {
        this.url = this.phraseResources(url);
        this.concurrency = concurrency;
        this.idx = 0; // remove the index of :invoke and :failed
    }

    String phraseResources(String url) {
        String history = HistoryReader.class.getResource("/").getPath() + url;
        System.out.println("Reading history in " + history);
        return history;
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
        int index = idx++;
        return new HistoryItem(type, f, value, process, time, position, link, index, concurrency);
    }

    public LinkedList<HistoryItem> readHistories() throws IOException {
        return readHistories(Integer.MAX_VALUE);
    }

    public LinkedList<HistoryItem> readHistories(int maxIndex) throws IOException {
        System.out.println("Reading history in " + url);
        LinkedList<HistoryItem> histories = new LinkedList<HistoryItem>();
        BufferedReader in = new BufferedReader(new FileReader(this.url));
        String line;
        while ((line = in.readLine()) != null) {
            line = StringUtils.strip(line, "{}");
            HistoryItem history = this.getHistoryItem(line);
            if (history != null) {
                histories.add(history);
            }
            if (idx > maxIndex) {
                break;
            }
        }
        in.close();
        return histories;
    }

    public History readHistory() throws IOException {
        LinkedList<HistoryItem> histories = readHistories();
        return new History(histories);
    }

    public History readHistory(int maxIndex) throws IOException {
        LinkedList<HistoryItem> histories = readHistories(maxIndex);
        return new History(histories);
    }


    public static void main(String[] args) {

        String url = "tiny_history.edn";
        HistoryReader reader = new HistoryReader(url, 10);
        try {
            reader.readHistories();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
