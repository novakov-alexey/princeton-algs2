package edu.princeton.alg2.week2;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.stream.IntStream;

/**
 * @author Alexey Novakov
 */
public class SeamCarver {
    public static final int DEFAULT_ENERGY = 1_000;
    private int width, height;
    private int[][] color;           // int array as intermediate representation of the picture
    private double[][] energy;        // caching energy for every pixel
    private boolean isTransposed;  // is color currently transposed?

    /**
     * constructor
     */
    public SeamCarver(Picture picture) {
        isTransposed = false;
        width = picture.width();
        height = picture.height();
        color = new int[height][width];
        energy = new double[height][width];

        IntStream.range(0, height)
                .forEach(r -> IntStream.range(0, width)
                        .forEach(c -> color[r][c] = picture.get(c, r).getRGB()));

        // energy initialization involves neighbours, hence is done after color initialization
        IntStream.range(0, height)
                .forEach(r -> IntStream.range(0, width)
                        .forEach(c -> energy[r][c] = energy(c, r)));
    }

    /**
     * current picture
     */
    public Picture picture() {
        Picture seamed = new Picture(width, height);
        if (isTransposed) transpose(height, width);

        // transfer back to picture
        for (int c = 0; c < width; c++)
            for (int r = 0; r < height; r++)
                seamed.set(c, r, new Color(color[r][c]));
        return seamed;
    }

    /**
     * width of the current picture
     */
    public int width() {
        return width;
    }

    /**
     * height of the current picture
     */
    public int height() {
        return height;
    }


    /**
     * energy of pixel at column x and row y in the current picture
     *
     * @param x column number in the final picture
     * @param y row number in the final picture
     */
    public double energy(int x, int y) {
        if (isTransposed) transpose(height, width);
        return getEnergy(x, y, height, width);
    }

    // helper for compute energy
    private double deltaSquared(int x, int y) {
        int r = ((x >> 16) & 0x0ff) - ((y >> 16) & 0x0ff);
        int g = ((x >> 8) & 0x0ff) - ((y >> 8) & 0x0ff);
        int b = (x & 0x0ff) - (y & 0x0ff);
        return r * r + g * g + b * b;
    }

    // computes energy for intermediate color with height h and width w
    private double getEnergy(int x, int y, int h, int w) {
        if (x < 0 || x >= w || y < 0 || y >= h) throw new IndexOutOfBoundsException();
        if (x == 0 || x == w - 1 || y == 0 || y == h - 1) return DEFAULT_ENERGY;
        return Math.sqrt(deltaSquared(color[y - 1][x], color[y + 1][x]) + deltaSquared(color[y][x - 1], color[y][x + 1]));
    }

    /**
     * sequence of indices for vertical seam in current picture
     */
    public int[] findVerticalSeam() {
        if (isTransposed) transpose(height, width);  // transpose back if color is transposed
        return findSeam(height, width);
    }

    /**
     * sequence of indices for horizontal seam in current picture
     */
    public int[] findHorizontalSeam() {
        if (!isTransposed) transpose(width, height); // transpose color if not already transposed
        return findSeam(width, height);
    }

