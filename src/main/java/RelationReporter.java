import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryReader;
import Relation.CausalOrder;
import Relation.HappenBeforeO;
import Relation.ProgramOrder;
import Relation.ReadFrom;

import java.io.IOException;

public class RelationReporter {


    public static void main(String[] args) throws IOException {
//        String edn = "D:\\Education\\Programs\\Java\\Causal-Memory-Checking-Java\\src\\main\\resources\\adhoc\\paper_history_e.edn";
        String edn = "D:\\Education\\Programs\\Java\\Causal-Memory-Checking-Java\\src\\main\\resources\\adhoc\\paper_history_a.edn";
        int concurrency = 10; // Actually, does not work
        int maxIndex = 100000;
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
        int size = PO.getSize();
        for (int o = 0; o < history.getOperations().size(); o++) {
            HappenBeforeO HBo = new HappenBeforeO(size - 1, o);
            System.out.println("----------HBo " + o);
            HBo.calculateHappenBefore(PO, CO, history);
//            HBo.printRelations();
            HBo.printRelationsMatrix();
        }
    }
}
