import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class AgentManager {

    Random rnd = new Random();
    SlimeManager slimeManager;

    ArrayList<float[]> agents = new ArrayList();

    public void initializeAgents(Image img){
        for (int i = 0; i < img.getWidth(); i++){
            for (int j = 0; j < img.getHeight(); j++){
                float dx = rnd.nextFloat()-0.5f;
                float dy = rnd.nextFloat()-0.5f;

                float cluster = 0;
                float dist = (float) Math.sqrt(Math.pow(img.getPixelReader().getColor(i, j).getRed()-slimeManager.clusterManager.clusters[0][0], 2)
                        + Math.pow(img.getPixelReader().getColor(i, j).getGreen()-slimeManager.clusterManager.clusters[0][1], 2)
                        + Math.pow(img.getPixelReader().getColor(i, j).getBlue()-slimeManager.clusterManager.clusters[0][2], 2));

                for (int c = 0; c < slimeManager.clusterManager.clusters.length; c++){
                    float tmp = (float) Math.sqrt(Math.pow(img.getPixelReader().getColor(i, j).getRed()-slimeManager.clusterManager.clusters[c][0], 2)
                            + Math.pow(img.getPixelReader().getColor(i, j).getGreen()-slimeManager.clusterManager.clusters[c][1], 2)
                            + Math.pow(img.getPixelReader().getColor(i, j).getBlue()-slimeManager.clusterManager.clusters[c][2], 2));
                    if (tmp < dist){
                        dist = tmp;
                        cluster = c;
                    }
                }

                float norm = (float)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

                dx /= norm;
                dy /= norm;

                if (0.3 < img.getPixelReader().getColor(i, j).getBlue() + img.getPixelReader().getColor(i, j).getGreen() + img.getPixelReader().getColor(i, j).getRed()) {
                    agents.add(new float[]{i, j, dx, dy, cluster,
                            (float) img.getPixelReader().getColor(i, j).getBlue(), (float) img.getPixelReader().getColor(i, j).getGreen(), (float) img.getPixelReader().getColor(i, j).getRed()});
                }
            }
        }
    }


    public void moveAgents(){
        //System.out.println("a move");
        agents.parallelStream().forEach(this::moveOneAgent);
        //System.out.println(agents.stream().filter(a -> a[0] > 720).collect(Collectors.toList()).size());
        //System.out.println("a move finished");
    }

    public void moveOneAgent(float[] agent){

        boolean nearBorder = false;

        if (agent[0] > AppStart.rX-1){
            agent[0] = agent[0]-AppStart.rX;
            //agent[1] = rnd.nextFloat()*rY;
            nearBorder = true;
        }
        if (agent[1] > AppStart.rY-1){
            agent[1] = agent[1]-5;
            //agent[0] = rnd.nextFloat()*rX;
            agent[3] = -agent[3];
            nearBorder = true;
        }
        if (agent[0] < 0){
            agent[0] = agent[0] +AppStart.rX;
            //agent[1] = rnd.nextFloat()*rY;
            nearBorder = true;
        }
        if (agent[1] < 0){
            agent[1] = agent[1]+5;
            //agent[0] = rnd.nextFloat()*rX;
            agent[3] = -agent[3];
            nearBorder = true;
        }

        float[] pos = {agent[0]+agent[2]*AppStart.checkDistance, agent[1]+agent[3]*AppStart.checkDistance};

        float[] left = {(float)(Math.cos(AppStart.angle)*agent[2]-Math.sin(AppStart.angle)*agent[3]), (float) (Math.sin(AppStart.angle)*agent[2]+Math.cos(AppStart.angle)*agent[3])};
        float[] right = {(float)(Math.cos(-AppStart.angle)*agent[2]-Math.sin(-AppStart.angle)*agent[3]), (float) (Math.sin(-AppStart.angle)*agent[2]+Math.cos(-AppStart.angle)*agent[3])};

        float[] leftTurn = {(float)(Math.cos(AppStart.angle*AppStart.turnFactor)*agent[2]-Math.sin(AppStart.angle*AppStart.turnFactor)*agent[3]),
                (float) (Math.sin(AppStart.angle*AppStart.turnFactor)*agent[2]+Math.cos(AppStart.angle*AppStart.turnFactor)*agent[3])};
        float[] rightTurn = {(float)(Math.cos(-AppStart.angle*AppStart.turnFactor)*agent[2]-Math.sin(-AppStart.angle*AppStart.turnFactor)*agent[3]),
                (float) (Math.sin(-AppStart.angle*AppStart.turnFactor)*agent[2]+Math.cos(-AppStart.angle*AppStart.turnFactor)*agent[3])};

        float[] posleft = {agent[0]+left[0]*AppStart.checkDistance, agent[1]+left[1]*AppStart.checkDistance};
        float[] posright = {agent[0]+right[0]*AppStart.checkDistance, agent[1]+right[1]*AppStart.checkDistance};

        double p1 = 0;
        double p2 = 0;
        double p3 = 0;

        boolean followed = false;

        //System.out.println("poses");


        if ((pos[0] >= 0 && pos[0] <= AppStart.rX-1) && (pos[1] >= 0 && pos[1] <= AppStart.rY-1)) {
            float[] c = slimeManager.trails[(int)pos[0]][(int)pos[1]];
            for (int i = 0; i < c.length; i++){
                if (i == agent[4]){
                    p1 += c[i];
                } else {
                    p1 -= c[i]*0.3;
                }
            }
        }
        if ((posleft[0] >= 0 && posleft[0] <= AppStart.rX-1) && (posleft[1] >= 0 && posleft[1] <= AppStart.rY-1)) {
            float[] c = slimeManager.trails[(int)posleft[0]][(int)posleft[1]];
            for (int i = 0; i < c.length; i++){
                if (i == agent[4]){
                    p2 += c[i];
                } else {
                    p2 -= c[i]*0.3;
                }
            }
        }
        if ((posright[0] >= 0 && posright[0] <= AppStart.rX-1) && (posright[1] >= 0 && posright[1] <= AppStart.rY-1)) {
            float[] c = slimeManager.trails[(int)posright[0]][(int)posright[1]];
            for (int i = 0; i < c.length; i++){
                if (i == agent[4]){
                    p3 += c[i];
                } else {
                    p3 -= c[i]*0.3;
                }
            }
        }

        if (p1 >= p2 && p1 >= p3){
            agent[0] = agent[0]+agent[2]*AppStart.movementSpeed;
            agent[1] = agent[1]+agent[3]*AppStart.movementSpeed;
            followed = true;
        } else if (p2 >= p1 && p2 >= p3){
            agent[0] = agent[0]+leftTurn[0]*AppStart.movementSpeed;
            agent[1] = agent[1]+leftTurn[1]*AppStart.movementSpeed;
            agent[2] = leftTurn[0];
            agent[3] = leftTurn[1];
            followed = true;
        } else if (p3 >= p2 && p3 >= p1){
            agent[0] = agent[0]+rightTurn[0]*AppStart.movementSpeed;
            agent[1] = agent[1]+rightTurn[1]*AppStart.movementSpeed;
            agent[2] = rightTurn[0];
            agent[3] = rightTurn[1];
            followed = true;
        }

        if ((followed?rnd.nextFloat() < AppStart.randomness:rnd.nextFloat() < AppStart.randomness*3)){
            switch (rnd.nextInt(4)){
                case 0: {
                    agent[0] = agent[0]+agent[2]*AppStart.movementSpeed;
                    agent[1] = agent[1]+agent[3]*AppStart.movementSpeed;
                    break;
                }
                case 1: {
                    agent[0] = agent[0]+leftTurn[0]*AppStart.movementSpeed;
                    agent[1] = agent[1]+leftTurn[1]*AppStart.movementSpeed;
                    agent[2] = leftTurn[0];
                    agent[3] = leftTurn[1];
                    break;
                }
                case 2: {
                    agent[0] = agent[0]+rightTurn[0]*AppStart.movementSpeed;
                    agent[1] = agent[1]+rightTurn[1]*AppStart.movementSpeed;
                    agent[2] = rightTurn[0];
                    agent[3] = rightTurn[1];
                    break;
                }
                default: {
                    if (agent[1]+agent[3]*2 > agent[1]+leftTurn[1]*2 && agent[1]+agent[3]*2 > agent[1]+rightTurn[1]*2){
                        agent[0] = agent[0]+agent[2]*AppStart.movementSpeed;
                        agent[1] = agent[1]+agent[3]*AppStart.movementSpeed;
                    } else if (agent[1]+leftTurn[1]*2 > agent[1]+agent[3]*2 && agent[1]+leftTurn[1]*2 > agent[1]+rightTurn[1]*2) {
                        agent[0] = agent[0]+leftTurn[0]*AppStart.movementSpeed;
                        agent[1] = agent[1]+leftTurn[1]*AppStart.movementSpeed;
                        agent[2] = leftTurn[0];
                        agent[3] = leftTurn[1];
                    } else {
                        agent[0] = agent[0]+rightTurn[0]*AppStart.movementSpeed;
                        agent[1] = agent[1]+rightTurn[1]*AppStart.movementSpeed;
                        agent[2] = rightTurn[0];
                        agent[3] = rightTurn[1];
                    }
                }
            }
        }


    }


}
