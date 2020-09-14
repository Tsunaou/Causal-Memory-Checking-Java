package CycleChecker;

import CycleChecker.Johnson.ElementaryCyclesSearch;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CycleChecker {

    public static boolean CyclicHBo(boolean[][] relations, int index) {
        return Cyclic(relations);
    }

    public static boolean Cyclic(boolean[][] relations) {
        for (int i = 0; i < relations.length; i++) {
            if (relations[i][i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean CyclicJGrapht(boolean[][] relations){
        int n = relations.length;
        String[] nodes = new String[n];
        Graph<Integer, DefaultEdge> directedGraph = new DefaultDirectedGraph<Integer, DefaultEdge>(DefaultEdge.class);
        for (int i = 0; i < n; i++) {
            directedGraph.addVertex(i);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (relations[i][j]) {
                    directedGraph.addEdge(i, j);

                }
            }
        }

        CycleDetector<Integer, DefaultEdge> detector = new CycleDetector<Integer, DefaultEdge>(directedGraph);
        return detector.detectCycles();
    }

    @Deprecated
    public static boolean CyclicOld(boolean[][] relations) {
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
//                    System.out.print(node + " -> ");
                    return true;
                } else {
//                    System.out.print(node);
                    return true;
                }
            }
//            System.out.print("\n");
        }
        return cyclicFlag;
    }

    public static boolean TopoCycleChecker(boolean [][] relations){
        boolean cyclic = false;
        LinkedList<Integer> stack = new LinkedList<>();
        int size = relations.length;
        int[] inDegree = new int[size];
        int tempDegree = 0;

        //根据邻接矩阵为每个点初始化入度
        for(int j = 0; j < size; j++){
            tempDegree = 0;
            for (boolean[] relation : relations) {
                if (relation[j]) {
                    tempDegree++;
                }
            }
            inDegree[j] = tempDegree;
        }

        int count = 0; //判环辅助变量
        for(int i = 0; i < size; i++){
            if(inDegree[i] == 0){ //找到入度为0的点，入栈
                stack.addFirst(i);
                inDegree[i] = -1;
            }
        }
        int curID;
        while(!stack.isEmpty()){
            curID = stack.removeFirst();
            count++;
            for(int i = 0; i < size; i++){
                if(relations[curID][i]){
                    inDegree[i]--;
                    if(inDegree[i] == 0){
                        stack.addFirst(i);
                        inDegree[i] = -1;
                    }
                }
            }
        }
        if(count < size){
            cyclic = true;
            System.out.println("Detected Cyclic!");
        }
        System.out.println("Count: " + count + " Size:" + size);
        return cyclic;
    }

    public static void main(String[] args) {

        int size;
        int x;
        int y;
        Random rand = new Random(1000);

        boolean[][] a;
        for(int i=0;i<100;i++){
            size = rand.nextInt(1000);
            a = new boolean[size][size];
            System.out.println("--------------------- Round " + i + ", Size " + size);
            for(int j=0;j<200;j++){
                x = rand.nextInt(size);
                y = rand.nextInt(size);
                a[x][y] = true;
            }
            try {
                boolean f1 = TopoCycleChecker(a);
                System.out.println("Topo " + f1);
                boolean f2 = CyclicJGrapht(a);
                System.out.println("JGraph " + f2);
                boolean f3 = CyclicOld(a);
                System.out.println("Old " + f3);
                assert (f1 == f2);
                assert (f1 == f3);
            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }

        }

    }
}
