package clustering;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package clustering;  //imports everything inside package , eg.conceptFinder,form,Matoperation etc here.

import Jama.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import java.util.*;
import java.io.*;

/**
 *
 * @author raghav04.TRN
 */
public class QoS_Logit{
	
	static String[] arg;
   //static double[][] QoS;
	static String filepath;
	static double[] QoS_train,QoS_test;
	static double[][] X;
   static double[][] Y;
   static double[][] MaxMinActual,MaxMinPredicted;
   static int dataSize;
   static int train_dataSize;
   static int test_dataSize;
   public static int K;
   static cluster[] myFinalCluster;
   static boolean printIsOK=false;	
   static HashMap<Integer,Double> distMap;
	private static List<double[]> freeFormsVector = new ArrayList<double[]>();	// every double[] is a vector representing the coordinates of a free forms ( 1 if contains the i-th keyword, 0 if not)
   static String[] cat=new String[5];	static String[] app=new String[5];	static String[] sub=new String[5];	static String[] type=new String[5];
private static BufferedWriter bw;
     
		public static void main(String path) throws FileNotFoundException {
				// TODO code application logic here
				// arg=args;
				int i=0;
				double[]  QoS=KeywordExtraction.QoS; 
				System.out.println("QoS_read Done");
				filepath=path;
				dataSize=QoS.length;
				QoS_Clustering(QoS);
				System.out.println("QoS_Clustering Done");
				print_cluster(QoS,path); //to save to ClusterList.csv
				System.out.println("QoS_Print Cluster Done");
				Logistic.main(path); //To find actual weights using all ticket data
		}

		private static void QoS_Clustering(double[] QoS) {
			// TODO Auto-generated method stub
			System.out.println("rows="+QoS.length);
			for(int i=0;i<QoS.length;i++){
            	double[] form = new double[1];  //Considering only resolution time:1 dimension only
               form[0]=QoS[i];
               freeFormsVector.add(form);
            }
            int k=3;
            //for(k=1;k<=15;k++)
            //{
               K=k;
               clustering(QoS);
               System.out.println("stage {"+k+"} Clear"); 
//         }
		}

