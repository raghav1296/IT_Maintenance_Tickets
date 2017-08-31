/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

/**
 *
 * @author Administrator
 */
public class Kernel {
    /** Vector list data */
    public Data[] x;
    public double b;
    /** parameter kernel */
    public double Sigma;
    public double Pengali;
    public double Penambah;
    public double Pangkat;
    int counterData=0;
    
    public Kernel(int l){
        x = new Data[l];
    }
    public void addData (double [] vector, double idx){
        System.out.println(counterData); //Data Counter
        x[counterData] = new Data(idx,vector);
        counterData++;
    }
    //static kernel
    public static double dot_product(Data a, Data b) {
            double sum=0;
            for (int i=0;i<a.vectorData.length;i++)
                sum+=a.vectorData[i]*b.vectorData[i];
            return sum;
	}
    public static double euclidean_dist(Data a, Data b) {
            double sum=0;
            for (int i=0;i<a.vectorData.length;i++) 
                sum+=Math.pow(a.vectorData[i]-b.vectorData[i],2);
            return Math.sqrt(sum);
	}
    public static double kLinear(Data a, Data b) {
        return dot_product(a, b);
    }
    
    public static double kGaussian(Data a, Data b, double sigma) {
        return Math.exp(Math.pow(euclidean_dist(a, b),2)/(-2*(sigma*sigma)));
    }
    //Coba
    public static void main(String [] args){
        Data data1 = new Data(1,new double[]{5,1});
        Data data2 = new Data(1,new double[]{6,0});
        System.out.println(kGaussian(data1,data2,1));
    }
}