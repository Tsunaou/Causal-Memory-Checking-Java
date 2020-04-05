import CausalChecker.CCChecker;
import CausalChecker.CMChecker;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryReader;
import Relation.CausalOrder;
import Relation.HappenBefore;
import Relation.ProgramOrder;
import Relation.ReadFrom;


import java.io.IOException;

public class Checker {

    String url;
    int concurrency;
    HistoryReader reader;

    public Checker(String url, int concurrency) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency);
    }

    public void checkCausal(boolean CC, boolean CM){
        try {
            History history = reader.readHistory();
            int lastIndex = history.getLastIndex();
            // get program order
            ProgramOrder PO = new ProgramOrder(lastIndex);
            PO.calculateProgramOrder(history, concurrency);
            // get read-from
            ReadFrom RF = new ReadFrom(lastIndex);
            RF.calculateReadFrom(history, concurrency);
            // get causal order
            CausalOrder CO = new CausalOrder(lastIndex);
            CO.calculateCausalOrder(PO, RF);
            if(CC){
                // Causal consistency checker
                CCChecker ccChecker = new CCChecker(PO, RF, CO, history);
                ccChecker.checkCausalConsistency();
            }
            if(CM){
                // get happen before
                HappenBefore HB = new HappenBefore(lastIndex, PO, CO, history);
                // Causal Memory checker
                CMChecker cmChecker = new CMChecker(PO, RF, CO, HB, history);
                cmChecker.checkCausalMemory();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkCausalConsistency() {
        checkCausal(true, false);
    }

    public void checkCausalMemory() {
        checkCausal(false, true);
    }

    public static void main(String[] args) {
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\history.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\tiny_history.edn";
        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\small_history.edn";
        int concurrency = 10;
        Checker cheker = new Checker(url, concurrency);
        cheker.checkCausalMemory();
    }
}
