package main;

import java.util.Random;

public class NNearestN {

    public static float[][] lastClusters = null;

    public static float[][][] cluster(float[][][] pixel, int n, int maxReps, int resX, int resY, int seed, boolean hue,
                                      float space, boolean hueCluster, float rgb, float distStrength, float objectStrength, float meds, float medm, float medb, float h, float s, float v){
        System.out.println("Start clustering: " + n + " Clusters");
        System.out.println("for " + pixel.length + "*" + pixel[0].length + " pixel");
        float[][] centers = new float[n][21];
        float[][] nextCenters = new float[n][22];
        Random ran = new Random(seed);

        init(ran, n, centers);

        if (hue){
            System.out.println("only Hue");
            hueNormalizeOrExpand(pixel, resX, resY, true);
        } else {
            hueNormalizeOrExpand(pixel, resX, resY, false);
        }


        clusterLoop(maxReps, nextCenters, n, resX, resY, space, pixel, centers, hue, rgb, distStrength, objectStrength, meds, medm, medb, h, s, v);

        if (hue && hueCluster){
            clusterHue(n, pixel, resX, resY, ran);
        }

        recolorLoop(resX, resY, centers, space, pixel, hue, n,  rgb, distStrength, objectStrength, meds, medm, medb, h, s, v);


        System.out.println("finished clustering");
        lastClusters = centers;
        return pixel;
    }

    public static void init(Random ran, int n, float[][] centers){
        for (int i = 0; i < n; i++){
            centers[i][0] = ran.nextFloat();
            centers[i][1] = ran.nextFloat();
            centers[i][2] = ran.nextFloat();
            centers[i][3] = ran.nextFloat();
            centers[i][4] = ran.nextFloat();
            centers[i][5] = ran.nextFloat();
            centers[i][6] = ran.nextFloat();
            centers[i][7] = ran.nextFloat();
            centers[i][8] = ran.nextFloat();
            centers[i][9] = ran.nextFloat();
            centers[i][10] = ran.nextFloat();
            centers[i][11] = ran.nextFloat();
            centers[i][12] = ran.nextFloat();
            centers[i][13] = ran.nextFloat();
            centers[i][14] = ran.nextFloat();
            centers[i][15] = ran.nextFloat();
            centers[i][16] = ran.nextFloat();
            centers[i][17] = ran.nextFloat();
            centers[i][18] = ran.nextFloat();
            centers[i][19] = ran.nextFloat();
            centers[i][20] = ran.nextFloat();
        }
        System.out.println(centers.length + " Clusters init");
    }

