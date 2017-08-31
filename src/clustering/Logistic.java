package clustering;

import clustering.Logistic.Instance;
import clustering.QoS_Logit.cluster;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.*;

import clustering.QoS_Logit.cluster;

/**
 * Performs simple logistic regression.
 *
 * @author raghav04.TRN
 */
public class Logistic {
	static cluster[] myFinalCluster;
	/** the learning rate */
	private double rate;
	static Logistic logistic,logisticRF;
	static List<Instance> train_instances,kf_train_instance1,kf_train_instance2,kf_train_instance3,rf_train_instance1,rf_train_instance2,rf_train_instance3; 
	static List<Instance> test_instances;
	static List<Instance> new_test_instances;
	static List<Instance> org_instances;
	static List<Instance> remove_instance;
	static int trainDataSize,K,M=3;   //M=>#of ranfom forests/decision nodes
	static TreeSet<Integer> remove_indices;
	static int cat;// cat v/s others
	static int row,col;
	public static double logistic_accuracy,logistic_accuracy1,logistic_accuracy2,logistic_accuracy3;
	static int[][] conf={{0,0,0},{0,0,0},{0,0,0}};
	static int[][] conf1={{0,0,0},{0,0,0},{0,0,0}};static int[][] conf2={{0,0,0},{0,0,0},{0,0,0}};static int[][] conf3={{0,0,0},{0,0,0},{0,0,0}};
	/** the weight to learn */
	public static double[] weights,weights1,weights0,weights2,weights3;
	static double[][] ttcm=MatrixFormation.ttcm;

	/** the number of iterations */
	private int ITERATIONS =5*ttcm.length;

	public Logistic(int n) {
		this.rate = 0.1;
		weights = new double[n];
		weights1=new double[n];		weights2=new double[n];		weights3=new double[n];
	}

	private static double sigmoid(double z) {
		return 1.0 / (1.0 + Math.exp(-z));
	}

	public void train(List<Instance> instances) {
		//System.out.println(instances.size()+"instances.size()");
		System.out.println("cat"+cat);
		int n=0;
		weights =new double[weights.length];
		double lik = 0.0,prev_lik=10;
		while( n<ITERATIONS) {
			prev_lik=lik;
			lik = 0.0;
			for (int i=0; i<instances.size(); i++) {
				double[] x = instances.get(i).x;
				double predicted = classify(x);
				int label = instances.get(i).label;
				for (int j=0; j<weights.length; j++) {
					weights[j] = weights[j] + rate * (label - predicted) * x[j];
				}
				// not necessary for learning
				lik += label * Math.log(classify(x)) + (1-label) * Math.log(1- classify(x)); //log likelihood
			}
			n++;	
			System.out.println("iteration: "+ n  + " mle: " + lik+ " prev_lik" +prev_lik);//+ Arrays.toString(weights) );
		} 
	}

	private static double classify(double[] x) {
		double logit = .0;
		for (int i=0; i<weights.length;i++)  {
			logit += weights[i] * x[i];
		}
		return sigmoid(logit);
	}
	
	public static class Instance {
		public int label;
		public double[] x;

		public Instance(int label, double[] x) {
			this.label = label;
			this.x = x;
		}
	}

	public static List<Instance> readDataSet(double[][] ttcm) throws FileNotFoundException {
		List<Instance> dataset = new ArrayList<Instance>();
	//	System.out.println("Inside Logistic->readDataSet "+ttcm.length+" ,"+ttcm[0].length+"cat"+cat);
		myFinalCluster=QoS_Logit.myFinalCluster;
		double[] data = new double[ttcm[0].length];
		int cluster_index=0;
		for(cluster clust:myFinalCluster){
			 for(int i:clust.group){		
				 int count=0;
				 data=ttcm[i];
				 int label=0;
				 if(cluster_index==cat)
					 {
					 label=1 ;  //Cluster cat v/s others
					//System.out.println("Label is One") ;
					}
				Instance instance = new Instance(label, data);
				//if(count>0)dataset.add(instance);
				dataset.add(instance);
				 }		
			 cluster_index++;
		}
		return dataset;
}

