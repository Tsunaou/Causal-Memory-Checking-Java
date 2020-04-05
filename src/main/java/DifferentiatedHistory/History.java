package DifferentiatedHistory;

import java.util.HashMap;
import java.util.LinkedList;

public class History {

    LinkedList<HistoryItem> histories;
    LinkedList<HistoryItem> writeHistories;
    LinkedList<HistoryItem> readHistories;
    HashMap<Integer, HistoryItem> operations; // index -> operation(or say history)
    int lastIndex;

    public History(LinkedList<HistoryItem> histories) {
        this.histories = histories;
        this.initOperations();
        this.initWriteReadHistories();
        this.lastIndex = histories.get(histories.size() - 1).getIndex(); // the max index in the  histories

    }

    private void initOperations(){
        operations = new HashMap<Integer, HistoryItem>();
        for (HistoryItem item : histories) {
            operations.put(item.getIndex(), item);
        }
    }

    private void initWriteReadHistories(){
        writeHistories = new LinkedList<HistoryItem>();
        readHistories = new LinkedList<HistoryItem>();
        for(HistoryItem item : histories){
            if(item.isWrite()){
                writeHistories.add(item);
            }
            if(item.isRead()){
                readHistories.add(item);
            }
        }
    }

    public LinkedList<HistoryItem> getHistories() {
        return histories;
    }

    public LinkedList<HistoryItem> getWriteHistories() {
        return writeHistories;
    }

    public LinkedList<HistoryItem> getReadHistories() {
        return readHistories;
    }

    public HashMap<Integer, HistoryItem> getOperations() {
        return operations;
    }

    public int getLastIndex() {
        return lastIndex;
    }
}
