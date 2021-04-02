import javafx.scene.image.Image;

public class ClusterManager {

    float[][] clusters;

    public void createClusters(Image img, int n, int reps, int seed){
        KMeans.cluster(img, n, reps, (int)img.getWidth(), (int)img.getHeight(), seed);
        clusters = KMeans.lastClusters;
    }



}
