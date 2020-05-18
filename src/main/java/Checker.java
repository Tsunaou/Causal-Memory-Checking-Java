import CausalChecker.CCChecker;
import CausalChecker.CMChecker;
import CausalLogger.CheckerWithLogger;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryReader;
import Relation.CausalOrder;
import Relation.HappenBefore;
import Relation.ProgramOrder;
import Relation.ReadFrom;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Checker implements CheckerWithLogger {

    String url;
    int concurrency;
    HistoryReader reader;
    boolean file;
    int maxIndex;
    protected Logger logger;


    public Checker(String url, int concurrency) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency);
        this.maxIndex = Integer.MAX_VALUE;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    public Checker(String url, int concurrency, boolean file) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency, file);
        this.maxIndex = Integer.MAX_VALUE;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    public Checker(String url, int concurrency, boolean file, int maxIndex) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency, file);
        this.maxIndex = maxIndex;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
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

        long start = System.currentTimeMillis();

        int concurrency = 100;
//        String url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/adhoc/paper_history.edn";
//        String url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/history.edn";
//        String url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/latest/history.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\adhoc\\paper_history_2.edn";
//        String url = "E:\\Causal-Memory-Checking-Java\\src\\main\\resources\\latest\\history.edn";
//        String url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/adhoc/paper_history.edn";
        // 并发100 写50 读50 每键5客户端
//        String pre = "/home/young/Desktop/NJU-Bachelor/mongodb/store/mongo-causal-register-wc-:majority-rc-:majority-ti-360-sd-2-cry-100-wn-50-rn-50-cpk-5/latest/";
        // 并发100 写100 读100 每键5客户端
//        String pre = "/home/young/Desktop/NJU-Bachelor/mongodb/store/mongo-causal-register-wc-:majority-rc-:majority-ti-360-sd-2-cry-100-wn-100-rn-100-cpk-5/latest/";
        // 并发100 写50 读50 每键10客户端
//        String pre = "/home/young/Desktop/NJU-Bachelor/mongodb/store/mongo-causal-register-wc-:majority-rc-:majority-ti-360-sd-2-cry-100-wn-50-rn-50-cpk-10/latest/";
        // 并发100 写50 读50 每键5客户端 w1 local
//        String pre = "/home/young/Desktop/NJU-Bachelor/mongodb/store/mongo-causal-register-wc-:w1-rc-:local-ti-360-sd-2-cry-100-wn-50-rn-50-cpk-5/latest/";
        // 并发100 写50 读50 每键20客户端
        String pre = "/home/young/Desktop/NJU-Bachelor/mongodb/store/mongo-causal-register-wc-:majority-rc-:majority-ti-360-sd-2-cry-100-wn-50-rn-50-cpk-20/latest/";
        String url = pre + "history.edn";
        boolean file = false;
        boolean typeCC = false;
        int maxIndex = Integer.MAX_VALUE;
        maxIndex = 5000;
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

        long end = System.currentTimeMillis();

        cheker.checkLoggerInfo("Cost " + (end-start) + " ms");
    }

    @Override
    public void checkLoggerInfo(String message) {
        if (LOGGER) {
            logger.info(message);
        } else {
            System.out.println(message);
        }
    }

    @Override
    public void checkLoggerWarning(String message) {
        if (LOGGER) {
            logger.warning(message);
        } else {
            System.out.println(message);
        }
    }
}
