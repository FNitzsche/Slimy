import javafx.scene.image.Image;

import java.util.Random;

public class KMeans {

    public static float[][] lastClusters = null;

    public static float[][][] cluster(Image img, int n, int maxReps, int resX, int resY, int seed){
        System.out.println("Start clustering: " + n + " Clusters");
        float[][] centers = new float[n][3];
        float[][] nextCenters = new float[n][3];

        float[][][] pixel = new float[(int)img.getWidth()][(int)img.getHeight()][3];

        for (int i = 0; i < img.getWidth(); i++){
            for (int j = 0; j < img.getHeight(); j++){
                pixel[i][j][0] = (float) img.getPixelReader().getColor(i, j).getRed();
                pixel[i][j][1] = (float) img.getPixelReader().getColor(i, j).getGreen();
                pixel[i][j][2] = (float) img.getPixelReader().getColor(i, j).getBlue();
            }
        }

        Random ran = new Random(seed);

        init(ran, n, centers);

        clusterLoop(maxReps, nextCenters, n, resX, resY, pixel, centers);

        System.out.println("finished clustering");
        lastClusters = centers;
        return pixel;
    }

    public static void init(Random ran, int n, float[][] centers){
        for (int i = 0; i < n; i++){
            centers[i][0] = ran.nextFloat();
            centers[i][1] = ran.nextFloat();
            centers[i][2] = ran.nextFloat();
        }
        System.out.println(centers.length + " Clusters init");
    }

    public static void clusterLoop(int maxReps, float[][] nextCenters, int n, int resX, int resY, float[][][] pixel, float[][] centers){
        boolean changed = false;
        L:
        for (int i = 0; i < maxReps; i++){
            System.out.println("Rep " + i );
            changed = false;
            nextCenters = new float[n][4];
            for (int x = 0; x < resX; x++){
                for (int y = 0; y < resY; y++){
                    int nearest = -1;
                    float minDist = 1000;
                    for (int k = 0; k < n; k++){
                        float dist = 5;
                            dist = featureDistance(pixel[x][y], centers[k]);
                        if (dist < minDist){
                            nearest = k;
                            minDist = dist;
                        }
                    }
                    if (nearest != -1) {
                        nextCenters[nearest][0] += pixel[x][y][0];
                        nextCenters[nearest][1] += pixel[x][y][1];
                        nextCenters[nearest][2] += pixel[x][y][2];
                        nextCenters[nearest][3]++;
                    }
                }
            }
            for (int k = 0; k < n; k++){
                nextCenters[k][0] /= nextCenters[k][3];
                nextCenters[k][1] /= nextCenters[k][3];
                nextCenters[k][2] /= nextCenters[k][3];

                if (nextCenters[k][0] != centers[k][0] || nextCenters[k][1] != centers[k][1] || nextCenters[k][2] != centers[k][2]){
                    changed = true;
                }
                centers[k][0] = nextCenters[k][0];
                centers[k][1] = nextCenters[k][1];
                centers[k][2] = nextCenters[k][2];
            }

            if (!changed){
                break L;
            }
        }
    }

    public static float featureDistance(float[] pixel, float[] cluster){
        return (float) Math.sqrt(
                  Math.pow(pixel[0]-cluster[0], 2) + Math.pow(pixel[1]-cluster[1], 2) + Math.pow(pixel[2]-cluster[2], 2)
        );
    }

}
