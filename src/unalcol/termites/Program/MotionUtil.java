/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unalcol.termites.Program;

import java.util.ArrayList;
import unalcol.random.RandomUtil;

/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class MotionUtil {

    /**
     *
     * @param pheromone
     * @return
     */
    public static int Roulette(float[] pheromone) {
        //System.out.println("roulette");
        float sum = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
        }
        double rand = (double) (Math.random() * sum);
        sum = 0;
        int mov = 0;
        for (int k = 0; k < pheromone.length; k++) {
            sum += pheromone[k];
            if (rand < sum) {
                mov = k;
                break;
            }
        }
        return mov;
    }

    /**
     *
     * @param pheromone
     * @return
     */
    public static int seek(float[] pheromone) {
        int dirPos = 0;
        double q0 = 0.9;
        ArrayList<Integer> temp = new ArrayList();
        if (Math.random() <= q0) {
            for (int k = 0; k < pheromone.length; k++) {
                if (pheromone[k] != -1) {
                    dirPos = k;
                    break;
                }
            }
            //System.out.println("maximum");
            for (int k = 1; k < pheromone.length; k++) {
                if (pheromone[k] != -1 && pheromone[dirPos] < pheromone[k]) {
                    dirPos = k;
                }
            }
            //store location with the max amount of pheromone
            float max = pheromone[dirPos];
            //System.out.println("dirpos" + dirPos);
            for (int k = 0; k < pheromone.length; k++) {
                if (pheromone[k] == max) {
                    temp.add(k);
                }
            }
            //System.out.println("temp" + temp);
            //System.out.print("1");
            dirPos = temp.get(RandomUtil.nextInt(temp.size()));
        } else {
            //System.out.print("2");
            float[] phinv = new float[pheromone.length];
            for (int i = 0; i < pheromone.length; i++) {
                if (pheromone[i] != -1) {
                    phinv[i] = pheromone[i];
                }else{
                    phinv[i] = 0;
                }
            }
            dirPos = Roulette(phinv);
        }

        //System.out.print("," + dirPos + "\n");
        return dirPos;
    }

    /**
     *
     * @param pheromone
     * @return
     */
    public static int carry(float[] pheromone) {
        int dirPos = 0;
        double q0 = 0.9;
        ArrayList<Integer> temp = new ArrayList();
        if (Math.random() <= q0) {
            for (int k = 0; k < pheromone.length; k++) {
                if (pheromone[k] != -1) {
                    dirPos = k;
                    break;
                }
            }

            for (int k = dirPos + 1; k < pheromone.length; k++) {
                if (pheromone[k] != -1 && pheromone[dirPos] > pheromone[k]) {
                    dirPos = k;
                }
            }

            //store location with the min amount of pheromone
            float min = pheromone[dirPos];
            for (int k = 0; k < pheromone.length; k++) {
                if (pheromone[k] == min) {
                    temp.add(k);
                }
            }
            dirPos = temp.get(RandomUtil.nextInt(temp.size()));
        } else {
            //Idea is choose direction with the less amount of pheromone
            float[] phinv = new float[pheromone.length];

            for (int i = 0; i < pheromone.length; i++) {
                if (pheromone[i] != -1) {
                    phinv[i] = 1 - pheromone[i];
                } else {
                    phinv[i] = 0;
                }
            }
            dirPos = Roulette(phinv);
        }
        return dirPos;
    }

}
