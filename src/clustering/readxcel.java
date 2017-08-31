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
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Locale;

import jxl.*;

import java.text.SimpleDateFormat;
import java.util.*;

import TokenizerTools.LemmeAndTag;
import TokenizerTools.tagRemover;
import edu.stanford.nlp.ling.CoreLabel;
import edu.smu.tspell.wordnet.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

import Jama.Matrix;

/**
 *
 * @author rajaprabu.TRN
 */
public class readxcel {
    
    static List<List<CoreLabel>> allForms;
    static List<List<CoreLabel>> allnewForms;
    static List<form> forms = new LinkedList<form>();
    static HashSet<String> stopWords;
    static ArrayList<String> Unigram;
    
    static List<LinkedList<String>> finalKWBiTri = new LinkedList<LinkedList<String>>();
    
    static keyWords ListOfkeywords = new keyWords();
    
    static HashMap<Integer,ArrayList<Integer>> ticketbucket=new HashMap<Integer,ArrayList<Integer>>();
    static HashMap<Integer , String> ticketid=new HashMap<Integer,String>();
    static HashMap<Integer , String > rawdata=new HashMap<Integer , String>();
    static HashMap<String , Double> silhouette=new HashMap<String , Double>();
    
    static int total_tickets=0;
    static double[][] tfidfValue;
    
    static String[] fixedfield;
    static double[][] fixedfieldvalue;
    
    static int topicvalue;
    
    static String[][] topicwords;
    
    static String[] freelist;
    
