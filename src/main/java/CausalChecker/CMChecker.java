package CausalChecker;

import BadPattern.BAD_PATTERN;
import CycleChecker.CycleChecker;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;
import Relation.*;

import javax.sound.midi.Soundbank;
import java.text.SimpleDateFormat;
import java.util.*;

public class CMChecker extends CCChecker {

    public CMChecker(ProgramOrder PO, ReadFrom RF, CausalOrder CO, History history) {
        super(PO, RF, CO, history);
        this.initCMMap();
    }

    void initCMMap() {
        badMap.put(BAD_PATTERN.WriteHBInitRead, false);
        badMap.put(BAD_PATTERN.CyclicHB, false);
    }

    private void checkCM() {
        int size = PO.getSize();
        int n = size -1;
        // New Version
        HashMap<Integer, ArrayList<Integer>> OpMapByProcess = new HashMap<>();

        // 根据线程给操作分组
        for(int o = 0; o < history.getOperations().size(); o++){
            HistoryItem op = history.getOperations().get(o);
            int process = op.getProcess();
            if(!OpMapByProcess.containsKey(process)) {
                OpMapByProcess.put(process, new ArrayList<>());
            }
            OpMapByProcess.get(process).add(o);
        }
        // 根据 Program Order 对每个线程上的排序并计算HBo
        for (Map.Entry<Integer, ArrayList<Integer>> entry : OpMapByProcess.entrySet()) {
            ArrayList<Integer> subOpInProcess = entry.getValue();
            subOpInProcess.sort((o1, o2) -> {
                if(o1.equals(o2)){
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
            for(int i=0; i<subOpInProcess.size()-1;i++){
                int o1 = subOpInProcess.get(i);
                int o2 = subOpInProcess.get(i+1);
                assert (PO.isPO(o1, o2));
                HappenBeforeO hbo1 = new HappenBeforeO(n, o1);
                hbo1.calculateHappenBefore(PO, CO, history);
                HappenBeforeO hbo2 = new HappenBeforeO(n, o2);
                hbo2.calculateHappenBefore(PO, CO, history);
                assert (hbo1.isSubSetTo(hbo2));
            }

            // 计算第一个操作的HBo
            int firstOp = subOpInProcess.get(0);
            HappenBeforeO preHBo = new HappenBeforeO(n, firstOp);
            preHBo.calculateHappenBefore(PO, CO, history);

            int curOp = firstOp;
            int preOp = firstOp;
            HappenBeforeO curHBo = preHBo;
            for(int i=1; i<subOpInProcess.size();i++){
                curOp = subOpInProcess.get(i);
                curHBo = new HappenBeforeO(n, curOp);
                curHBo.copy(preHBo);
                assert (preHBo.isSubSetTo(curHBo));

//                if(operations.get(curOp).isWrite()){
//                    curHBo.update_HBo(preOp, curOp);
//                }else{
//                    curHBo.calculateHappenBefore(PO, CO, history);
//                }
                curHBo.calculateHappenBefore(PO, CO, history);
                assert (preHBo.isSubSetTo(curHBo));

                preHBo = curHBo;
                preOp = curOp;

                checkLoggerInfo("o is " + curOp);
                HappenBeforeO HBo = new HappenBeforeO(n, curOp);
                HBo.calculateHappenBefore(PO, CO, history);
                if(!curHBo.equals(HBo)){
                    checkLoggerInfo("Sorry");
                    checkLoggerInfo("Operation is " + operations.get(curOp));
                    checkLoggerInfo("curHBo");
                    curHBo.printRelations();
                    checkLoggerInfo("HBo");
                    HBo.printRelations();

                    boolean[][] curMatrix = curHBo.getRelations(true);
                    boolean[][] stdMatrix = HBo.getRelations(true);
                    for(int ii=0;ii<n;ii++){
                        for(int jj=0;jj<n;jj++){
                            if(curMatrix[ii][jj] != stdMatrix[ii][jj]){
                                System.out.println("curMatrix["+ii+"]["+jj+"] is " + curMatrix[ii][jj]);
                                System.out.println("stdMatrix["+ii+"]["+jj+"] is " + stdMatrix[ii][jj]);
                            }
                        }
                    }
                    System.out.println("dddd");
                }

                assert (curHBo.equals(HBo));

            }
            // 此时每个线程上最后一个操作的HB就是需要检验的HB，此时为 curHBo
            checkWriteHBInitRead(curHBo);
            checkCyclicHB(curHBo);
        }
    }


    public HashMap<BAD_PATTERN, Boolean> checkCausalMemory(boolean checkCC) {
        checkLoggerInfo("Starting Check Causal Memory");
        if (checkCC) {
            checkCC();
        }
        checkCM();
        return this.badMap;
    }

    void checkWriteHBInitRead(HappenBeforeO HBo) {

        if (badMap.get(BAD_PATTERN.WriteHBInitRead)) {
            return;
        }

        checkLoggerInfo("Checking WriteHBInitRead " + HBo.oIndex);
        HistoryItem o = operations.get(HBo.oIndex);
        for (HistoryItem r : readHistories) {
            if (PO.isPO(r, o) && r.readInit()) {
                for (HistoryItem w : writeHistories) {
                    // w <HBo r and var(w) = var(r)
                    if (HBo.isHBo(w, r) && (w.getK().equals(r.getK()))) {
                        badMap.put(BAD_PATTERN.WriteHBInitRead, true);
                        return;
                    }
                }
            }
        }
    }

    void checkCyclicHB(HappenBeforeO HBo) {

        if (badMap.get(BAD_PATTERN.CyclicHB)) {
            return;
        }

        checkLoggerInfo("Checking CyclicHB " + HBo.oIndex);
        boolean cyclic = CycleChecker.Cyclic(HBo.getRelations(true));
//        HBo.printRelationsMatrix();
        if (cyclic) {
            badMap.put(BAD_PATTERN.CyclicHB, true);
        }
    }

    @Override
    public void checkLoggerInfo(String message) {
        if (LOGGER) {
            logger.info(message);
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()) + " " + message);
        }
    }

    @Override
    public void checkLoggerWarning(String message) {
        if (LOGGER) {
            logger.warning(message);
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()) + " " + message);
        }
    }
}
