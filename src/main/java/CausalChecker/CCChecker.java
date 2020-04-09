package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import Relation.CausalOrder;
import Relation.ProgramOrder;
import Relation.ReadFrom;

import java.util.Map;

public class CCChecker extends CausalChecker {

    public CCChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, History history) {
        super(PO, RF, CO, history);
        this.initCCMap();
    }

    void initCCMap() {
        badMap.put(BAD_PATTERN.CyclicCO, false);
        badMap.put(BAD_PATTERN.ThinAirRead, false);
        badMap.put(BAD_PATTERN.WriteCOInitRead, false);
        badMap.put(BAD_PATTERN.WriteCORead, false);
    }

    protected void checkCC() {
        checkCyclicCO();
        checkThinAirRead();
        checkWriteCOInitRead();
        checkWriteCORead();
    }


    public void checkCausalConsistency() {
        logger.info("Starting Check Causal Consistency");
        checkCC();
        printCheckStatus();
    }

    void checkCyclicCO() {
        logger.info("Checking CyclicCO");
        boolean cyclic = CycleChecker.Cyclic(CO.getRelations());
        if (cyclic) {
            badMap.put(BAD_PATTERN.CyclicCO, true);
        }
    }

    void checkWriteCOInitRead() {
        logger.info("Checking WriteCOInitRead");
        for (HistoryItem write : writeHistories) {
            for (HistoryItem read : readHistories) {
                if (!write.getK().equals(read.getK())) {
                    continue;
                }
                if (CO.isCO(write, read)) {
                    if (read.readInit()) {
                        // TODO: record more information
                        badMap.put(BAD_PATTERN.ThinAirRead, true);
                        return;
                    }
                }
            }
        }
    }

    void checkThinAirRead() {
        logger.info("Checking ThinAirRead");
        boolean exists = false;
        for (HistoryItem read : readHistories) {
            int value = read.getV();
            if (value != 0 && value != -1) {
                for (HistoryItem write : writeHistories) {
                    if (RF.isRF(write, read)) {
                        exists = true;
                    }
                }
            }
        }
        if (!exists) {
            badMap.put(BAD_PATTERN.WriteCOInitRead, true);
        }
    }

    void checkWriteCORead() {
        logger.info("Checking WriteCORead");
        for (HistoryItem w1 : writeHistories) {
            for (HistoryItem w2 : writeHistories) {
                if (w1.getK().equals(w2.getK())) {
                    for (HistoryItem r1 : readHistories) {
                        if (CO.isCO(w1, w2) && CO.isCO(w2, r1) && RF.isRF(w1, r1)) {
                            badMap.put(BAD_PATTERN.WriteCORead, true);
                            return;
                        }
                    }
                }
            }
        }

    }
}
