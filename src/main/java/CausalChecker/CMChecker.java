package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import Relation.*;

import java.util.ArrayList;
import java.util.HashMap;

public class CMChecker extends CCChecker {

    public CMChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, History history) {
        super(PO, RF, CO, history);
        this.initCMMap();
    }

    void initCMMap() {
        badMap.put(BAD_PATTERN.WriteHBInitRead, false);
        badMap.put(BAD_PATTERN.CyclicHB, false);
    }

    private void checkCM() {
        int size = PO.getSize();
        ArrayList<Thread> subCheckers = new ArrayList<>();
        for (Integer o : operations.keySet()) {
            HappenBeforeO HBo = new HappenBeforeO(size, o);
            HBo.calculateHappenBefore(PO, CO, history);
//            if(o==7){
//                PO.printRelationsMatrix();
//                CO.printRelationsMatrix();
//                HBo.printRelationsMatrix();
//            }
//            System.out.println("-----------------------------------------------------------------------");
//            System.out.println("for operation" + history.getOperations().get(o));
            Thread subChecker = new Thread(() -> {
                checkWriteHBInitRead(HBo);
                checkCyclicHB(HBo);
            });
            subChecker.start();
            // TODO:
            // FIXME:
//            try {
//                subChecker.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            subCheckers.add(subChecker);
        }
        try {
            for (Thread subChecker : subCheckers) {
                subChecker.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void checkCausalMemory() {
        checkLoggerInfo("Starting Check Causal Memory");
        checkCC();
        checkCM();
        printCheckStatus();
    }

    void checkWriteHBInitRead(HappenBeforeO HBo) {

        if (badMap.get(BAD_PATTERN.WriteHBInitRead)) {
            return;
        }

        checkLoggerInfo("Checking WriteHBInitRead " + HBo.oIndex);
        HistoryItem o = operations.get(HBo.oIndex);
        for (HistoryItem r : readHistories) {
            if (PO.isPO(r, o) && r.readInit()) {
                for (HistoryItem w : writeHistories) {
                    // w <HBo r and var(w) = var(r)
                    if (HBo.isHBo(w, r) && (w.getK().equals(r.getK()))) {
                        badMap.put(BAD_PATTERN.WriteHBInitRead, true);
                        return;
                    }
                }
            }
        }
    }

    void checkCyclicHB(HappenBeforeO HBo) {

        if (badMap.get(BAD_PATTERN.CyclicHB)) {
            return;
        }

        checkLoggerInfo("Checking CyclicHB " + HBo.oIndex);
        boolean cyclic = CycleChecker.Cyclic(HBo.getRelations());
        if (cyclic) {
            badMap.put(BAD_PATTERN.CyclicHB, true);
        }
    }

    @Override
    public void checkLoggerInfo(String message) {
        if (LOGGER) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }

    @Override
    public void checkLoggerWarning(String message) {
        if (LOGGER) {
            logger.warning(message);
        } else {
            System.out.println(message);
        }
    }
}
