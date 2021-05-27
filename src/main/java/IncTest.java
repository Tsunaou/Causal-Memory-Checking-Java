import IncrementalCCC.HappenBeforeGenerator;
import IncrementalCCC.Relations.BasicRelation;

import java.util.HashMap;
import java.util.Iterator;

public class IncTest {
    public static void main(String[] args) throws Exception {
        HappenBeforeGenerator hboGenerator = new HappenBeforeGenerator();
        String url = "D:\\Education\\Programs\\Java\\Causal-Memory-Checking-Java\\src\\main\\resources\\latest\\history_1w.edn";
        hboGenerator.setMaxIndex(10);
        HashMap<Integer, BasicRelation> processMatrix = hboGenerator.getProcessMatrix(url);
        Iterator<Integer> iterator = processMatrix.keySet().iterator();

        while(iterator.hasNext()) {
            Integer curProcess = iterator.next();
            BasicRelation curMatrix = processMatrix.get(curProcess);
            System.out.println("Process " + curProcess);
            curMatrix.printMatrix();
            curMatrix.printRelationMatrix();
        }
    }
}
