import edu.princeton.cs.algs4.Picture;

import java.awt.*;

public class SeamCarver {
    private Picture picture;
    private int height, width;
    int[][] matrix;

    public SeamCarver(Picture picture){
        this.picture = picture;
        width = picture.width();
        height = picture.height();
        matrix = new int[height][width];
        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                matrix[i][j] = getEnergy(j, i);
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
        return Math.sqrt(1.0 * matrix[y][x]);
    }

    public int[] findHorizontalSeam(){
        return null;
    }

    public int[] findVerticalSeam(){
        return null;
    }

    public void removeHorizontalSeam(int[] seam) throws IllegalArgumentException{

    }

    public void removeVerticalSeam(int[] seam) throws IllegalArgumentException{

    }

    private int getEnergy(int x, int y){
        int leftPixel = picture.getRGB((x > 0 ? x - 1 : width - 1), y);
        int rightPixel = picture.getRGB((x == width - 1 ? 0 : x + 1), y);
        int lowerPixel = picture.getRGB(x, (y == height - 1 ? 0 : y + 1));
        int upperPixel = picture.getRGB(x, (y > 0 ? y - 1 : height - 1));
        int horizontalGrad = 0, verticalGrad = 0, mask = 0xFF;
        for(int i = 0; i < 3; ++i){
            int l = leftPixel & mask, r = rightPixel & mask;
            int u = upperPixel & mask, d = lowerPixel & mask;
            int vDiff = u - d;
            int hDiff = l - r;
            horizontalGrad += hDiff * hDiff;
            verticalGrad += vDiff * vDiff;
            leftPixel >>= 8;
            rightPixel >>= 8;
            upperPixel >>= 8;
            lowerPixel >>= 8;
        }
        return horizontalGrad + verticalGrad;
    }


    public static void main(String[] args) {
        Picture picture = new Picture("3x4.png");
        SeamCarver seamCarver = new SeamCarver(picture);
        System.out.println(seamCarver.energy(1, 2));
    }
}
