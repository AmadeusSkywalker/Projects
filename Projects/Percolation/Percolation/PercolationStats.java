package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    double[] means;
    double numofexper;

    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        numofexper = (double) T;
        means = new double[T];
        for (int i = 0; i < T; i++) {
            Percolation experiment = new Percolation(N);
            while (!experiment.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                while (experiment.isOpen(row, col)) {
                    row = StdRandom.uniform(N);
                    col = StdRandom.uniform(N);
                }
                experiment.open(row, col);
            }
            double open = (double) experiment.numberOfOpenSites();
            double whole = (double) (N * N);
            double ratio = open / whole;
            means[i] = ratio;
        }
    }   // perform T independent experiments on an N-by-N grid

    public double mean() {
        return StdStats.mean(means);
    }                    // sample mean of percolation threshold

    public double stddev() {
        return StdStats.stddev(means);
    }                  // sample standard deviation of percolation threshold

    public double confidenceLow() {
        return mean() - 1.96 * stddev() / Math.sqrt(numofexper);
    }           // low  endpoint of 95% confidence interval

    public double confidenceHigh() {
        return mean() + 1.96 * stddev() / Math.sqrt(numofexper);
    }

    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(5, 30);
        System.out.println(stats.mean());
        System.out.println(stats.stddev());
        System.out.println(stats.confidenceLow());
        System.out.println(stats.confidenceHigh());
    }
}                       
