package Relation;

import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ProgramOrder extends PoSetMatrix {

    public ProgramOrder(int size) {
        super(size);
    }

    public void calculateProgramOrder(History history, int concurrency) {
        logger.info("Calculating PO");
        LinkedList<HistoryItem> histories = history.getHistories();
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


    public boolean isPO(int i, int j){
        return isRelation(i, j);
    }

    public boolean isPO(HistoryItem it1, HistoryItem it2){
        return isPO(it1.getIndex(),it2.getIndex());
    }

}
