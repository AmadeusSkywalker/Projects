package hw3.puzzle;

import java.util.ArrayList;

public class Board implements WorldState {
    int[][] board;
    int size;
    int[][] expected;

    public Board(int[][] tiles) {
        board = new int[tiles.length][tiles[0].length];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                board[i][j] = tiles[i][j];
            }
        }
        size = board.length;
        expected = new int[size][size];
        int index = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                expected[i][j] = index;
                index = index + 1;
            }
        }
        expected[size - 1][size - 1] = 0;
    }

    public int tileAt(int i, int j) {
        if (i >= 0 && i < size() && j >= 0 && j < size()) {
            return board[i][j];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int size() {
        return size;
    }


    public Iterable<WorldState> neighbors() {
        if (size() < 2) {
            return null;
        }
        ArrayList<WorldState> boards = new ArrayList<WorldState>();
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (board[i][j] == 0) {
                    if (j > 0) {
                        boards.add(neighbor(i, j, i, j - 1));
                    }
                    if (j < size() - 1) {
                        boards.add(neighbor(i, j, i, j + 1));
                    }
                    if (i > 0) {
                        boards.add(neighbor(i, j, i - 1, j));
                    }
                    if (i < size() - 1) {
                        boards.add(neighbor(i, j, i + 1, j));
                    }
                    return boards;
                }
            }
        }
        return null;
    }

    private Board neighbor(int a, int b, int c, int d) {
        Board result = new Board(board);
        int temp = result.board[a][b];
        result.board[a][b] = result.board[c][d];
        result.board[c][d] = temp;
        return result;
    }


    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (!(i == size() - 1 && j == size() - 1)) {
                    if (board[i][j] != expected[i][j]) {
                        hamming = hamming + 1;
                    }
                }
            }
        }
        return hamming;
    }


    public int manhattan() {
        int mahattan = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (board[i][j] != expected[i][j]) {
                    int index = board[i][j];
                    if (index != 0) {
                        int row = index / size();
                        int col = index % size() - 1;
                        if (index % size() == 0) {
                            row = index / size() - 1;
                            col = size() - 1;
                        }
                        int tobeadd = Math.abs(i - row) + Math.abs(j - col);
                        mahattan += tobeadd;
                    }
                }
            }
        }
        return mahattan;
    }

    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    public boolean isGoal() {
        return board.equals(expected);
    }

    public boolean equals(Object y) {
        Board real = (Board) y;
        if (this.size() != real.size()) {
            return false;
        } else {
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < size(); j++) {
                    if (board[i][j] != real.board[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                hash += board[i][j] * (i + size() * j);
            }
        }
        return hash;
    }


    /**
     * Returns the string representation of the board.
     * Uncomment this method.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    public static void main(String[] args) {
        int[][] testdrive = new int[3][3];
        testdrive[0][0] = 8;
        testdrive[0][1] = 1;
        testdrive[0][2] = 3;
        testdrive[1][0] = 4;
        testdrive[1][1] = 0;
        testdrive[1][2] = 2;
        testdrive[2][0] = 7;
        testdrive[2][1] = 6;
        testdrive[2][2] = 5;
        Board totest = new Board(testdrive);
        Iterable<WorldState> neigh = totest.neighbors();
        for (WorldState a : neigh) {
            System.out.println(a);
        }
        System.out.println(totest.hamming());
        System.out.println(totest.manhattan());
    }

}
