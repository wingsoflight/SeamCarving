import edu.princeton.cs.algs4.*;

import java.util.Arrays;

public class SeamCarver {
    private Picture picture;

    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    public Picture picture(){
        return picture;
    }

    public int width(){
        return picture.width();
    }

    public int height(){
        return picture.height();
    }

    public double energy(int x, int y) throws IllegalArgumentException{
        if(x >= picture.width() || x < 0 || y >= picture.height() || y < 0)
            throw new IllegalArgumentException();
        int grad = getEnergy(x, y);
        return Math.sqrt(1.0 * grad);
    }
    private int[] findSeam(boolean horizontal){
        // Transpose if we need to find horizontal seam
        if(horizontal)
            picture = transposePicture(picture);
        // Calculate minimal distance from every pixel of 1st row to every pixel of last row using DP
        int width = picture.width(), height = picture.height();
        int[][] energies = getEnergies();
        int[][] dp = new int[height][width];
        dp[0] = energies[0];
        int[][] prev = new int[height][width];
        int[] seam = new int[height];
        for(int y = 1; y < height; ++y){
            Arrays.fill(dp[y], Integer.MAX_VALUE);
            for(int x = 0; x < width; ++x){
                int l = Math.max(x - 1, 0);
                int r = Math.min(x + 1, width - 1);
                for(int _x = l; _x <= r; ++_x){
                    int s = dp[y - 1][_x] + energies[y][x];
                    if(dp[y][x] > s){
                        dp[y][x] = s;
                        prev[y][x] = _x;
                    }
                }
            }
        }
        // Find minimal distance to column from last row
        int minDist = dp[height - 1][0], ind = 0;
        for(int x = 1; x < width; ++x){
            if(minDist > dp[height - 1][x]){
                ind = x;
                minDist = dp[height - 1][x];
            }
        }
        // Restore minimal path from last row to first row
        for(int i = height - 1; i >= 0; --i){
            seam[i] = ind;
            ind = prev[i][ind];
        }
        // Transpose picture back if horizontal
        if(horizontal)
            picture = transposePicture(picture);
        return seam;
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
        int minVal = seam.length, maxVal = -1, prevVal = -1;
        for(int val: seam){
            minVal = Math.min(minVal, val);
            maxVal = Math.max(maxVal, val);
            if(prevVal == -1){
                prevVal = val;
                continue;
            }
            if(Math.abs(val - prevVal) > 1)
                throw new IllegalArgumentException("Adjacent seam values cannot differ more than by 1");
            prevVal = val;
        }
        if(maxVal >= maxValue || minVal < 0)
            throw new IllegalArgumentException(String.format("Seam values must be in range between 0 and %d", maxValue - 1));
    }

    public void removeHorizontalSeam(int[] seam) throws IllegalArgumentException{
        if (picture.height() == 1)
            throw new IllegalArgumentException("Cannot remove seam from picture with width height to 1");
        checkSeam(seam, picture.width(), picture.height());
        removeSeam(seam, true);
    }

    public void removeVerticalSeam(int[] seam) throws IllegalArgumentException{
        if (picture.width() == 1)
            throw new IllegalArgumentException("Cannot remove seam from picture with width equals to 1");
        checkSeam(seam, picture.height(), picture.width());
        removeSeam(seam, false);
    }

    private int getGrad(int pixelA, int pixelB){
        int grad = 0, mask = 0xFF;
        for(int i = 0; i < 3; ++i){
            int a = pixelA & mask, b = pixelB & mask;
            int diff = a - b;
            grad += diff * diff;
            pixelA >>= 8;
            pixelB >>= 8;
        }
        return grad;
    }

    private int getEnergy(int x, int y){
        int width = picture.width(), height = picture.height();
        int leftPixel = picture.getRGB((x > 0 ? x - 1 : width - 1), y);
        int rightPixel = picture.getRGB((x == width - 1 ? 0 : x + 1), y);
        int lowerPixel = picture.getRGB(x, (y == height - 1 ? 0 : y + 1));
        int upperPixel = picture.getRGB(x, (y > 0 ? y - 1 : height - 1));
        return getGrad(leftPixel, rightPixel) + getGrad(upperPixel, lowerPixel);
    }

    private int[][] getEnergies(){
        int width = picture.width(), height = picture.height();
        int[][] energies = new int[height][width];
        for (int i = 0; i < height; ++i){
            for (int j = 0; j < width; ++j){
                energies[i][j] = getEnergy(j, i);
            }
        }
        return energies;
    }

    private void removeSeam(int[] seam, boolean horizontal){
        if(horizontal)
            picture = transposePicture(picture);
        int width = picture.width(), height = picture.height();
        Picture _picture = new Picture(width - 1, height);
        for(int y = 0; y < height; ++y){
            int _x = 0;
            for(int x = 0; x < width; ++x){
                if(seam[y] == x)
                    continue;
                int color = picture.getRGB(x, y);
                _picture.setRGB(_x++, y, color);
            }
        }
        if(horizontal)
            _picture = transposePicture(_picture);
        picture = _picture;
    }

    private Picture transposePicture(Picture picture){
        int width = picture.width(), height = picture.height();
        Picture _picture = new Picture(height, width);
        for(int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int color = picture.getRGB(x, y);
                _picture.setRGB(y, x, color);
            }
        }
        return _picture;
    }

    public static void main(String[] args) {
        Picture picture = new Picture("HJocean.png");
        SeamCarver seamCarver = new SeamCarver(picture);
        for(int i = 0; i < 150; ++i) {
            int[] seam = seamCarver.findVerticalSeam();
            seamCarver.removeVerticalSeam(seam);
        }
        seamCarver.picture().show();
    }
}