	public static void main(String path) throws FileNotFoundException {
		ttcm=MatrixFormation.ttcm;
	logistic = new Logistic(ttcm[0].length);
	logisticRF=new Logistic(ttcm[0].length/3);
	int K=QoS_Logit.K;//High,Mid,Low //3 clusters
		//org_instances=new ArrayList<Instance>();
		//cat=0;
		//org_instances = readDataSet(ttcm);
		//logistic.train(org_instances); //gives Actual Weights;		
		for(row=0;row<K;row++)
			{
					conf[row][row]=0;
					conf1[row][row]=0;conf2[row][row]=0;conf3[row][row]=0;
					cat=row;
				 	readTrainSet(ttcm);
				 	readTestSet(ttcm);
				//logistic.train(train_instances);  confDiagMatrix(path);  //For Logistic Regression(General)
				// confkForestDiagMatrix(path);  //For K-Fold CV: Majority Vote
				 confRandomForestDiagMatrix(path); //For simpleRF
		for(col=1;col<K;col++)
				{
					cat=(row+col)%K;
					conf[row][cat]=0;
					conf1[row][cat]=0;conf2[row][cat]=0;conf3[row][cat]=0;
					readTrainSet(ttcm);
					//logistic.train(train_instances);//For Logistic Regression(General), and K-Fold CV: Majority Vote
					 //For simpleRF Begin
					logisticRF.train(train_instances); weights0=weights;
					logisticRF.train(rf_train_instance1); weights1=weights;
					logisticRF.train(rf_train_instance2); weights2=weights;
					logisticRF.train(rf_train_instance3); weights3=weights;
					//For simpleRF End
					confMatrix(path); 
				} 
			}
		confusionToFile(path);
		logistic_accuracy=(double)100*(conf[0][0]+conf[1][1]+conf[2][2])/(conf[0][0]+conf[0][1]+conf[0][2]+conf[1][0]+conf[1][1]+conf[1][2]+conf[2][0]+conf[2][1]+conf[2][2]);
		logistic_accuracy1=(double)100*(conf1[0][0]+conf1[1][1]+conf1[2][2])/(conf1[0][0]+conf1[0][1]+conf1[0][2]+conf1[1][0]+conf1[1][1]+conf1[1][2]+conf1[2][0]+conf1[2][1]+conf1[2][2]);
		logistic_accuracy2=(double)100*(conf2[0][0]+conf2[1][1]+conf2[2][2])/(conf2[0][0]+conf2[0][1]+conf2[0][2]+conf2[1][0]+conf2[1][1]+conf2[1][2]+conf2[2][0]+conf2[2][1]+conf2[2][2]);
		logistic_accuracy3=(double)100*(conf3[0][0]+conf3[1][1]+conf3[2][2])/(conf3[0][0]+conf3[0][1]+conf3[0][2]+conf3[1][0]+conf3[1][1]+conf3[1][2]+conf3[2][0]+conf3[2][1]+conf3[2][2]);
		
	}
	
	public static void confRandomForestDiagMatrix(String path) throws FileNotFoundException
	{
		logisticRF.train(train_instances); weights0=weights;
		logisticRF.train(rf_train_instance1); weights1=weights;
		logisticRF.train(rf_train_instance2); weights2=weights;
		logisticRF.train(rf_train_instance3); weights3=weights;
		new_test_instances=new ArrayList<Instance>();
		remove_indices=new TreeSet(Collections.reverseOrder());
	//	System.out.println("Inside confDiagMatrix");
		Iterator<Instance> it=test_instances.iterator();
		int index=0;
		conf[row][row]=0;
		conf1[row][row]=0;conf2[row][row]=0;conf3[row][row]=0;
		while(it.hasNext())
		{
			Instance inst=it.next();
			int vote=0;
		//System.out.println("inst.label"+inst.label+"classify(inst.x)"+classify(inst.x)+"inst.x"+inst.x);
		if(inst.label==1) //correctly classified
				{
			//weights=weights0; if(classify(inst.x)>=0.5) vote++;// comment this one out and change vote to 1 for confkFold and general case
			weights=weights1; if(classify(inst.x)>=0.5) {vote++; conf1[row][row]++;}
			weights=weights2; if(classify(inst.x)>=0.5) {vote++; conf2[row][row]++;}
			weights=weights3; if(classify(inst.x)>=0.5) {vote++; conf3[row][row]++;}
			if(vote>=2)conf[row][row]++;  //diagonal entries //means 2/3 of the training dataSet is agreeing
			if(vote<2) new_test_instances.add(inst); //as we know this instances will have label zero for other categories
				}
		}
		System.out.println(conf[row][row]+"conf[row][row]"+row);
		System.out.println("new_test_instances"+new_test_instances.size());
	}
	
