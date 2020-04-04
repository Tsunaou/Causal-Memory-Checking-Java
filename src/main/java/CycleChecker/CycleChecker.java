package CycleChecker;

import java.util.List;

public class CycleChecker {

    public static boolean Cyclic(boolean [][] relations){
        int n = relations.length;
        String[] nodes = new String[n];

        for (int i = 0; i < n; i++) {
            nodes[i] = "Node " + i;
        }

        ElementaryCyclesSearch ecs = new ElementaryCyclesSearch(relations, nodes);
        List cycles = ecs.getElementaryCycles();
        boolean cyclicFlag = false;
        for (int i = 0; i < cycles.size(); i++) {
            List cycle = (List) cycles.get(i);
            for (int j = 0; j < cycle.size(); j++) {
                String node = (String) cycle.get(j);
                if (j < cycle.size() - 1) {
                    System.out.print(node + " -> ");
                    cyclicFlag = true;
                } else {
                    System.out.print(node);
                    cyclicFlag = true;
                }
            }
            System.out.print("\n");
        }
        return cyclicFlag;
    }

    public static void main(String[] args) {

    }
}
