/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import TokenizerTools.LemmeAndTag;
import TokenizerTools.tagRemover;
//import static clustering.readxcel.allForms;

import edu.stanford.nlp.ling.CoreLabel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import java.util.Iterator;

/**
 *
 * @author rajaprabu.trn
 */
public class readtickets {
    
    
    static String[] freelist;
    static double[] qos_data; 
    
    //static List<List<CoreLabel>> allForms;
   // static List<form> forms = new LinkedList<form>();
    static HashSet<String> stopWords;
    
     public static void read(){
    
        try{
            //String filename = "dataset/"+"NBTY-PMATicketData"+".xls";
        	 // String filename = "dataset/"+"allstate27102014"+".xls";
        	String filename = "dataset/"+"GLMS_edited"+".xls";
        	//String filename = "dataset/"+"Belgacom_rfp21042015"+".xls";
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
                                  //  System.out.println("freelist[i] begining"+freelist[i]);
                                    //System.out.println("freelist[i] before"+freelist[i]+freelist[i].length()+freelist.length);
                                 //  freelist[i]=freelist[i].toLowerCase();
                                  //  System.out.println("freelist[i] after"+freelist[i]+freelist[i].length()+freelist.length);
                                   // System.out.println("error==>"+i+"==>"+freelist[i].length());
                                    //System.out.println(row[11].getContents());
                                }
                               }
                               catch(ArrayIndexOutOfBoundsException e5){
                                   System.out.println(i);
                               }
                                System.out.println("after scanf freelist");
                                //Original Order: clean->filterKeywords->removeLessFrequency 
                                freelist=clean(freelist);  
                               freelist=filterKeywords(freelist);
                               freelist=removeLessFrequency(freelist);
                               for(i=1;i<freelist.length;i++) 
                                	freelist[i]=freelist[i].toLowerCase();
                                		
             Scanner s1=new Scanner(new File("category\\categorylist.csv"));
            //Scanner s1=new Scanner(new File("walmart\\categorylist.csv"));
            
