package Relation;

import Exception.ClosureException;

public class PoSetMatrix implements PoSet {

    private boolean[][] relations;
    private boolean isClose;  // whether the transitive closure is calculate
    private int size;

    public PoSetMatrix(int size) {
        int n = size + 1;
        this.relations = new boolean[n][n];
        this.size = n;
        this.isClose = false;
    }

    public void addRelation(int a, int b) {
        relations[a][b] = true;
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
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (relations[i][j]) {
                    System.out.printf("(%d, %d), ", i, j);
                }
            }
        }
        System.out.println();
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
}
