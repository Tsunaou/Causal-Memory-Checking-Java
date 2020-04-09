package CausalLogger;

public interface CheckerWithLogger {
    boolean LOGGER = true;
    void checkLoggerInfo(String message);
    void checkLoggerWarning(String message);
}
