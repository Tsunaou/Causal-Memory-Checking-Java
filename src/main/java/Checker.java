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
    int maxIndex;

    public Checker(String url, int concurrency) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency);
        this.maxIndex = Integer.MAX_VALUE;
    }

    public Checker(String url, int concurrency, boolean file) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency, file);
        this.maxIndex = Integer.MAX_VALUE;
    }

    public Checker(String url, int concurrency, boolean file, int maxIndex) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency, file);
        this.maxIndex = maxIndex;
    }

    public void checkCausal(boolean CC, boolean CM) {
        try {
            History history = reader.readHistory(maxIndex);
            int lastIndex = history.getLastIndex();
            System.err.println("LastIndex is " + lastIndex);
            // get program order
            ProgramOrder PO = new ProgramOrder(lastIndex);
            PO.calculateProgramOrder(history, concurrency);
//            PO.printRelations();
            // get read-from
            ReadFrom RF = new ReadFrom(lastIndex);
            RF.calculateReadFrom(history, concurrency);
//            RF.printRelations();
            // get causal order
            CausalOrder CO = new CausalOrder(lastIndex);
            CO.calculateCausalOrder(PO, RF);
            if (CC) {
                // Causal consistency checker
                CCChecker ccChecker = new CCChecker(PO, RF, CO, history);
                ccChecker.checkCausalConsistency();
            }
            if (CM) {
                // Causal Memory checker
                CMChecker cmChecker = new CMChecker(PO, RF, CO, history);
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
        String url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/adhoc/hy_history.edn";
//        String url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/small_history.edn";
        boolean file = false;
        boolean typeCC = false;
        int maxIndex = Integer.MAX_VALUE;
        if (args.length == 3 && args[0].matches("\\d+")) {
            concurrency = Integer.parseInt(args[0]);
            url = args[1];
            file = true;
            if (args[2].equals("CM")) {
                typeCC = false;
            }
        }
        if (args.length == 4 && args[0].matches("\\d+") && args[3].matches("\\d+")) {
            concurrency = Integer.parseInt(args[0]);
            url = args[1];
            file = true;
            if (args[2].equals("CM")) {
                typeCC = false;
            }
            maxIndex = Integer.parseInt(args[3]);
        }


        Checker cheker = new Checker(url, concurrency, file, maxIndex);
        if (typeCC) {
            cheker.checkCausalConsistency();
        } else {
            cheker.checkCausalMemory();
        }
    }
}