    private int[] findSeam(int h, int w) {
        if (w == 1) return new int[h];

        double[] lastEnergyTo = new double[w];
        double[] currentEnergyTo = new double[w];
        int[][] edgeTo = new int[h][w];

        System.arraycopy(energy[0], 0, lastEnergyTo, 0, w);

        for (int r = 1; r < h; r++) {
            // edge case when column number is 0
            if (lastEnergyTo[0] <= lastEnergyTo[1]) {
                currentEnergyTo[0] = lastEnergyTo[0] + energy[r][0];
                edgeTo[r][0] = 0;
            } else {
                currentEnergyTo[0] = lastEnergyTo[1] + energy[r][0];
                edgeTo[r][0] = 1;
            }
            // when column number is between 0 and w - 1
            for (int c = 1; c < w - 1; c++) {
                currentEnergyTo[c] = lastEnergyTo[c - 1];
                edgeTo[r][c] = c - 1;
                if (lastEnergyTo[c] < currentEnergyTo[c]) {
                    currentEnergyTo[c] = lastEnergyTo[c];
                    edgeTo[r][c] = c;
                }
                if (lastEnergyTo[c + 1] < currentEnergyTo[c]) {
                    currentEnergyTo[c] = lastEnergyTo[c + 1];
                    edgeTo[r][c] = c + 1;
                }
                currentEnergyTo[c] += energy[r][c];
            }
            // edge case when column number is w - 1
            if (lastEnergyTo[w - 2] <= lastEnergyTo[w - 1]) {
                currentEnergyTo[w - 1] = lastEnergyTo[w - 2] + energy[r][w - 1];
                edgeTo[r][w - 1] = w - 2;
            } else {
                currentEnergyTo[w - 1] = lastEnergyTo[w - 1] + energy[r][w - 1];
                edgeTo[r][w - 1] = w - 1;
            }

            // swap last and current
            double[] swap = lastEnergyTo;
            lastEnergyTo = currentEnergyTo;
            currentEnergyTo = swap;
        }

        // because of swap, lastEnergyTo records the total energy to reach bottom
        double minEnergyTo = lastEnergyTo[0];
        int minCol = 0;
        for (int c = 1; c < w; c++) {
            if (lastEnergyTo[c] < minEnergyTo) {
                minEnergyTo = lastEnergyTo[c];
                minCol = c;
            }
        }

        // trace back the seam
        int[] seam = new int[h];
        seam[h - 1] = minCol;
        for (int r = h - 1; r > 0; r--) {
            minCol = edgeTo[r][minCol];
            seam[r - 1] = minCol;
        }
        return seam;
    }

    // helper for horizontal seam
    private void transpose(int h, int w) {
        int[][] transposedColor = new int[h][w];
        double[][] transposedEnergy = new double[h][w];
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                transposedColor[r][c] = color[c][r];
                transposedEnergy[r][c] = energy[c][r];
            }
        }
        color = transposedColor;
        energy = transposedEnergy;
        isTransposed = !isTransposed;
    }

    /**
     * remove vertical seam from current picture
     *
     * @param a vertical seam array
     */
    public void removeVerticalSeam(int[] a) {
        if (isTransposed) transpose(height, width);
        removeSeam(a, height, width);
        width--;
    }

    /**
     * remove horizontal seam from current picture
     *
     * @param a horizontal seam array
     */
    public void removeHorizontalSeam(int[] a) {
        if (!isTransposed) transpose(width, height);
        removeSeam(a, width, height);
        height--;
    }

    // helper for remove seam
    // suppose the picture is not transposed
    private void removeSeam(int[] a, int h, int w) {
        handleRemoveSeamExceptions(a, h, w);
        for (int r = 0; r < h; r++) {
            if (a[r] < w - 1) {
                System.arraycopy(color[r], a[r] + 1, color[r], a[r], w - a[r] - 1);
                System.arraycopy(energy[r], a[r] + 1, energy[r], a[r], w - a[r] - 1);
            }
        }
        // only the energy of the seam element and its left element changes
        for (int r = 1; r < h - 1; r++) {
            int x = a[r];
            if (x > 0) energy[r][x - 1] = (int) getEnergy(x - 1, r, h, w - 1);
            if (x < w - 1) energy[r][x] = (int) getEnergy(x, r, h, w - 1);
        }
    }

    // helper for remove seam
    private void handleRemoveSeamExceptions(int a[], int h, int w) {
        if (width <= 1 && !isTransposed || height <= 1 && isTransposed) throw new IllegalArgumentException();
        if (a.length != h) throw new IllegalArgumentException();

        int prevSeamEntry = a[0];
        for (int x : a) {
            if (x < 0 || x >= w) throw new IllegalArgumentException();
            if (Math.abs(prevSeamEntry - x) > 1) throw new IllegalArgumentException();
            prevSeamEntry = x;
        }
    }
}
