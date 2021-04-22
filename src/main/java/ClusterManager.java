import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class ClusterManager {

    float[][] clusters;
    int[][] clusterMap;

    public void createClusters(Image img, int n, int reps, int seed){
        KMeans.cluster(img, n, reps, (int)img.getWidth(), (int)img.getHeight(), seed);
        clusters = KMeans.lastClusters;
        clusterMap = new int[(int)img.getWidth()][(int)img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++){
            for (int j = 0; j < img.getHeight(); j++){
                Color c = img.getPixelReader().getColor(i, j);
                int nearest = -1;
                float dist = 10;
                for (int k = 0; k < clusters.length; k++){
                    float tmp = KMeans.featureDistance(new float[]{(float)c.getRed(), (float)c.getGreen(), (float)c.getBlue()}, clusters[k]);
                    if (tmp < dist){
                        dist = tmp;
                        nearest = k;
                    }
                }
                clusterMap[i][j] = nearest;
            }
        }
    }



}
