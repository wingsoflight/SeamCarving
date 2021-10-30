import edu.princeton.cs.algs4.*;

import javax.swing.plaf.synth.SynthLookAndFeel;
import java.awt.*;
import java.util.ArrayList;
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

    void checkSeam(int[] seam, int expectedLength, int maxValue){
        if (seam == null)
            throw new IllegalArgumentException("Seam cannot be NULL");
        if(seam.length != expectedLength)
            throw new IllegalArgumentException(String.format("Seam length must be equal to %d", expectedLength));
        int mn = seam.length, mx = -1, prev = -1;
        for(int x: seam){
            mn = Math.min(mn, x);
            mx = Math.max(mx, x);
            if(prev == -1){
                prev = x;
                continue;
            }
            if(Math.abs(x - prev) > 1)
                throw new IllegalArgumentException("Adjacent seam values cannot differ more than by 1");
            prev = x;
        }
        if(mx >= width || mn < 0)
            throw new IllegalArgumentException(String.format("Seam values must be in range between 0 and %d", maxValue - 1));
    }

    public void removeHorizontalSeam(int[] seam) throws IllegalArgumentException{
        if (height == 1)
            throw new IllegalArgumentException("Cannot remove seam from picture with width height to 1");
        checkSeam(seam, width, height);
        removeSeam(seam, true);
    }

    public void removeVerticalSeam(int[] seam) throws IllegalArgumentException{
        if (width == 1)
            throw new IllegalArgumentException("Cannot remove seam from picture with width equals to 1");
        checkSeam(seam, height, width);
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
        int[][] e = energies;
        if(horizontal)
            e = transpose(e);
        int n = e.length, m = e[0].length;
        int[][] dp = new int[n][m];
        dp[0] = e[0];
        int[][] prev = new int[n][m];
        int[] ret = new int[n];
        for(int i = 1; i < n; ++i){
            Arrays.fill(dp[i], Integer.MAX_VALUE);
            for(int j = 0; j < m; ++j){
                int l = Math.max(j - 1, 0);
                int r = Math.min(j + 1, m - 1);
                for(int k = l; k <= r; ++k){
                    int s = dp[i - 1][k] + e[i][j];
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
        int h = height, w = width;
        Picture tmp = new Picture((horizontal ? w : w - 1), (horizontal ? h - 1 : h));
        //Picture tmp = new Picture(w, h);
        if(horizontal) {
            h = width;
            w = height;
        }
        for(int y = 0; y < h; ++y){
            int k = 0;
            for(int x = 0; x < w; ++x){
                int _x = x, _y = y;
                if(horizontal){
                    _x = y;
                    _y = x;
                }
                int color = picture.getRGB(_x, _y);
                if(seam[y] == x)
                    continue;
                if(horizontal)
                    tmp.setRGB(_x, k++, color);
                else
                    tmp.setRGB(k++, _y, color);
            }
        }
        picture = tmp;
        calculateEnergies();
    }

    private int[][] transpose(int[][] arr){
        int n = arr.length, m = arr[0].length;
        int[][] ret = new int[m][n];
        for(int x = 0; x < m; ++x)
            for(int y = 0; y < n; ++y)
                ret[x][y] = arr[y][x];
        return ret;
    }

    public static void main(String[] args) {
        Picture picture = new Picture("HJocean.png");
        SeamCarver seamCarver = new SeamCarver(picture);
        for(int i = 0; i < 150; ++i) {
            int[] horizontalSeam = seamCarver.findVerticalSeam();
            seamCarver.removeVerticalSeam(horizontalSeam);
        }
        seamCarver.picture().show();
    }
}