		private static void clustering(double[] QoS) {
			// TODO Auto-generated method stub
			double averageD = 0;
			myFinalCluster=new cluster[K];
			if(K == 1){
                  System.out.println("inside k=1");
                  cluster[] c=new cluster[K];
                  c[0]=new cluster(0);
                  for(int i=0;i<QoS.length;i++){
                        c[0].add(i);
                    }
                    myFinalCluster=c;
                    System.out.println(c.length+"CLUSTER"+c[0].size);
                  }
                else{
        distMap = new HashMap<Integer,Double>();	
        int[] centers = new int[K];
		int[] newCenters = new int[K];
		newCenters=initMedoid();
		double dist=0;
		while(!isSameArray(centers, newCenters)){
			dist = 0;
			averageD = 0;
                    //   System.out.println("After entering");
			centers = newCenters.clone();
			cluster[] cl = makeGroup(centers);
                //      System.out.println("After Makegruoup");
			myFinalCluster = cl;
			if (printIsOK){
				System.out.println("----- Clustering round -----");
			}
			int i =0;
			for(cluster c : cl){
				for (int point: c.group){
					dist+=distance(point,c.medoid);
				}
                          //      System.out.println("After finding distance");
				averageD += c.diameter();
				int candidate = newMedoid(c, centers);
                            //  System.out.println("After new medoid");
				if(candidate>=0){
					newCenters[i]=candidate;
				}
				i++;
			}
			dist /= (double)QoS.length;
			averageD /= (double)K;
        }
                try{
                            FileWriter f=new FileWriter("QoS_distance.csv",true);
                            f.write(K+","+averageD+","+dist+"\n");
                            f.flush();
                            f.close();
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
	                }
		 evaluation();
		
		}
		
		public static int[] initMedoid(){
			int[] centers = new int[K];
			centers[0]=0;
			for(int i=1;i<K;i++){
				double max = 0;
				int ind = 0;
				for(int j=0;j<dataSize;j++){
					double min = distance(j,centers[0]);
					for(int k=1;k<i;k++){
						double dist = distance(j,centers[k]);
						if (dist<min){
							min = dist;
						}
					}
					if (min>max){
						max=min;
						ind = j;
					}
				}
				centers[i]=ind;
			}
			return centers;
		}

		public static double distance(int i,int j){
			//System.out.println(i+" "+j);
			double cos =euclideanDistance(freeFormsVector.get(i),freeFormsVector.get(j));
		//	double cos = cosineDistance(freeFormsVector.get(i),freeFormsVector.get(j));
		//	double cos = manhattanDistance(freeFormsVector.get(i),freeFormsVector.get(j));
			//double jac = computeSimilarity(fixedFieldsOfClusters.get(i),fixedFieldsOfClusters.get(j));
			//double distance = alpha*cos+(1-alpha)*jac;
			double distance =cos;
			distance = Math.round(distance * 10000);
			distance = distance/10000;
			return distance;

		}
	
		public static double manhattanDistance(double[] docVector1 , double[] docVector2){
			double res=0.0,div=0.0,normD=0.0;
			for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
			{
				res+=Math.abs(docVector1[i]-docVector2[i]);
				div+=Math.abs(docVector1[i])+Math.abs(docVector2[i]);
			}
			if(div==0) normD=0;
			else normD=res/div; //Normalized distance
		//	System.out.println("Manhattan Distance"+res+"Normalized"+normD);
			return normD; ///Changed on 24th May		
		}
		
		public static double euclideanDistance(double[] docVector1 , double[] docVector2){
			double res=0.0,div=0.0,normD=0.0;
			//for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length //Considering Only Resolution Time
			for (int i = 0; i <1; i++) //docVector1 and docVector2 must be of same length //Comment this and uncomment previous one for all three QoS parameter
			{ 
				res+=(docVector1[i]-docVector2[i])*(docVector1[i]-docVector2[i]);
				div+=(docVector1[i]+docVector2[i])*(docVector1[i]+docVector2[i]);
			}
			if(div==0) normD=0;
			else normD=res/div; //Normalized distance
				return normD; ///Changed on 24th May		
		}
				
		public static double cosineDistance(double[] docVector1, double[] docVector2) {
			double dotProduct = 0.0;
			double magnitude1 = 0.0;
			double magnitude2 = 0.0;
			double cosineSimilarity = 0.0;

			for (int i = 0; i < docVector1.length; i++) //docVector1 and docVector2 must be of same length
			{
				double a=docVector1[i];
				double b=docVector2[i];
				dotProduct +=  a*b ;  //a.b
				magnitude1 += a*a;  //(a^2)
				magnitude2 += b*b; //(b^2)
			}

			magnitude1 = Math.sqrt(magnitude1);//sqrt(sum(a^2))
			magnitude2 = Math.sqrt(magnitude2);//sqrt(sum(b^2))
			cosineSimilarity=0;
			if (magnitude1 != 0.0 && magnitude2 != 0.0) {
				cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
				if(Math.abs(cosineSimilarity)>1)cosineSimilarity=1.0; //System.out.println("ERROR"+cosineSimilarity+"B"+dotProduct+"DotProduct"+magnitude1+"magnitude1"+magnitude2+"magnitude2");
			} else {
				return 0.0;
				}
			//System.out.println(cosineSimilarity+"Cosine Distance"+Math.acos(cosineSimilarity)/Math.PI);
			return (Math.acos(cosineSimilarity)/Math.PI); //Between -1 to 1
		}
		
		public static boolean isSameArray(int[] a,int[] b){
			for(int i=0;i<a.length;i++){
				if(a[i]!=b[i]){
					return false;
				}
			}
			return true;
		}
	
		public static cluster[] makeGroup(int[] medoids){
			cluster[] allCluster = new cluster[K];
			for(int j=0;j<K;j++){
				allCluster[j]=new cluster(medoids[j]);
			}
			for(int i=0;i<dataSize;i++){
				double min = 1;
				int ind = 0;
				for (int c=0;c<K;c++){
					double dist = distance(i,allCluster[c].medoid);
					if(dist<min){
						min = dist;
						ind = c;
					}
				}
				allCluster[ind].add(i);
			}
			return allCluster;
		}
		public static int newMedoid(cluster cl, int[] medoids){
			int originalMedoid = cl.medoid;
			double minCost = Double.MAX_VALUE;
			int bestCandidate = originalMedoid;
			for(int i=0;i<cl.size;i++){
				int currentMedoid  = cl.group.get(i);
				cl.medoid = currentMedoid; // we try every point of the cluster as the new medoid and compute the cost of this change in the group
				double TC = 0; // total cost
				cluster[] newClusters = partialClustering(medoids, originalMedoid, cl.medoid, cl);
				for(cluster C : newClusters){
					for (int point : C.group){
						double cost = betterDist(point,C.medoid)-betterDist(point,originalMedoid);
						//double cost =distance(point,C.medoid)-distance(point,originalMedoid);
						TC += cost;
					}
				}
				if(TC<minCost){
					minCost = TC;
					bestCandidate = currentMedoid;
				}

			}
			cl.medoid = originalMedoid;  // we put the old medoid back
			if(minCost<0 && bestCandidate!=originalMedoid){
				if (printIsOK){
					System.out.println("old:"+originalMedoid+"  new:"+bestCandidate+"  cost:"+minCost);
				}
				return bestCandidate;
			}
			else {
				if (printIsOK){
					System.out.println("No change");
				}
				return -1; // -1 means no best candidate
			}
		}
	
		public static cluster[] partialClustering(int[] medoids,int oldMedoid, int newMedoid, cluster cl ){
			cluster[] partialClustering = new cluster[K];
			for (int i=0;i<K;i++){
				int medoid = medoids[i];
				if (medoid == oldMedoid){
					medoid = newMedoid;
				}
				partialClustering[i] = new cluster(medoid);
				partialClustering[i].group.remove(0); //  remove the center which is added automatically
				partialClustering[i].size--;
			}

			for (int point : cl.group){
				double distMin = 2;
				int bestIndice = 0;
				for (int j=0;j<K;j++ ){
					double dist = betterDist(partialClustering[j].medoid, point);
					//double dist = distance(partialClustering[j].medoid, point);
					if(dist<distMin){
						distMin = dist;
						bestIndice = j;
					}
				}
				partialClustering[bestIndice].add(point);
			}
			return partialClustering;
		}
		
		public static double betterDist(int a, int b){
			int id;
			if (a<b){id=a*100000+b;}
			else {id = b*100000+a;}
			if (distMap.containsKey(id)){
				return distMap.get(id);
			}
			else {
				double d = distance(a,b);
				if (distMap.size()>100000){
					distMap.clear();
				}
				distMap.put(id,d);
				return d;
			}
		}
		
		public static double evaluation(){
			double BigS = 0,len=0;
			int clusterIndex = -1;
			System.out.println("inside evalutaion");
	        for (cluster clust : myFinalCluster){
				clusterIndex++;
				//System.out.println("   "+clust.size);
				for (int i : clust.group){
					//System.out.println(i);
					double a = 0;
					int size = 0;
					for (int j : clust.group){  
					//	if (i!=j){ //commented on 24th May
							a += distance(i,j);
							size ++;
						//}
					}
					if(size>1) a=a/size;
					double b=1;
					for (int k=0;k< myFinalCluster.length;k++){
						if (k!=clusterIndex){
							double dist = 0;
							int siz = 0;
							cluster clust2 = myFinalCluster[k];
							for (int j : clust2.group){
								dist += distance(i,j);
								siz ++;
							}
							dist = dist / siz;
							if (dist<b){
								b=dist;
							}

						}
					}
					double s=(a>b)?(b - a)/a :(b-a)/b;
					BigS += s;
					len++;
				}
				//System.out.println("   "+BigS);
				
			}
			//BigS = BigS / dataSize;
	        System.out.println("BigS"+BigS+"len"+len);
	        BigS = BigS /len;
	        // System.out.println("after evaluation");
	                
			try {
				FileWriter fw = new FileWriter("QoS_Silhouette.txt", true);
	   			fw.write("("+K+")"+BigS+","+"\n");
	   			fw.flush();
				fw.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("Silhouette: ("+ K+") "+BigS);
			return BigS;

		}
		private static void print_cluster(double[] QoS,String path) {
			System.out.println("Inside Categorize Cluster"+" QoS.length"+QoS.length);
			X=new double[QoS.length][1];
			Y=new double[QoS.length][1];
		double[][]	Max_Min=new double[K][2];
			System.out.println("dataSize"+dataSize+"QoS.length"+QoS.length);
			// TODO Auto-generated method stub
			try{
			  File fileName=new File(path+"ClusterList.csv");
			  OutputStream os = (OutputStream) new FileOutputStream(fileName);
			String encoding = "UTF8";
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			BufferedWriter bw = new BufferedWriter(osw); 
			bw.write("Index"+","+"Resolution Time");
			bw.newLine();
			int j=0,cluster_index=0;
				for(cluster clust:myFinalCluster)
	   			{
					for (int i : clust.group)
	   					{
					 X[j][0]=freeFormsVector.get(i)[0];  //Useful for partial linear regression
	   				 Y[j][0]=cluster_index;
	   				 bw.write(cluster_index+"("+i+")"+","+X[j][0]+",");
	   				 bw.newLine();
	   				j++;
	   					}
					cluster_index++;
	   			}
				bw.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
			Max_Min=FindMaxMin();
	}
		
		public static double[][] FindMaxMin(){
			double[][] max_min=new double[K][2];
			int ci=0; //cluster_index
			for(cluster clust:myFinalCluster)
   			{
				max_min[ci][0]=0; max_min[ci][1]=1000;
				for(int i:clust.group)
				{
					double val=freeFormsVector.get(i)[0]; //Resolution Time
					if (val>max_min[ci][0]) max_min[ci][0] = val;
					if(val<max_min[ci][1]) max_min[ci][1]=val;
				}
				System.out.println(max_min[ci][0]+","+max_min[ci][1]);
				ci++;
   			}
			MatToFile(new Matrix(max_min),"QoS_Logisitc_Max_Min");
			return max_min;
		}

		static class cluster {
		int medoid;					// the medoid of the cluster
		ArrayList<Integer> group;	// all the point of the cluster
		int size;					// the size of the cluster
		
		public void add(int i){
			if (!group.contains(i)){
				group.add(i);
				size++;
			}
		}
		public void removeIndex(Integer I){
			if(size>I){
				group.remove(I);
				size--;
			}
		}
		public cluster(int center){
			this.medoid=center;
			group = new ArrayList<Integer>();
			group.add(center);
			size=1;
		}

		public double diameter(){
			double max = 0;
			for(int i=0;i<size;i++){
				for(int j=0;j<size;j++){
					double dist = distance(group.get(i),group.get(j));
					if (dist>max){
						max = dist;
					}
				}
			}
			return max;
		}

		public void printState(){
			String output = "";
			output+="diameter : "+this.diameter()+"; ";
			output+="size : "+size+"; ";
			System.out.println(output);
		}

		public double radius(){
			double max = 0;
			for(int i=0;i<size;i++){
				double dist = distance(group.get(i),medoid);
				if (dist>max){
					max = dist;
				}
			}
			return max;
		}
	}
		public static void MatToFile(Matrix out, String filename) {
			try {
				int err=0;
				double sum=0;
				File matFile = new File(filepath+filename+".csv");
				//if(matFile.exists()){ matFile.delete(); matFile.createNewFile();}
				@SuppressWarnings("resource")
				OutputStream os= (OutputStream) new FileOutputStream(matFile);
				String encoding = "UTF8";
				OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
				BufferedWriter bw = new BufferedWriter(osw);
				for(int i=0;i<out.getRowDimension();i++)
				{
					for(int j=0;j<out.getColumnDimension();j++) bw.write(out.get(i, j)+",");
					bw.newLine();
				}
				bw.close();		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}    
}     

