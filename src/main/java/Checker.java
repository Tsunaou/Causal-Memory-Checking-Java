import BadPattern.BAD_PATTERN;
import CausalChecker.CCChecker;
import CausalChecker.CCvChecker;
import CausalChecker.CMChecker;
import CausalLogger.CheckerWithLogger;
import DifferentiatedHistory.History;
import DifferentiatedHistory.HistoryReader;
import Relation.CausalOrder;
import Relation.ProgramOrder;
import Relation.ReadFrom;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
        this.reader = new HistoryReader(url, concurrency, true);
        this.maxIndex = Integer.MAX_VALUE;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    public Checker(String url, int concurrency, int maxIndex) {
        this.url = url;
        this.concurrency = concurrency;
        this.reader = new HistoryReader(url, concurrency);
        this.maxIndex = maxIndex;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    public void checkCausal(String type) {
        try {
            History history = reader.readHistory(maxIndex);
            int lastIndex = history.getLastIndex();
            System.err.println("LastIndex is " + lastIndex);
            for (int i = 0; i <= lastIndex; i++) {
                System.out.println(history.getOperations().get(i));
            }
            // 1. Get PO(Program Order)
            ProgramOrder PO = new ProgramOrder(lastIndex);
            PO.calculateProgramOrder(history, concurrency);
            PO.printRelations();
            // 2. Get RF(Read-From)
            ReadFrom RF = new ReadFrom(lastIndex);
            RF.calculateReadFrom(history, concurrency);
            RF.printRelations();
            // 3. Get CO(Causal Order)
            CausalOrder CO = new CausalOrder(lastIndex);
            CO.calculateCausalOrder(PO, RF);
            CO.printRelations();

            CCChecker ccChecker = null;
            CMChecker cmChecker = null;
            CCvChecker ccvChecker = null;
            HashMap<BAD_PATTERN, Boolean> result = null;
            switch (type) {
                case "CC":
                    ccChecker = new CCChecker(PO, RF, CO, history);
                    result = ccChecker.checkCausalConsistency();
                    break;
                case "CM":
                    cmChecker = new CMChecker(PO, RF, CO, history);
                    result = cmChecker.checkCausalMemory(true);
                    break;
                case "CCv":
                    ccvChecker = new CCvChecker(PO, RF, CO, history);
                    result = ccvChecker.checkCausalConvergence(true);
                    break;
                case "CMv":
                    HashMap<BAD_PATTERN, Boolean> resCCv = null;
                    cmChecker = new CMChecker(PO, RF, CO, history);
                    result = cmChecker.checkCausalMemory(true);
                    ccvChecker = new CCvChecker(PO, RF, CO, history);
                    resCCv = ccvChecker.checkCausalConvergence(false);
                    // Merge Results
                    for (Map.Entry<BAD_PATTERN, Boolean> entry : resCCv.entrySet()) {
                        BAD_PATTERN badPattern = entry.getKey();
                        boolean isBad = entry.getValue();
                        if(result.containsKey(badPattern)){
                            result.put(badPattern, result.get(badPattern) || isBad);
                        }else{
                            result.put(badPattern, isBad);
                        }
                    }
                    break;
                default:
                    System.err.println("Invalid Causal Variants");
                    System.exit(0);
            }
            checkLoggerInfo("Checking " + type + " outcome list");
            for (Map.Entry<BAD_PATTERN, Boolean> entry : result.entrySet()) {
                BAD_PATTERN bad = entry.getKey();
                boolean has_bad = entry.getValue();
                checkLoggerInfo("Collecting " + entry);
                if (has_bad) {
                    checkLoggerWarning("Inconsistency of " + bad);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        /*
         * args[0]: concurrency
         * args[1]: url of history
         * args[2]: type(CC,CM,CCv,CMv)
         * */
        long start = System.currentTimeMillis();

        Integer concurrency = null;
        String url = null;
        String type = null;

        // Default Value
        concurrency = 100;
        url = "/home/young/Desktop/NJU-Bachelor/Causal-Memory-Checking-Java/src/main/resources/adhoc/paper_history_a.edn";
        type = "CMv";

        if (args.length == 3){
            if (args[0].matches("\\d+")) {
                concurrency = Integer.parseInt(args[0]);
                url = args[1];
                type = args[2];
            } else {
                System.err.println("Parameter Error. The format should be [concurrency url type]");
            }
        }

        Checker checker = new Checker(url, concurrency);
        checker.checkCausal(type);

        long end = System.currentTimeMillis();
        checker.checkLoggerInfo("Cost " + (end - start) + " ms");
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
            System.err.println(df.format(new Date()) + " " + message);
        }
    }
}
