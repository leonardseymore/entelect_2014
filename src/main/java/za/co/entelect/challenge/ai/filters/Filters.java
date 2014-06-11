package za.co.entelect.challenge.ai.filters;

import za.co.entelect.challenge.Util;

public class Filters {

    public static final float[][] GAUSSIAN_BLUR3 = Util.mult(new float[][]{
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
    }, 1/16f);

    public static final float[][] GAUSSIAN_BLUR5 = Util.mult(new float[][]{
            {1, 4, 6, 4, 1},
            {4, 16, 24, 16, 4},
            {6, 24, 36, 24, 6},
            {4, 16, 24, 16, 4},
            {1, 4, 6, 4, 1}
    }, 1/256f);


    public static void convolve(float[][] kernel, float[][] source, float[][] destination, int iterations) {
        float[][] map1;
        float[][] map2;
        if (iterations % 2 > 0) {
            map1 = source;
            map2 = destination;
        } else {
            destination = source;
            map1 = destination;
            map2 = source;
        }

        for (int i = 0; i < iterations; i++) {
            convolve(kernel, map1, map2);
            float[][] tmp = map1;
            map1 = map2;
            map2 = tmp;
        }
    }

    public static void convolve(float[][] kernel, float[][] source, float[][] destination) {
        int matrixLength = kernel.length;
        int size = (matrixLength - 1) / 2;

        int w = source.length;
        int h = source[0].length;

        for (int i = size; i < w - size; i++) {
            for (int j = size; j < h - size; j++) {
                destination[i][j] = 0;

                for (int x = 0; x < matrixLength; x++) {
                    for (int y = 0; y < matrixLength; y++) {
                        destination[i][j] += source[i + x - size][j + y - size] * kernel[x][y];
                    }
                }
            }
        }
    }
}
