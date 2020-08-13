package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import DifferentiatedHistory.History;
import Relation.CausalOrder;
import Relation.ConflictRelation;
import Relation.ProgramOrder;
import Relation.ReadFrom;

public class CCvChecker extends CCChecker{
    ConflictRelation CF;
    public CCvChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, History history) {
        super(PO, RF, CO, history);
        this.initCCvMap();
        int size = PO.getSize();
        this.CF = new ConflictRelation(size-1);
        this.CF.calculateConflictRelation(CO, history);
        this.CF.calculateTransitiveClosure();
    }

    void initCCvMap(){
        badMap.put(BAD_PATTERN.CyclicCF, false);
    }

    protected void checkCCv(){
        checkCyclicCF();
    }

    public void checkCausalConvergence(){
        checkLoggerInfo("Starting Check Causal Convergence");
        checkCCv();
        printCheckStatus();
    }

    protected void checkCyclicCF(){
        checkLoggerInfo("Checking CyclicCF");
//        CF.printRelationsMatrix();
        boolean cyclic = CycleChecker.Cyclic(CF.getRelations(true));
        if (cyclic) {
            badMap.put(BAD_PATTERN.CyclicCF, true);
        }
    }
}
