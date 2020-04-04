package CausalChecker;

import BadPattern.BAD_PATTERN;
import History.HistoryItem;
import Relation.CausalOrder;
import Relation.ProgramOrder;
import Relation.ReadFrom;

import java.util.ArrayList;
import java.util.HashMap;

public class CausalChecker {

    ProgramOrder PO;
    ReadFrom RF;
    CausalOrder CO;
    ArrayList<HistoryItem> histories;
    HashMap<Integer, HistoryItem> operations;
    HashMap<BAD_PATTERN, Boolean> badMap;

    ArrayList<HistoryItem> writeHistories;
    ArrayList<HistoryItem> readHistories;


    public CausalChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, ArrayList<HistoryItem> histories, HashMap<Integer, HistoryItem> operations) {
        this.PO = PO;
        this.RF = RF;
        this.CO = CO;
        this.histories = histories;
        this.operations = operations;
        this.badMap = new HashMap<BAD_PATTERN, Boolean>();
        this.writeHistories = new ArrayList<HistoryItem>();
        this.readHistories = new ArrayList<HistoryItem>();
        this.initWriteReadHistories();
    }

    private void initWriteReadHistories(){
        for(HistoryItem item : histories){
            if(item.isWrite()){
                writeHistories.add(item);
            }
            if(item.isRead()){
                readHistories.add(item);
            }
        }
    }
}
