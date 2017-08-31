package clustering;
/**
*
* @author raghav04.TRN
*/
import java.io.*;
import Jama.*;
import java.util.*;

//import com.multistage.correlations.clcontrol.Global;
import clustering.DataHolder;
import clustering.DataPoint;
import clustering.Get;
//import com.multistage.correlations.utils.*;

/*
 * This class implements a basic Fuzzy C-Means clustering algorithm.
 */
public class Fuzzy {

	private static double[][] indat;
	private static double[] QoS=KeywordExtraction.QoS;
	private static int nrow;
	private static String path;
	public static double FCM_accuracy;
	private static int ncol;
	public static int filenum=0;

	private int Ierr = 0; // error statement
	private static double[][] max_min;//=new double[mem[0].length][2];
	// cluster centers
	private DataHolder cMeans;

	private int maxIterations, numClusters;

	// The FCM additional parameters and membership function values.
	private static double fuzziness; // "m"

	public static double[][] membership,predict_mem;

	// The iteration counter will be global so we can get its value on the
	// middle of the clustering process.
	private static int iteration;

	// A metric of clustering "quality", called "j" as in the equations.
	private static double j = 1000000;

	// A small value, if the difference of the cluster "quality" does not
	// changes beyond this value, we consider the clustering converged.
	private static double epsilon;

	private static long position;

	// The cluster centers.
	private static double[][] clusterCenters;

	// A big array with all the input data and a small one for a single pixel.
	private static double[] aPixel;

	// A big array with the output data (cluster indexes).
	private static int[] assignment;

	private static String description;

	private static Random generator;
	
	public Fuzzy(int nrow,int ncol,int numClusters,int maxIterations,double fuzziness,double epsilon)
	{
		this.nrow=nrow;
		this.ncol=ncol;
		this.numClusters=numClusters;
		this.maxIterations=maxIterations;
		this.fuzziness=fuzziness;
		this.epsilon=epsilon;
	}

	// main initialisation
	public Fuzzy() {
		description = "Fuzzy C-means clustering";
		indat = new double[nrow][ncol]; // hold data
		aPixel = new double[ncol];
		assignment = new int[nrow];
		// Input array, values to be read in successively, float
		for (int i = 0; i < nrow; i++) {
			DataPoint dp;//=new DataPoint();	//DataPoint dp = SetEnv.DATA.getRaw(i);
			for (int i2 = 0; i2 < ncol; i2++)
			{				
				indat[i][i2] = QoS[i];
			}
		}
	}

	/**
	 * @param numClusters
	 *            Set the desired number of clusters.
	 */
	// set clusters
	public void setClusters(int N) {

		if (N > this.nrow) {
			//System.out.println("Too many clusters! Set to number of points");
			N = this.nrow;
		}
		this.numClusters = N;
	}

	/**
	 *   Get number of clusters.
	 */
	public int  getClusters() {
		
		return this.numClusters;
	}

	/**
	 * GetMembeship
	 */
	// set clusters
	public double[][] Membership() {

		return this.membership;

	}

	/**
	 * @param maxIterations
	 *            the maximum number of iterations.
	 * @param fuzziness
	 *            the fuzziness (a.k.a. the "m" value)
	 * @param epsilon
	 *            a small value used to verify if clustering has converged.
	 */

	// set epsilon, fuzziness
	public void setOptions(int maxIterations, double epsilon, double fuzziness) {
		this.maxIterations = maxIterations;
		this.fuzziness = fuzziness;
		this.epsilon = epsilon;
	}

	// set epsilon, fuzziness
	public void Delete() {

		this.numClusters = 0;
		this.maxIterations = 0;
		this.fuzziness = 00;
		this.epsilon = 0;
		this.nrow = 0;
		this.ncol = 0;
		this.indat = null;
		this.clusterCenters = null;
		this.membership = null;
		this.generator = null;
		this.aPixel = null;

	}

	/**
	 * Returns cluster centers
	 * 
	 * @return DataHolder
	 */
	public DataHolder getCenters() {

		cMeans = new DataHolder();

		for (int i = 0; i < this.numClusters; i++) {
			double[] a = new double[this.ncol];
			for (int j = 0; j < this.ncol; j++)
				a[j] = clusterCenters[i][j];
			DataPoint c = new DataPoint(a, this.ncol);
			// c.showAttributes();
			cMeans.add(c);
		}

		return cMeans;

	}