	public static void confkForestDiagMatrix(String path) throws FileNotFoundException
	{
		logistic.train(train_instances); weights0=weights;
		logistic.train(kf_train_instance1); weights1=weights;
		logistic.train(kf_train_instance2); weights2=weights;
		logistic.train(kf_train_instance3); weights3=weights;
		System.out.println(kf_train_instance1.size()+"kf_train_instance1.size()"+kf_train_instance2.size()+"kf_train_instance2.size()"+kf_train_instance3.size()+"kf_train_instance3.size()");			
		new_test_instances=new ArrayList<Instance>();
		remove_indices=new TreeSet(Collections.reverseOrder());
	//	System.out.println("Inside confDiagMatrix");
		Iterator<Instance> it=test_instances.iterator();
		int index=0;
		conf[row][row]=0;
		while(it.hasNext())
		{
			Instance inst=it.next();
			int vote=0;
		//System.out.println("inst.label"+inst.label+"classify(inst.x)"+classify(inst.x)+"inst.x"+inst.x);
		if(inst.label==1) //correctly classified
				{
			weights=weights1; if(classify(inst.x)>=0.49) vote++;
			weights=weights2; if(classify(inst.x)>=0.49) vote++;
			weights=weights3; if(classify(inst.x)>=0.49) vote++;
			if(vote>=1)conf[row][row]++;  //diagonal entries //means 2/3 of the training dataSet is agreeing
			if(vote<1) new_test_instances.add(inst); //as we know this instances will have label zero for other categories
				}
		}
		System.out.println(conf[row][row]+"conf[row][row]"+row);
		System.out.println("new_test_instances"+new_test_instances.size());
	}

