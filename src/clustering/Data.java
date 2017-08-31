package clustering;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
public class Data {
    public double target;
    public double [] vectorData;
    public double alpha;
    public Data (double y,double [] vector){
        vectorData = vector;
        target=y;
        alpha=0;
    }
}