            while(s1.hasNextLine()){
                String[] spl=s1.nextLine().split(",");
                String[] category = spl[0].split("@");
               //topicvalue=Integer.valueOf(spl[2]);
                System.out.println("category_length"+category.length+"category"+category.clone());
                if(category.length==0)continue;
                String catName=category[0];
                String appName=category[1];
                String intype=category[2];
                String sub=category[3];
                
                readAll_AMD(catName,appName,intype,sub);   
           }  
        }
       // catch (UnsupportedEncodingException e) {
	//System.err.println(e.toString());
     
        catch (IOException e) {
 	System.err.println(e.toString());
     } catch (Exception e) {
	System.err.println(e.toString());
     }
        
    }
      
     private  static String[] clean(String[] freelist) {
        
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
                cleaned = cleaned.replace('\\', ' ');
                cleaned = cleaned.replace('>', ' ');
                cleaned = cleaned.replace('<', ' ');
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
                //http://www.grammar-quizzes.com/noun-forms.html CHECK THIS OUT FOR REMOVAL OF SOME WORDS      
		// manual modification of some obvious words
		cleaned = cleaned.replace(" mail", " email ");  //email->eemail
		cleaned = cleaned.replace(" gmail", " email "); 
		cleaned = cleaned.replace("  bb ", " blackberry "); 
		cleaned = cleaned.replace("  qty ", " quantity "); 
		cleaned = cleaned.replace("  pkg ", " package "); 
		
		//cleaned = cleaned.replace("ly"," "); //doodle->doodownload //That's why spaces at both arguments are Important 
		//cleaned = cleaned.replace("ly "," "); //We shouldn't replace as currenly will be converted(adv) to current(noun), which will be considered for keywords although we don't want that
            
                newlist[j]=cleaned.trim(); //.toLowerCase(); //CHANGES ARE MADE ON 16th MAY BY RAGHAV
        //cleaned.endsWith("ly");
        }        
               //cleaned = filterKeywords(cleaned);                         
		return newlist;
	}
     
     private static String[] filterKeywords(String[] freelist){
      
        String[] newlist=new String[freelist.length];
        try{
        
       System.out.println("inside filterkeywords");     
       LemmeAndTag slem = new LemmeAndTag();
       
        File stopWordsFile = new File("stopwords.txt");
	BufferedReader in = null;
	in = new BufferedReader(new FileReader(stopWordsFile));
	String line;
	
	/* make stop words hash set */
	stopWords = new HashSet<String>();
	
	while ((line = in.readLine()) != null) {
		stopWords.add(line);
	} 
        for(int i=1;i<freelist.length;i++){  
        //allForms = new LinkedList<List<CoreLabel>>();
            String summary=freelist[i];
        
            List<CoreLabel> sentence=slem.lemmatize(summary);
// remove stop words 
            String keyword="";
            
            for(CoreLabel token: sentence){
                
                if(token.tag().startsWith("NN") && ! stopWords.contains(token.lemma())){
                    
                    keyword+=token.lemma();
                    keyword+=" ";
                    
                }
      
            }
            
            newlist[i]=keyword.trim();
        }
     
        in.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
     
    
     
        return newlist;
    }
     
     private static String[] removeLessFrequency(String[] freelist){
         
         String[] newlist=new String[freelist.length];
         
         keyWords Listofkeywords=new keyWords();
         
        try{ 
         
         for(int i=1;i<freelist.length;i++){
             
            String summary=freelist[i];
             
             Set<String> uniquekeyword=new HashSet<String>();
             
             String[] words=summary.split(" ");
             
             for(String w:words){
                 if(!uniquekeyword.contains(w)){
                     uniquekeyword.add(w);
                 }
             }
             
             
             Iterator itr=uniquekeyword.iterator();
             
             while(itr.hasNext()){
                 Listofkeywords.addWord(itr.next().toString());
             }
         }
         Listofkeywords.removeSmallFreq(2);  
         for(int i=1;i<freelist.length;i++){
             String keyword="";
             
             String[] summary=freelist[i].split(" ");
             
             for(String words:summary){
                 if(Listofkeywords.words.containsKey(words)){
                     keyword+=words;
                     keyword+=" ";
                 }
             }            
             newlist[i]=keyword.trim();
         }
        }
        catch(Exception e){
            e.printStackTrace();
        }
         
         return newlist;
         
     }
     
    public static void readAll_AMD(String category,String appName,String intype,String sub) 
	{
		try
		{
			// File to store data in form of CSV
                        int count=0;
                         //Unigram = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                        String path="Category\\"+category+"@"+appName+"@"+intype+"@"+sub+"\\";
                        // String path="walmart\\"+category+"@"+appName+"@"+intype+"@"+sub+"\\";
			
			File form = new File(path+"form.csv");
			
			@SuppressWarnings("resource")
			OutputStream os2 = (OutputStream) new FileOutputStream(form);
            String encoding = "UTF8";
			OutputStreamWriter osw2 = new OutputStreamWriter(os2, encoding);
			BufferedWriter bw2 = new BufferedWriter(osw2);

			LinkedList<String> Months = new LinkedList<String>();
		//Months.add("NBTY-PMATicketData");
         // Months.add("allstate27102014");
          Months.add("GLMS_edited");
             //Months.add("Belgacom_rfp21042015");
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
                                
				qos_data=new double[s.getRows()];
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
                                                        
                                                        
                                                        
                                                        //form myForm = new form();
                                                        if(freeForm.length() > 0){
                                                            count++;
                                                            qos_data[i]=Double.parseDouble(row[16].getContents());
                                                           // bw2.write(freeForm+","+raw+","+qos_data[i]);
                                                            bw2.write(freeForm+","+"removed-commented"+","+qos_data[i]);
                                                            bw2.newLine();
                                                           
                                                        
                                                        }
                                                        
                                                        
						//forms.add(myForm);
					}
				}

			}
			
			bw2.flush();
			bw2.close();
			
			
			os2.close();
                        
                      
                        
                        
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.toString());
		} catch (IOException e) {
			System.err.println(e.toString());
		} catch (Exception e) {
			System.err.println(e.toString());
		}
                
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
    
     
     private static String cleanline(String data){
        
       String cleaned = data;
                
                while(cleaned.contains("\n\n")){
			cleaned = cleaned.replaceAll("\n\n","\n");
		}
                cleaned = cleaned.replaceAll("\n"," ");
                cleaned = cleaned.replaceAll(","," ");
        
        return cleaned;
        
        
        
    }
}
