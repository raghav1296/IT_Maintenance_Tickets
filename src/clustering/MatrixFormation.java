/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import edu.stanford.nlp.ling.CoreLabel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import Jama.*;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Scanner;

/**
 *
 * @author rajaprabu.trn
 */
public class MatrixFormation {
        static List<List<CoreLabel>> allForms; 
        static keyWords ListOfkeywords; 	// a list of keywords store with the keywords format
		static List<form> forms ;
		static int keywordSize;
		static int ticketSize;
		static double[][] tfidfValueUni;
        static double[][] tfidfValueBi;
        static double[][] tfidfValueTri;
        static double[][] confreq;
        static double[][] ttcm;//Ticket Term concept Matrix
        static double[][] tfidfMatrix;
        
        static HashMap<String , HashSet<String>> keywordConcept;
        static HashMap<String , HashSet<String>> conceptKeyword;
        static HashMap<String , HashSet<String>> hyperKeyword;
        static HashSet<String> unigramConcepts;
        static HashSet<String> bigramTrigramConcepts;
        
        static HashMap<String , Integer> uniKeywordIndex=new HashMap<String , Integer>();
        static HashMap<String , Integer> biKeywordIndex=new HashMap<String , Integer>();
        static HashMap<String , Integer> triKeywordIndex=new HashMap<String , Integer>();

