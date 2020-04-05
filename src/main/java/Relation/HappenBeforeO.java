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
        logger.info("Calculating HBo " + oIndex);
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
        // First Closure
        calculateTransitiveClosure();
        // Definition
        HistoryItem o = history.getOperations().get(oIndex);
        LinkedList<HistoryItem> writeList = history.getWriteHistories();
        LinkedList<HistoryItem> readList = history.getReadHistories();
        for (HistoryItem w1 : writeList) {
            for (HistoryItem w2 : writeList) {
                if ((w1.getK() == w2.getK()) && (w1.getV() != w2.getV())) {
                    for (HistoryItem r2 : readList) {
                        if (isHBo(w1, r2) && PO.isPO(r2, o) && (r2.getV() == w2.getV())) {
                            addRelation(w1.getIndex(), w2.getIndex());
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
