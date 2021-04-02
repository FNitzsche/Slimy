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
    static final float turnFactor = 0.2f;

    static  float checkDistance = 20;

    static final int rX = 720;
    static final int rY = 720;

    //static final int agentCount = 50000;

    static  float randomness = 0.05f;

    static final float sustain = 0.95f;

    public ArrayList<Mat> frameBuffer = new ArrayList<>();

    int frameCount = 0;

    int maxSaveFrames = 600;

    Image img = new Image("file:\\G:\\Medien\\MyProgrammBilder\\RoseCut.png", 720, 720, false, true);

    Canvas canvas = new Canvas(rX, rY);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(new HBox(canvas)));
        stage.show();

        SlimeManager sm = new SlimeManager();
        AgentManager am = new AgentManager();
        ClusterManager cm = new ClusterManager();
        sm.agentManager = am;
        am.slimeManager = sm;
        sm.clusterManager = cm;
        sm.initialize(img);

        //sm.paintTrails(canvas);

        sm.agentManager.moveAgents();

        sm.paintTrails(canvas);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                long s = System.currentTimeMillis();
                sm.agentManager.moveAgents();
                System.out.println("UpdateTime = " + (System.currentTimeMillis()-s) + "ms");
                s = System.currentTimeMillis();
                sm.paintTrails(canvas);
                System.out.println("RenderTime = " + (System.currentTimeMillis()-s) + "ms");
                //checkDistance+=0.02;
            }
        };
        ScheduledExecutorService exe = Executors.newSingleThreadScheduledExecutor();
        exe.scheduleWithFixedDelay(run, 100, 50, TimeUnit.MILLISECONDS);

    }




}