	/**
	 * Returns the number of points in each cluster
	 * 
	 * @return int[] getNumberPoints
	 */
	public int[] getNumberPoints() {

		int[] NN = new int[numClusters];

		return NN;
	} // end

	// run in the best cluster mode
	public void runBest() {

		int iter = 1 + (int) (nrow / 2);
		
//		iter=4;
		
		double[] selec = new double[iter];
		for (int j = 0; j < iter; j++)
			selec[j] = Double.MAX_VALUE;

		int[] clus = new int[iter];

		int N = 0;
		for (int j = 0; j < iter; j++) {
			int nclus = 2 + j;
			setClusters(nclus);
			run("");
			selec[j] = getCompactness();
			clus[j] = nclus;
			// stop if it's increasing
			N++;
			if (j > 5) {
				if (selec[j - 1] < selec[j] && selec[j - 2] < selec[j - 1]) {
					break;

				}
			}
			;
		}

		// VEC.printVect(selec, 4, 10);
		int ibest =findSmallest(selec, N);//int ibest = ArrayOps.findSmallest(selec, N);
		this.numClusters=clus[ibest];
		this.description = "Fuzzy C-means clustering: Best estimate";
		// System.out.println("Best clusters=" + this.numClusters);
		// now run
		run("");
	}

	private int findSmallest(double[] selec, int n) {
		// TODO Auto-generated method stub
		double min=n;
		for(int i =0;i<selec.length;i++)
		{
			if(selec[i]<min) min=selec[i];
		}
		return 0;
	}

	/**
    *Classic Fuzzy C-Means clustering algorithm: Calculate the cluster
	 * centers. Update the membership function. Calculate statistics and repeat
	 * from 1 if needed.
	 */
	public void run(String filepath) {
		System.out.println("Inside Run");
		iteration = 0;
		clusterCenters = new double[this.numClusters][this.ncol];
		membership = new double[this.nrow][this.numClusters];
		assignment=new int[this.nrow];
		// Initialize the membership functions randomly.
		generator = new Random(); // easier to debug if a seed is used
		// For each data point (in the membership function table)
		for (int h = 0; h < this.nrow; h++) {
			// For each cluster's membership assign a random value.
			double sum = 0f;
			for (int c = 0; c < this.numClusters; c++) {
				membership[h][c] = 0.01f + generator.nextDouble();
				sum += membership[h][c];
			}
			// Normalize so the sum of MFs for a particular data point will be
			// equal to 1.
			for (int c = 0; c < this.numClusters; c++)
				membership[h][c] /= sum;
			//System.out.println(h+"membership[h][c]"+membership[h][0]+","+membership[h][1]+","+membership[h][2]);
		}
		System.out.println("Initializing random membership");
		// Initialize the global position value.
		position = 0;
		double lastJ;

		// Calculate the initial objective function JUST FOR KICKS.
		lastJ = calculateObjectiveFunction();
		// Do all required iterations (until the clustering converges)
		for (iteration = 0; iteration < maxIterations; iteration++) {
			// Calculate cluster centers from MFs.
			calculateClusterCentersFromMFs();
			// Then calculate the MFs from the cluster centers !
			calculateMFsFromClusterCenters();
			// Then see how our objective function is going.
			j = calculateObjectiveFunction();
			if (Math.abs(lastJ - j) < epsilon)
				break;
			lastJ = j;
			System.out.print(j+"j");
		} // end of the iterations loop.
		// Means that all calculations are done, too.
		position = getSize();
		System.out.println("position"+position);

		// VEC.printMatrix(nrow,numClusters,membership,5,5);
		// VEC.printMatrix(numClusters,ncol,clusterCenters,5,5);

		for (int h = 0; h < this.nrow; h++)
			assignment[h] = -1;
		
		System.out.println("init assignment");
		// assume that a cluster has 68% probability
			for (int h = 0; h < this.nrow; h++) {
					assignment[h] =max_index(membership[h]);
		}
		// Crisp Clustering
		System.out.println("Writing Membership Matrix to File");
		membershipToFile(filepath);		
	}

