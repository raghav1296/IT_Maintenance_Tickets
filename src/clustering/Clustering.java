/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;  //imports everything inside package , eg.conceptFinder,form,Matoperation etc here.

import Jama.*;
import java.util.*;
import java.io.*;
;/**
 *
 * @author rajaprabu.TRN
 */
public class Clustering {

    static String[] arg;
    static String path;
    static double[][] acc_plot=new double[7][13];
    public static void deletefile(File f){
        
        String[] child=f.list();
        for(String c:child){
            File cname=new File(f.getPath(),c);
            if(cname.isDirectory()){
                deletefile(cname);
            }
            cname.delete();
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
       arg=args;
        
         Scanner s1=null;
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////      
        try{          
        File dir=new File("category");	//directory named 'category' is created
          //File dir=new File("walmart");
         if(dir.exists()){ 
          deletefile(dir);
          dir.delete();
         } 
          dir.mkdir(); 
         // CreateFolder c=new CreateFolder("NBTY-PMATicketData"); 
         // CreateFolder c=new CreateFolder("allstate27102014"); //Change here, readtickets.read(); and readtickets.Months.add	
          CreateFolder c=new CreateFolder("GLMS_edited");
         //  CreateFolder c=new CreateFolder("Belgacom_rfp21042015");
          //readxcel.read();
         readtickets.read();
         int count=0;
             s1=new Scanner(new File("category\\categorylist.csv"));
            //Scanner s1=new Scanner(new File("walmart\\categorylist.csv"));
             s1.nextLine();s1.nextLine();s1.nextLine();
             s1.nextLine(); s1.nextLine(); //To Skip the Empty ttcm matrix      
      while(s1.hasNextLine())
       {
                String[] spl=s1.nextLine().split(",");
                System.out.println(spl[0]+"s1.nextLine().split[0]");	 System.out.println(spl[1]+"Number Of Tickets");
                String path = "category\\"+spl[0]+"\\";
               //topicvalue=Integer.valueOf(spl[2]);
                System.out.println(spl.clone()+"This are spl(split words) of s1(Category List)");
                KeywordExtraction.findKeywords(path,spl[1]);                                
               System.out.println("Stage 1 Clear");
                conceptFinder.findUnigramConcepts(path);                
                System.out.println("stage 2 Clear");                
                conceptFinder.findBigramTrigramConcepts(path);               
                System.out.println("stage 3 Clear");                
                conceptFinder.addConceptToTicket();   
                System.out.println("stage 4 Clear");                
                MatrixFormation.initialize();               
                Matrix tfidf=MatrixFormation.tfidf(path);               
                System.out.println("stage 5 Clear");                               
                Matrix tcm=MatrixFormation.ticketCorrelationMatrix(path);                
                System.out.println("stage 6 Clear");                
                Matrix conceptVector=MatrixFormation.conceptVector(path);                
                System.out.println("stage 7 Clear"); 
                Matrix ttcm=MatrixFormation.ticketTermConcept(tfidf,conceptVector); 
                PAM.readFiles(path);
                System.out.println("stage 8 Clear"); 
                int k;
                System.out.println("Entering Logistic Regression");
             // QoS_Logit.main(path); //For Logistic Regression on QoS parameters
    /*     for(k=1;k<=15;k++)
                {
                PAM.setK(k);   //To find optimum #of clusters
                PAM.main();	//Silhouette Index Vs #of cluster
                System.out.println("stage 9 {"+k+"} Clear"); 
                }		*/
         k=3;
         		System.out.println("Entering FCM");
                Fuzzy.main(path); 	//for Fuzzy membership analysis needed for SVM as well
                System.out.println("Entering SVM");
                SVM.main(path);  //Implements Support Vector Machine  Model
                acc_plot[count][0]=ttcm.getRowDimension();	//#of Tickets
                acc_plot[count][1]=ttcm.getColumnDimension();//#of Feature
                acc_plot[count][2]=KeywordExtraction.ListOfkeywords.words.size(); //Uni_keyword
                acc_plot[count][3]=KeywordExtraction.ListOfkeywords.bigrams.size();//Bi_keyword	
                acc_plot[count][4]=KeywordExtraction.ListOfkeywords.trigrams.size();//Tri_Keyword 
                acc_plot[count][5]=acc_plot[count][2]+acc_plot[count][3]+acc_plot[count][4];//#of terms
                acc_plot[count][6]=acc_plot[count][1]-acc_plot[count][5];//#of concepts
                acc_plot[count][7]=Fuzzy.FCM_accuracy;//FCM_Accuracy
                acc_plot[count][8]=Logistic.logistic_accuracy;
                acc_plot[count][9]=SVM.SVM_accuracy; //SVM_SMO_Accuracy
                acc_plot[count][10]=Logistic.logistic_accuracy1;
                acc_plot[count][11]=Logistic.logistic_accuracy2;
                acc_plot[count][12]=Logistic.logistic_accuracy3;
                KeywordExtraction.ListOfkeywords.clear();
                KeywordExtraction.allForms.clear();
                KeywordExtraction.forms.clear();
                count++;
        	}  
         SVM.MatToFile(new Matrix(acc_plot), "category\\accuracy_plot");
       }
     catch(Exception e){
            System.out.println(e);
        }
        finally {
            if(s1!=null)
                s1.close();
        }
 }
    
}
