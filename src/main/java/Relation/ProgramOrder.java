package Relation;

import History.HistoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProgramOrder extends PoSetMatrix {

    public ProgramOrder(int size) {
        super(size);
    }

    public void calculateProgramOrder(ArrayList<HistoryItem> histories, int concurrency) {
        HashMap<Integer, ArrayList<HistoryItem>> groups = new HashMap<Integer, ArrayList<HistoryItem>>();  // group by process
        // initial list for each process
        for (int i = 0; i < concurrency; i++) {
            groups.put(i, new ArrayList<HistoryItem>());
        }
        // group histories by process
        for (HistoryItem item : histories) {
            int process = item.getProcess();
            groups.get(process).add(item);
        }
        // add sample program order
        for (Map.Entry<Integer, ArrayList<HistoryItem>> entry : groups.entrySet()) {
            int process = entry.getKey();
            ArrayList<HistoryItem> subHistories = entry.getValue();
            for (int i = 0; i < subHistories.size() - 1; i++) {
                int op1 = subHistories.get(i).getIndex();
                int op2 = subHistories.get(i + 1).getIndex();
                this.addRelation(op1, op2);
            }
        }
        // calculate transitive closure
        this.calculateTransitiveClosure();

    }

}
