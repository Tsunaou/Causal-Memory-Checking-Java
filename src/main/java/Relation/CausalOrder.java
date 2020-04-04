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
}