        public static void initialize(){
            
            allForms=KeywordExtraction.allForms; 
            ListOfkeywords=KeywordExtraction.ListOfkeywords; 	// a list of keywords store with the keywords format
            forms = KeywordExtraction.forms; 
            keywordSize=ListOfkeywords.words.size()+ListOfkeywords.bigrams.size()+ListOfkeywords.trigrams.size();      
            ticketSize=forms.size();            
            keywordConcept=conceptFinder.keywordConcept;
            conceptKeyword=conceptFinder.conceptKeyword;
            hyperKeyword=conceptFinder.hyperKeyword;   
            unigramConcepts=conceptFinder.unigramConcepts;
            bigramTrigramConcepts=conceptFinder.bigramTrigramConcepts;    
                        
       }        
        public static Matrix tfidf(String path){
           Matrix temp=new Matrix(10,10);    
            try{
                tfidfValueUni=new double[ticketSize][ListOfkeywords.words.size()];
                double[][] termFrequencyUni = new double[ticketSize][ListOfkeywords.words.size()];
                double[] idfUni=new double[ListOfkeywords.words.size()];
                for(int i=0; i<ListOfkeywords.words.size();i++){
                    idfUni[i]=0;
                }
                tfidfValueBi=new double[ticketSize][ListOfkeywords.bigrams.size()];
                double[][] termFrequencyBi = new double[ticketSize][ListOfkeywords.bigrams.size()];
                double[] idfBi=new double[ListOfkeywords.bigrams.size()];
                        
                for(int i=0; i<ListOfkeywords.bigrams.size();i++){
                    idfBi[i]=0;
                }
                tfidfValueTri=new double[ticketSize][ListOfkeywords.trigrams.size()];
                double[][] termFrequencyTri = new double[ticketSize][ListOfkeywords.trigrams.size()];
                double[] idfTri=new double[ListOfkeywords.trigrams.size()];
                
            int j=0,present=0;
          
                for(int i=0; i<ListOfkeywords.trigrams.size();i++){
                    idfTri[i]=0;
                }
                
                int i=0;
                          
                for(form myform: forms){                         
                     j=0;
                    for(String word:ListOfkeywords.words.keySet()){
                        for(String s:myform.words.keySet()){
                           if(word.equalsIgnoreCase(s)){ 
                            termFrequencyUni[i][j]=myform.words.get(s);
                            if(termFrequencyUni[i][j] > 0)
                                idfUni[j]+=1;
                           }
                        }
                        j++;
                    }
                    j=0;
                    for(String word:ListOfkeywords.bigrams.keySet()){
                        for(String s:myform.bigrams.keySet()){
                            if(word.equalsIgnoreCase(s)){
                            termFrequencyBi[i][j]=myform.bigrams.get(s);
                            if(termFrequencyBi[i][j] > 0)
                                idfBi[j]+=1;
                            }
                        }
                         j++;
                    }
                    j=0;
                    for(String word:ListOfkeywords.trigrams.keySet()){
                        for(String s:myform.trigrams.keySet()){
                            if(word.equalsIgnoreCase(s)){
                            termFrequencyTri[i][j]=myform.trigrams.get(s);
                            if(termFrequencyTri[i][j] > 0)
                                idfTri[j]+=1;
                            }
                        }
                        j++;
                    }
                    i++;
        }        
        for(i=0;i<ticketSize;i++){
            for(j=0;j<ListOfkeywords.words.size();j++){
                if(idfUni[j]==0) {
                    tfidfValueUni[i][j]=0;
                System.out.println("BOOM"); }
                else
                    tfidfValueUni[i][j]=Math.log10(ticketSize/idfUni[j])*termFrequencyUni[i][j];
                
            }
            for( j=0;j<ListOfkeywords.bigrams.size();j++){
                 if(idfBi[j]==0 ){
                     tfidfValueBi[i][j]=0;
                     System.out.println("BOOM"); }
                else
                    tfidfValueBi[i][j]=Math.log10(ticketSize/idfBi[j])*termFrequencyBi[i][j];
                
            }
            for( j=0;j<ListOfkeywords.trigrams.size();j++){
                 if(idfTri[j]==0) {
                     tfidfValueTri[i][j]=0;
                     System.out.println("BOOM"); }
                else
                    tfidfValueTri[i][j]=Math.log10(ticketSize/idfTri[j])*termFrequencyTri[i][j];
                
            }
        }
        
       
        Matrix m1=new Matrix(tfidfValueUni);
        Matrix m2=new Matrix(tfidfValueBi);
        Matrix m3=new Matrix(tfidfValueTri);
        
        Matrix m=Matoperation.merge(m1,m2,m3);
        
        tfidfMatrix=new double[m.getRowDimension()][m.getColumnDimension()];
        
        tfidfMatrix=m.getArray();
        
        
        File tf_idf = new File(path+"TFIDF.csv");
			

	@SuppressWarnings("resource")
	OutputStream os = (OutputStream) new FileOutputStream(tf_idf);
	String encoding = "UTF8";
	OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
	BufferedWriter bw = new BufferedWriter(osw);
        
        bw.write(",,,");
        
        for(String word:ListOfkeywords.words.keySet()){
            bw.write(word+",");
        }
        for(String word:ListOfkeywords.bigrams.keySet()){
            bw.write(word+",");
        }
        for(String word:ListOfkeywords.trigrams.keySet()){
            bw.write(word+",");
        }
        bw.newLine();
        
        for(i=0;i<m.getRowDimension();i++){
            
            form myform=forms.get(i);
                    
                    for(String s:myform.words.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    
                    for(String s:myform.bigrams.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    
                    for(String s:myform.trigrams.keySet()){
                        bw.write(s+"/");

                    }
                    bw.write(",");
            
            for( j=0;j<m.getColumnDimension();j++){
                bw.write(String.valueOf(m.get(i, j))+",");
            }
            bw.newLine();
        }
        
        bw.flush();
        bw.close();
        os.close();
        
        return m;

       }
       catch(IOException e){
           System.out.println(e);
       }
       
       return temp;
    }
        
        public static Matrix ticketCorrelationMatrix(String path){
        
        double[][] mat= new double[ticketSize][ticketSize];
        Matrix x=new Matrix(10,10);
        try{
            int i,j;
            i=0;
            for(form myform1:forms){
                j=0;
                for(form myform2:forms){
                    int similar=0;
                    int formsize1;
                    int formsize2;
                    formsize1=myform1.words.size()+myform1.bigrams.size()+myform1.trigrams.size()+myform1.concepts.size();
                    formsize2=myform2.words.size()+myform2.bigrams.size()+myform2.trigrams.size()+myform2.concepts.size();
                    for(String s:myform1.words.keySet()){
                        if(myform2.words.containsKey(s)){
                            similar++;
                        }
                    }
                    for(String s:myform1.bigrams.keySet()){
                        if(myform2.bigrams.containsKey(s)){
                            similar++;
                        }
                    }
                    for(String s:myform1.trigrams.keySet()){
                        if(myform2.trigrams.containsKey(s)){
                            similar++;
                        }
                    }
                    for(String s:myform1.concepts.keySet()){
                        if(myform2.concepts.containsKey(s)){
                            similar++;
                        }
                    }  
                    mat[i][j]=similar/(Math.sqrt(formsize1)*Math.sqrt(formsize2));
                    mat[j][i]=mat[i][j];
                    j++;
                }
                i++;
            }
            File ticketCorrelationMatrix = new File(path+"ticketCorrelationMatrix.csv");
            OutputStream os = (OutputStream) new FileOutputStream(ticketCorrelationMatrix);
            String encoding = "UTF8";
            OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            for(i=0;i<ticketSize;i++){    
                form myform=forms.get(i);
                    for(String s:myform.words.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    for(String s:myform.bigrams.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    for(String s:myform.trigrams.keySet()){
                    	 bw.write(s+"/");
                    }
                    bw.write(",");
                for(j=0;j<ticketSize;j++){
                    bw.write(String.valueOf(mat[i][j]));
                    bw.write(',');
                }
                bw.newLine();
            }
           bw.flush();
			bw.close();
			os.close();
            Matrix m=new Matrix(mat);
            return m;                 
        }
        catch(IOException e){
            System.out.println(e);
        }        
        return x;        
    }
        
        public static Matrix conceptVector(String path){
            confreq=new double[ticketSize][unigramConcepts.size()+ bigramTrigramConcepts.size()];
            Matrix temp=new Matrix(10,10);
            try{
               int i,j;
               for(i=0;i<ticketSize;i++)
            	   for(j=0;j<unigramConcepts.size()+ bigramTrigramConcepts.size();j++)
            		   confreq[i][j]=0;
               i=0;
               for(String s:ListOfkeywords.words.keySet()){
                   uniKeywordIndex.put(s, i);
                   i++;
               }
               i=0;
               for(String s:ListOfkeywords.bigrams.keySet()){
                   biKeywordIndex.put(s, i);
                   i++;
               }
               i=0;
               for(String s:ListOfkeywords.trigrams.keySet()){
                   triKeywordIndex.put(s, i);
                   i++;
               }     
               i=0;               
                for(form myform:forms){
                   j=0;
                   for(String con:unigramConcepts){               	   
                       if(conceptKeyword.containsKey(con))
                       {
                       HashSet<String> conkey=conceptKeyword.get(con);
                       for(String s:conkey){
                           if(myform.words.containsKey(s)){
                               int index=uniKeywordIndex.get(s);
                               confreq[i][j]+=tfidfValueUni[i][index];
                           		}
                       		}
                       	}
                       
                       
                    //   else if(hyperKeyword.containsKey(con)){
                       if(hyperKeyword.containsKey(con)){
                          HashSet<String> conkey=hyperKeyword.get(con);            
                         // System.out.println("null pointer throws geting hyper unigram");
                       for(String s:conkey){
                           if(myform.words.containsKey(s)){
                                //  System.out.println("null pointer throws alolcateiong hyper unigram");
                               int index=uniKeywordIndex.get(s);
                               confreq[i][j]+=tfidfValueUni[i][index];
                           		}
                       		} 
                       }
                       
                       j++;
                   }
                   
                   
                     // System.out.println("null pointer throws before bigram");
                   
                   for(String bicon:bigramTrigramConcepts){
                     //  confreq[i][j]=0;
                       if(myform.bigrams.containsKey(bicon)){
                           int index=biKeywordIndex.get(bicon);
                           confreq[i][j]+=tfidfValueBi[i][index];
                       }
                       if(myform.trigrams.containsKey(bicon)){
                           int index=triKeywordIndex.get(bicon);
                           confreq[i][j]+=tfidfValueTri[i][index];
                       }
                       
                       
                       if(conceptKeyword.containsKey(bicon))
                       {
                       HashSet<String> conkey=conceptKeyword.get(bicon);
                         // System.out.println("null pointer throws getting unigram");
                       for(String s:conkey){
                           if(myform.words.containsKey(s)){
                                 // System.out.println("null pointer throws alolcateiong unigram");
                               int index=uniKeywordIndex.get(s);
                               confreq[i][j]+=tfidfValueUni[i][index];
                             //  System.out.println(confreq[i][j]+"confreq");
                           		}
                       		}
                       	}
                       
                    //   else if(hyperKeyword.containsKey(con)){
                       if(hyperKeyword.containsKey(bicon)){
                          HashSet<String> conkey=hyperKeyword.get(bicon);            
                         // System.out.println("null pointer throws geting hyper unigram");
                       for(String s:conkey){
                           if(myform.words.containsKey(s)){
                                //  System.out.println("null pointer throws alolcateiong hyper unigram");
                               int index=uniKeywordIndex.get(s);
                               confreq[i][j]+=tfidfValueUni[i][index];
                           		}
                       		} 
                       }
                       
                       //////////////////////////////raja
                       
                       j++;
                   }
                    // System.out.println("null pointer throws after bigram");
                   i++;
                }
                /*
                for(i=0;i<ticketSize;i++)
                	for(j=0;j<unigramConcepts.size();j++)
                		if(confreq[i][j]!=0) System.out.println(confreq[i][j]+"CF"+i+"i"+j+"index");
                */
                		
               // System.out.println(confreq[i][j]+"confreq");
                File convect = new File(path+"TicketConceptMatrix.csv");
                @SuppressWarnings("resource")
                OutputStream os = (OutputStream) new FileOutputStream(convect);
                String encoding = "UTF8";
                OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(",,,");
                    String[] a=new String[unigramConcepts.size()];
                    for(String s:unigramConcepts.toArray(a)){
                        bw.write(s+",");
                    }
                    a=new String[bigramTrigramConcepts.size()];
                    for(String s:bigramTrigramConcepts.toArray(a)){
                        bw.write(s+",");
                    }
                    bw.newLine();
                for(i=0;i<ticketSize;i++){
                    form myform=forms.get(i);
                    for(String s:myform.words.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    for(String s:myform.bigrams.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    for(String s:myform.trigrams.keySet()){
                        bw.write(s+"/");
                    }
                    bw.write(",");
                    for(j=0;j<unigramConcepts.size()+bigramTrigramConcepts.size();j++){
                        bw.write(String.valueOf(confreq[i][j])+",");
                    }
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                os.close();
                Matrix x=new Matrix(confreq);
                return x;
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return temp;
        }
        
        public static Matrix ticketTermConcept(Matrix term,Matrix concept){
            
            Matrix temp=new Matrix(10,10);//temporary matrix
            
            try{
                ttcm=new double[term.getRowDimension()][term.getColumnDimension()+concept.getColumnDimension()];     
                for(int i=0;i<term.getRowDimension();i++){
                    for(int j=0;j<term.getColumnDimension();j++){
                        ttcm[i][j]=term.get(i, j);
                    }
                    int t_count=term.getColumnDimension();
                    for(int j=0;j<concept.getColumnDimension();j++){
                        ttcm[i][t_count+j]=concept.get(i, j);
                    }
                }
              
                Matrix res=new Matrix(ttcm);
                
                return res;
                
                
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
            return temp;
            
        }
    
}