    public static void clusterLoop(int maxReps, float[][] nextCenters, int n, int resX, int resY, float space, float[][][] pixel, float[][] centers, boolean hue,
                                   float rgb, float distStrength, float objectStrength, float meds, float medm, float medb, float h, float s, float v){
        boolean changed = false;
        L:
        for (int i = 0; i < maxReps; i++){
            System.out.println("Rep " + i );
            changed = false;
            nextCenters = new float[n][22];
            for (int x = 0; x < resX; x++){
                for (int y = 0; y < resY; y++){
                    int nearest = -1;
                    float minDist = 1000;
                    for (int k = 0; k < n; k++){
                        float dist = 5;
                            dist = featureDistance(pixel[x][y], (x/(float)resX), (y/(float)resY), centers[k], rgb, space, distStrength, objectStrength, meds, medm, medb, h, s, v);
                        if (dist < minDist){
                            nearest = k;
                            minDist = dist;
                        }
                    }
                    if (nearest != -1) {
                        nextCenters[nearest][0] += pixel[x][y][0];
                        nextCenters[nearest][1] += pixel[x][y][1];
                        nextCenters[nearest][2] += pixel[x][y][2];
                        nextCenters[nearest][3] += (x/(float)resX);
                        nextCenters[nearest][4] += (y/(float)resY);
                        nextCenters[nearest][5] += pixel[x][y][5];
                        nextCenters[nearest][6] += pixel[x][y][6];
                        nextCenters[nearest][7] += pixel[x][y][7];
                        nextCenters[nearest][8] += pixel[x][y][8];
                        nextCenters[nearest][9] += pixel[x][y][9];
                        nextCenters[nearest][10] += pixel[x][y][10];
                        nextCenters[nearest][11] += pixel[x][y][11];
                        nextCenters[nearest][12] += pixel[x][y][12];
                        nextCenters[nearest][13] += pixel[x][y][13];
                        nextCenters[nearest][14] += pixel[x][y][14];
                        nextCenters[nearest][15] += pixel[x][y][15];
                        nextCenters[nearest][16] += pixel[x][y][16];
                        nextCenters[nearest][17] += pixel[x][y][17];
                        nextCenters[nearest][18] += pixel[x][y][18];
                        nextCenters[nearest][19] += pixel[x][y][19];
                        nextCenters[nearest][20] += pixel[x][y][20];
                        nextCenters[nearest][21]++;
                        pixel[x][y][4] = nearest;
                    }
                }
            }
            for (int k = 0; k < n; k++){
                nextCenters[k][0] /= nextCenters[k][21];
                nextCenters[k][1] /= nextCenters[k][21];
                nextCenters[k][2] /= nextCenters[k][21];
                nextCenters[k][3] /= nextCenters[k][21];
                nextCenters[k][4] /= nextCenters[k][21];
                nextCenters[k][5] /= nextCenters[k][21];
                nextCenters[k][6] /= nextCenters[k][21];
                nextCenters[k][7] /= nextCenters[k][21];
                nextCenters[k][8] /= nextCenters[k][21];
                nextCenters[k][9] /= nextCenters[k][21];
                nextCenters[k][10] /= nextCenters[k][21];
                nextCenters[k][11] /= nextCenters[k][21];
                nextCenters[k][12] /= nextCenters[k][21];
                nextCenters[k][13] /= nextCenters[k][21];
                nextCenters[k][14] /= nextCenters[k][21];
                nextCenters[k][15] /= nextCenters[k][21];
                nextCenters[k][16] /= nextCenters[k][21];
                nextCenters[k][17] /= nextCenters[k][21];
                nextCenters[k][18] /= nextCenters[k][21];
                nextCenters[k][19] /= nextCenters[k][21];
                nextCenters[k][20] /= nextCenters[k][21];
                if (nextCenters[k][0] != centers[k][0] || nextCenters[k][1] != centers[k][1] || nextCenters[k][2] != centers[k][2]){
                    changed = true;
                }
                centers[k][0] = nextCenters[k][0];
                centers[k][1] = nextCenters[k][1];
                centers[k][2] = nextCenters[k][2];
                centers[k][3] = nextCenters[k][3];
                centers[k][4] = nextCenters[k][4];
                centers[k][5] = nextCenters[k][5];
                centers[k][6] = nextCenters[k][6];
                centers[k][7] = nextCenters[k][7];
                centers[k][8] = nextCenters[k][8];
                centers[k][9] = nextCenters[k][9];
                centers[k][10] = nextCenters[k][10];
                centers[k][11] = nextCenters[k][11];
                centers[k][12] = nextCenters[k][12];
                centers[k][13] = nextCenters[k][13];
                centers[k][14] = nextCenters[k][14];
                centers[k][15] = nextCenters[k][15];
                centers[k][16] = nextCenters[k][16];
                centers[k][17] = nextCenters[k][17];
                centers[k][18] = nextCenters[k][18];
                centers[k][19] = nextCenters[k][19];
                centers[k][20] = nextCenters[k][20];
            }

            if (!changed){
                break L;
            }
        }
    }

    public static void recolorLoop(int resX, int resY, float[][] centers, float space, float[][][] pixel, boolean hue, int n,
                                   float rgb, float distStrength, float objectStrength, float meds, float medm, float medb, float h, float s, float v){
        for (int x = 0; x < resX; x++){
            for (int y = 0; y < resY; y++){
                int nearest = -1;
                float minDist = 1000;
                for (int k = 0; k < n; k++){
                    float dist = 5;
                    dist = featureDistance(pixel[x][y], (x/(float)resX), (y/(float)resY), centers[k], rgb, space, distStrength, objectStrength, meds, medm, medb, h, s, v);
                    if (dist < minDist){
                        nearest = k;
                        minDist = dist;
                    }
                }
                if (nearest != -1) {
                    pixel[x][y][0] = centers[nearest][0];
                    pixel[x][y][1] = centers[nearest][1];
                    pixel[x][y][2] = centers[nearest][2];
                    if (hue) {
                        pixel[x][y][0] = Math.min(1, Math.max(0, pixel[x][y][0] * pixel[x][y][3]));
                        pixel[x][y][1] = Math.min(1, Math.max(0, pixel[x][y][1] * pixel[x][y][3]));
                        pixel[x][y][2] = Math.min(1, Math.max(0, pixel[x][y][2] * pixel[x][y][3]));
                    }
                }
            }
        }
    }

