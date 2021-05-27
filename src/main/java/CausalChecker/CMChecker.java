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
            int processNum = entry.getKey();
            checkLoggerInfo("Checking Happened Before Order in Process " + processNum);
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
            }

            int opNumCurProcess = subOpInProcess.size();
            int lastOp = subOpInProcess.get(opNumCurProcess - 1);
            HappenBeforeO lastHBo = new HappenBeforeO(size - 1, lastOp);
            lastHBo.calculateHappenBefore(PO, CO, history);
            // 此时每个线程上最后一个操作的HB就是需要检验的HB，此时为 curHBo
            checkWriteHBInitRead(lastHBo);
            checkCyclicHB(lastHBo);
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