	private static void confusionToFile(String path) {
		// TODO Auto-generated method stub
		try {
			 File fileName=new File(path+"Logistic_ConfusionMatrix.csv");
			 OutputStream os = (OutputStream) new FileOutputStream(fileName);
			String encoding = "UTF8";
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			BufferedWriter bw = new BufferedWriter(osw); 
			bw.write(","+"Predicted Category 0"+","+"Predicted Category 1"+","+"Predicted Category 2" );
			bw.newLine();
			bw.write("Actual Category 0"+","+conf[0][0]+","+conf[0][1]+","+conf[0][2] );
			bw.newLine();
			bw.write("Actual Category 1"+","+conf[1][0]+","+conf[1][1]+","+conf[1][2] );
			bw.newLine();
			bw.write("Actual Category 2"+","+conf[2][0]+","+conf[2][1]+","+conf[2][2] );
			bw.flush();
			bw.close();
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void readTrainSet(double[][] ttcm) throws FileNotFoundException {
		//System.out.println("Inside Logistic->readTrainSet ");
		myFinalCluster=QoS_Logit.myFinalCluster;
		train_instances=new ArrayList<Instance>();
		kf_train_instance1=new ArrayList<Instance>();kf_train_instance2=new ArrayList<Instance>();kf_train_instance3=new ArrayList<Instance>();
		rf_train_instance1=new ArrayList<Instance>();rf_train_instance2=new ArrayList<Instance>();rf_train_instance3=new ArrayList<Instance>();
		trainDataSize=(int)(0.6*ttcm.length);  //TrainDataSize
		System.out.println("trainDataSize"+trainDataSize);
		double[] data = new double[ttcm[0].length]; double[] data1 = new double[ttcm[0].length/3]; double[] data2 = new double[ttcm[0].length/3]; double[] data3 = new double[ttcm[0].length/3];
		int cluster_index=0,count=0;
		for(cluster clust:myFinalCluster){
			 for(int i:clust.group){		
				 if(i<trainDataSize)
				 {
					 // System.out.print("TrainTkt"+i);
					  data=ttcm[i];
				for(int j=0;j<data1.length;j++){data1[j]=data[3*j]; data2[j]=data[3*j+1]; data3[j]=data[3*j+2]; }
				System.out.println(i);
				int label=0;
				if(cluster_index==cat) {
					label=1;     
					}
				Instance instance = new Instance(label, data);
				Instance instance1=new Instance(label, data1);Instance instance2=new Instance(label, data2);Instance instance3=new Instance(label, data3);
				//if(count>0) train_instances.add(instance);
				 train_instances.add(instance);
				 if(count%M!=0) 	kf_train_instance1.add(instance);
				 if((count+1)%M!=0) 	kf_train_instance2.add(instance);
				 if((count+2)%M!=0) 	kf_train_instance3.add(instance);
				 rf_train_instance1.add(instance1);
				 rf_train_instance2.add(instance2);
				 rf_train_instance3.add(instance3);
				 count++;
				 }		
			 }
			 cluster_index++;
		}
		System.out.println("train count"+count);
	}

	public static void readTestSet(double[][] ttcm) throws FileNotFoundException {
		//System.out.println("Inside Logistic->readTestSet ");
		test_instances=new ArrayList<Instance>();
		System.out.println("testDataSize"+String.valueOf(ttcm.length-trainDataSize));
		double[] data = new double[ttcm[0].length];
		myFinalCluster=QoS_Logit.myFinalCluster;
		int cluster_index=0,count=0;
		for(cluster clust:myFinalCluster){
			 for(int i:clust.group){		
				if(i>=trainDataSize){
					   //System.out.print("TestTkt"+i);
						 data=ttcm[i];
						int label=0;
						if(cluster_index==cat) {
							label=1;              //Cluster cat v/s others
							count++;
							//System.out.println("Label is One");
						}
						Instance instance = new Instance(label, data);
						//if(count>0) test_instances.add(instance);
						test_instances.add(instance);
				 }
			 }
			 cluster_index++;
		}
		System.out.println("\n"+"count"+count+"elements in  actual cat"+cat+"test_instances.size()"+test_instances.size()+"cluster_index"+cluster_index);
	}
	
	public static void confDiagMatrix(String path) throws FileNotFoundException
	{
		new_test_instances=new ArrayList<Instance>();
		remove_indices=new TreeSet(Collections.reverseOrder());
	//	System.out.println("Inside confDiagMatrix");
		Iterator<Instance> it=test_instances.iterator();
		int index=0;
		conf[row][row]=0;
		while(it.hasNext())
		{
			Instance inst=it.next();
		//System.out.println("inst.label"+inst.label+"classify(inst.x)"+classify(inst.x)+"inst.x"+inst.x);
		if(inst.label==1 & classify(inst.x)>=0.5) //correctly classified
				{
				conf[row][row]++;  //diagonal entries
				}
		if(inst.label==1 & classify(inst.x)<0.5) new_test_instances.add(inst); //as we know this instances will have label zero for other categories
		}
		System.out.println(conf[row][row]+"conf[row][row]"+row);
		System.out.println("new_test_instances"+new_test_instances.size());
	}
	
	public static void confMatrix(String path) throws FileNotFoundException
	{
		conf[row][cat]=0;
		Iterator<Instance> it=new_test_instances.iterator();
		int index=0;
		while(it.hasNext())
		{
			Instance inst=it.next();
			//System.out.print(String.valueOf(classify(inst.x)) +remove_indices.contains(index))
			weights=weights1;
		if(classify(inst.x)>0.5 & !remove_indices.contains(index)) //correctly classified
		{
		conf1[row][cat]++;  //non_diagonal entries
		}
			weights=weights2;
		if(classify(inst.x)>0.5 & !remove_indices.contains(index)) //correctly classified
		{
		conf2[row][cat]++;  //non_diagonal entries
		}
		
			weights=weights3;
		if(classify(inst.x)>0.5 & !remove_indices.contains(index)) //correctly classified
		{
		conf3[row][cat]++;  //non_diagonal entries
		}

		weights=weights0;
	if(classify(inst.x)>0.5 & !remove_indices.contains(index)) //correctly classified
			{
			conf[row][cat]++;  //non_diagonal entries
			remove_indices.add(index);
//			System.out.println("new_test_instances.remove(inst)"+new_test_instances.remove(inst));
			}
		index++;
		}
	}

}