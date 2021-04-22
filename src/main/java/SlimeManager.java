import com.sun.tools.javac.tree.JCTree;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SlimeManager {

    Image startImage;
    ExecutorService parallelExe = Executors.newCachedThreadPool();

    Image snap = null;

    float[][][] trails;

    Mat img;

    AgentManager agentManager;
    ClusterManager clusterManager;

    public void initialize(Image image){
        startImage = image;
        img = new Mat(AppStart.rX, AppStart.rY, CvType.CV_8UC4, new Scalar(0, 0, 0, 0));

        clusterManager.createClusters(startImage, 10, 5, 10);

        trails = new float[AppStart.rX][AppStart.rY][clusterManager.clusters.length];

        agentManager.initializeAgents(startImage);

    }

    public void paintTrails(Canvas canvas, VideoWriter videoWriter){


        float[][][] nTrails = new float[AppStart.rX][AppStart.rY][clusterManager.clusters.length];
        for (int i = 1; i < AppStart.rX-1; i++){
            for (int j = 1; j < AppStart.rY-1; j++){
                for (int cl = 0; cl < nTrails[i][j].length; cl++) {
                    float tmp = 0;
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 2; y++) {
                            tmp += trails[i+x][j+y][cl];
                        }
                    }
                    tmp /= 9;
                    if (cl == clusterManager.clusterMap[i][j]){
                        tmp += 0.05;
                    } else {
                        tmp *= 0.8f;
                    }
                    nTrails[i][j][cl] = tmp*0.9f;
                }
            }
        }

        trails = nTrails;

        agentManager.agents.stream().parallel().filter(agent -> agent[0] >= 0 && agent[0] <= AppStart.rX-1 && agent[1] >= 0 && agent[1] <= AppStart.rY-1)
                .forEach(agent -> {
                    trails[(int) agent[0]][(int) agent[1]][(int) agent[4]] = (float) Math.min(trails[(int) agent[0]][(int) agent[1]][(int) agent[4]]+0.02, 1);
                });

        Runnable run = new Runnable() {
            @Override
            public void run() {
                //Imgproc.blur(img, img, new Size(3, 3));
                Core.multiply(img, new Scalar(AppStart.sustain, AppStart.sustain, AppStart.sustain, AppStart.alphaSustain), img);
                agentManager.agents.stream().parallel().filter(agent -> agent[0] >= 0 && agent[0] <= AppStart.rX-1 && agent[1] >= 0 && agent[1] <= AppStart.rY-1)
                        .forEach(agent -> {
                            Imgproc.circle(img, new Point(agent[0], agent[1]), 0, new Scalar(agent[5]*255, agent[6]*255, agent[7]*255, 50), 1);
                        });
                //Imgproc.medianBlur(img, img, 3);

                Image image = mat2Image(img);

                /*WritableImage wimg = new WritableImage((int)image.getWidth(), (int)image.getHeight());

                for (int i = 0; i < image.getWidth(); i++){
                    for (int j = 0; j < image.getHeight(); j++){
                        Color cA = image.getPixelReader().getColor(i, j);
                        double aN = 0;//1-cA.getOpacity();
                        Color c = Color.color(cA.getRed()*cA.getOpacity()+aN, cA.getGreen()*cA.getOpacity()+aN, cA.getBlue()*cA.getOpacity()+aN);
                        wimg.getPixelWriter().setColor(i, j, c);
                    }
                }*/



                Platform.runLater(() -> {
                    //canvas.getGraphicsContext2D().
                    if (snap == null) {
                        canvas.getGraphicsContext2D().drawImage(AppStart.img, 0, 0);
                    } else {
                        canvas.getGraphicsContext2D().drawImage(snap, 0, 0);
                    }
                    canvas.getGraphicsContext2D().drawImage(image, 0, 0);
                    Image wimg = canvas.snapshot(new SnapshotParameters(), new WritableImage((int)image.getWidth(), (int)image.getHeight()));
                    snap = wimg;
                    if (videoWriter != null){
                        Mat tmp = imageToMat(wimg);
                        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGBA2BGRA);
                        videoWriter.write(tmp);
                    } else {
                        saveToFile(wimg);
                    }
                });

            }
        };

        parallelExe.execute(run);
    }

    public Mat imageToMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] buffer = new byte[width * height * 4];

        PixelReader reader = image.getPixelReader();
        WritablePixelFormat<ByteBuffer> format = WritablePixelFormat.getByteBgraInstance();
        reader.getPixels(0, 0, width, height, format, buffer, 0, width * 4);

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, buffer);
        return mat;
    }

    public WritableImage mat2Image(Mat src)
    {
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", src, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new WritableImage(new Image(new ByteArrayInputStream(buffer.toArray())).getPixelReader(), src.width(), src.height());
    }

    int count = 0;

    public void saveToFile(Image image) {
        File outputFile = new File(".\\imgs3\\dress2_" + count++ + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
