import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppStart extends Application {

    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    static final double angle = Math.PI/5;
    static final float turnFactor = 1f;

    static  float checkDistance = 20;

    static final int rX = 720;
    static final int rY = 720;

    //static final int agentCount = 50000;

    static  float randomness = 0.05f;

    static final float sustain = 0.95f;

    public ArrayList<Mat> frameBuffer = new ArrayList<>();

    int frameCount = 0;

    int maxSaveFrames = 600;

    Image img = new Image("file:\\G:\\Medien\\MyProgrammBilder\\DSC_2921Two_Bearbeitet.png", 500, 500, false, true);

    Mat imgMat = imageToMat(img);

    Canvas canvas = new Canvas(rX, rY);

    WritableImage wimg = new WritableImage(rX, rY);

    Mat trails;

    ArrayList<float[]> agents = new ArrayList<>();

    Random rnd = new Random();

    ExecutorService parallelExe = Executors.newCachedThreadPool();

    @Override
    public void start(Stage stage) throws Exception {
        trails = imageToMat(wimg);
        stage.setScene(new Scene(new HBox(canvas)));
        stage.show();
        for (int i = 0; i < rX; i++) {
            for (int j = 0; j < rY; j++) {
                wimg.getPixelWriter().setColor(i, j, Color.color(0, 0, 0));
            }
        }

        canvas.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()){
                Imgproc.circle(trails, new Point(e.getX(), e.getY()), 3, Scalar.all(255), -1);
            }
        });

        canvas.getGraphicsContext2D().drawImage(wimg, 0, 0);
        initializeAgents();
        paintTrails();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                long s = System.currentTimeMillis();
                moveAgents();
                //System.out.println("UpdateTime = " + (System.currentTimeMillis()-s) + "ms");
                s = System.currentTimeMillis();
                paintTrails();
                //System.out.println("RenderTime = " + (System.currentTimeMillis()-s) + "ms");
            }
        };
        ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
        exe.scheduleWithFixedDelay(run, 100, 30, TimeUnit.MILLISECONDS);

    }




    public void paintTrails(){
        Imgproc.blur(trails, trails, new Size(3, 3));
        Core.multiply(trails, new Scalar(sustain, sustain, sustain, 1), trails);

        canvas.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()){
                Imgproc.circle(trails, new Point(e.getX(), e.getY()), 3, Scalar.all(255), -1);
            }
        });

        /*Runnable save = new Runnable() {
            @Override
            public void run() {
                VideoWriter videoWriter;
                String p = ".\\" + "testvideo.mp4";
                videoWriter = new VideoWriter(p, VideoWriter.fourcc('x', '2','6','4'), 25, trails.size());

                for (int i = maxSaveFrames-1; i >= 0; i--){
                    videoWriter.write(frameBuffer.get(i));
                }

                videoWriter.release();
                System.out.println("saved Video");
            }
        };*/

        Runnable run = new Runnable() {
            @Override
            public void run() {
                agents.stream().parallel().filter(agent -> agent[0] >= 0 && agent[0] <= rX-1 && agent[1] >= 0 && agent[1] <= rY-1)
                        .forEach(agent -> Imgproc.circle(trails, new Point(agent[0], agent[1]),0, new Scalar(agent[5]*255, agent[6]*255, agent[7]*255, 100), 1));

                /*if (frameCount%5 == 0 && frameBuffer.size() < maxSaveFrames) {
                    Mat mat = new Mat();
                    trails.copyTo(mat);
                    frameBuffer.add(mat);
                } else if (!saved && !(frameBuffer.size() < maxSaveFrames)) {
                    saved = true;
                    parallelExe.execute(save);
                }
                frameCount++;*/
                wimg = mat2Image(trails);
                Platform.runLater(() -> canvas.getGraphicsContext2D().drawImage(wimg, 0, 0));

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
        return new WritableImage(new Image(new ByteArrayInputStream(buffer.toArray())).getPixelReader(), rX, rY);
    }
}
