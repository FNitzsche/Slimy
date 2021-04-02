import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class SlimeManager {

    Image startImage;

    float[][][] trails;

    float[][][] img;

    AgentManager agentManager;
    ClusterManager clusterManager;

    public void initialize(String path){
        startImage = new Image(path);

        //Cluster

        trails = new float[(int)startImage.getWidth()][(int)startImage.getHeight()][1];

        agentManager.initializeAgents(startImage);

    }

    public void paintTrails(Canvas canvas){
        //Imgproc.blur(trails, trails, new Size(3, 3));
        //Core.multiply(trails, new Scalar(sustain, sustain, sustain, 1), trails);

        float[][][] nTrails = new float[(int)startImage.getWidth()][(int)startImage.getHeight()][1];
        for (int i = 1; i < (int)startImage.getWidth()-1; i++){
            for (int j = 1; j < (int)startImage.getHeight()-1; j++){
                for (int cl = 0; cl < nTrails[i][j].length; cl++) {
                    float tmp = 0;
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 2; y++) {
                            tmp += trails[i+x][j+y][cl];
                        }
                    }
                    tmp /= 9;
                    nTrails[i][j][cl] = (float) Math.max(0, tmp-0.02);
                }
            }
        }



        Runnable run = new Runnable() {
            @Override
            public void run() {
                agents.stream().parallel().filter(agent -> agent[0] >= 0 && agent[0] <= rX-1 && agent[1] >= 0 && agent[1] <= rY-1)
                        .forEach(agent -> Imgproc.circle(trails, new Point(agent[0], agent[1]),0, new Scalar(agent[5]*255, agent[6]*255, agent[7]*255, 100), 1));
                wimg = mat2Image(trails);
                Platform.runLater(() -> canvas.getGraphicsContext2D().drawImage(wimg, 0, 0));

            }
        };

        parallelExe.execute(run);
    }

}
