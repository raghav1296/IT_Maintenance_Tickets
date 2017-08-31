package clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import Jama.*;

/**    PAM.java
 * This class create :
 * 		- clusters.csv the list of clusters
 * 
 * Used document :
 * 		- keywords.csv 
 * 		- FinalKW.csv
 * 		- FixedFields.csv
 *  
 * @author Fabien_524445
 *
 * 19-Apr-2016 yeung_chiang - modified printclusters to print requestno
 */

public class PAM {
	/** GLOBAL PARMETERS
	 */
	public static int numDocuments; //number of documents
	public static int K;			// the number of clusters we want
	static double alpha = 0.1;         //0.8;		// the coefficient when we compute the distance
	static int dataSize; 
	static String usedKeyWords = "unigram_keywords_after_jaccard.csv"; 	// file where are stored the keywords found previously
	static String formAdress = "finalKW.csv";		// file where are stored the free forms ( just a list of all the keywords for every form )
	static String TFIDFAdress = "TFIDF.csv";		// file where are stored the free forms ( just a list of TFIDF values for all the keywords for every form )
	static HashMap<Integer,Integer> ticketMapping;	// for discarded tickets, to keep track of old index in file and new index in list  
	static boolean printIsOK=false;		// set true to print more details
	
	/** GLOBAL LISTS AND ARRAYS
	 *  some global list to store data
	 */
	private static List<double[]> freeFormsVector = new ArrayList<double[]>();	// every double[] is a vector representing the coordinates of a free forms ( 1 if contains the i-th keyword, 0 if not)
	//private static List<String> keyWordList = new LinkedList<String>();			// List of all the keyword find previously
	static cluster[] myFinalCluster;
	static LinkedList<LinkedList<Integer>> clusterInList = new LinkedList<LinkedList<Integer>>();
    static double[][] ttcm;
    static String path;

	/** setK
	 * @param k
	 */

	public static void setK(int k){
		K=k;
	}

	/** class cluster
	 * A class to store a cluster easily
	 * there is 
	 *   - an "add"/"remove" function to add/remove a new point
	 *   - functions to compute diameter and radius
	 *   - a function to find the new medoid
	 * @author Fabien_524445
	 *
	 */

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
			//System.out.println(output);
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

