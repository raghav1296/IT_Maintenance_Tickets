/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

/**
 *
 * @author rajaprabu.TRN
 */

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.*;
import java.util.*;

import TokenizerTools.LemmeAndTag;
import TokenizerTools.tagRemover;
import static clustering.readxcel.allForms;

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


import TokenizerTools.*;
public class readoutput {

    static int count=0;
    
    /**
     * @param args the command line arguments
     */
    
    
    public static boolean filter(String str){
        
        boolean result=true;
        
        if(str.equalsIgnoreCase("Adding annotator lemma")){
            count++;
            return false;
        }
        if(str.equalsIgnoreCase("Adding annotator pos"))
            return false;
        if(str.equalsIgnoreCase("Adding annotator ssplit"))
            return false;
        if(str.equalsIgnoreCase("Adding annotator tokenize"))
            return false;
        return result;
    }
    
    
    
    public static void findSC() throws IOException {
		//BufferedWriter br = new BufferedWriter(new FileWriter(path+"first_concepts_need_manual_inspection.csv"));
		//br.write("keyword,concept1,concept2,concept3\n");
		System.setProperty("wordnet.database.dir", "D:\\WordNet\\dict\\"); 
		
		HashSet<String> concepts_candidates = new HashSet<String>();
                
                String[] keywords={"printer"};
		
		for(String keyword : keywords) {
                    System.out.println("=================================");
                    System.out.println(keyword);
                    System.out.println("=================================");
			NounSynset nounSynset; 
						
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			Synset[] synsets = database.getSynsets(keyword, SynsetType.NOUN); 
                        
                        //String[] parent=database.getBaseFormCandidates(keyword, SynsetType.VERB);
                        
                        nounSynset=(NounSynset) synsets[0];
                        
                        System.out.println(nounSynset.getHypernyms()[0].getWordForms()[0]);
                        
			/*for (int i = 0; i < synsets.length; i++) { 
                            System.out.println(synsets[i]);
                            
			    nounSynset = (NounSynset)(synsets[i]);
                            
                            System.out.println(nounSynset);
                            
			    String concept = nounSynset.getWordForms()[0];
                            
                            //System.out.println(nounSynset.getUsageExamples());
                            
                            
                            
                           // System.out.println(nounSynset.getWordForms());
                            
                            //System.out.println(nounSynset.);
                            
                            
			    int sc = database.getSynsets(concept).length;
                            
                            System.out.println(concept+"==>"+sc);
			    
                        
                        
			}*/
                    
                }
    }
                        
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        try{
            
            /*Scanner s=new Scanner(new File("output.txt"));
            while(s.hasNext()){
                String text=s.nextLine();
                if(filter(text)){
                    System.out.println(text);
                }
                
            }
            
            System.out.println(count);*/
            //findSC();
            
            
           // String s=" INVINQIM    INGRID MARTINEZ  BLD 90 PKG   Needs to be able to print Option 36  SIGLE WORK ORDER FORM REPRINT   to WPPKG1 Currently she does not know where it is going ";
            /*String s="tensorflow python suhas raja";
            LemmeAndTag slem=new LemmeAndTag();
            List<CoreLabel> sentence=slem.lemmatize(s);
            
            for(CoreLabel word:sentence){
                
                System.out.println(word.tag());
                System.out.println(word.lemma());
                
               // if(word.tag().startsWith("NN")){
                //    System.out.println("inside noun");
               // }
            
                
            }
            */
            
            String str=" ";
            
           
            
            System.out.println(str.split(" ").length);
            
            if(str.split(" ").length == 1)
                System.out.println("true");
            else
                System.out.println("false");
            
        }
        catch(Exception e){
            
        //}
       // catch(FileNotFoundException e){
            System.out.println(e);
        }
        
    }
    
}
