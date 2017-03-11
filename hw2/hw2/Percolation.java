package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Percolation {
    Placeholder[][] grid;
    int gridlength;
    WeightedQuickUnionUF union;
    WeightedQuickUnionUF nobackwash;
    int numofopensites;
    int virtualtopsite;
    int virtualbottomsite;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        grid = new Placeholder[N][N];
        gridlength = N;
        numofopensites = 0;
        for (int i = 0; i < N; i++) {
            for (int k = 0; k < N; k++) {
                grid[i][k] = new Placeholder(i, k, N);
            }
        }
        virtualtopsite = N * N;
        virtualbottomsite = N * N + 1;
        union = new WeightedQuickUnionUF(N * N + 2);
        nobackwash = new WeightedQuickUnionUF(N * N + 2);
        for (int i = 0; i < gridlength; i++) {
            union.union(grid[0][i].xyTo1D(0, i), virtualtopsite);
            nobackwash.union(grid[0][i].xyTo1D(0, i), virtualtopsite);
        }
        for (int k = 0; k < gridlength; k++) {
            union.union(grid[gridlength - 1][k].xyTo1D(gridlength - 1, k), virtualbottomsite);
        }
    }

    public void open(int row, int col) {
        if (row > gridlength - 1 || col > gridlength - 1 || row < 0 || col < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!grid[row][col].isOpen()) {
            grid[row][col].open();
            int index = grid[row][col].xyTo1D(row, col);
            int left = index - 1;
            int right = index + 1;
            int up = index - gridlength;
            int down = index + gridlength;
            if (left >= 0 && index % gridlength != 0 && grid[row][col - 1].isOpen()) {
                union.union(index, left);
                nobackwash.union(index, left);
            }
            if (right < gridlength * gridlength && (index + 1) % gridlength != 0
                    && grid[row][col + 1].isOpen()) {
                union.union(index, right);
                nobackwash.union(index, right);
            }
            if (up >= 0 && grid[row - 1][col].isOpen()) {
                union.union(index, up);
                nobackwash.union(index, up);
            }
            if (down < gridlength * gridlength && grid[row + 1][col].isOpen()) {
                union.union(index, down);
                nobackwash.union(index, down);
            }
            numofopensites = numofopensites + 1;
        }
    }

    public boolean isOpen(int row, int col) {
        if (row > gridlength - 1 || col > gridlength - 1 || row < 0 || col < 0) {
            throw new IndexOutOfBoundsException();
        }
        return grid[row][col].isOpen();
    }

    public boolean isFull(int row, int col) {
        if (row > gridlength - 1 || col > gridlength - 1 || row < 0 || col < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (grid[row][col].isOpen()) {
            int index = grid[row][col].xyTo1D(row, col);
            return nobackwash.connected(index, virtualtopsite);
        }
        return false;
    }

    public int numberOfOpenSites() {
        return numofopensites;
    }

    public boolean percolates() {
        if (gridlength == 1) {
            return isOpen(0, 0);
        }
        return union.connected(virtualtopsite, virtualbottomsite);
    }

    private class Placeholder {
        boolean isopen;
        int index;
        int rownum;
        int colnum;
        int rowlength;

        Placeholder(int row, int col, int length) {
            rownum = row;
            colnum = col;
            index = xyTo1D(rownum, colnum);
            rowlength = length;
            isopen = false;
        }

        public int xyTo1D(int r, int c) {
            return r * rowlength + c;
        }

        public boolean isOpen() {
            return isopen;
        }

        public void open() {
            isopen = true;
        }
    }


    @Test
    public static void main(String[] args) {
        Percolation testing = new Percolation(5);
        assertEquals(false, testing.percolates());
        testing.open(3, 4);
        testing.open(2, 4);
        assertEquals(true, testing.isOpen(3, 4));
        testing.open(2, 2);
        testing.open(2, 3);
        assertEquals(false, testing.percolates());
        testing.open(0, 2);
        testing.open(1, 2);
        assertEquals(true, testing.isFull(2, 2));
        testing.open(4, 4);
        assertEquals(true, testing.percolates());
        assertEquals(7, testing.numberOfOpenSites());
        assertEquals(false, testing.isOpen(2, 1));

        Percolation testing2 = new Percolation(2);
        testing2.open(0, 0);
        testing2.open(0, 1);
        testing2.open(1, 0);
        assertEquals(false, testing2.isOpen(1, 1));

        Percolation input8 = new Percolation(8);
        input8.open(0, 2);
        input8.open(1, 5);
        assertEquals(false, input8.isFull(1, 5));

        Percolation input1 = new Percolation(1);
        assertEquals(false, input1.percolates());
    }
}                       
