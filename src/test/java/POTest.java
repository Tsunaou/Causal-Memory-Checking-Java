import History.HistoryItem;
import History.HistoryReader;
import Relation.ProgramOrder;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class POTest {

    @Test
    public void main() {
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
            // TODO: test whether PO is in the same process
            boolean[][] relations = PO.getRelations();
            int n = PO.getSize();
            for(int i=0;i<n;i++){
                for(int j=0;j<n;j++){
                    if(relations[i][j]){
                        HistoryItem it1 = operations.get(i);
                        HistoryItem it2 = operations.get(i);
                        assertEquals(it1.getProcess(),it2.getProcess());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}