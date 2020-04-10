package CausalLogger;

public interface CheckerWithLogger {
    boolean LOGGER = true;
    boolean PRINT = true;
    void checkLoggerInfo(String message);
    void checkLoggerWarning(String message);
}