    public static void read(){
    
        try{
                                String filename = "dataset/"+"NBTY-PMATicketData"+".xls";
				//String filename = "dataset/GLMS.xls";
                                WorkbookSettings ws = new WorkbookSettings();
				ws.setLocale(new Locale("en", "EN"));
				Workbook w = Workbook.getWorkbook(new File(filename),ws);

				// Get the first sheet
				Sheet s = w.getSheet(0);
				
				// Convert the contents of the cells  
				Cell[] row = null;
				
                                System.out.println(s.getRows());
                                
                               freelist=new String[s.getRows()];
                               int i=-1;
                               try{ 
                                for(i=1;i<s.getRows();i++){
                                    row=s.getRow(i);
                                    freelist[i]=row[11].getContents();
                                   // System.out.println("error==>"+i+"==>"+freelist[i].length());
                                    //System.out.println(row[11].getContents());
                                }
                               }
                               catch(ArrayIndexOutOfBoundsException e5){
                                   System.out.println(i);
                               }
                                System.out.println("after scanf freelist");
                                freelist=clean(freelist);
                                freelist=filterKeywords(freelist);
        
             Scanner s1=new Scanner(new File("category\\categorylist.csv"));
            //Scanner s1=new Scanner(new File("walmart\\categorylist.csv"));
            
            while(s1.hasNextLine()){
                String[] spl=s1.nextLine().split(",");
                String[] category = spl[0].split("@");
                topicvalue=Integer.valueOf(spl[2]);
                
                String catName=category[0];
                String appName=category[1];
                String intype=category[2];
                String sub=category[3];
                
                readAll_AMD(catName,appName,intype,sub);
                       
                   
            
           }
            
            
            //readAll_AMD("Administration","AS400 Legacy - Direct Response");
           
            //readCheck();
           
          /*  File f = new File("silhouette.csv");
			

            @SuppressWarnings("resource")
            OutputStream os = (OutputStream) new FileOutputStream(f);
            String encoding = "UTF8";
            OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            
           
            Set<String> str=silhouette.keySet();
            Iterator it=str.iterator();
            while(it.hasNext()){
                String  temp=it.next().toString();
                bw.write(temp);
                bw.write(',');
                bw.write(String.valueOf(silhouette.get(temp)));
                bw.newLine();
            }
            
            bw.flush();
            bw.close();
            os.close();
            
        
        */
        }
       // catch (UnsupportedEncodingException e) {
	//System.err.println(e.toString());
     
        catch (IOException e) {
 	System.err.println(e.toString());
     } catch (Exception e) {
	System.err.println(e.toString());
     }
        
    }
    
    
    public static void readAll_AMD(String category,String appName,String intype,String sub) 
	{
		try
		{
			// File to store data in form of CSV
                        int count=0;
                        
                        //Unigram = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                        
                        allForms.clear();
                        forms.clear();
                        ListOfkeywords.clear();
                        
                        Unigram = new ArrayList<String>();
                        String path="Category\\"+category+"@"+appName+"@"+intype+"@"+sub+"\\";
                        // String path="walmart\\"+category+"@"+appName+"@"+intype+"@"+sub+"\\";
			File fixedFields = new File(path+"fixedFields.csv");
			File form = new File(path+"form.csv");
			

			@SuppressWarnings("resource")
			OutputStream os = (OutputStream) new FileOutputStream(fixedFields);
			String encoding = "UTF8";
			OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
			BufferedWriter bw = new BufferedWriter(osw);

			@SuppressWarnings("resource")
			OutputStream os2 = (OutputStream) new FileOutputStream(form);
			OutputStreamWriter osw2 = new OutputStreamWriter(os2, encoding);
			BufferedWriter bw2 = new BufferedWriter(osw2);

			

			LinkedList<String> Months = new LinkedList<String>();
			Months.add("NBTY-PMATicketData");
                        //Months.add("GLMS");
			
			for (String m : Months){
				// Excel document to be imported
				System.out.println("opening excel");
				String filename = "dataset/"+m+".xls";
				WorkbookSettings ws = new WorkbookSettings();
				ws.setLocale(new Locale("en", "EN"));
				Workbook w = Workbook.getWorkbook(new File(filename),ws);

				// Get the first sheet
				Sheet s = w.getSheet(0);
				
				// Convert the contents of the cells  
				Cell[] row = null;
				count = 0;
                                
                                LemmeAndTag slem = new LemmeAndTag();
                                
				for (int i = 1 ; i < s.getRows() ; i++) {

					row = s.getRow(i);
					
                                       String thisCategory=(clean_folder(row[3].getContents())).toLowerCase();
                                       String thisappName=(clean_folder(row[5].getContents())).toLowerCase();
                                       String thisintype=(clean_folder(row[2].getContents())).toLowerCase();
                                       String thissub=(clean_folder(row[4].getContents())).toLowerCase();
					
					
					
					if (row.length > 0 && thisCategory.equalsIgnoreCase(category) && thisappName.equalsIgnoreCase(appName) && thisintype.equalsIgnoreCase(intype) && thissub.equalsIgnoreCase(sub)) {
						
						//if (count<=N/3){
						
							// need to check if open/close
							
                                                        //System.out.println("inside main");                                                        
							String freeForm = row[11].getContents();
                                                        //bw2.write(freeForm);
                                                        //bw2.write(',');
							String raw=cleanline(freeForm);
                                                        freeForm = freelist[i];
                                                        
                                                        form myForm = new form();
                                                        if(freeForm.length() > 0){
                                                            count++;
                                                            
                                                            rawdata.put(count-1, raw);
                                                            
                                                            bw.write(delete_comm(row[2].getContents()));
                                                            bw.write(',');
                                                            bw.write(delete_comm(row[4].getContents()));
                                                           
                                                            
                                                            
                                                            String[] keyword=freeForm.split(" ");
                                                            
                                                                                                                                 for(int k=0;k<keyword.length;k++){
                                                                if(!Unigram.contains(keyword[k])){
                                                                    Unigram.add(keyword[k]);
                                                                    
                                                                }
                                                                ListOfkeywords.addWord(keyword[k]);
                                                                myForm.addWord(keyword[k]);
                                                            }
                                                        
                                                            bw2.write(freeForm);
                                                            bw2.newLine();
                                                            bw.newLine();
                                                        
                                                        }
                                                        
                                                        allnewForms.add(slem.lemmatize(freelist[i]));
                                               	
						forms.add(myForm);
					}
				}

			}
			bw.flush();
			bw.close();
			bw2.flush();
			bw2.close();
			
			os.close();
			os2.close();
                        
                       /* findPotentialBigramTrigram();
                        
			
			System.out.println("excel read correctly");
                        
                       /* System.out.println("==================================================");
                        System.out.println(category+"--"+appName);
                        System.out.println("==================================================");
                        
                        if(count <= 10){
                            System.out.println("This tuple has enough tickets");
                            ticketid.clear();
                            ticketbucket.clear();
                            rawdata.clear();
                            Unigram.clear();
                            
                        }
                        else{
                        
                        removeKeywords(path,count);    
                            
                        Matrix x=ticketCorrelationMatrix(path,count,Unigram.size());
                        Matrix m=tfidf(path,count);
                        
                        System.out.println("After Matrix creaton");
                        
                        System.out.println("keywordsLength:" + Unigram.size());
                        
                       /* PAM.readFiles(path);
                        
                        for(int kvalue=2;kvalue<25;kvalue++){
                            PAM.setK(kvalue);
                            System.out.println("kvalue:"+kvalue);
                            PAM.main(Clustering.arg);
                                    
                        }    
                        
                       // Optimizedsvd s=new Optimizedsvd(m);
                        
                       
                        
                        int topics=topicvalue;
                        
                        topicwords=new String[topics][10];
                        for(int t1=0;t1<topics;t1++){
                            for(int t2=0;t2<10;t2++){
                                topicwords[t1][t2]="";
                            }
                        }
                        
                        //int topics=s.kvalue();
                        //if(topics>5)
                          //  topics=5;
                        
                        
                        Nmf n=new Nmf(x,topics);
                        
                        System.out.println("After First NMF");
                        
                        Matrix u=n.getU();
                        
                         File fu = new File(path+"Umatirx.csv");
			@SuppressWarnings("resource")
			OutputStream osu = (OutputStream) new FileOutputStream(fu);
			OutputStreamWriter oswu = new OutputStreamWriter(osu, encoding);
			BufferedWriter bwu = new BufferedWriter(oswu);
                        
                        System.out.println(u.getRowDimension());
                        
                        for(int i=0;i<u.getRowDimension();i++){
                                bwu.write(rawdata.get(i));
                                bwu.write(",");
                            for(int j=0;j<u.getColumnDimension();j++){
                                
                                bwu.write(String.valueOf(u.get(i, j)));
                                bwu.write(",");
                                
                            }
                            bwu.newLine();
                        }
                        
                        bwu.flush();
                        
                        bwu.close();
                        osu.close();
                        
                        total_tickets=u.getRowDimension();
                        
                        Nmf n1=new Nmf(m,u,topics);
                        
                        System.out.println("AFter SEcond NMF");
                        
                        Matrix v=n1.getV();
                        
                        
                        
                        
                        System.out.println("After findTicketcount");
                        
                        String[] potentialKeywords=new String[Unigram.size()];
                        
                        Iterator it=Unigram.iterator();
                        
                        int len=0;
                        
                        while(it.hasNext()){
                            potentialKeywords[len]=it.next().toString();
                            len++;
                        }
                        
                        System.out.println("After assinging potential keyphrases");
                        
                        File keyphrases = new File(path+"Keyphrases.csv");
			

			@SuppressWarnings("resource")
			OutputStream os3 = (OutputStream) new FileOutputStream(keyphrases);
		
			OutputStreamWriter osw3 = new OutputStreamWriter(os3, encoding);
			BufferedWriter bw3 = new BufferedWriter(osw3);
                        
                       
                        System.out.println("After Creating keyphrases");
                        
                        
                        for(int i=0;i<v.getRowDimension();i++){
                            
                            HashMap<Integer,Double> map_value=new HashMap<Integer,Double>();
                            
                            for(int j=0;j<v.getColumnDimension();j++){
                                
                                map_value.put(j,v.get(i, j));
                                
                            }
                            
                            //System.out.println("Map Value");
                            
                            Map<Integer,Double> map=SortByValue.sort(map_value);
                            
                            //System.out.println("Map Value:After sorting");
                            
                            
                            Set set2 = map.entrySet();
                            Iterator iterator2 = set2.iterator();
                            int top=0;
                            double[] prob=new double[10];
                            double totalprob=0;
                            double[] marprob=new double[10];
                            while(iterator2.hasNext() && top<10) {
                                Map.Entry me2 = (Map.Entry)iterator2.next();
                                prob[top]=Double.valueOf(me2.getValue().toString());
                                totalprob+=prob[top];
                                topicwords[i][top]=potentialKeywords[Integer.valueOf(me2.getKey().toString())];
                                bw3.write(potentialKeywords[Integer.valueOf(me2.getKey().toString())]);
                                bw3.write(',');
                                top++;
                                
                            }
                            for(int iv=0;iv<top;iv++){
                                if(totalprob>0)
                                    marprob[iv]=(prob[iv]/totalprob);
                                else
                                    marprob[iv]=0;
                                
                                bw3.write(String.valueOf(marprob[iv]));
                                bw3.write(",");
                            }
                           // bw3.write("total ticket:"+String.valueOf(topic_ticket_count[i]));
                            bw3.newLine();
                            
                            //System.out.println("Map Value:After writing keyphrases into excel");
                        }
                        
                        int[] topic_ticket_count=findTicketCount(u,path);
                        
                        System.out.println("After keyphrases");
                        double sil=findSilhouette();
                        
                        silhouette.put(category+"-"+appName, sil);
                        
                        bw3.flush();
                        bw3.close();
                        os3.close();
                        System.out.println("After silhouette");
                        System.out.println(sil);
                        
                        
                       
                        
                }    
                    */    
                        
                        
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.toString());
		} catch (IOException e) {
			System.err.println(e.toString());
		} catch (Exception e) {
			System.err.println(e.toString());
		}
                finally{
                        ticketid.clear();
                        ticketbucket.clear();
                        rawdata.clear();
                        Unigram.clear();
                }
	}
    
    
    
    private static String delete_comm(String field){
		return field.replace(',', '_');
	}
    
    
    private static String[] clean(String[] freelist) {
        
        String[] newlist=new String[freelist.length];
         System.out.println("inside clean");
        for(int j=1;j<freelist.length;j++){
            String cleaned=freelist[j];
           
		
                
                while(cleaned.contains("\n\n")){
			cleaned = cleaned.replaceAll("\n\n","\n");
		}
                cleaned = cleaned.replaceAll("\n"," ");
                
		cleaned = cleaned.replace(',', ' ');
		cleaned = cleaned.replace(':', ' ');
                cleaned = cleaned.replace('?', ' ');
                cleaned = cleaned.replace('(', ' ');
                cleaned = cleaned.replace(')', ' ');
                cleaned = cleaned.replace('\"', ' ');
                cleaned = cleaned.replace('\'', ' ');
                cleaned = cleaned.replace('[', ' ');
                cleaned = cleaned.replace(']', ' ');
                
		//cleaned = cleaned.replaceAll("[?,(,),\",\']", " ");
                
		String[] fragment = cleaned.split(" ");
		
                ArrayList<String> filterWords=new ArrayList<String>();
               
                for(int i=0;i<fragment.length;i++){
                    
                    if((!fragment[i].equals("")) && fragment[i].length() > 2  && fragment[i].matches("[a-zA-Z]+")){
                    
                        filterWords.add(fragment[i]);
                    
                    }
                    
                }
                
                cleaned="";
                for(int i=0;i<filterWords.size();i++){
                    
                   cleaned+=filterWords.get(i);
                   cleaned+=" "; 
                }
                
                
		// manual modification of some obvious words
		cleaned = cleaned.replace(" mail", " email");
		cleaned = cleaned.replace(" bb", " blackberry");
		cleaned = cleaned.replace(" dl"," download");
                
                newlist[j]=cleaned;
        
        }        
               //cleaned = filterKeywords(cleaned);
           
                
		return newlist;
	}
    
    
    private static String clean_folder(String freeForm) {
		String cleaned = freeForm;
		cleaned = cleaned.replace('/', '-');
		cleaned = cleaned.replace('\\', '-');
		cleaned = cleaned.replace(':', '-');
		cleaned = cleaned.replace('?', '-');
		cleaned = cleaned.replace('*', '-');
		cleaned = cleaned.replace('"', '-');
		cleaned = cleaned.replace('<', '-');
		cleaned = cleaned.replace('>', '-');
                cleaned = cleaned.replace('|', '-');
                
        return cleaned;
     }
    
    
    
    private static String[] filterKeywords(String[] freelist){
      
        String[] newlist=new String[freelist.length];
        try{
        
       System.out.println("inside filterkeywords");     
       LemmeAndTag slem = new LemmeAndTag();
       
     for(int i=1;i<freelist.length;i++){  
        allForms = new LinkedList<List<CoreLabel>>();
        String summary=freelist[i];
        
        allForms.add(slem.lemmatize(summary));
        form myForm = new form();
        
        for (List<CoreLabel> sentence : allForms) {
			
			
			for (CoreLabel token : sentence) {
				String tag = token.tag();
                                
				if (tagRemover.check(tag)) {
					String lemma = token.lemma();
                                        
                                        
					//myForm.addWord(lemma);
					//if (tag.startsWith("NN") && isInWordNet(lemma)) {
					if (tag.startsWith("NN")) {
						myForm.addWord(lemma);
                                                
					}
				}
			}
			forms.add(myForm);
			
		}
        
            File stopWordsFile = new File("stopwords.txt");
		BufferedReader in = null;
		in = new BufferedReader(new FileReader(stopWordsFile));
		String line;
		
		/* make stop words hash set */
		stopWords = new HashSet<String>();
		
		while ((line = in.readLine()) != null) {
			stopWords.add(line);
		} 
		
		
		/* remove stop words */
		List<String> allWords = new LinkedList<String>(
				myForm.words.keySet());
		
		for (String terms : allWords) {
			if (stopWords.contains(terms)) {
				myForm.words.remove(terms);
			}
		}
                
                String keyword="";
                
                List<String> allWords1 = new LinkedList<String>(
				myForm.words.keySet());
                
                
                //Store all the unique words in the data
                
                
                
                
                for (String terms : allWords1) {
			keyword+=terms;
                        keyword+=" ";
		}
                
                newlist[i]=keyword;
                               
		in.close();
            }   
                return newlist;
        }
        catch(Exception e){
            System.out.println(e);
        }
      
        
        return newlist;
    }
    
    
        
    public static Matrix ticketCorrelationMatrix(String path,int count,int keywordcount){
        
        double[][] mat= new double[count][count];
        Matrix x=new Matrix(10,10);
        
        fixedfield=new String[count];
        fixedfieldvalue=new double[count][count];
        
        
        try{
            Scanner fixed1=new Scanner(new File(path+"fixedFields.csv"));
            Scanner form1=new Scanner(new File(path+"form.csv"));
            
            for(int i=0;i<count;i++){
                String fixedfield1=fixed1.nextLine();
                String freeform1=form1.nextLine();
                
                fixedfield[i]=fixedfield1;
                
                 Scanner fixed2=new Scanner(new File(path+"fixedFields.csv"));
                 Scanner form2=new Scanner(new File(path+"form.csv"));
                 int j;
                 for(j=0;j<i;j++){
                     String temp1=fixed2.nextLine();
                     String temp2=form2.nextLine();
                 }
                 for(;j<count;j++){
                     String fixedfield2=fixed2.nextLine();
                     String freeform2=form2.nextLine();
                     
                     double jaccardValue=Matoperation.JaccardSimilarity(fixedfield1,fixedfield2);
                     double cosineValue=Matoperation.CosineSimilarity(freeform1,freeform2);
                     
                     double alpha=Math.max(0.2, 2/(2+keywordcount));
                     
                     //mat[j][i]=alpha*jaccardValue + (1-alpha)*cosineValue;
                     mat[j][i]=cosineValue;
                     
                     mat[i][j]=mat[j][i];
                     
                     fixedfieldvalue[i][j]=jaccardValue;
                     fixedfieldvalue[j][i]=jaccardValue;
                 }
                 
                 
            }
            
            File ticketCorrelationMatrix = new File(path+"ticketCorrelationMatrix.csv");
            
            OutputStream os = (OutputStream) new FileOutputStream(ticketCorrelationMatrix);
            String encoding = "UTF8";
            OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
            BufferedWriter bw = new BufferedWriter(osw);
            
            for(int i=0;i<count;i++){
                for(int j=0;j<count;j++){
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
    
    public static double countFrequency(String data,String key){
        double frequency=0;
        
        String[] terms=data.split(" ");
        for(int i=0;i<terms.length;i++){
            if(terms[i].equalsIgnoreCase(key)){
                frequency+=1;
            }
        }
        
        return frequency;
    }
    
    
    public static void removeKeywords(String path,int count){
        
        try{
           
        
        
        double[][] termFrequency = new double[count][Unigram.size()];
        double[] idf=new double[Unigram.size()];
        
        for(int i=0; i<Unigram.size();i++)
            idf[i]=0;
        
        
        Scanner s=new Scanner(new File(path+"form.csv"));
        
        for(int i=0;i<count;i++){
            String data=s.nextLine();
            
            Iterator itr=Unigram.iterator();
            
            int j=0;
            while(itr.hasNext()){
                termFrequency[i][j]=countFrequency(data,itr.next().toString());
                if(termFrequency[i][j] > 0)
                    idf[j]+=1;
                j++;
            }
        }
                     
        for(int i=Unigram.size()-1;i>=0;i--){
            
            if(idf[i]<=3){
                Unigram.remove(i);
            }
            
        }
        
        
       }
       catch(IOException e){
           System.out.println(e);
       }
        
    }
    
    public static Matrix tfidf(String path,int count){
     
        Matrix temp=new Matrix(count,Unigram.size());//in order to avoid error;
        
       try{
           
        
        tfidfValue=new double[count][Unigram.size()];
        double[][] termFrequency = new double[count][Unigram.size()];
        double[] idf=new double[Unigram.size()];
        
        for(int i=0; i<Unigram.size();i++)
            idf[i]=0;
        
        
        Scanner s=new Scanner(new File(path+"form.csv"));
        
        for(int i=0;i<count;i++){
            String data=s.nextLine();
            
            Iterator itr=Unigram.iterator();
            
            int j=0;
            while(itr.hasNext()){
                termFrequency[i][j]=countFrequency(data,itr.next().toString());
                if(termFrequency[i][j] > 0)
                    idf[j]+=1;
                j++;
            }
        }
        
        
        
        File keywords = new File(path+"Unigram.csv");
	File tf_idf = new File(path+"TFIDF.csv");
			

	@SuppressWarnings("resource")
	OutputStream os = (OutputStream) new FileOutputStream(keywords);
	String encoding = "UTF8";
	OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
	BufferedWriter bw = new BufferedWriter(osw);

	@SuppressWarnings("resource")
	OutputStream os2 = (OutputStream) new FileOutputStream(tf_idf);
	OutputStreamWriter osw2 = new OutputStreamWriter(os2, encoding);
	BufferedWriter bw2 = new BufferedWriter(osw2);
        
        
        for(int i=0;i<count;i++){
            for(int j=0;j<Unigram.size();j++){
                tfidfValue[i][j]=Math.log10(count/idf[j])*termFrequency[i][j];
                bw2.write(String.valueOf(tfidfValue[i][j]));
                bw2.write(',');
            }
            bw2.newLine();
        }
        
        
        
        Iterator itr=Unigram.iterator();
        while(itr.hasNext()){
            bw.write(itr.next().toString());
            bw.newLine();
        }
        
        bw.flush();
			bw.close();
			bw2.flush();
			bw2.close();
			
			os.close();
			os2.close();
        
        Matrix m=new Matrix(tfidfValue);
        
        return m;
                        
        
       }
       catch(IOException e){
           System.out.println(e);
       }
       
       return temp;
    }
    
    public static int[] findTicketCount(Matrix u,String path){
        
        int[] count=new int[u.getColumnDimension()];
        
        try{
        
        for(int i=0;i<u.getColumnDimension();i++)
            count[i]=0;
        
        Scanner s=new Scanner(new File(path+"form.csv"));
        
        for(int i=0;i<u.getRowDimension();i++){
            double max=0;
            int index=0;
            
            String ticket=s.nextLine();
            //System.out.println(ticket);
            
            for(int j=0;j<u.getColumnDimension();j++){
                
                if(u.get(i, j)> max){
                    index=j;
                    max=u.get(i, j);
                }
                
            }
            
            count[index]++;
            
            
            ticketid.put(i, ticket);
            
            if(ticketbucket.containsKey(index)){
                
                ArrayList<Integer> tid=ticketbucket.get(index);
                tid.add(i);
                
            }
            else{
                
                ArrayList<Integer> tid=new ArrayList<Integer>();
                tid.add(i);
                ticketbucket.put(index, tid);
                
            }         
            
          
        }
        for(int i=0;i<u.getColumnDimension();i++)
            System.out.println(count[i]);
        
        
       System.out.println("After ticket bucket"); 
        
       
            
        for(int i=0;i<u.getColumnDimension();i++){    
            
                                    
                            File f= new File(path+"tickets_"+i+".csv");
			
                            int[] wordticketcount=new int[11];
                            for(int w=0;w<11;w++)
                                wordticketcount[w]=0;
                            @SuppressWarnings("resource")
                            OutputStream out_str = (OutputStream) new FileOutputStream(f);
                            String encoding = "UTF8";
                            OutputStreamWriter out_strw = new OutputStreamWriter(out_str, encoding);
                            BufferedWriter buff_w = new BufferedWriter(out_strw);
                            
                            if(ticketbucket.containsKey(i)){
                            
                            for(int wl=0;wl<10;wl++){
                                buff_w.write(topicwords[i][wl]+",");
                            }
                            
                            buff_w.newLine();
                            buff_w.newLine();
                            buff_w.newLine();
                            
                            ArrayList<Integer> tid=ticketbucket.get(i);
                            
                            for(int t=0;t<tid.size();t++){
                                 buff_w.write(ticketid.get(tid.get(t)));
                                 buff_w.write(',');
                                 buff_w.write(rawdata.get(tid.get(t)));
                                 buff_w.write(",");
                                 //System.out.println(st);
                                 int match=0;
                                 String[] data=ticketid.get(tid.get(t)).split(" ");
                                 for(int d=0;d<data.length;d++){
                                     for(int key=0;key<10;key++){
                                         if(topicwords[i][key].equalsIgnoreCase(data[d])){
                                             match++;
                                         }
                                     }
                                 }
                                 wordticketcount[match]++;
                                 buff_w.write(String.valueOf(match));
                                 buff_w.write(",");
                                 /*String[] ff=fixedfield[t].split(",");
                                 for(String f_f:ff){
                                     buff_w.write(f_f);
                                     buff_w.write(",");
                                 }*/
                                 
                                 buff_w.newLine();
                            }
                               buff_w.newLine();
                               buff_w.newLine();
                               buff_w.newLine();
                               
                            for(int w=0;w<11;w++){
                                buff_w.write(String.valueOf(w)+","+wordticketcount[w]);
                                buff_w.newLine();
                            }
                            
                            }
                            else{
                                System.out.println("no ticket is allocated to this cluster:"+i);
                            }
                            
                            buff_w.flush();
                            buff_w.close();
                            out_str.close();
                            
                            
            
        } 
        
                            File f= new File(path+"ticketid.csv");
			

                            @SuppressWarnings("resource")
                            OutputStream out_str = (OutputStream) new FileOutputStream(f);
                            String encoding = "UTF8";
                            OutputStreamWriter out_strw = new OutputStreamWriter(out_str, encoding);
                            BufferedWriter buff_w = new BufferedWriter(out_strw);
                           
                            for(int i=0;i<u.getColumnDimension();i++){ 
                              if(ticketbucket.containsKey(i)){
                                ArrayList<Integer> tid=ticketbucket.get(i);
                            
                                for(int t=0;t<tid.size();t++){
                                    buff_w.write(String.valueOf(tid.get(t)));
                                    //System.out.println(st);
                                    buff_w.write(',');
                                }
                              }  
                                buff_w.newLine();
                              
                            }
                            
                            buff_w.flush();
                            buff_w.close();
                            out_str.close();        
        
        
        
        }
        catch(IOException e){
            System.out.println(e);
        }
        
        
        return count;
    }
    
    public static double findSilhouette(){
        double result=0;
        
        double[][] distance_ticket=new double[total_tickets][total_tickets];
        
        for(int i=0;i<total_tickets;i++){
            for(int j=0;j<total_tickets;j++){
                
                double value=0;
                for(int k=0;k<tfidfValue[0].length;k++){
                    value+=Math.pow(tfidfValue[i][k]-tfidfValue[j][k],2);//+(1-fixedfieldvalue[i][j]);
                }
                distance_ticket[i][j]=Math.sqrt(value);
            }
        }
        
        
        
        for(int i=0;i<ticketbucket.size();i++){
            
            if(ticketbucket.containsKey(i)){
            
            ArrayList<Integer> ticket_id=ticketbucket.get(i);
            
             
                       
            for(int j=0;j<ticket_id.size();j++){
                
                
                double a=(findClusterSimilarity(ticket_id.get(j),ticket_id,distance_ticket))/ticket_id.size();
                
                double b=Double.MAX_VALUE;
                for(int k=0;k<ticketbucket.size();k++){
                    
                  if(k!=i){
                    
                    if(ticketbucket.containsKey(k)){
                      
                    ArrayList<Integer> temp=ticketbucket.get(k);
                    double tempvalue=(findClusterSimilarity(ticket_id.get(j),temp,distance_ticket)/temp.size());
                    if(tempvalue<b){
                        b=tempvalue;
                    }
                    
                    }
                  }  
                }
                if(b!=0 || a!=0){
                    
                    result+=(b-a)/(b>a?b:a);
                }
                
                
            }
            }
        }
        System.out.println(total_tickets);
        return (result/total_tickets);
    }
    
    
    public static double findClusterSimilarity(int index,ArrayList<Integer> list,double[][] distance_ticket){
        double result=0;
       
        for(int i=0;i<list.size();i++){
            result+=distance_ticket[index][list.get(i)];
        }
       
        return result;
    }
    
    
    public static String cleanline(String data){
        
       String cleaned = data;
                
                while(cleaned.contains("\n\n")){
			cleaned = cleaned.replaceAll("\n\n","\n");
		}
                cleaned = cleaned.replaceAll("\n"," ");
                cleaned = cleaned.replaceAll(","," ");
        
        return cleaned;
        
        
        
    }
    
    
    
    public static void findPotentialBigramTrigram() {
		try {
			findBigramTrigram();
			writeBigramTrigram();
			writeFinalKWBiTri();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** writeBigramTrigram
	 * write potential bigrams and trigrams in csv file
	 * @throws IOException
	 */
	public static void writeBigramTrigram() throws IOException {
		BufferedWriter br = new BufferedWriter(
				new FileWriter("BigramTrigramList.csv"));
		
		for(String key : ListOfkeywords.bigrams.keySet()) {
			br.write(key+'\n');
		}
		for(String key : ListOfkeywords.trigrams.keySet()) {
			br.write(key+'\n');
		}
		br.close();
	}
	
	/** findBigramTrigram
	 * store bigram and trigram in myForm
	 */
	public static void findBigramTrigram() {
		int i = 0;
		
		for (List<CoreLabel> sentence : allnewForms) {
			i++;
			String second_tag = "";
			String second_word = "";
			String third_tag = "";
			String third_word = "";
			form myForm = new form();
			for (CoreLabel token : sentence) {
				String tag = token.tag();
				String lemma = token.lemma();
				
				/* if token is started with capital letter, consider it the beginning of a sentence */
				if (Character.isUpperCase(token.originalText().charAt(0))) {
					second_tag = "";
					second_word = "";
					third_tag = "";
					third_word = "";
				}
				
				/* find bigram and trigram keywords using every word heuristic method */
				if (tag.startsWith("NN") && ListOfkeywords.words.containsKey(lemma)) {
					if(second_tag.startsWith("NN") && ListOfkeywords.words.containsKey(second_word)) {
						myForm.addBigrams(second_word + " " + lemma);
						if(third_tag.startsWith("NN") && ListOfkeywords.words.containsKey(third_word)) {
							myForm.addTrigrams(third_word + " " + second_word + " " + lemma);
						//	System.out.println("sentence: "+sentence.toString());
							//System.out.println(third_word + " " + second_word + " " + lemma);
						}
					}
				}
				third_tag = second_tag;
				third_word = second_word;
				second_tag = tag;
				second_word = lemma;
			}
			forms.add(myForm);
			
			LinkedList<String> localkeyWords = new LinkedList<String>();
			
			for (String s : myForm.bigrams.keySet()) {
				ListOfkeywords.addBigrams(s);
				localkeyWords.add(s);				
			}
			for (String s : myForm.trigrams.keySet()) {
				ListOfkeywords.addTrigrams(s);
				localkeyWords.add(s);
			}
			
			finalKWBiTri.add(localkeyWords);
		}
		System.out.println("forms read : "+i);
		//minFrequency = i/100;
				
		//ListOfkeywords.removeSmallSize(2);
		//ListOfkeywords.removeSmallFreq(minFrequency);
		
	}
	
	/**    writeFinalKWBiTri
	 * 	write the bigram_trigram_keywords_per_ticket.csv file
	 *  List of the bigram,trigram for every freeform
	 * @throws IOException
	 */

	public static void writeFinalKWBiTri() throws IOException {
		// Write the keyWords
		BufferedWriter br = new BufferedWriter(new FileWriter("bigram_trigram_keywords_per_ticket.csv"));
		String s = "";
		for (LinkedList<String> liste : finalKWBiTri) {
			s="";
			for (String kw : liste) {
				s += kw;
				s += ",";

			}
			s += "\n";
			br.write(s);
		}
		br.flush();
		br.close();
	}
    
    
}
