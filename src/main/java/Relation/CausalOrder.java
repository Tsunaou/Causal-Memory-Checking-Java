package Relation;

import DifferentiatedHistory.HistoryItem;

import java.util.LinkedList;

public class CausalOrder extends PoSetMatrix {
    public CausalOrder(int size) {
        super(size);
    }

    public void calculateCausalOrder(PoSetMatrix PO, PoSetMatrix RF) {
        checkLoggerInfo("Calculating CO");
        union(PO, RF);
        calculateTransitiveClosure();
    }

    public boolean isCO(int i, int j) {
        return isRelation(i, j);
    }

    public boolean isCO(HistoryItem it1, HistoryItem it2) {
        return isCO(it1.getIndex(), it2.getIndex());
    }

    public LinkedList<Integer> CausalPast(int o) {
        LinkedList<Integer> causalPast = new LinkedList<Integer>();
        int n = getSize();
        boolean[][] co = getRelations();
        for (int i = 0; i < n; i++) {
            // TODO: <=
            if(co[i][o] || i == o){
                causalPast.add(i);
            }
        }
        return causalPast;
    }
}
