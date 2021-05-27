import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import DifferentiatedHistory.HistoryReader;
import IncrementalCCC.HappenBeforeGenerator;
import IncrementalCCC.Relations.BasicRelation;
import Relation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RelationReporter {


    public static void main(String[] args) throws Exception {
//        String edn = "D:\\Education\\Programs\\Java\\Causal-Memory-Checking-Java\\src\\main\\resources\\adhoc\\paper_history_e.edn";
//        String edn = "D:\\Education\\Programs\\Java\\Causal-Memory-Checking-Java\\src\\main\\resources\\adhoc\\paper_history_e.edn";
        String edn = "D:\\Education\\Programs\\Java\\Causal-Memory-Checking-Java\\src\\main\\resources\\latest\\history_1w.edn";
        int concurrency = 10; // Actually, does not work
        int maxIndex = 130;
        HistoryReader reader = new HistoryReader(edn, concurrency, true);
        History history = reader.readHistory(maxIndex);
        int lastIndex = history.getLastIndex();
        System.err.println("LastIndex is " + lastIndex);
        for (int i = 0; i <= lastIndex; i++) {
            System.out.println(history.getOperations().get(i));
        }
        // 1. Get PO(Program Order)
        ProgramOrder PO = new ProgramOrder(lastIndex);
        PO.calculateProgramOrder(history, concurrency);
        System.out.println("Program Order");
        PO.printRelations();
        // 2. Get RF(Read-From)
        ReadFrom RF = new ReadFrom(lastIndex);
        RF.calculateReadFrom(history, concurrency);
        System.out.println("Read From");
        RF.printRelations();
        // 3. Get CO(Causal Order)
        CausalOrder CO = new CausalOrder(lastIndex);
        CO.calculateCausalOrder(PO, RF);
        System.out.println("Causal Order");
        CO.printRelations();

        // 4. Get HB(Happened-Before Order)
        // 对照组
        HappenBeforeGenerator hboGenerator = new HappenBeforeGenerator();
        hboGenerator.setMaxIndex(maxIndex);
        HashMap<Integer, BasicRelation> processMatrix = hboGenerator.getProcessMatrix(edn);
        // 实验组
        int size = PO.getSize();
        ArrayList<HistoryItem> operations = history.getOperations();
        // New Version
        HashMap<Integer, ArrayList<Integer>> OpMapByProcess = new HashMap<>();

        // 根据线程给操作分组
        for (int o = 0; o < history.getOperations().size(); o++) {
            HistoryItem op = history.getOperations().get(o);
            int process = op.getProcess();
            if (!OpMapByProcess.containsKey(process)) {
                OpMapByProcess.put(process, new ArrayList<>());
            }
            OpMapByProcess.get(process).add(o);
        }
        // 根据 Program Order 对每个线程上的排序并计算HBo
        for (Map.Entry<Integer, ArrayList<Integer>> entry : OpMapByProcess.entrySet()) {
            Integer curProcess = entry.getKey();
            System.out.println("Checking for process " + curProcess);
            // 对照组， 当前线程上最后一个操作的Happened Before
            boolean[][] pmatrix = processMatrix.get(curProcess).basic_matrix();

            // 实验组
            ArrayList<Integer> subOpInProcess = entry.getValue();
            subOpInProcess.sort((o1, o2) -> {
                if (o1.equals(o2)) {
                    return 0;
                }
                boolean res = PO.isPO(o1, o2);
                if (res) {
                    return -1;
                } else {
                    return 1;
                }
            });

            // 测试排序结果是否正确
            for (int i = 0; i < subOpInProcess.size() - 1; i++) {
                int o1 = subOpInProcess.get(i);
                int o2 = subOpInProcess.get(i + 1);
                assert (PO.isPO(o1, o2));
//                HappenBeforeO hbo1 = new HappenBeforeO(size - 1, o1);
//                hbo1.calculateHappenBefore(PO, CO, history);
//                HappenBeforeO hbo2 = new HappenBeforeO(size - 1, o2);
//                hbo2.calculateHappenBefore(PO, CO, history);
//                assert (hbo1.isSubSetTo(hbo2));
            }

            // 计算该线程上最后一个操作的HBo
            int opNumCurProcess = subOpInProcess.size();
            int lastOp = subOpInProcess.get(opNumCurProcess - 1);
            System.out.println("Last op is " + lastOp + " " + operations.get(lastOp));
            HappenBeforeO lastHBo = new HappenBeforeO(size - 1, lastOp);
            lastHBo.calculateHappenBefore(PO, CO, history);
            boolean[][] cmatrix = lastHBo.getRelations(true);
            System.out.println(pmatrix.length);
            System.out.println(cmatrix.length);
            PoSetMatrix poset = new PoSetMatrix(size - 1);
            poset.setRelations(pmatrix);
            if (!poset.equals(lastHBo)) {
                System.out.println("Std Version");
                poset.printRelations();
                System.out.println("My Version");
                lastHBo.printRelations();
                assert poset.equals(lastHBo);
            }

//            // 计算第一个操作的HBo
//            int firstOp = subOpInProcess.get(0);
//            HappenBeforeO preHBo = new HappenBeforeO(n, firstOp);
//            preHBo.calculateHappenBefore(PO, CO, history);
//
//            int curOp = firstOp;
//            int preOp = firstOp;
//            HappenBeforeO curHBo = preHBo;
//            for (int i = 1; i < subOpInProcess.size(); i++) {
//                curOp = subOpInProcess.get(i);
//                curHBo = new HappenBeforeO(n, curOp);
//                curHBo.copy(preHBo);
//                assert (preHBo.isSubSetTo(curHBo));
//
////                if(operations.get(curOp).isWrite()){
////                    curHBo.update_HBo(preOp, curOp);
////                }else{
////                    curHBo.calculateHappenBefore(PO, CO, history);
////                }
//                curHBo.calculateHappenBefore(PO, CO, history);
//                assert (preHBo.isSubSetTo(curHBo));
//
//                preHBo = curHBo;
//                preOp = curOp;
//
//                System.out.println("o is " + curOp);
//                HappenBeforeO HBo = new HappenBeforeO(n, curOp);
//                HBo.calculateHappenBefore(PO, CO, history);
//                if (!curHBo.equals(HBo)) {
//                    System.out.println("Sorry");
//                    System.out.println("Operation is " + operations.get(curOp));
//                    System.out.println("curHBo");
//                    curHBo.printRelations();
//                    System.out.println("HBo");
//                    HBo.printRelations();
//
//                    boolean[][] curMatrix = curHBo.getRelations(true);
//                    boolean[][] stdMatrix = HBo.getRelations(true);
//                    for (int ii = 0; ii < n; ii++) {
//                        for (int jj = 0; jj < n; jj++) {
//                            if (curMatrix[ii][jj] != stdMatrix[ii][jj]) {
//                                System.out.println("curMatrix[" + ii + "][" + jj + "] is " + curMatrix[ii][jj]);
//                                System.out.println("stdMatrix[" + ii + "][" + jj + "] is " + stdMatrix[ii][jj]);
//                            }
//                        }
//                    }
//                    System.out.println("dddd");
//                }
//
//                assert (curHBo.equals(HBo));
//
//            }
        }
    }
}
