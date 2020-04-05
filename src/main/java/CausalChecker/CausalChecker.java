package CausalChecker;

import BadPattern.BAD_PATTERN;
import CausalLogger.CausalLogHandler;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import Relation.CausalOrder;
import Relation.ProgramOrder;
import Relation.ReadFrom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CausalChecker {

    ProgramOrder PO;
    ReadFrom RF;
    CausalOrder CO;
    LinkedList<HistoryItem> histories;
    HashMap<Integer, HistoryItem> operations;
    HashMap<BAD_PATTERN, Boolean> badMap;

    LinkedList<HistoryItem> writeHistories;
    LinkedList<HistoryItem> readHistories;
    Logger logger;

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
        logger.info(checker + " outcome list");
        for (Map.Entry<BAD_PATTERN, Boolean> entry : badMap.entrySet()) {
            BAD_PATTERN bad = entry.getKey();
            boolean has_bad = entry.getValue();
            logger.info("Collecting " + entry);
            if (has_bad) {
                logger.warning("Inconsistency of " + bad);
            }
        }
    }


}
