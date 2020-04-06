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
    boolean file;

    public Checker(String url, int concurrency) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency);
    }

    public Checker(String url, int concurrency, boolean file) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency, file);
    }

    public void checkCausal(boolean CC, boolean CM) {
        try {
            History history = reader.readHistory();
            int lastIndex = history.getLastIndex();
            System.err.println("LastIndex is " + lastIndex);
            // get program order
            ProgramOrder PO = new ProgramOrder(lastIndex);
            PO.calculateProgramOrder(history, concurrency);
            // get read-from
            ReadFrom RF = new ReadFrom(lastIndex);
            RF.calculateReadFrom(history, concurrency);
            // get causal order
            CausalOrder CO = new CausalOrder(lastIndex);
            CO.calculateCausalOrder(PO, RF);
            if (CC) {
                // Causal consistency checker
                CCChecker ccChecker = new CCChecker(PO, RF, CO, history);
                ccChecker.checkCausalConsistency();
            }
            if (CM) {
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
        int concurrency = 100;
        String url = "tiny_history.edn";
        boolean file = false;
        if(args.length == 2 && args[0].matches("\\d+")){
            concurrency = Integer.parseInt(args[0]);
            url = args[1];
            file = true;
        }
        Checker cheker = new Checker(url, concurrency, file);
        cheker.checkCausalConsistency();
//        cheker.checkCausalMemory();
    }
}
