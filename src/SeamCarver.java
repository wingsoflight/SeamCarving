import edu.princeton.cs.algs4.Picture;

import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private int height, width;
    int[][] energies;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        calculateEnergies();
    }

    public Picture picture(){
        return picture;
    }

    public int width(){
        return width;
    }

    public int height(){
        return height;
    }

    public double energy(int x, int y) throws IllegalArgumentException{
        if(x >= width || x < 0 || y >= height || y < 0)
            throw new IllegalArgumentException();
        return Math.sqrt(1.0 * energies[y][x]);
    }

    public int[] findHorizontalSeam(){
        return findSeam(true);
    }

    public int[] findVerticalSeam(){
        return findSeam(false);
    }

    public void removeHorizontalSeam(int[] seam) throws IllegalArgumentException{
        removeSeam(seam, true);
    }

    public void removeVerticalSeam(int[] seam) throws IllegalArgumentException{
        removeSeam(seam, false);
    }

    private int getGrad(int pixel1, int pixel2){
        int grad = 0, mask = 0xFF;
        for(int i = 0; i < 3; ++i){
            int a = pixel1 & mask, b = pixel2 & mask;
            int diff = a - b;
            grad += diff * diff;
            pixel1 >>= 8;
            pixel2 >>= 8;
        }
        return grad;
    }

    private int getEnergy(int x, int y){
        int leftPixel = picture.getRGB((x > 0 ? x - 1 : width - 1), y);
        int rightPixel = picture.getRGB((x == width - 1 ? 0 : x + 1), y);
        int lowerPixel = picture.getRGB(x, (y == height - 1 ? 0 : y + 1));
        int upperPixel = picture.getRGB(x, (y > 0 ? y - 1 : height - 1));
        return getGrad(leftPixel, rightPixel) + getGrad(upperPixel, lowerPixel);
    }

    private void calculateEnergies(){
        height = picture.height();
        width = picture.width();
        energies = new int[height][width];
        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                energies[i][j] = getEnergy(j, i);
    }

    private int[] findSeam(boolean horizontal){
        int n = height, m = width;
        if(horizontal) {
            n = width;
            m = height;
        }
        int[][] dp = new int[n][m];
        int[][] prev = new int[n][m];
        int[] ret = new int[n];
        for(int i = 0; i < n; ++i){
            Arrays.fill(dp[i], Integer.MAX_VALUE);
            for(int j = 0; j < m; ++j){
                int l = Math.max(j - 1, 0);
                int r = Math.min(j + 1, m - 1);
                for(int k = l; k <= r; ++k){
                    System.out.println(k);
                    int s = dp[i - 1][k] + energies[i][j];
                    if(dp[i][j] > s){
                        dp[i][j] = s;
                        prev[i][j] = k;
                    }
                }
            }
        }
        int min = dp[n - 1][0], ind = 0;
        for(int j = 1; j < m; ++j){
            if(min > dp[n - 1][j]){
                ind = j;
                min = dp[n - 1][j];
            }
        }
        for(int i = n - 1; i >= 0; --i){
            ret[i] = ind;
            ind = prev[i][ind];
        }
        return ret;
    }

    private void removeSeam(int[] seam, boolean horizontal){
        int n = height, m = width;
        if(horizontal) {
            n = width;
            m = height;
        }
        Picture tmp = new Picture(width, height);
        for(int i = 0; i < n; ++i){
            int k = 0;
            for(int j = 0; j < m; ++j){
                int color = picture.getRGB(j, i);
                if(seam[i] == j)
                    color = 0xFF0000;
                tmp.setRGB(k++, i, color);
            }
        }
        picture = tmp;
    }

    private int[][] transpose(int[][] arr){
        int n = arr.length, m = arr[0].length;
        int[][] ret = new int[m][n];
        for(int i = 0; i < m; ++i)
            for(int j = 0; j < n; ++j)
                ret[i][j] = arr[j][i];
        return ret;
    }

    public static void main(String[] args) {
        Picture picture = new Picture("HJocean.png");
        SeamCarver seamCarver = new SeamCarver(picture);
        for(int i = 0; i < 1; ++i){
            int[] seam = seamCarver.findHorizontalSeam();
            seamCarver.removeHorizontalSeam(seam);
        }
        seamCarver.picture.show();
    }
}
