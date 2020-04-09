package CausalChecker;

import BadPattern.BAD_PATTERN;
import CausalLogger.CheckerWithLogger;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import Relation.CausalOrder;
import Relation.ProgramOrder;
import Relation.ReadFrom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CausalChecker implements CheckerWithLogger {

    ProgramOrder PO;
    ReadFrom RF;
    CausalOrder CO;
    LinkedList<HistoryItem> histories;
    HashMap<Integer, HistoryItem> operations;
    HashMap<BAD_PATTERN, Boolean> badMap;

    LinkedList<HistoryItem> writeHistories;
    LinkedList<HistoryItem> readHistories;
    Logger logger;
    boolean NO_LOGGER = true;

    public CausalChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, History history) {
        this.PO = PO;
        this.RF = RF;
        this.CO = CO;
        this.histories = history.getHistories();
        this.operations = history.getOperations();
        this.badMap = new HashMap<BAD_PATTERN, Boolean>();
        this.writeHistories = history.getWriteHistories();
        this.readHistories = history.getReadHistories();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    public void printCheckStatus(){
        String checker = this.getClass().getName();
        checkLoggerInfo(checker + " outcome list");
        for (Map.Entry<BAD_PATTERN, Boolean> entry : badMap.entrySet()) {
            BAD_PATTERN bad = entry.getKey();
            boolean has_bad = entry.getValue();
            checkLoggerInfo("Collecting " + entry);
            if (has_bad) {
                checkLoggerWarning("Inconsistency of " + bad);
            }
        }
    }


    @Override
    public void checkLoggerInfo(String message) {
        if(LOGGER){
            logger.info(message);
        }else {
            System.out.println(message);
        }
    }

    @Override
    public void checkLoggerWarning(String message) {
        if(LOGGER){
            logger.warning(message);
        }else {
            System.out.println(message);
        }
    }
}
