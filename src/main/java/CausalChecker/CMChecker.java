package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import Relation.CausalOrder;
import Relation.HappenBefore;
import Relation.ProgramOrder;
import Relation.ReadFrom;

public class CMChecker extends CCChecker {
    HappenBefore HB;

    public CMChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, HappenBefore HB, History history) {
        super(PO, RF, CO, history);
        this.HB = HB;
        this.initCMMap();
    }

    void initCMMap() {
        badMap.put(BAD_PATTERN.WriteHBInitRead, false);
        badMap.put(BAD_PATTERN.CyclicHB, false);
    }

    private void checkCM() {
        checkWriteHBInitRead();
        checkCyclicHB();
    }


    public void checkCausalMemory() {
        logger.info("Starting Check Causal Memory");
        checkCC();
        checkCM();
        printCheckStatus();
    }

    void checkWriteHBInitRead() {
        logger.info("Checking WriteHBInitRead");
        for (HistoryItem o : histories) {
            for (HistoryItem r : readHistories) {
                if (PO.isPO(r, o) && r.readInit()) {
                    int oIndex = o.getIndex();
                    for (HistoryItem w : writeHistories) {
                        // w <HBo r and var(w) = var(r)
                        if (HB.getHBo(oIndex).isHBo(w, r) && (w.getK().equals(r.getK()))){
                            badMap.put(BAD_PATTERN.WriteHBInitRead, true);
                            return;
                        }
                    }
                }
            }
        }
    }

    void checkCyclicHB() {
        logger.info("Checking CyclicHB");
        boolean cyclic;
        for (int oIndex : operations.keySet()) {
            logger.info("Checking CyclicHB " + oIndex );
            cyclic = CycleChecker.Cyclic(HB.getHBo(oIndex).getRelations());
            if (cyclic) {
                badMap.put(BAD_PATTERN.CyclicHB, true);
                return;
            }
        }
    }

}
