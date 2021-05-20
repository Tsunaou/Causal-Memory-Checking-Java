package Relation;

import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryItem;

import java.util.*;

public class HappenBefore {
    HashMap<Integer, HappenBeforeO> HB;

    public HappenBefore(int size, ProgramOrder PO, CausalOrder CO, History history) {
        this.HB = new HashMap<Integer, HappenBeforeO>();
        HashMap<Integer, ArrayList<HappenBeforeO>> HBoMapByProcess = new HashMap<>();
        // 根据线程给操作分组
        for(int o = 0; o < history.getOperations().size(); o++){
            HistoryItem op = history.getOperations().get(o);
            int process = op.getProcess();
            if(!HBoMapByProcess.containsKey(process)) {
                HBoMapByProcess.put(process, new ArrayList<>());

            }
            HappenBeforeO HBo = new HappenBeforeO(size, o);
            HBoMapByProcess.get(process).add(HBo);
        }
        // 根据 Program Order 对每个线程上的排序并计算HBo
        for (Map.Entry<Integer, ArrayList<HappenBeforeO>> entry : HBoMapByProcess.entrySet()) {
            int process = entry.getKey();
            ArrayList<HappenBeforeO> subHBoInProcess = entry.getValue();
            subHBoInProcess.sort(new Comparator<HappenBeforeO>() {
                @Override
                public int compare(HappenBeforeO hbo1, HappenBeforeO hbo2) {
                    if(hbo1.oIndex == hbo2.oIndex){
                        return 0;
                    }
                    boolean res = PO.isPO(hbo1.oIndex, hbo2.oIndex);
                    if (res) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

            // 测试排序结果是否正确
            for(int i=0; i<subHBoInProcess.size()-1;i++){
                HappenBeforeO hbo1 = subHBoInProcess.get(i);
                HappenBeforeO hbo2 = subHBoInProcess.get(i+1);
                assert (PO.isPO(hbo1.oIndex, hbo2.oIndex));
            }
            // 计算第一个操作的HBo
            HappenBeforeO HBoFirst = subHBoInProcess.get(0);
            HBoFirst.calculateHappenBefore(PO, CO, history);
            HB.put(HBoFirst.oIndex, HBoFirst);

            for(int i=1; i<subHBoInProcess.size();i++){
                HappenBeforeO preHBo = subHBoInProcess.get(i-1);
                HappenBeforeO HBo = subHBoInProcess.get(i);
                HBo.copy(preHBo);
                assert (HBo.equals(preHBo));
                HBo.calculateHappenBefore(PO, CO, history);
                HB.put(HBo.oIndex, HBo);
            }
        }
    }

    public HappenBeforeO getHBo(int oIndex) {
        return HB.get(oIndex);
    }
}
