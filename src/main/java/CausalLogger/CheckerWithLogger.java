package CausalLogger;

public interface CheckerWithLogger {
    boolean LOGGER = false;
    void checkLoggerInfo(String message);
    void checkLoggerWarning(String message);
}