	public void membershipToFile(String filepath) {
		// TODO Auto-generated method stub
	     File fileName=new File( filepath+"FCM_Membership.csv");
		try {
			OutputStream os=(OutputStream) new FileOutputStream(fileName);
			String encoding = "UTF8";
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			BufferedWriter bw = new BufferedWriter(osw); 
			bw.write("Cluster 0"+","+"Cluster 1"+","+"Cluster 2"+","+"Crisp approximation");
			bw.newLine();
			//bw.write(clusterCenters[0][0]+"centre"+","+clusterCenters[1][0]+"centre"+","+clusterCenters[2][0]+","+"centre"); bw.newLine();
			 for(int i=0;i<membership.length;i++){
				 for(int j=0;j<membership[0].length;j++){
					 bw.write(membership[i][j]+",");
				 }
				 bw.write(String.valueOf(assignment[i]));
				 bw.newLine();
			 }
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * This method calculates the cluster centers from the membership functions.
	 */
	private void calculateClusterCentersFromMFs() {
		//System.out.println("calculateClusterCentersFromMFs");
		double top, bottom;
		// For each band and cluster
		for (int b = 0; b < this.ncol; b++)
		{
			//System.out.println("cluster centers");
			for (int c = 0; c < this.numClusters; c++) {
				// For all data points calculate the top and bottom parts of the
				// equation.
				top = bottom = 0;
				for (int h = 0; h < this.nrow; h++) {
					// Index will help locate the pixel data position.
					top += Math.pow(membership[h][c], fuzziness) * indat[h][b];
					bottom += Math.pow(membership[h][c], fuzziness);
				}
				// Calculate the cluster center.
				clusterCenters[c][b] = top / bottom;
				// Upgrade the position vector (batch).
				// position += width*height;
				//System.out.print(clusterCenters[c][b]);
			}
		}
	}

	/**
	 * This method calculates the membership functions from the cluster centers.
	 */
	private void calculateMFsFromClusterCenters() {
		//System.out.println("calculateMFsFromClusterCenters");
		double sumTerms;
		// For each cluster and data point
		for (int c = 0; c < this.numClusters; c++)
			for (int h = 0; h < this.nrow; h++) {
				// Get a pixel (as a single array).
				for (int b = 0; b < this.ncol; b++)
					aPixel[b] = this.indat[h][b];
				// Top is the distance of this data point to the cluster being
				// read.
				double top = Get.calcDistance(aPixel, clusterCenters[c]);
				// Bottom is the sum of distances from this data point to all
				// clusters.
				sumTerms = 0f;
				for (int ck = 0; ck < numClusters; ck++) {
					double thisDistance = Get.calcDistance(aPixel,
							clusterCenters[ck]);
					sumTerms += Math.pow(top / thisDistance,
							(2f / (fuzziness - 1f)));
				}
				// Then the MF can be calculated as...
				this.membership[h][c] = (double) (1f / sumTerms);
				// Upgrade the position vector (batch).
				position += (ncol + numClusters);
			}
	}

	/*
	 * This method calculates the objective function ("j") which reflects the
	 * quality of the clustering.
	 */
	private double calculateObjectiveFunction() {
		double j = 0;
		//System.out.println(nrow+","+ncol+","+numClusters+","+maxIterations+","+fuzziness+","+epsilon);
		aPixel = new double[ncol];
		//System.out.println("Inside Calculate Objective Function");
		// For all data values and clusters
		for (int h = 0; h < nrow; h++){
			for (int c = 0; c < numClusters; c++) {
				// Get the current pixel data.
				for (int b = 0; b < ncol; b++) aPixel[b] = indat[h][b];
				// Calculate the distance between a pixel and a cluster center.
				double distancePixelToCluster = Get.calcDistance(aPixel,	clusterCenters[c]);
				j += distancePixelToCluster* Math.pow(membership[h][c], fuzziness);
				// Upgrade the position vector (batch).
				position += (2 * ncol);
			}
		}
		//System.out.println("Calculate Objective Function Complete");
		return j;
	}

	/**
	 * This method returns the estimated size (steps) for this task. The value
	 * is, of course, an approximation, just so we will be able to give the user
	 * a feedback on the processing time. In this case, the value is calculated
	 * as the number of loops in the run() method.
	 */
	public long getSize() {
		/*
		 * // Return the estimated size for this task: return
		 * (long)maxIterations* // The maximum number of iterations times (
		 * (numClusters*width*height*(2*ncol))+ // Step 0 of method run()
		 * (width*height*ncol*numClusters)+ // Step 1 of method run()
		 * (numClusters*width*height*(ncol+numClusters))+ // Step 2 of run()
		 * (numClusters*width*height*(2*ncol)) // Step 3 of method run() );
		 */

		return (long) maxIterations;
	}

	/**
	 * This method returns a measure of the progress of the algorithm.
	 */
	public long getPosition() {
		return position;
	}

	/**
	 * This method returns true if the clustering has finished.
	 */
	public boolean isFinished() {
		return (position == getSize());
	}

	/**
	 * This method returns the Partition Coefficient measure of cluster validity
	 * (see Fuzzy Algorithms With Applications to Image Processing and Pattern
	 * Recognition, Zheru Chi, Hong Yan, Tuan Pham, World Scientific, pp. 91)
	 */
	public double getPartitionCoefficient() {
		double pc = 0;
		// For all data values and clusters
		for (int h = 0; h < this.nrow; h++)
			for (int c = 0; c < this.numClusters; c++)
				pc += membership[h][c] * membership[h][c];
		pc = pc / this.nrow;
		return pc;
	}

	/**
	 * This method returns the Partition Entropy measure of cluster validity
	 * (see Fuzzy Algorithms With Applications to Image Processing and Pattern
	 * Recognition, Zheru Chi, Hong Yan, Tuan Pham, World Scientific, pp. 91)
	 */
	public double getPartitionEntropy() {
		double pe = 0;
		// For all data values and clusters
		for (int h = 0; h < this.nrow; h++)
			for (int c = 0; c < this.numClusters; c++)
				pe += membership[h][c] * Math.log(membership[h][c]);
		pe = -pe / this.nrow;
		return pe;
	}

	// get error statement: 0 - looks good
	public int GetError() {
		return this.Ierr;
	}

	/**
	 * This method returns the Compactness and Separation measure of cluster
	 * validity (see Fuzzy Algorithms With Applications to Image Processing and
	 * Pattern Recognition, Zheru Chi, Hong Yan, Tuan Pham, World Scientific,
	 * pp. 93)
	 */
	public double getCompactness() {
		double cs = 0;
		// For all data values and clusters
		for (int h = 0; h < this.nrow; h++) {
			// Get the current pixel data.
			for (int b = 0; b < this.ncol; b++)
				aPixel[b] = this.indat[h][b];
			for (int c = 0; c < this.numClusters; c++) {
				// Calculate the distance between a pixel and a cluster center.
				double distancePixelToCluster = Get.calcSquaredDistance(aPixel,
						clusterCenters[c]);
				cs += membership[h][c] * membership[h][c]
						* distancePixelToCluster ;//* distancePixelToCluster;
			}
		}
		cs /= (nrow);
		// Calculate minimum distance between ALL clusters
		double minDist = Double.MAX_VALUE;
		for (int c1 = 0; c1 < this.numClusters - 1; c1++)
			for (int c2 = c1 + 1; c2 < this.numClusters; c2++) {
				double distance = Get.calcSquaredDistance(clusterCenters[c1],
						clusterCenters[c2]);
				minDist = Math.min(minDist, distance);
			}
		cs = cs / (minDist * minDist);
		return cs;
	}

	/**
	 * 
	 * 
	 * Get description
	 */

	public String getName() {
		return this.description;
	}

	
	 // The main method contains the body of the program public static void
public static void main(String filepath) { 
		 	path=filepath;
			QoS=KeywordExtraction.QoS; //while using more than 1 QoS parameters: double[][] QoS
		  	// Row and column sizes, read in first st.nextToken(); 
		  	nrow =QoS.length; 
		  	ncol = 1; //while using more than 1 QoS parameters; ncol=QoS[0].length;
		  	System.out.println(" No. of rows, nrow = " + nrow); 
		  	System.out.println("	No. of cols, ncol = " + ncol);
	   // Input array, values to be read in successively,
		  	indat = new double[nrow][ncol];
		  	// New read in input array values, successively
		  	for (int i = 0; i < nrow; i++)
		  	{
		  		for (int j = 0; j < ncol; j++) 
		  		{ 
		  			indat[i][j] = QoS[i];  //while using more than 1 QoS parameters: QoS
		  			//System.out.print(indat[i][j]+"QoS[i]"+QoS[i]);
		  		}
		  	}
		  	System.out.println(); 
		  	int numClusters=3; int maxIterations=200; double fuzziness=2; 
		  	double  epsilon=0.0001; 
		  	Fuzzy km= new Fuzzy(nrow,ncol,numClusters,maxIterations,fuzziness,epsilon);
		  	km.run(filepath); 
//Comment this out for Linear Regression
//		  	double[][] in=MatrixFormation.ttcm;
//		  	Matrix in_mat=new Matrix(in);
//		  	in=PCA(in_mat).getArray();
//		  	Linear_regression(in,membership);
//		  	clusterBound(membership,QoS);
//		  	MatToFile(new Matrix(max_min),"Max_Min_QoS");
//		  	Fuzzy_confusion(membership,QoS);	
//			try {
//				FileWriter fw = new FileWriter(path+"FCM_metrices.txt", true);
//	   			fw.write("km.XieBeni()"+Get.compactness(indat, assignment, numClusters, in)+"km.getCompactness"+km.getCompactness()+"\n"+"km.getPartitionCoefficient()"+km.getPartitionCoefficient()+"\n"+"km.getPartitionEntropy()"+km.getPartitionEntropy()+"\n");
//	   			fw.flush();
//				fw.close();
//			}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
} // End of main

private static void Fuzzy_confusion(double[][] mem, double[] q) {
	// TODO Auto-generated method stub
	int dataSize=q.length;
	double[][] confusion={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
	for(int i=0;i<q.length;i++) confusion[max_index(mem[i])][max_index(predict_mem[i])]++;
	confusion[3][3]=(confusion[0][0]+confusion[1][1]+confusion[2][2])/(confusion[0][0]+confusion[0][1]+confusion[0][2]+confusion[1][0]+confusion[1][1]+confusion[1][2]+confusion[2][0]+confusion[2][1]+confusion[2][2]);
	FCM_accuracy=confusion[3][3]*100;
	MatToFile(new Matrix(confusion),"FCM_Confusion Matrix");
	/*int test_dataSize=(int)(0.7*q.length); 
	double[] q_test=new double[test_dataSize];
	double[][] mem_test=new double[test_dataSize][mem[0].length];
	for(int i=0;i<test_dataSize;i++)
	{	q_test[i]=q[i]; mem_test[i]=mem[i]; }
	clusterBound(mem_test,q_test);
	MatToFile(new Matrix(max_min));*/
}

private static void clusterBound(double[][] mem, double[] q) {
	System.out.println("Inside ClusterBound");
	// TODO Auto-generated method stub
	max_min=new double[mem[0].length][3];
	for(int i=0;i<max_min.length;i++){max_min[i][0]=0; max_min[i][1]=1000; max_min[i][2]=0;}
	for(int i=0;i<q.length;i++)
		{
		int cluster_index=max_index(mem[i]);
		if(max_min[cluster_index][0]<q[i]) max_min[cluster_index][0]=q[i];
		if(max_min[cluster_index][1]>q[i]) max_min[cluster_index][1]=q[i];
		}
	for(int i=0;i<q.length;i++)
	{
		for(int cluster_index=0;cluster_index<max_min.length;cluster_index++)
		 if(q[i]<=max_min[cluster_index][0] & q[i]>=max_min[cluster_index][1]) max_min[cluster_index][2]++;
	}
}

public static int max_index(double[] arr) {
	int index=0;
	double max=arr[0];
	for(int i=0;i<arr.length;i++)
		if(max<arr[i])
			{
			max=arr[i];
			index=i;
			}
		return index;
}

private static void Linear_regression(double[][] in, double[][] mem) {
	// TODO ttcm:Input Matrix; mat: dependent outcome
	//double[][] mat=new double[in.length][in[0].length];  //Not Considering bias
	System.out.println("Inside Linear Regression");
	double[][] mat=new double[in.length][in[0].length+1];
	predict_mem=new double[mem.length][mem[0].length];
	double[][][] diff_mem=new double[11][mem.length][mem[0].length];	
	for(int i=0;i<in.length;i++)
	{	
		mat[i][0]=1;
		for(int j=1;j<in[0].length+1;j++)mat[i][j]=in[i][j-1];
	}
	Matrix X=new Matrix(mat);		MatToFile(X,"ttcm_matrix"); 		
	Matrix Y=new Matrix(mem);	MatToFile(Y,"Membership_matrix");			
	System.out.println("diff_mem.length"+diff_mem.length+","+diff_mem[0].length+","+diff_mem[0][0].length);
	//LS_Linear_regression(X,mem);  //For LS 
	NNLS_Linear_regression(X,mem);  //For NNLS
	int stage_count=5;
	//////////////////////////////////////////////Boosting
	for(int i=0;i<mem.length;i++) for(int j=0;j<mem[0].length;j++){ diff_mem[0][i][j]=predict_mem[i][j]; }
	
	for(int count=1;count<stage_count;count++){
		System.out.println(count+"count");
	for(int i=0;i<mem.length;i++) for(int j=0;j<mem[0].length;j++){ diff_mem[count][i][j]=mem[i][j]-predict_mem[i][j]; }
	//LS_Linear_regression(X,diff_mem[count]); 
	NNLS_Linear_regression(X,diff_mem[count]); 
	}
	for(int count=0;count<stage_count;count++){ for(int i=0;i<mem.length;i++) for(int j=0;j<mem[0].length;j++){ predict_mem[i][j]+=diff_mem[count][i][j]; } } 
	//////////////////////////////////////////////Boosting
}

private static void LS_Linear_regression(Matrix X,double[][] mem ){
	///////////////////////////////////////////////////////////////////////Principal Component Analysis(PCA) and LS Regression Begins
	Matrix Xr=X; 
	System.out.println("X.rank()"+X.rank());
	if(X.rank()<X.getColumnDimension() & X.rank()<X.getRowDimension())  Xr=PCA(X); //Principal Component Analysis of X//Should I do PCA afterwards or earlier
	Matrix Y=new Matrix(mem); 	
	Matrix	Xr_T=Xr.transpose();
	System.out.println("Matrix	Xr_T=Xr.transpose()");
	Matrix Xr_T_Xr=Xr_T.times(Xr);
	System.out.println("Matrix Xr_T_Xr=Xr_T.times(Xr);");
	Matrix Xr_T_Xr_inv=Xr_T_Xr.inverse();	
	System.out.println("Matrix Xr_T_Xr_inv=Xr_T_Xr.inverse()");
	Matrix Xr_T_Y=Xr_T.times(Y);
	System.out.println("Matrix Xr_T_Y=Xr_T.times(Y)");
	Matrix B_lr=Xr_T_Xr_inv.times(Xr_T_Y);				
	//B_lr=new Matrix(Matrix_Refine(B_lr.getArray()));/////////////////////////////////////
	MatToFile(B_lr,"PCA_LS_B_Matrix");
	System.out.println("Matrix B_lr=Xr_T_Xr_inv.times(Xr_T_Y)");
	Matrix Out_lr=Xr.times(B_lr);								MatToFile(Out_lr,"PCA_LS_Predicted_Membership");	
	System.out.println("Matrix Out_lr=Xr.times(B_lr)");
	predict_mem=normalize(Out_lr.getArray());	  MatToFile(new Matrix(predict_mem),"normalized_PCA_LS_Predicted_Membership");	
	////////////////////////////////////////////////////////////////////////Pricipal Component Analysis(PCA) and LS Regression Ends	
}

private static void NNLS_Linear_regression(Matrix X,double[][] mem){
////////////////////////////////////////////////////////////////////////From Here NNLS Linear Regression Starts
double[][] mem_col=new double[mem.length][1];
for(int i=0;i<mem.length;i++)	mem_col[i][0]=mem[i][0];
Matrix Y=new Matrix(mem_col); 	
Matrix B1=NNLSSolver.solveNNLS(X, Y);					
for(int i=0;i<mem.length;i++)	mem_col[i][0]=mem[i][1];
Y=new Matrix(mem_col);
Matrix B2=NNLSSolver.solveNNLS(X, Y);
for(int i=0;i<mem.length;i++)	mem_col[i][0]=mem[i][2];
Y=new Matrix(mem_col);
Matrix B3=NNLSSolver.solveNNLS(X, Y);
Matrix B=Matoperation.merge(B1, B2, B3); MatToFile(B,"B_matrix_linear_regression_without_PCA");	
Matrix Out=X.times(B);
predict_mem=normalize(Out.getArray());  MatToFile(new Matrix(predict_mem),"predicted_membership_without_PCA");	

///////////////////////////////////////////////////////////////////////NNLS Linear Regression Ends Here
}

private static double[][] normalize(double[][] array) {
	// TODO Auto-generated method stub
	for(int i=0;i<array.length;i++)
	{ 
		double sum=0;
		for(int j=0;j<array[0].length;j++){			sum+=array[i][j];						}
		for(int j=0;j<array[0].length;j++){			array[i][j]=array[i][j]/sum;		}
	}
	return array;
}

public static Matrix PCA(Matrix x) {
	// TODO Auto-generated method stub
	System.out.println("Inside PCA");
	Matrix x_r=x;
	if(x.rank()<x.getColumnDimension() & x.rank()<x.getRowDimension())
	{
		if(x.getRowDimension()>=x.getColumnDimension())
			{
		//x.svd(); System.out.println("SVD done");/////////////////////////////
		Matrix u=x.svd().getU();	System.out.println("SVD getU");
		Matrix v=x.svd().getV();	System.out.println("SVD getV");
		Matrix s=x.svd().getS();	System.out.println("SVD getS");
		int thres=0;
		for(int i=0;i<s.getRowDimension() & s.get(i, i)>0.000001;i++) thres++;
		System.out.println("threshold "+thres+"u.getRowDimension()"+u.getRowDimension()+"v.getRowDimension()"+v.getRowDimension()+"s.getRowDimension()"+s.getRowDimension());
		Matrix u_r=u.getMatrix(0,u.getRowDimension()-1,0,u.getColumnDimension()-1);	//	MatToFile(u_r,"SVD_V");
		Matrix v_r=v.getMatrix(0,thres-1,0,v.getColumnDimension()-1);		//MatToFile(v_r,"SVD_V");
		Matrix s_r=s.getMatrix(0, s.getRowDimension()-1, 0,s.getRowDimension()-1);	//MatToFile(s_r,"SVD_V");
		x_r=u_r.times(s_r).times(v_r.transpose());			//	MatToFile(x_r,"SVD_Reconstructed");
		System.out.println("PCA Done with Case1");
		}
	else{
		x.transpose().svd(); System.out.println("SVD done");
		Matrix u=x.transpose().svd().getU();	System.out.println("SVD getU");
		Matrix v=x.transpose().svd().getV();	System.out.println("SVD getV");
		Matrix s=x.transpose().svd().getS();	System.out.println("SVD getS");
		int thres=0;
		for(int i=0;i<s.getRowDimension() & s.get(i, i)>0.000001;i++) thres++;
		System.out.println("threshold "+thres+"u.getRowDimension()"+u.getRowDimension()+"v.getRowDimension()"+v.getRowDimension()+"s.getRowDimension()"+s.getRowDimension());
		Matrix u_r=u.getMatrix(0,thres-1,0,u.getColumnDimension()-1);	//	MatToFile(u_r,"SVD_V");
		Matrix v_r=v.getMatrix(0,v.getRowDimension()-1,0,v.getColumnDimension()-1);		//MatToFile(v_r,"SVD_V");
		Matrix s_r=s.transpose();	//MatToFile(s_r,"SVD_V");
		x_r=v_r.times(s_r).times(u_r.transpose());			//	MatToFile(x_r,"SVD_Reconstructed");
		System.out.println("PCA Done with Case2");
		}
	}
	return x_r;
}

private static void MatToFile(Matrix out, String filename) {
	try {
		int err=0;
		double sum=0;
		File matFile = new File(path+filename+".csv"); filenum++;
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
		bw.flush();
		bw.close();		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}    

/**
 * This method removes negative entries from the double
 */
public static double[][] Matrix_Refine(double[][] matrix)
	{
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[0].length;j++)
			{
				if(matrix[i][j]<0) matrix[i][j]=0;
			}	
		}
		return matrix;
	}
}
