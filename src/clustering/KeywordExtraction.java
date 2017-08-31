/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clustering;

import TokenizerTools.LemmeAndTag;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.ling.CoreLabel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.*;
import java.io.FileWriter;

/*
 *
 * @author rajaprabu.trn
 */
public class KeywordExtraction {
    
        static List<List<CoreLabel>> allForms; 
        static HashMap<String,Integer> idf=new HashMap <String,Integer>();    
        static keyWords ListOfkeywords = new keyWords(); 	// a list of keywords store with the keywords format	
        static List<form> forms = new LinkedList<form>();	// List of all the forms store in "form" format (see form.java)
        static int ticketSize;
		static double[][] tfidfValueUni;
        static double[][] tfidfValueBi;
        static double[][] tfidfValueTri;
        static double[] QoS;
	
   public static void documentReader(String filepath,String Size) throws IOException {
		allForms = new LinkedList<List<CoreLabel>>();
		File form = new File(filepath+"form.csv");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(form), "UTF8");
		BufferedReader bf = new BufferedReader(isr);
		LemmeAndTag slem = new LemmeAndTag();	// this is the tool to lemmatize and tag the text
		String s = null;
		double[] QoS_dummy=new double[Integer.valueOf(Size)];
		System.out.println("QoS Size"+Integer.valueOf(Size));
		int compteur = 0;
		int i=0;
		while ((s = bf.readLine()) != null) {
			compteur++;
                        String[] summary=s.split(",");
                        //System.out.println(summary.length);
                        QoS_dummy[i]=Double.parseDouble(summary[2]);
                        i++;
                        //System.out.println(summary[0]);
			if (compteur % 100 == 0) {
				System.out.println("forms processed :" + compteur);
			}
			allForms.add(slem.lemmatize(summary[0]));
                        String[] words=summary[0].split(" ");
                }
		QoS=new double[i];
		for(int j=0;j<i;j++) QoS[j]=QoS_dummy[j];
		bf.close();
	}
   
   	public static void findBigramTrigram() {
		int i = 0;
		
		for (List<CoreLabel> sentence : allForms) {
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
				
                                myForm.addWord(lemma);
                               	ListOfkeywords.addWord(lemma);
                               
                                
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
			
			//LinkedList<String> localkeyWords = new LinkedList<String>();			
			for (String s : myForm.bigrams.keySet()) {
				ListOfkeywords.addBigrams(s);
				//localkeyWords.add(s);				
			}
			for (String s : myForm.trigrams.keySet()) {
				ListOfkeywords.addTrigrams(s);
				//localkeyWords.add(s);
			}			
			//finalKWBiTri.add(localkeyWords); */                        
		}
		System.out.println("forms read : "+i);
		//minFrequency = i/100;	
		
           ////////////////////////////////////////////////////////////////////////     
                int keywordSize=ListOfkeywords.words.size()+ListOfkeywords.bigrams.size()+ListOfkeywords.trigrams.size();
                System.out.println("keywordSize"+keywordSize+","+ListOfkeywords.words.size()+","+ListOfkeywords.bigrams.size()+","+ListOfkeywords.trigrams.size());
               if(keywordSize>380){ //WHY 380???????????????????????????????????????????????????????????????????????????????????
                
                int totalwordfrequency=0;
                
                for(String s:ListOfkeywords.words.keySet()){
                    totalwordfrequency+=ListOfkeywords.words.get(s);
                }
                for(String s:ListOfkeywords.bigrams.keySet()){
                    totalwordfrequency+=ListOfkeywords.bigrams.get(s);
                }
                for(String s:ListOfkeywords.trigrams.keySet()){
                    totalwordfrequency+=ListOfkeywords.trigrams.get(s);
                }
                
                totalwordfrequency++;  //WHY THIS??????????????????????????????????????????????????????????????????????????????
       
                HashMap<String,Double> map_value=new HashMap<String,Double>();   
                
                for(String s:ListOfkeywords.words.keySet()){
                    int n=ListOfkeywords.words.get(s)+1;  //WHY " +1"
                    double value=0;
                    
                    value=1-((Math.log10(n)/Math.log10(2))/((Math.log10(totalwordfrequency)/Math.log10(2))));
                    
                    map_value.put(s, value);
                    
                }
                for(String s:ListOfkeywords.bigrams.keySet()){
                    int n=ListOfkeywords.bigrams.get(s);
                    double value=0;
                    
                    value=1-((Math.log10(n)/Math.log10(2))/((Math.log10(totalwordfrequency)/Math.log10(2))));
                    
                     map_value.put(s, value);
                }
                for(String s:ListOfkeywords.trigrams.keySet()){
                    int n=ListOfkeywords.trigrams.get(s);
                    double value=0;
                    
                    value=1-((Math.log10(n)/Math.log10(2))/((Math.log10(totalwordfrequency)/Math.log10(2))));                    
                     map_value.put(s, value);
                }
                    // int removefreq=(int)(0.1*keywordSize); //WHY ?????????????????????????????????????????????????????????????????????????
                int removefreq=0;
                    Map<String,Double> map=SortByValue.sortKeyword(map_value);
                    //System.out.println("Map Value:After sorting");
                           
                            Set set2 = map.entrySet();
                            Iterator iterator2 = set2.iterator();
                            int top=0;
 /////////////////////////////////WHAT IS THIS,BELOW??????????????????????????????????????????????????????????????????????????????                           
                            while(iterator2.hasNext() && top<removefreq) {
                                Map.Entry me2 = (Map.Entry)iterator2.next();                                
                                String eliminate=me2.getKey().toString();
                                
                                if(ListOfkeywords.words.containsKey(eliminate))
                                    ListOfkeywords.words.remove(eliminate);
                                else if(ListOfkeywords.bigrams.containsKey(eliminate))
                                    ListOfkeywords.bigrams.remove(eliminate);
                                else if(ListOfkeywords.trigrams.containsKey(eliminate))
                                    ListOfkeywords.trigrams.remove(eliminate);
                               
                                top++;
                                
                            }             
              }  
                List<form> removeforms = new LinkedList<form>();
                
                for(form myform:forms){
                    
                    boolean present=false;
                    for(String s:myform.words.keySet()){
                        if(ListOfkeywords.words.containsKey(s)){
                            present=true;
                            break;
                        }
                    }
                    
                    if(present == false){
                      removeforms.add(myform);
                    }
                    
                }
                
                for(form myform:removeforms){
                    forms.remove(myform);
                }
		
	}
        
    public static void printwords(String filepath){
        
        File wordlist = new File(filepath+"wordlist.csv");
			
	try{		

	@SuppressWarnings("resource")
	OutputStream os = (OutputStream) new FileOutputStream(wordlist);
	String encoding = "UTF8";
	OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
	BufferedWriter bw = new BufferedWriter(osw);
        
      //  for(form myform:forms){
          for(form myform:forms){

            String words="";
            String bigram="";
            String trigram="";
            
            for(String s:myform.words.keySet()){
                words+=s+"("+myform.words.get(s)+")"+" ";                               
            }
            
            for(String s:myform.bigrams.keySet()){
                bigram+=s+"("+myform.bigrams.get(s)+")"+"/";
            }
            
            for(String s:myform.trigrams.keySet()){
                trigram+=s+"("+myform.trigrams.get(s)+")"+"/";
            }
            
            bw.write(words+","+bigram+","+trigram);
            bw.newLine();
            
          
        }
          
        FileWriter fr=new FileWriter(filepath+"listofkeyword.csv",true);  
          
       // System.out.println("Bigram length"+ListOfkeywords.bigrams.size());
       // System.out.println("Trigram length"+ListOfkeywords.trigrams.size());
        
        fr.write("raja\n");
        
        for(String s:ListOfkeywords.bigrams.keySet())
            fr.write(s+",");
        
        fr.write("\n");
        
        for(String s:ListOfkeywords.trigrams.keySet())
            fr.write(s+",");
        
        fr.flush();
        fr.close();
          
        bw.flush();
        bw.close();
        os.close();
               
        } 
        catch (UnsupportedEncodingException e) {
			System.err.println(e.toString());
	} catch (IOException e) {
			System.err.println(e.toString());
	} catch (Exception e) {
			System.err.println(e.toString());
	}
    }
        
    public static void findKeywords(String filepath,String Size){
        try {
			documentReader(filepath,Size);
			//removeLessIDF();
			ListOfkeywords.removeSmallSize(3);
			//ListOfkeywords.removeSmallFreq(2);
			findBigramTrigram();
			printwords(filepath);
			removeUnwantedKeywords();
			/*analyseForm();
			
			//-- ++ 30-Sep-2016 yeung_chiang remove tickets if length<=3
			filterTickets();
			//-- -- 30-Sep-2016 yeung_chiang remove tickets if length<=3
			convertInList();
			makeInverseFrequency();
			maketermFrequency();
			writeMatrix();*/
		} catch (IOException e) {e.printStackTrace();}
       
    }
    
   public static boolean isInWordNet(String keyword) {
		System.setProperty("wordnet.database.dir", "WordNet\\dict\\");  

		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		Synset[] synsets = database.getSynsets(keyword, SynsetType.NOUN);
		
		return synsets.length > 0;
    }

 public static void removeUnwantedKeywords(){
	 ticketSize=forms.size();
	 tfidfValueUni=new double[ticketSize][ListOfkeywords.words.size()];
	double[][]  termFrequencyUni = new double[ticketSize][ListOfkeywords.words.size()];
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
//////////////////////////////////////////////////////////////////////////////////////////////NEW              
       i=0;
j=0;
  HashSet<String> removeStr=new HashSet<String>();  
   // System.out.println(ListOfkeywords.words.keySet());
     for(String word:ListOfkeywords.words.keySet()){  
     //	System.out.println("WORD"+word);
     	if(idfUni[j]<1 && ListOfkeywords.words.containsKey(word)){ 
     	//	System.out.println(idfUni[j]+"True");
     		removeStr.add(word);
     		}
     	j++;
     }
     Iterator itr=removeStr.iterator();
     while(itr.hasNext()) 	
     {
     	Object p=itr.next();
     ListOfkeywords.words.remove(p);
      for(form myform: forms) 
      {
     	 if(myform.words.containsKey(p)) myform.words.remove(p);
      }
     }
     
    i=0; j=0;
     removeStr=new HashSet<String>();  
       //System.out.println(ListOfkeywords.bigrams.keySet());
        for(String word:ListOfkeywords.bigrams.keySet()){  
        //	System.out.println("WORD"+word);
        	if(idfBi[j]<1 && ListOfkeywords.bigrams.containsKey(word)){ 
        //		System.out.println(idfBi[j]+"True");
        		removeStr.add(word);
        	}
        	j++;
        }
         itr=removeStr.iterator();
         while(itr.hasNext()) 	
         {
         	Object p=itr.next();
         ListOfkeywords.bigrams.remove(p);
          for(form myform: forms) 
          {
         	 if(myform.bigrams.containsKey(p)) myform.bigrams.remove(p);
          }
         }
        
       i=0; j=0;
        removeStr=new HashSet<String>();  
      //    System.out.println(ListOfkeywords.trigrams.keySet());
           for(String word:ListOfkeywords.trigrams.keySet()){  
         //  	System.out.println("WORD"+word);
           	if(idfTri[j]<1 && ListOfkeywords.trigrams.containsKey(word)){ 
           	//	System.out.println(idfTri[j]+"True");
           		removeStr.add(word);
           		}
           	j++;
           }
            itr=removeStr.iterator();
            while(itr.hasNext()) 	
            {
            	Object p=itr.next();
            ListOfkeywords.trigrams.remove(p);
             for(form myform: forms) 
             {
            	 if(myform.trigrams.containsKey(p)) myform.trigrams.remove(p);
             }
            }
      
 }
    
 public static void removeLessIDF()
{
	
		for(String s:ListOfkeywords.words.keySet())
		{
			idf.put(s, 0);
			for(List<CoreLabel> singleform:allForms)
			{
				for(CoreLabel c:singleform)
				{
				if(c.originalText().equals(s))
					idf.put(s, 1);
				}
			}
			
			if(idf.get(s)==0) ListOfkeywords.words.remove(s);
		}	
	}
}
