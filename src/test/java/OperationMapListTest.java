import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import DifferentiatedHistory.HistoryReader;
import Relation.ProgramOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class OperationMapListTest {

    @Test
    public void main() {
        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\history.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\tiny_history.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\small_history.edn";
        int concurrency = 10;
        HistoryReader reader = new HistoryReader(url, concurrency);
        try {
            // Test whether operations can be replaced by histories
            History history = reader.readHistory();
            LinkedList<HistoryItem> histories = history.getHistories();
            HashMap<Integer, HistoryItem> operations = history.getOperations();
            // Round1 test op numbers
            assertEquals(histories.size(), operations.keySet().size());
            // Round2 fill in
            boolean[] visit = new boolean[histories.size()];
            boolean[] except = new boolean[histories.size()];
            for (int i = 0; i < histories.size(); i++) {
                if (operations.containsKey(i) && operations.get(i) == histories.get(i)) {
                    visit[i] = true;
                }
                except[i] = true;
            }
            assertArrayEquals(except, visit);
        } catch (IOException e) {
            e.printStackTrace();
    }
    }
}