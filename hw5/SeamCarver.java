/**
 * Created by vip on 4/20/17.
 */

import edu.princeton.cs.algs4.Picture;

import java.awt.*;


public class SeamCarver {
    Picture mainframe;

    public SeamCarver(Picture picture) {
        mainframe = picture;
    }

    public Picture picture() {
        Picture newpic = new Picture(mainframe);
        return newpic;
    }                     // current picture

    public int width() {
        return mainframe.width();
    }                        // width of current picture

    public int height() {
        return mainframe.height();
    }                       // height of current picture

    public double energy(int x, int y) {
        if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) {
            throw new IndexOutOfBoundsException();
        }
        return energyx(x, y) + energyy(x, y);
    }  // energy of pixel at column x and row y

    private double energyx(int x, int y) {
        Color first;
        Color second;
        if (x == mainframe.width() - 1) {
            first = mainframe.get(0, y);
        } else {
            first = mainframe.get(x + 1, y);
        }
        if (x == 0) {
            second = mainframe.get(mainframe.width() - 1, y);
        } else {
            second = mainframe.get(x - 1, y);
        }
        double rx = first.getRed() - second.getRed();
        double gx = first.getGreen() - second.getGreen();
        double bx = first.getBlue() - second.getBlue();
        return rx * rx + gx * gx + bx * bx;
    }

    private double energyy(int x, int y) {
        Color first;
        Color second;
        if (y == mainframe.height() - 1) {
            first = mainframe.get(x, 0);
        } else {
            first = mainframe.get(x, y + 1);
        }
        if (y == 0) {
            second = mainframe.get(x, mainframe.height() - 1);
        } else {
            second = mainframe.get(x, y - 1);
        }
        double ry = first.getRed() - second.getRed();
        double gy = first.getGreen() - second.getGreen();
        double by = first.getBlue() - second.getBlue();
        return ry * ry + gy * gy + by * by;
    }


    public int[] findHorizontalSeam() {
        int[] result = new int[width()];
        double[][] matrix = setmatrixY();
        int col = matrix[0].length - 1;
        double downmin = Double.MAX_VALUE;
        int leftindex = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0] < downmin) {
                downmin = matrix[i][0];
                leftindex = i;
            }
        }
        int index = result.length - 1;
        while (col > 0) {
            result[index] = leftindex;
            double sum1 = Double.MAX_VALUE;
            double sum2;
            double sum3 = Double.MAX_VALUE;
            sum2 = matrix[leftindex][col - 1];
            if (leftindex != matrix.length - 1) {
                sum3 = matrix[leftindex + 1][col - 1];
            }
            if (leftindex != 0) {
                sum1 = matrix[leftindex - 1][col - 1];
            }
            double min = findmin(sum1, sum2, sum3);
            if (min == sum1) {
                leftindex = leftindex - 1;
            } else if (min == sum3) {
                leftindex = leftindex + 1;
            }
            index -= 1;
            col -= 1;
        }
        result[0] = leftindex;
        return result;
    }        // sequence of indices for horizontal seam

    private double findmin(double x1, double x2, double x3) {
        double min1 = Math.min(x1, x2);
        double min2 = Math.min(min1, x3);
        if (min2 == x1) {
            return x1;
        } else if (min2 == x2) {
            return x2;
        } else {
            return x3;
        }
    }

    private double[][] setmatrixX() {
        double[][] sums = new double[mainframe.height()][mainframe.width()];
        for (int i = 0; i < width(); i++) {
            sums[0][i] = energy(i, 0);
        }
        for (int j = 1; j < height(); j++) {
            for (int i = 0; i < width(); i++) {
                double energy = energy(i, j);
                double sum1 = Double.MAX_VALUE;
                double sum2;
                double sum3 = Double.MAX_VALUE;
                sum2 = energy + sums[j - 1][i];
                if (i != 0) {
                    sum1 = energy + sums[j - 1][i - 1];
                }
                if (i != width() - 1) {
                    sum3 = energy + sums[j - 1][i + 1];
                }
                double min1 = Math.min(sum1, sum2);
                double min2 = Math.min(min1, sum3);
                sums[j][i] = min2;
            }
        }
        return sums;
    }

    private double[][] setmatrixY() {
        double[][] sums = new double[mainframe.height()][mainframe.width()];
        for (int i = 0; i < height(); i++) {
            sums[i][0] = energy(0, i);
        }
        for (int j = 1; j < width(); j++) {
            for (int i = 0; i < height(); i++) {
                double energy = energy(j, i);
                double sum1 = Double.MAX_VALUE;
                double sum2;
                double sum3 = Double.MAX_VALUE;
                sum2 = energy + sums[i][j - 1];
                if (i != 0) {
                    sum1 = energy + sums[i - 1][j - 1];
                }
                if (i != height() - 1) {
                    sum3 = energy + sums[i + 1][j - 1];
                }
                double min1 = Math.min(sum1, sum2);
                double min2 = Math.min(min1, sum3);
                sums[i][j] = min2;
            }
        }
        return sums;

    }

    public int[] findVerticalSeam() {
        int[] result = new int[height()];
        double[][] matrix = setmatrixX();
        int row = matrix.length - 1;
        double downmin = Double.MAX_VALUE;
        int downindex = 0;
        for (int i = 0; i < matrix[row].length; i++) {
            if (matrix[row][i] < downmin) {
                downmin = matrix[row][i];
                downindex = i;
            }
        }
        int index = result.length - 1;
        while (row > 0) {
            result[index] = downindex;
            double sum1 = Double.MAX_VALUE;
            double sum2;
            double sum3 = Double.MAX_VALUE;
            sum2 = matrix[row - 1][downindex];
            if (downindex != matrix[row].length - 1) {
                sum3 = matrix[row - 1][downindex + 1];
            }
            if (downindex != 0) {
                sum1 = matrix[row - 1][downindex - 1];
            }
            double min = findmin(sum1, sum2, sum3);
            if (min == sum1) {
                downindex = downindex - 1;
            } else if (min == sum3) {
                downindex = downindex + 1;
            }
            index -= 1;
            row -= 1;
        }
        result[0] = downindex;
        return result;
    }          // sequence of indices for vertical seam

    public void removeHorizontalSeam(int[] seam) {
        if (!checkHorzitonal(seam)) {
            throw new IllegalArgumentException();
        }
        Color white = new Color(0, 0, 0);
        for (int i = 0; i < seam.length; i++) {
            mainframe.set(seam[i], i, white);
        }
    } // remove horizontal seam from picture

    public void removeVerticalSeam(int[] seam) {
        if (!checkVerticalSeam(seam)) {
            throw new IllegalArgumentException();
        }
        Color white = new Color(0, 0, 0);
        for (int i = 0; i < seam.length; i++) {
            mainframe.set(i, seam[i], white);
        }
    }  // remove vertical seam from picture

    private boolean checkHorzitonal(int[] seam) {
        if (seam.length != width()) {
            return false;
        }
        for (int i = 0; i < seam.length - 1; i++) {
            int tocheck = seam[i];
            if (tocheck < 0 || tocheck >= height()) {
                return false;
            }
            if (Math.abs(tocheck - seam[i + 1]) > 1) {
                return false;
            }
        }
        return true;
    }

    private boolean checkVerticalSeam(int[] seam) {
        if (seam.length != height()) {
            return false;
        }
        for (int j = 0; j < seam.length - 1; j++) {
            int tocheck = seam[j];
            if (tocheck < 0 || tocheck >= width()) {
                return false;
            }
            if (Math.abs(tocheck - seam[j + 1]) > 1) {
                return false;
            }
        }
        return true;
    }


}
