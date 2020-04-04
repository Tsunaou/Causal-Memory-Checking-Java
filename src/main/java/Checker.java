import History.HistoryItem;
import History.HistoryReader;
import Relation.ProgramOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Checker {

    void timeTemplate(){
        long startTime = System.currentTimeMillis();
        // do something
        long endTime = System.currentTimeMillis();
        System.out.println("It takes ：" + (endTime - startTime) + "ms to do something");
    }


    public static void main(String[] args) {
        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\history.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\tiny_history.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\small_history.edn";
        int concurrency = 10;
        HistoryReader reader = new HistoryReader(url, concurrency);
        try {
            ArrayList<HistoryItem> histories = reader.readHistories();
            int lastIndex = histories.get(histories.size() - 1).getIndex(); // the max index in the  histories
            HashMap<Integer, HistoryItem> operations = new HashMap<Integer, HistoryItem>(); // index -> operation(or say history)
            for(HistoryItem item: histories){
                operations.put(item.getIndex(), item);
            }
            // get program order
            ProgramOrder PO = new ProgramOrder(lastIndex);
            PO.calculateProgramOrder(histories, concurrency);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
