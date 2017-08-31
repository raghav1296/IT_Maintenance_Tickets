/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import java.io.*;
import java.util.*;
import Jama.*;
/**
 *
 * @author https://github.com/noerarief23/SVM-Multiclass/tree/master/src/Proses/SVM
 * @editor raghav04.TRN
 */
public class SVM {
    /** Trained/loaded model */
    private double C = 100;
    private int kernel;
    public static double SVM_accuracy;
    /** Tolerance */
    private double tol = 10e-7;  //should be low(very low!!!!)
    /** Tolerance */
    private double tol2 = 10e-7;
    private int maxpass = 500;
    private double Ei, Ej;
    private double ai_old, aj_old, b_old;
    private double L, H;
    /* mapping data dan hasil training */
    static private Kernel mapData;
    private static double[][] alpha_array;
    public static double[] label;
    public SVM (Kernel map, int kernel){
        mapData = map;
        this.kernel = kernel;
        SMO_simple();
    }
    public double svmTestOne(Data x) {
        double f = 0;
	for (int i=0; i<mapData.x.length; i++) {
            f += mapData.x[i].alpha*mapData.x[i].target*hitungNilaiKernel(x, mapData.x[i]);
	}
	return f+mapData.b;
    }
    private void SMO_simple() {
	int pass = 0;
	int alpha_change = 0;
	int i, j,itr=0,Max_itr=1000;
	double eta;
	//Main iteration:
	while(itr<Max_itr){
		//System.out.println(itr+"itr");
	//while (pass < maxpass) {
		//System.out.println(pass+"pass");
            alpha_change = 0;
            for (i=0; i<mapData.x.length; i++) {
             // Ei = Math.signum(svmTestOne(mapData.x[i])) - mapData.x[i].target;/////////////////////////////////Changed on 28th June
               Ei = svmTestOne(mapData.x[i]) - mapData.x[i].target;
              //  System.out.println(mapData.x[i].target+"target"+Ei+"Ei"+mapData.x[i].target*Ei+"mapData.x[i].target*Ei");
                if ((mapData.x[i].target*Ei<-tol && mapData.x[i].alpha<C) || (mapData.x[i].target*Ei>tol && mapData.x[i].alpha>0)) {
                  j=(int)Math.floor(Math.random()*(mapData.x.length-1));
                 // j=i-1;   if(j<0) j=mapData.x.length-1; // for(j=0;j<mapData.x.length-1;j++){   //////////////////////////////////////////////////////////////////////////27th June
                   j = (j<i)?j:(j+1);
                    //Ej = Math.signum(svmTestOne(mapData.x[j])) - mapData.x[j].target;  //////////////////////////////////////////////////////////////////////////28th June
                    Ej = svmTestOne(mapData.x[j]) - mapData.x[j].target; 
                    ai_old = mapData.x[i].alpha;
                    aj_old = mapData.x[j].alpha;
                    L = computeL(mapData.x[i].target, mapData.x[j].target);
                    H = computeH(mapData.x[i].target, mapData.x[j].target);
                    if (L == H)   continue;  //next   
                    double kij = hitungNilaiKernel(mapData.x[i],mapData.x[j]); 
                    double kii = hitungNilaiKernel(mapData.x[i],mapData.x[i]); 
                    double kjj = hitungNilaiKernel(mapData.x[j],mapData.x[j]); 
                    eta = 2*kij-kii-kjj;
                    if (eta >= 0) 	  continue;			//next i
                    mapData.x[j].alpha = aj_old - (mapData.x[j].target*(Ei-Ej))/eta;
                    if (mapData.x[j].alpha > H)
                        mapData.x[j].alpha = H;
                    else if (mapData.x[j].alpha < L)
                        mapData.x[j].alpha = L;
                    if (Math.abs(mapData.x[j].alpha-aj_old) < tol2) continue;  //next i
                    mapData.x[i].alpha = ai_old + mapData.x[i].target*mapData.x[j].target*(aj_old-mapData.x[j].alpha);
                    computeBias(mapData.x[i].alpha, mapData.x[j].alpha, mapData.x[i].target, mapData.x[j].target, kii, kjj, kij);
                    alpha_change++;
                 //   }////////////////////////////////////////////////////////////////////////27th June
		}
            }
            if (alpha_change == 0)
                pass++;
            else
                pass = 0;
            itr++;
        }
    }
    
