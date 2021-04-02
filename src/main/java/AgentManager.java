import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
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

                float norm = (float)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

                dx /= norm;
                dy /= norm;

                agents.add(new float[] {i, j, dx, dy, cluster,
                        (float) img.getPixelReader().getColor(i, j).getBlue(), (float)img.getPixelReader().getColor(i, j).getGreen(), (float)img.getPixelReader().getColor(i, j).getRed()});
            }
        }
    }


    public void moveAgents(){
        agents.parallelStream().forEach(agent -> moveOneAgent(agent));
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

        float[] posleft = {agent[0]+left[0]*10, agent[1]+left[1]*AppStart.checkDistance};
        float[] posright = {agent[0]+right[0]*10, agent[1]+right[1]*AppStart.checkDistance};

        double p1 = 0;
        double p2 = 0;
        double p3 = 0;

        boolean followed = false;

        if ((pos[0] >= 0 && pos[0] <= AppStart.rX-1) && (pos[1] >= 0 && pos[1] <= AppStart.rY-1)) {
            float[] c = slimeManager.trails[(int)pos[0]][(int)pos[1]];
            for (int i = 0; i < c.length; i++){
                if (i == agent[4]){
                    p1 += c[i];
                } else {
                    p1 -= c[i];
                }
            }
        }
        if ((posleft[0] >= 0 && posleft[0] <= AppStart.rX-1) && (posleft[1] >= 0 && posleft[1] <= AppStart.rY-1)) {
            float[] c = slimeManager.trails[(int)posleft[0]][(int)posleft[1]];
            for (int i = 0; i < c.length; i++){
                if (i == agent[4]){
                    p1 += c[i];
                } else {
                    p1 -= c[i];
                }
            }
        }
        if ((posright[0] >= 0 && posright[0] <= AppStart.rX-1) && (posright[1] >= 0 && posright[1] <= AppStart.rY-1)) {
            float[] c = slimeManager.trails[(int)posright[0]][(int)posright[1]];
            for (int i = 0; i < c.length; i++){
                if (i == agent[4]){
                    p1 += c[i];
                } else {
                    p1 -= c[i];
                }
            }
        }

        if (p1 <= p2 && p1 <= p3){
            agent[0] = agent[0]+agent[2]*2;
            agent[1] = agent[1]+agent[3]*2;
            followed = true;
        } else if (p2 <= p1 && p2 <= p3){
            agent[0] = agent[0]+leftTurn[0]*2;
            agent[1] = agent[1]+leftTurn[1]*2;
            agent[2] = leftTurn[0];
            agent[3] = leftTurn[1];
            followed = true;
        } else if (p3 <= p2 && p3 <= p1){
            agent[0] = agent[0]+rightTurn[0]*2;
            agent[1] = agent[1]+rightTurn[1]*2;
            agent[2] = rightTurn[0];
            agent[3] = rightTurn[1];
            followed = true;
        }

        if ((followed?rnd.nextFloat() < AppStart.randomness:rnd.nextFloat() < AppStart.randomness*3)){
            switch (rnd.nextInt(4)){
                case 0: {
                    agent[0] = agent[0]+agent[2]*2;
                    agent[1] = agent[1]+agent[3]*2;
                    break;
                }
                case 1: {
                    agent[0] = agent[0]+leftTurn[0]*2;
                    agent[1] = agent[1]+leftTurn[1]*2;
                    agent[2] = leftTurn[0];
                    agent[3] = leftTurn[1];
                    break;
                }
                case 2: {
                    agent[0] = agent[0]+rightTurn[0]*2;
                    agent[1] = agent[1]+rightTurn[1]*2;
                    agent[2] = rightTurn[0];
                    agent[3] = rightTurn[1];
                    break;
                }
                default: {
                    if (agent[1]+agent[3]*2 > agent[1]+leftTurn[1]*2 && agent[1]+agent[3]*2 > agent[1]+rightTurn[1]*2){
                        agent[0] = agent[0]+agent[2]*2;
                        agent[1] = agent[1]+agent[3]*2;
                    } else if (agent[1]+leftTurn[1]*2 > agent[1]+agent[3]*2 && agent[1]+leftTurn[1]*2 > agent[1]+rightTurn[1]*2) {
                        agent[0] = agent[0]+leftTurn[0]*2;
                        agent[1] = agent[1]+leftTurn[1]*2;
                        agent[2] = leftTurn[0];
                        agent[3] = leftTurn[1];
                    } else {
                        agent[0] = agent[0]+rightTurn[0]*2;
                        agent[1] = agent[1]+rightTurn[1]*2;
                        agent[2] = rightTurn[0];
                        agent[3] = rightTurn[1];
                    }
                }
            }
        }


    }


}
