package Relation;

import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;

import java.util.LinkedList;

public class HappenBeforeO extends PoSetMatrix {

    int oIndex;

    public HappenBeforeO(int size, int oIndex) {
        super(size);
        this.oIndex = oIndex;
    }

    public void calculateHappenBefore(ProgramOrder PO, CausalOrder CO, History history) {
        checkLoggerInfo("Calculating HBo " + oIndex);
        // calculate CasualPast(o)
        LinkedList<Integer> causalPast = CO.CausalPast(oIndex);
        // CO|CausalPast(o) \subset HB_o
        for (int i : causalPast) {
            for (int j : causalPast) {
                if (CO.isCO(i, j)) {
                    addRelation(i, j);
                }
            }
        }

        // Definition
        HistoryItem o = history.getOperations().get(oIndex);
        LinkedList<HistoryItem> writeList = history.getWriteHistories();
        LinkedList<HistoryItem> readList = history.getReadHistories();
        boolean flag = true;
        int count_continue = 0;
        out:
        while (flag) {
            // First Closure
//            System.out.println("count_continue is " + count_continue);
            count_continue = count_continue + 1;
            flag = false;
            calculateTransitiveClosure();
            for (HistoryItem w1 : writeList) {
                for (HistoryItem w2 : writeList) {
                    if ((w1.getK().equals(w2.getK())) && (w1.getV() != w2.getV())) {
                        for (HistoryItem r2 : readList) {
                            if (isHBo(w1, r2) && PO.isPOEQ(r2, o) && (r2.getV() == w2.getV()) && !isHBo(w1, w2)) {
                                addRelation(w1.getIndex(), w2.getIndex());
//                                System.out.println("Find new HB " + w1.getIndex() + ", " + w2.getIndex());
                                flag = true;
                                continue out;
                            }
                        }
                    }
                }
            }
        }
        // Second Closure
        calculateTransitiveClosure();
    }

    public boolean isHBo(int i, int j) {
        return isRelation(i, j);
    }

    public boolean isHBo(HistoryItem it1, HistoryItem it2) {
        return isHBo(it1.getIndex(), it2.getIndex());
    }


    public static void main(String[] args) {

    }
}
