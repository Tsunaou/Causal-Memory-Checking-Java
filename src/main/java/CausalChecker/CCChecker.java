package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import History.HistoryItem;
import Relation.CausalOrder;
import Relation.ProgramOrder;
import Relation.ReadFrom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CCChecker extends CausalChecker {

    public CCChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, ArrayList<HistoryItem> histories, HashMap<Integer, HistoryItem> operation) {
        super(PO, RF, CO, histories, operation);
        this.initCCMap();
    }

    void initCCMap() {
        badMap.put(BAD_PATTERN.CyclicCO, false);
        badMap.put(BAD_PATTERN.ThinAirRead, false);
        badMap.put(BAD_PATTERN.WriteCOInitRead, false);
        badMap.put(BAD_PATTERN.WriteCORead, false);
    }

    public void checkCausalConsistency() {
        checkCyclicCO();
        checkThinAirRead();
        checkWriteCOInitRead();
        checkWriteCORead();
        for (Map.Entry<BAD_PATTERN, Boolean> entry : badMap.entrySet()) {
            BAD_PATTERN bad = entry.getKey();
            boolean has_bad = entry.getValue();
            System.out.println("Collecting " + entry);
            if (has_bad) {
                System.out.println("Inconsistency of " + bad);
            }
        }
    }

    void checkCyclicCO() {
        System.out.println("Checking CyclicCO");
        boolean cyclic = CycleChecker.Cyclic(CO.getRelations());
        if (cyclic) {
            badMap.put(BAD_PATTERN.CyclicCO, true);
        }
    }

    void checkThinAirRead() {
        System.out.println("Checking ThinAirRead");
        for (HistoryItem write : writeHistories) {
            for (HistoryItem read : readHistories) {
                if (write.getK() != read.getK()) {
                    continue;
                }
                if (CO.isCO(write, read)) {
                    int value = read.getV();
                    if (value == 0 || value == -1) {
                        // TODO: record more information
                        badMap.put(BAD_PATTERN.ThinAirRead, true);
                        return;
                    }
                }
            }
        }
    }

    void checkWriteCOInitRead() {
        System.out.println("Checking WriteCOInitRead");
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
        System.out.println("Checking WriteCORead");
        for (HistoryItem w1 : writeHistories) {
            for (HistoryItem w2 : writeHistories) {
                if (w1.getK() == w2.getK()) {
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