    public static float featureDistance(float[] pixel, float x, float y, float[] cluster, float rgb, float space, float distStrength, float obj, float meds, float medm, float medb, float h, float s, float v){
        return (float) Math.sqrt(
                  Math.pow(pixel[0]-cluster[0], 2)*rgb + Math.pow(pixel[1]-cluster[1], 2)*rgb + Math.pow(pixel[2]-cluster[2], 2)*rgb
                + Math.pow(x-cluster[3], 2)*space + Math.pow(y-cluster[4], 2)*space + Math.pow(pixel[5]-cluster[5], 2)*distStrength
                + Math.pow(pixel[6]-cluster[6], 2)*obj + Math.pow(pixel[7]-cluster[7], 2)*obj + Math.pow(pixel[8]-cluster[8], 2)*obj
                + Math.pow(pixel[9]-cluster[9], 2)*meds + Math.pow(pixel[10]-cluster[10], 2)*meds + Math.pow(pixel[11]-cluster[11], 2)*meds
                + Math.pow(pixel[12]-cluster[12], 2)*medm + Math.pow(pixel[13]-cluster[13], 2)*medm + Math.pow(pixel[14]-cluster[14], 2)*medm
                + Math.pow(pixel[15]-cluster[15], 2)*medb + Math.pow(pixel[16]-cluster[16], 2)*medb + Math.pow(pixel[17]-cluster[17], 2)*medb
                + Math.pow(pixel[18]-cluster[18], 2)*h + Math.pow(pixel[19]-cluster[19], 2)*s + Math.pow(pixel[20]-cluster[20], 2)*v
        );
    }

    public static void hueNormalizeOrExpand(float[][][] pixel, int resX, int resY, boolean norm){
        for (int i = 0; i < resX; i ++){
            for (int j = 0; j < resY; j++){
                if (pixel[i][j][0]+pixel[i][j][1]+pixel[i][j][2] > 0) {
                    float length = (float) Math.sqrt(Math.pow(pixel[i][j][0], 2) + Math.pow(pixel[i][j][1], 2) + Math.pow(pixel[i][j][2], 2));
                    if (norm) {
                        pixel[i][j][0] = pixel[i][j][0] / length;
                        pixel[i][j][1] = pixel[i][j][1] / length;
                        pixel[i][j][2] = pixel[i][j][2] / length;
                    }
                    pixel[i][j][3] = length;
                } else {
                    pixel[i][j][0] = 0;
                    pixel[i][j][1] = 0;
                    pixel[i][j][2] = 0;
                    pixel[i][j][3] = 0;
                }
            }
        }
    }

    public static void clusterHue(int n, float[][][] pixel, int resX, int resY, Random ran){
        int hueCount = 5;
        float[][] centers = new float[hueCount*n][1];
        float[][] nextCenters = new float[hueCount*n][2];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < hueCount; j++){
                centers[(hueCount*i)+j][0] = (j/(float)hueCount)*1.5f;
                //System.out.println("Startcenters: " + centers[(hueCount*i)+j][0]);
            }
        }
        boolean changed = false;
        L:
        for (int i = 0; i < 5; i++){
            System.out.println("HueRep " + i );
            changed = false;
            nextCenters = new float[hueCount*n][2];
            for (int x = 0; x < resX; x++){
                for (int y = 0; y < resY; y++){
                    int nearest = -1;
                    float minDist = 5;
                    for (int k = 0; k < hueCount; k++){
                        float dist = Math.abs(pixel[x][y][3]-centers[(hueCount*(int)pixel[x][y][4])+k][0]);
                        if (dist < minDist){
                            nearest = k;
                            minDist = dist;
                        }
                    }
                    if (nearest != -1) {
                        nextCenters[(hueCount*(int)pixel[x][y][4])+nearest][0] += pixel[x][y][3];
                        nextCenters[(hueCount*(int)pixel[x][y][4])+nearest][1]++;
                    }
                }
            }
            for (int k = 0; k < hueCount*n; k++){
                nextCenters[k][0] /= nextCenters[k][1];
                if (nextCenters[k][0] != centers[k][0]){
                    changed = true;
                }
                centers[k][0] = nextCenters[k][0];
                //System.out.println(centers[k][0]);
            }

            if (!changed){
                break L;
            }
        }

        for (int x = 0; x < resX; x++){
            for (int y = 0; y < resY; y++){
                int nearest = -1;
                float minDist = 5;
                for (int k = 0; k < hueCount; k++){
                    float dist = Math.abs(pixel[x][y][3]-centers[(hueCount*(int)pixel[x][y][4])+k][0]);
                    if (dist < minDist){
                        nearest = k;
                        minDist = dist;
                    }
                }
                if (nearest != -1) {
                    pixel[x][y][3] = centers[(hueCount*(int)pixel[x][y][4])+nearest][0];
                }
            }
        }
    }

}