    private void computeBias(double ai, double aj, double yi, double yj, double kii, double kjj, double kij) {
	double b1 = mapData.b - Ei - yi*(ai-ai_old)*kii - yj*(aj-aj_old)*kij;
	double b2 = mapData.b - Ej - yi*(ai-ai_old)*kij - yj*(aj-aj_old)*kjj;
	if (0 < ai && ai<C)
            mapData.b = b1;
	else if (0 < aj && aj < C)
            mapData.b = b2;
	else
            mapData.b = (b1+b2)/2;		
    }
    private double computeL(double yi, double yj) {
        double L = 0;
	if (yi != yj) {
            L = Math.max(0, -ai_old+aj_old);
	} else {
            L = Math.max(0, ai_old+aj_old-C);
	}
	return L;
    }
    private double computeH(double yi, double yj) {
        double H = 0;
	if (yi != yj) {
            H = Math.min(C, -ai_old+aj_old+C);
	} else {
            H = Math.min(C, ai_old+aj_old);
	}
	return H;
    }
    private double hitungNilaiKernel(Data a, Data b) {
        double ret = 0;
	switch (kernel) {
            case 0: //user defined
		break;
            case 1: //linear
		ret = Kernel.kLinear(a, b);
                break;
            case 3: //Gaussian
		ret = Kernel.kGaussian(a, b, mapData.Sigma);
		break;
        }
	return ret;
    }
    public static void main (String path){
    	double[][] ttcm=Fuzzy.PCA(new Matrix(MatrixFormation.ttcm)).getArray(); //With PCA
    	//double[][] ttcm=MatrixFormation.ttcm;  //Without PCA
    	ttcm=normalize_array(ttcm); /////////////////////////////////////////////29th June
    	double[][] conf={{0,0,0},{0,0,0},{0,0,0}};
        for(int value=0;value<3;value++)
        {
        	System.out.println(value);
        	int train_dataSize=(int)(ttcm.length*0.7);
            Kernel SVMLinemodel = new Kernel(train_dataSize);  //input is #of training data 
            SVMLinemodel.Sigma=1;  //UniNormal/Variance Gaussian
            double[][] membership=Fuzzy.membership;
            double[] idx=new double[ttcm.length];
            double[] p_idx=new double[ttcm.length];
        	for(int i=0;i<ttcm.length;i++) 
        		{
        		idx[i]=-1;
        		if(Fuzzy.max_index(membership[i])==value) idx[i]=1; //Cluster 1 v/s all
        		//System.out.println(i+","+idx[i]);
        		if(i<train_dataSize) SVMLinemodel.addData(ttcm[i],idx[i]);
        		p_idx[i]=-1;
        		}
        	label=idx;
        	SVM smoLineSimple = new SVM(SVMLinemodel,1); //Training Data with Gaussian Kernel
        	//for(int i=0;i<ttcm.length;i++){		System.out.println(SVMLinemodel.x[i].alpha);       		}
        	//System.out.println(SVMLinemodel.b);
        	for(int i=train_dataSize;i<ttcm.length;i++)
        	{
        		//p_idx[i]=-1;
        		if(smoLineSimple.svmTestOne(new Data(0,ttcm[i]))>0) p_idx[i]=1;
        		if(idx[i]==p_idx[i]){
        				if(idx[i]>0)conf[value][0]++;
        				//else conf[1][1]++;
       				}
        		else{
        			if(idx[i]>0)conf[value][1]++;
        			//else conf[1][0]++;
        		}       
        	}
        	System.out.println("Confusion Matrix");
   		//System.out.println("conf[0][0]"+conf[0][0]+"conf[0][1]"+conf[0][1]+"conf[1][0]"+conf[1][0]+"conf[1][1]"+conf[1][1]);        
        	//Fuzzy.MatToFile(new Matrix(conf),String.valueOf(value)+ "SVM_CRISP_confusion");
        }
        conf[0][2]=(double)(conf[0][0]+conf[1][0]+conf[2][0])/(conf[0][0]+conf[1][0]+conf[2][0]+conf[0][1]+conf[1][1]+conf[2][1]);
        SVM_accuracy=100*conf[0][2];
        System.out.println("conf[0][0]"+conf[0][0]+"conf[0][1]"+conf[0][1]+"conf[1][0]"+conf[1][0]+"conf[1][1]"+conf[1][1]+"conf[2][0]"+conf[2][0]+"conf[2][1]"+conf[2][1]+"Accuracy"+conf[0][2]);        
        MatToFile(new Matrix(conf),path+"SVM_CRISP_confusion");
    alpha_array  =new double[mapData.x.length+1][1];   
    for(int i=0;i<mapData.x.length;i++){ alpha_array[i][0]=mapData.x[i].alpha; System.out.print("alpha_array[i][0]"+alpha_array[i][0]);}
    alpha_array[mapData.x.length][0]=mapData.b;
    MatToFile(new Matrix(alpha_array),path+"SVM_alpha_array");
    }

     /**
    *
    * normalizes the 2-D array row-wise
    */
    private static double[][] normalize_array(double[][] arr) {
		// TODO Auto-generated method stub
    	for(int i=0;i<arr.length;i++){
    		double sum=0;
    		for(int j=0;j<arr[0].length;j++){	sum+=arr[i][j];	}
    		if(sum!=0)for(int j=0;j<arr[0].length;j++){	arr[i][j]=arr[i][j]/sum;	}
    	}
    	MatToFile(new Matrix(arr),"normalized_ttcm");
		return arr;
	}
	public static void MatToFile(Matrix out, String filename) {
    	try {
    		 File matFile = new File(filename+".csv"); 
    		//@SuppressWarnings("resource")
    		OutputStream os= (OutputStream) new FileOutputStream(matFile);
    		String encoding = "UTF8";
    		OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
    		BufferedWriter bw = new BufferedWriter(osw);
    		for(int i=0;i<out.getRowDimension();i++)
    		{
    			for(int j=0;j<out.getColumnDimension();j++) bw.write(out.get(i, j)+",");
    			bw.newLine();
    		}
    		bw.flush();
    		bw.close();		
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    } 
}