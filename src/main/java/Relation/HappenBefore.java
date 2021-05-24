package Relation;

import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;

import java.util.*;

public class HappenBefore {
    HashMap<Integer, HappenBeforeO> HB;

    public HappenBefore(int size, ProgramOrder PO, CausalOrder CO, History history) {
        this.HB = new HashMap<>();
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
            int process = entry.getKey();
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
            // 计算第一个操作的HBo
            int firstOp = subOpInProcess.get(0);
            HappenBeforeO preHBo = new HappenBeforeO(size, firstOp);
            preHBo.calculateHappenBefore(PO, CO, history);

            int curOp;
            HappenBeforeO curHBo = preHBo;
            for(int i=1; i<subOpInProcess.size();i++){
                curOp = subOpInProcess.get(i);
                curHBo = new HappenBeforeO(size, curOp);
                curHBo.copy(preHBo);
                curHBo.calculateHappenBefore(PO, CO, history);
                HB.put(curHBo.oIndex, curHBo);
                preHBo = curHBo;
            }
            HB.put(curHBo.oIndex, curHBo);
        }
    }

    public HappenBeforeO getHBo(int oIndex) {
        return HB.get(oIndex);
    }

    public HashMap<Integer, HappenBeforeO> getHB() {
        return HB;
    }
}
