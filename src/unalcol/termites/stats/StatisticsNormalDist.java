/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unalcol.termites.stats;

import java.util.Arrays;
import java.util.ArrayList;


/**
 *
 * @author Arles Rodriguez <arles.rodriguez@gmail.com>
 */
public class StatisticsNormalDist {
    private ArrayList<Double> data;
    private int size;
    
    /**
     *
     * @param arr
     * @param s
     */
    public StatisticsNormalDist(ArrayList<Double> arr, int s){
        data = arr;
        size = s;
    }
    
    /**
     *
     * @return
     */
    public double getMean()
    {
        double sum = 0.0;
        for(double a : getData())
            sum += a;
        return sum/getSize();
    }

    /**
     *
     * @return
     */
    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :getData())
            temp += (mean-a)*(mean-a);
        return temp/getSize();
    }

    /**
     *
     * @return
     */
    public double getStdDev()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :getData())
            temp += (mean-a)*(mean-a);
        
        return Math.sqrt(temp/(getSize()-1));
    }

    /**
     *
     * @return
     */
    public double median() 
    {
       double[] b = new double[getData().size()];
       System.arraycopy(getData(), 0, b, 0, b.length);
       Arrays.sort(b);

       if (getData().size() % 2 == 0) 
       {
          return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
       } 
       else 
       {
          return b[b.length / 2];
       }
    }

    /**
     * @return the data
     */
    public ArrayList<Double> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList data) {
        this.data = data;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
}
