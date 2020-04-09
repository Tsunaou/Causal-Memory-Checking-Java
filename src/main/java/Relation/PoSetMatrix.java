package Relation;

import CausalLogger.CausalLogHandler;
import CausalLogger.CheckerWithLogger;
import Exception.ClosureException;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PoSetMatrix implements PoSet, CheckerWithLogger {

    private boolean[][] relations;
    private boolean isClose;  // whether the transitive closure is calculate
    private int size;
    protected Logger logger;
    private boolean NO_LOGGER = false;


    public PoSetMatrix(int size) {
        int n = size + 1;
        this.relations = new boolean[n][n];
        this.size = n;
        this.isClose = false;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.setLevel(Level.ALL);
    }

    public void addRelation(int a, int b) {
        relations[a][b] = true;
    }

    @Deprecated
    public void addNewLink(int a, int b) {
        assert (relations[a][b]);
        int n = this.size;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (relations[i][a] && relations[b][j] && !relations[i][j]) {
                    relations[i][j] = true;
                }
            }
        }
    }

    public void calculateTransitiveClosure() {
        int n = this.size;
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (relations[i][j]) {
                        continue;
                    }
                    relations[i][j] = relations[i][k] && relations[k][j];
                }
            }
        }
        isClose = true;
    }

    public void printRelations() {
        int n = size;
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (relations[i][j]) {
                    System.out.printf("(%d, %d), ", i, j);
                    count = count + 1;
                }
            }
        }
        if (count != 0) {
            System.out.println();
        }
//        System.out.println(this.getClass().getName() + " has " + count + " relations");
    }

    public boolean[][] getRelations() {
        if (isClose) {
            return relations;
        } else {
            System.out.println("Transitive closure is not calculated");
            return null;
        }
    }

    public void union(PoSetMatrix s1, PoSetMatrix s2) {
        assert (s1.size == s2.size);
        assert (size == s1.size);

        boolean[][] r1 = s1.getRelations();
        boolean[][] r2 = s2.getRelations();

        int n = size;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                relations[i][j] = r1[i][j] || r2[i][j];
            }
        }

    }

    public boolean isClose() {
        return isClose;
    }

    public int getSize() {
        return size;
    }

    public boolean isRelation(int i, int j) {
        return relations[i][j];
    }

    @Override
    public void checkLoggerInfo(String message) {
        if(LOGGER){
            logger.info(message);
        }else {
            System.out.println(message);
        }
    }

    @Override
    public void checkLoggerWarning(String message) {
        if(LOGGER){
            logger.warning(message);
        }else {
            System.out.println(message);
        }
    }
}