	/** READ()
	 * Method to read some files : usedKeywords and formAdress 
	 * create dfDocsVector and keyWordsList
	 * also find the dataSize
	 * @throws FileNotFoundException 
	 */
	public static void readFiles(String p) {
		try {
			// -- ++ 30-Sep-2016 yeung_chiang read TFIDF values instead of DF values
			//read();
			System.out.println("Inside PAM.readFiles");
                        path=p; 
      		readTFIDF();
			// -- -- 30-Sep-2016 yeung_chiang read TFIDF values instead of DF values
			//read2();
			// -- ++ 13-Oct-2016 yeung_chiang to get concept vectors (vector of DF + CF)
			//freeFormsVector = ConceptVector.getFinalVectors(freeFormsVector);
			System.out.println("Vector length : " + freeFormsVector.get(0).length);
			// -- -- 13-Oct-2016 yeung_chiang to get concept vectors (vector of DF + CF)
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*public static List<String> getKeyWordList() {
		return keyWordList;
	}
        */
    
	/** readTFIDF()
	 * Method to read some files : usedKeywords and TFIDFAdress 
	 * create dfDocsVector and keyWordsList
	 * also find the dataSize
	 * @throws FileNotFoundException 
	 */	
	public static void readTFIDF() throws FileNotFoundException{
		//keyWordList = new LinkedList<String>();
		System.out.println("Inside PAM.readTFIDF");
		freeFormsVector = new ArrayList<double[]>();
		       // keyWordList=readxcel.Unigram;
                ttcm=MatrixFormation.ttcm;  
                System.out.println("row"+ttcm.length+"ttcm.length");
                System.out.println("Col"+ttcm[0].length+"ttcm[0].length");
                dataSize=ttcm.length;
                for(int i=0;i<dataSize;i++){
                	double[] form = new double[ttcm[0].length];
                   form=ttcm[i];
                   freeFormsVector.add(form);
                }
         //dataSize = freeFormsVector.size();
		System.out.println(freeFormsVector.size()+"freeFormsVector.size()");
	}

	/**  COSINE DISTANCE()
	 * Method to calculate cosine similarity between two documents.
	 * @param docVector1 : document vector 1 (a)
	 * @param docVector2 : document vector 2 (b)
	 * @return cosine distance
	 * imported from previous code (calculate Similarity)
	 */
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

		magnitude1 = Math.sqrt(magnitude1);//sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2);//sqrt(b^2)

		if (magnitude1 != 0.0 && magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			//if(magnitude1 == 0.0 && magnitude2 == 0.0 ){
				return 0.0;
			}
			//System.out.println(magnitude1+" "+magnitude2);
			//return 1.0;
 
		return (Math.acos(cosineSimilarity)/Math.PI);
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
	/** JACCARD DISTANCE
	 * Method to categorize time.
	 * @param two lists
	 * @return jaccard distance
	 */
	public static double computeSimilarity(ArrayList<String> List1, ArrayList<String> List2) {
		double intersection = 0;
		int l = List1.size();
		//System.out.println(List1);
		for (int i = 0; i < l; i++) {
			if (List1.get(i).equals(List2.get(i))) {
				intersection++;
			}
		}
		if (intersection > 0) {
			double union = 2*l - intersection;
			return 1.0 - ((double) intersection / union);
		} else {
			return 0;
		}
	}

	/** DISTANCE
	 * Compute the distance between the points i and j
	 * @param i
	 * @param j
	 * @return
	 */
	public static double distance(int i,int j){
		System.out.println(i+" "+j);
	//	double cos = cosineDistance(freeFormsVector.get(i),freeFormsVector.get(j));
		double cos = manhattanDistance(freeFormsVector.get(i),freeFormsVector.get(j));
		//double jac = computeSimilarity(fixedFieldsOfClusters.get(i),fixedFieldsOfClusters.get(j));
		//double distance = alpha*cos+(1-alpha)*jac;
		double distance =cos;
		distance = Math.round(distance * 10000);
		distance = distance/10000;
		return distance;

	}

	/**  makeGroup
	 * For each point find the closest medoid
	 * 
	 * @param medoids : an int[] of the medoids
	 * @return a cluster array with all the clusters.
	 */

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

	/** newMedoid
	 * 	
	 * 
	 * 
	 * @return the number of the new medoid found
	 */

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

	/**   partialClustering
	 * The medoid of cl (oldmedoid) is changed into another point of cl (newMedoid)
	 * for every point of cl we find the new cluster
	 * @param medoids : an array with all the medoids (contain the old medoid)
	 * @param oldMedoid : the medoid we removed to evaluate the cost. oldMedoid is the medoid of cl.
	 * @param newMedoid : the new medoid which we want to know the cost
	 * @param cl : the old cluster we are working in
	 * @return an array of the cluster (containing only the point of the previous cl)
	 */

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

	/** initMedoid
	 * take a random point as the fisrt point
	 * then select the next point as far as possible of all the point already chosen before
	 * -> we try to maximize the min distance to the points already chosen
	 * @return an int array of good potential medoids to begin the algo
	 */

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
	/**  printArray
	 * just a basic tool to primt an array
	 * @param arr
	 */
	public static void printArray(int[] arr){
		String str = "";
		for(int i : arr){
			str+=i+" ";
		}
		System.out.println(str);
	}


	/** printClusters
	 * write an array of cluster in clusters.csv
	 * one line per cluster, just a list of int representing the id of all the tickets in the cluster
	 * @param cl
	 * @throws IOException
	 */
	public static void printClusters(cluster[] cl) throws IOException{

		BufferedWriter br = new BufferedWriter(new FileWriter(path+"clusters.csv"));
		//BufferedWriter brReqNo = new BufferedWriter(new FileWriter("clusterswithreqno.csv"));
		for (cluster clus : cl){
			StringBuilder sb = new StringBuilder();
			//StringBuilder sbReqNo = new StringBuilder();
			int medoid = clus.medoid;
			int medoidPosition = clus.group.indexOf(medoid);
			clus.group.remove(medoidPosition);
			
			//medoid = ticketMapping.get(medoid); // for discarded tickets
			sb.append(medoid+",");
			//sbReqNo.append(reqno.get(medoid) + ",");
			
			for (Integer ticket : clus.group ){
				//ticket = ticketMapping.get(ticket); // for discarded tickets				
				sb.append(ticket+",");
				//sbReqNo.append(reqno.get(ticket) + ",");
			}
			clus.group.add(medoidPosition);
			sb.append("\n");
			//sbReqNo.append("\n");
			br.write(sb.toString());
			//brReqNo.write(sbReqNo.toString());
		}
		br.flush();
		br.close();
		
		//brReqNo.flush();
		//brReqNo.close();
	}

	/**  evaluation
	 * compute the average silhouette of all the clusters
	 * use myFinalCluster
	 * @return the average silhouette of myFinalCluster
	 */

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
				a = a / max(size,1);
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
				double s= (b - a)/max(a,b);
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
			FileWriter fw = new FileWriter(path+"Silhouette.txt", true);
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

	/** max
	 * basic tool to find the max of a and  b
	 * @param a
	 * @param b
	 * @return max(a,b)
	 */

	private static double max(double a, double b) {
		if (a>b) { return a;}
		else {return b;}
	}

	/** isSameArray
	 *  just compare the the two arrays
	 * @param a
	 * @param b
	 * @return true if they are the same
	 */

	public static boolean isSameArray(int[] a,int[] b){
		for(int i=0;i<a.length;i++){
			if(a[i]!=b[i]){
				return false;
			}
		}
		return true;
	}

	/** This try to improve the dist calculations
	 * @author Fabien_524445
	 *
	 */

	static HashMap<Integer,Double> distMap;
	static int limit;

	public static void initBetterDist(){
		distMap = new HashMap<Integer,Double>();
		limit = 1000000;
	}

	/** betterDist
	 * return distance between a and b, store the 1 000 000 last distance so we don't have to re-compute it 
	 * improve a bit the time
	 * @param a
	 * @param b
	 * @return
	 */

	public static double betterDist(int a, int b){
		int id;
		if (a<b){id=a*100000+b;}
		else {id = b*100000+a;}
		if (distMap.containsKey(id)){
			return distMap.get(id);
		}
		else {
			double d = distance(a,b);
			if (distMap.size()>limit){
				distMap.clear();
			}
			distMap.put(id,d);
			return d;
		}
	}
	
	/** main
	 * main fucntion that do several round of clustering until the cluster don't change
	 * 
	 * @param args
	 */

	public static double main(){
		double averageD = 0;
			if(K == 1){
                  System.out.println("inside k=1");
                  cluster[] c=new cluster[K];
                  c[0]=new cluster(0);
                  for(int i=0;i<dataSize;i++){
                        c[0].add(i);
                    }
                    myFinalCluster=c;
                    //System.out.println(c.length+"CLUSTER"+c[0].size);
                  }
                else{
		initBetterDist();
		int[] centers = new int[K];
		int[] newCenters = new int[K];
		newCenters=initMedoid();
		double dist=0;
		while(!isSameArray(centers, newCenters)){
			dist = 0;
			averageD = 0;
                     //  System.out.println("After entering");
			centers = newCenters.clone();
			cluster[] cl = makeGroup(centers);
                      // System.out.println("After Makegruoup");
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
			dist /= (double)dataSize;
			averageD /= (double)K;
			//System.out.println(K+" "+averageD+" "+dist);
        }
	//	System.out.println(K+"K value");
                try{
                            FileWriter f=new FileWriter(path+"distance.csv",true);
                            f.write(K+","+averageD+","+dist+"\n");
                            f.flush();
                            f.close();
                        }
                        catch(IOException e){
                            e.printStackTrace();
                        }
		/*try {
			//printClusters(myFinalCluster);
			//computeTime();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
                }
//			System.out.println("Returning evaluation");
		return evaluation();
	}
}
