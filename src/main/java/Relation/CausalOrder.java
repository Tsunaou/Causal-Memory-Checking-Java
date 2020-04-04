package Relation;

import History.HistoryItem;

import java.util.ArrayList;

public class CausalOrder extends PoSetMatrix{
    public CausalOrder(int size) {
        super(size);
    }

    public void calculateCausalOrder(PoSetMatrix PO, PoSetMatrix RF) {
        union(PO, RF);
        calculateTransitiveClosure();
    }

    public boolean isCO(int i, int j){
        return isRelation(i, j);
    }

    public boolean isCO(HistoryItem it1, HistoryItem it2){
        return isCO(it1.getIndex(),it2.getIndex());
    }
}
