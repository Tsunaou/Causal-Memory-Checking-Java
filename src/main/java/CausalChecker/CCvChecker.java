package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import DifferentiatedHistory.History;
import Relation.*;

import java.util.HashMap;

public class CCvChecker extends CCChecker{
    ConflictRelation CF;
    public CCvChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, History history) {
        super(PO, RF, CO, history);
        this.initCCvMap();
        int size = PO.getSize();
        this.CF = new ConflictRelation(size-1);
        this.CF.calculateConflictRelation(CO, history);
//        this.CF.calculateTransitiveClosure();
    }

    void initCCvMap(){
        badMap.put(BAD_PATTERN.CyclicCF, false);
    }

    protected void checkCCv(){
        checkCyclicCF();
    }

    public HashMap<BAD_PATTERN, Boolean> checkCausalConvergence(boolean checkCC){
        checkLoggerInfo("Starting Check Causal Convergence");
        if(checkCC){
            checkCC();
        }
        checkCCv();
        return this.badMap;
    }

    public PoSetMatrix union(PoSetMatrix s1, PoSetMatrix s2) {
        assert (s1.getSize() == s2.getSize());
        int n = s1.getSize();

        boolean[][] r1 = s1.getRelations(true);
        boolean[][] r2 = s2.getRelations(true);
        PoSetMatrix res = new PoSetMatrix(n-1);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (r1[i][j] || r2[i][j]) {
                    res.addRelation(i, j);
                }
            }
        }
        return res;
    }
    protected void checkCyclicCF(){
        checkLoggerInfo("Checking CyclicCF");
//        CF.printRelationsMatrix();
        boolean cyclic = CycleChecker.CyclicOld(union(CF, CO).getRelations(true));
        if (cyclic) {
            badMap.put(BAD_PATTERN.CyclicCF, true);
        }
    }
}
