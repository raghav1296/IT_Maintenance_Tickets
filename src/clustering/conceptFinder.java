package clustering;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
/** conceptFinder.java
 * This class create :
 * 		- first_concepts_manu.csv the list of the potential concepts from unigram keywords (need manual selection)
 * 		- bigram_trigram_concepts_manu.csv the list of the potential bigram and trigram concepts (need manual selection)
 * 
 * Used document :
 * 		- keyWords_manu.csv list of unigram keywords (created at keywordFinder.java)
 * 		- listKeywordsConcepts.csv result of manual selection of first_concepts_manu.csv
 * 		- BigramTrigramList_manu.csv list of bigram and trigram keywords (created at keywordFinder.java) 
 * 
 * 
 * Library used :
 * 		- edu.smu.tspell.wordnet to use database of wordnet
 * 
 * 
 * @author aeran.TRN
 *
 */
public class conceptFinder {
	/* global variables */
	static HashSet<String> unigramKeywords;
	static HashSet<String> unigramConcepts;
	static HashSet<String> bigramTrigramKeywords;
	static HashSet<String> bigramTrigramConcepts;
	static HashSet<String> baseList;
        static HashMap<String , HashSet<String>> keywordConcept=new HashMap<String , HashSet<String>>();
        static HashMap<String , HashSet<String>> conceptKeyword=new HashMap<String , HashSet<String>>();
        static HashMap<String , HashSet<String>> keywordHyper=new HashMap<String , HashSet<String>>();
        static HashMap<String , HashSet<String>> hyperKeyword=new HashMap<String , HashSet<String>>();
               
	static String filePath = "Keywords.csv";
        static List<form> forms;	
        static HashSet<String> stopWords;
	/** findUnigramConcepts 
	 * 
	 */
        
        public static void readStopwords(){
            
            stopWords=readtickets.stopWords;
            
        }
        
	public static void findUnigramConcepts(String path) {
		try {
                        bigramTrigramKeywords= new HashSet<String>();
                        //unigramConcepts=new HashSet<String>();
                    
             readStopwords();
			readKeywordFile();
			findUnigramConceptsBySC(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/** readKeywordFile
	 *  read unigram keywords file
	 *  and store unigram keywords in unigramKeywords hashset.
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	public static void readKeywordFile() throws FileNotFoundException {
		unigramKeywords = new HashSet<String>();
		/*Scanner s = new Scanner(new File("unigram_keywords_after_jaccard.csv"));
		String str = "";
		while (s.hasNextLine()) {
			str = s.nextLine();
			unigramKeywords.add(str);
		}
		s.close();*/
                
                for(String s:KeywordExtraction.ListOfkeywords.words.keySet()){
                    unigramKeywords.add(s);
                }
                System.out.println("Terms-Keywords"+ KeywordExtraction.ListOfkeywords.words.size());
	}	
	/** findUnigramConceptBySC
	 *  find candidates for concepts from unigram keyword
	 *  by adding more words from WordNet(3 lowest sense count).
	 *  write csv file containing the candidates
	 *  need to be selected manually later
	 * @throws IOException
	 */
	public static void findUnigramConceptsBySC(String path) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(path+"first_concepts_need_manual_inspection.csv"));
		//br.write("keyword,concept1,concept2,concept3\n");
		System.setProperty("wordnet.database.dir", "WordNet\\dict\\"); 		
		//HashSet<String> concepts_candidates = new HashSet<String>();
		HashSet<String> hyper_candidates = new HashSet<String>();
		
		for(String keyword : unigramKeywords) {
			NounSynset nounSynset,nounHyperSet,nounHypoSet; 
			int first_min = 100;
			String first_concept = "";
                        String first_btconcept ="";
			String first_hyper="";
			String first_bthyper="";
			int second_min = 100;
			String second_concept = "";
			String second_hyper="";
			String second_btconcept = "";
			String second_bthyper="";
			int third_min = 100;
			String third_concept = "";
			String third_hyper="";
			String third_btconcept = "";
			String third_bthyper="";
			
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			Synset[] synsets = database.getSynsets(keyword, SynsetType.NOUN); 
			HashSet<String> uniConceptSet=new HashSet<String>();
			HashSet<String> uniHyperSet=new HashSet<String>();
			
                        HashSet<String> bitriConceptSet=new HashSet<String>();
			HashSet<String> bitriHyperSet=new HashSet<String>();
			
                        
			for (int i = 0; i < synsets.length; i++)
			{
				nounSynset = (NounSynset)(synsets[i]);
				for(int j=0;j<nounSynset.getWordForms().length;j++)
					{
                                            
                                            String concept=nounSynset.getWordForms()[j].trim();
                                            
                                            if(concept.equalsIgnoreCase(keyword))
                                                continue;
                                            
                                            if(concept.split(" ").length == 1)
						uniConceptSet.add(concept);
                                            else if(concept.split(" ").length > 1)
                                                bitriConceptSet.add(concept);
                                            
                                            //System.out.println(concept);
                                            
					}
                                
                                for(int k=0;k<nounSynset.getHypernyms().length;k++)
					{
						nounHyperSet=(NounSynset)nounSynset.getHypernyms()[k];
						  	for(int p=0;p<nounHyperSet.getWordForms().length;p++)
						  	{
                                                           
                                                            String hypername=nounHyperSet.getWordForms()[p].trim();
						  	
                                                            if(hypername.equalsIgnoreCase(keyword))
                                                                continue;
                                                            
                                                            if(hypername.split(" ").length == 1)
                                                                uniHyperSet.add(hypername);
                                                            else if(hypername.split(" ").length > 1)
                                                                bitriHyperSet.add(hypername);
                                                            // System.out.println(hypername);
						  	}
                                                        
                                                       
					}
			}
                        
                    
                        //finding out least three unigram concept 
                    
                    
			Iterator concept1=uniConceptSet.iterator();
			while(concept1.hasNext()) { 
			//   System.out.println(keyword+"{KEYWORD}"+synsets+"{SYNSETS}"+ synsets.length+"{SENSECOUNT}"+nounSynset+"{NOUNSYNSET}"+nounSynset.getWordForms()+  "{nounSynset.getWordForms()}"+nounSynset.getWordForms().length+"{nounSynset.getWordForms().length}" +nounSynset.getWordForms()[0]+"{nounSynset.getWordForms()[0]}" +nounSynset.getWordForms()[1]+"{nounSynset.getWordForms()[1]}"+concept+"{CONCEPT}");			  			    
			   String current_concept=(String)concept1.next();
				int sc = database.getSynsets(current_concept).length;
			    if(sc<first_min) {
			    	first_min = sc;
                                third_concept = second_concept;
                                second_concept=first_concept;
                                first_concept = current_concept;
			    }
			    else if (sc<second_min) {
			    	second_min = sc;
                                third_concept = second_concept;
                                second_concept = current_concept;
			    }
			    else if (sc<third_min) {
			    	third_min = sc;
			    	third_concept = current_concept;
			    }
			}
			
                        first_min=100;
                        second_min=100;
                        third_min=100;
                        
                        //finding out least three bitrigram concept 
                    
                        
                        
                        Iterator concept2=bitriConceptSet.iterator();
			while(concept2.hasNext()) { 
			   String current_concept=(String)concept2.next();
			   
                           int sc = database.getSynsets(current_concept).length;
			   
                            if(sc<first_min) {
			    	first_min = sc;
                                third_btconcept = second_btconcept;
                                second_btconcept=first_btconcept;
                                first_btconcept = current_concept;
			    }
			    else if (sc<second_min) {
			    	second_min = sc;
                                third_btconcept = second_btconcept;
                                second_btconcept = current_concept;
			    }
			    else if (sc<third_min) {
			    	third_min = sc;
			    	third_btconcept = current_concept;
			    }
			}
                        
                        //finding out least three unigram Hypernyms 
                    
                        
                        first_min=100;
                        second_min=100;
                        third_min=100;
                        
			Iterator hyper1=uniHyperSet.iterator();
			while(hyper1.hasNext()) { 
			//   System.out.println(keyword+"{KEYWORD}"+synsets+"{SYNSETS}"+ synsets.length+"{SENSECOUNT}"+nounSynset+"{NOUNSYNSET}"+nounSynset.getWordForms()+  "{nounSynset.getWordForms()}"+nounSynset.getWordForms().length+"{nounSynset.getWordForms().length}" +nounSynset.getWordForms()[0]+"{nounSynset.getWordForms()[0]}" +nounSynset.getWordForms()[1]+"{nounSynset.getWordForms()[1]}"+concept+"{CONCEPT}");			  			    
			   String current_hyper=(String)hyper1.next();
				int sc = database.getSynsets(current_hyper).length;
			    if(sc<first_min) {
			    	first_min = sc;
                                third_hyper = second_hyper;
                                second_hyper=first_hyper;
                                first_hyper = current_hyper;
			    }
			    else if (sc<second_min) {
			    	second_min = sc;
                                third_hyper = second_hyper;
                                second_hyper = current_hyper;
			    }
			    else if (sc<third_min) {
			    	third_min = sc;
			    	third_hyper = current_hyper;
			    }
			}
			  
			//System.out.println(keyword+"{KEYWORD}"+first_concept+"{first_concept}"+ second_concept+"{second_concept}"+third_concept+"{third_concept}"+first_hyper+  "{first_hyper}"+second_hyper+"{second_hyper}" +third_hyper+"{third_hyper}");			  			    

                        first_min=100;
                        second_min=100;
                        third_min=100;
                        
			Iterator hyper2=bitriHyperSet.iterator();
			while(hyper2.hasNext()) { 
			//   System.out.println(keyword+"{KEYWORD}"+synsets+"{SYNSETS}"+ synsets.length+"{SENSECOUNT}"+nounSynset+"{NOUNSYNSET}"+nounSynset.getWordForms()+  "{nounSynset.getWordForms()}"+nounSynset.getWordForms().length+"{nounSynset.getWordForms().length}" +nounSynset.getWordForms()[0]+"{nounSynset.getWordForms()[0]}" +nounSynset.getWordForms()[1]+"{nounSynset.getWordForms()[1]}"+concept+"{CONCEPT}");			  			    
			   String current_hyper=(String)hyper2.next();
				int sc = database.getSynsets(current_hyper).length;
			    if(sc<first_min) {
			    	first_min = sc;
                                third_bthyper = second_bthyper;
                                second_bthyper=first_bthyper;
                                first_bthyper = current_hyper;
			    }
			    else if (sc<second_min) {
			    	second_min = sc;
                                third_bthyper = second_bthyper;
                                second_bthyper = current_hyper;
			    }
			    else if (sc<third_min) {
			    	third_min = sc;
			    	third_bthyper = current_hyper;
			    }
			}                        
                        
                        
                        first_concept=removeStopwords(first_concept);//?? whether it returns unigram words
                        second_concept=removeStopwords(second_concept);
                        third_concept=removeStopwords(third_concept);
                         
                        first_hyper=removeStopwords(first_hyper);
                        second_hyper=removeStopwords(second_hyper);
                        third_hyper=removeStopwords(third_hyper);
                        
                        first_btconcept=removeStopwords(first_btconcept);//?? whether it returns unigram words
                        second_btconcept=removeStopwords(second_btconcept);
                        third_btconcept=removeStopwords(third_btconcept);
                         
                        first_bthyper=removeStopwords(first_bthyper);
                        second_bthyper=removeStopwords(second_bthyper);
                        third_bthyper=removeStopwords(third_bthyper);

			String s=keyword;
		//Writing Concepts to file
			//s+=","+"{CONCEPTS}";
			
			//concepts_candidates.add(first_concept);         
                        //System.out.println("concepts");
                        //System.out.println(keyword);
                        //System.out.println(first_concept);
                       // System.out.println(second_concept);
                       // System.out.println(third_concept);     
                       
                       HashSet<String> concept=new HashSet<String>();
                       
                        if(!first_concept.equals("")){ 
                            s+=","+first_concept;
                            concept.add(first_concept);
                            if(conceptKeyword.containsKey(first_concept)){
                                conceptKeyword.get(first_concept).add(keyword);
                            }
                            else{
                                HashSet<String> conkey=new HashSet<String>();
                                conkey.add(keyword);
                                conceptKeyword.put(first_concept, conkey);
                            }
                        }
			if(!second_concept.equals("")) {
				s+=","+second_concept;
				//concepts_candidates.add(second_concept);
                                concept.add(second_concept);
                                if(conceptKeyword.containsKey(second_concept)){
                                    conceptKeyword.get(second_concept).add(keyword);
                                }
                                else{
                                    HashSet<String> conkey=new HashSet<String>();
                                    conkey.add(keyword);
                                    conceptKeyword.put(second_concept, conkey);
                                }                       
			}
			if(!third_concept.equals("")) {
				s+=","+third_concept;
				//concepts_candidates.add(third_concept);
                                concept.add(third_concept);
                                if(conceptKeyword.containsKey(third_concept)){
                                    conceptKeyword.get(third_concept).add(keyword);
                                }
                                else{
                                    HashSet<String> conkey=new HashSet<String>();
                                    conkey.add(keyword);
                                    conceptKeyword.put(third_concept, conkey);
                                }
			}
			/*if(concept != null){
                            keywordConcept.put(keyword, concept);
                        }*/
                        
                        
                        
                        
                        if(!first_btconcept.equals("")){
                            
                            bigramTrigramKeywords.add(first_btconcept);
                            
                           // System.out.println(first_btconcept+":"+first_btconcept.length());
                            
                            concept.add(first_btconcept);
                            
                            if(conceptKeyword.containsKey(first_btconcept)){
                                conceptKeyword.get(first_btconcept).add(keyword);
                            }
                            else{
                                HashSet<String> conkey=new HashSet<String>();
                                conkey.add(keyword);
                                conceptKeyword.put(first_btconcept, conkey);
                            }
                        }
			if(!second_btconcept.equals("")) {
                            
                            bigramTrigramKeywords.add(second_btconcept);
                            concept.add(second_btconcept);
                            
                            //System.out.println(second_btconcept+":"+second_btconcept.length());
                            
                            
                            if(conceptKeyword.containsKey(second_btconcept)){
                                    conceptKeyword.get(second_btconcept).add(keyword);
                                }
                                else{
                                    HashSet<String> conkey=new HashSet<String>();
                                    conkey.add(keyword);
                                    conceptKeyword.put(second_btconcept, conkey);
                                }                       
			}
			if(!third_btconcept.equals("")) {
			
                                 bigramTrigramKeywords.add(third_btconcept);
                                 concept.add(third_btconcept);
                            
                                 //System.out.println(third_btconcept+":"+third_btconcept.length());
                            
                                 
                                if(conceptKeyword.containsKey(third_btconcept)){
                                    conceptKeyword.get(third_btconcept).add(keyword);
                                }
                                else{
                                    HashSet<String> conkey=new HashSet<String>();
                                    conkey.add(keyword);
                                    conceptKeyword.put(third_btconcept, conkey);
                                }
			}
                        if(concept != null){
                            keywordConcept.put(keyword, concept);
                        }

                  //      s+=","+synsets.length+","; //uncomment this to print number of synsets of particular keyword
                        
//Writing Hypernyms(Parents) to file
			
			//hyper_candidates.add(first_hyper);         
                        //System.out.println("concepts");
                        //System.out.println(keyword);
                        //System.out.println(first_concept);
                       // System.out.println(second_concept);
                       // System.out.println(third_concept); 
                       
                       HashSet<String> hyper=new HashSet<String>();
                       
                        if(!first_hyper.equals("")){
                            s+=","+first_hyper;
                        	hyper.add(first_hyper);
                            if(hyperKeyword.containsKey(first_hyper)){
                            	hyperKeyword.get(first_hyper).add(keyword);
                            }
                            else{
                                HashSet<String> hypkey=new HashSet<String>();
                                hypkey.add(keyword);
                                hyperKeyword.put(first_hyper,hypkey);
                            }
                        }    			
			if(!second_hyper.equals("")) {
				s+=","+second_hyper;
				//hyper_candidates.add(second_hyper);
				               hyper.add(second_hyper);
                                if(hyperKeyword.containsKey(second_hyper)){
                                	hyperKeyword.get(second_hyper).add(keyword);
                                }
                                else{
                                    HashSet<String> hypkey=new HashSet<String>();
                                    hypkey.add(keyword);
                                    hyperKeyword.put(second_hyper, hypkey);
                                }                       
			}
			if(!third_hyper.equals("")) {
				s+=","+third_hyper;
				//hyper_candidates.add(third_hyper);
								hyper.add(third_hyper);
                                if(hyperKeyword.containsKey(third_hyper)){
                                	hyperKeyword.get(third_hyper).add(keyword);
                                }
                                else{
                                    HashSet<String> hypkey=new HashSet<String>();
                                    hypkey.add(keyword);
                                    hyperKeyword.put(third_hyper, hypkey);
                                }
			}
			/*if(hyper != null){
                            keywordHyper.put(keyword, hyper);
                        } */               
           //  Go to new Line     
           
                        if(!first_bthyper.equals("")){                       
                        
                             bigramTrigramKeywords.add(first_bthyper);
                             hyper.add(first_bthyper);
                            
                            // System.out.println(first_bthyper+":"+first_bthyper.length());
                            
                             
                            if(hyperKeyword.containsKey(first_bthyper)){
                            	hyperKeyword.get(first_bthyper).add(keyword);
                            }
                            else{
                                HashSet<String> hypkey=new HashSet<String>();
                                hypkey.add(keyword);
                                hyperKeyword.put(first_bthyper,hypkey);
                            }
                        }    			
			if(!second_bthyper.equals("")) {
				
                                 bigramTrigramKeywords.add(second_bthyper);
                                 hyper.add(second_bthyper);
                            //System.out.println(second_bthyper+":"+second_bthyper.length());
                            
                                if(hyperKeyword.containsKey(second_bthyper)){
                                	hyperKeyword.get(second_bthyper).add(keyword);
                                }
                                else{
                                    HashSet<String> hypkey=new HashSet<String>();
                                    hypkey.add(keyword);
                                    hyperKeyword.put(second_bthyper, hypkey);
                                }                       
			}
			if(!third_bthyper.equals("")) {
				
                                 bigramTrigramKeywords.add(third_bthyper);
                                 hyper.add(third_bthyper);
                            
                                // System.out.println(third_bthyper+":"+third_bthyper.length());
                            
                                 
                                if(hyperKeyword.containsKey(third_bthyper)){
                                	hyperKeyword.get(third_bthyper).add(keyword);
                                }
                                else{
                                    HashSet<String> hypkey=new HashSet<String>();
                                    hypkey.add(keyword);
                                    hyperKeyword.put(third_bthyper, hypkey);
                                }
			}
                        if(hyper != null){
                            keywordHyper.put(keyword, hyper);
                        }
           
           
			s+='\n';
			br.write(s);
		}
		br.close();	
		//System.out.println(concepts_candidates.size());
	}
	/** findBigramTrigramConcepts
	 *  find bigram and trigram concepts
	 *  first, select concepts from existing bigram and trigram keywords by WNSCA
	 *  then select more concepts by PE
	 */
	public static void findBigramTrigramConcepts(String path) {
		try {  
			/*
			INITIAL ORDER: 
			readUnigramConceptsFile(path);
			readBigramTrigramKeywordsFile();
			findBigramTrigramConceptsByWNSCA();
			writeBigramTrigramConcepts(path+"bigram_trigram_concepts_before_PE.csv");
			makeBaseList();
			populateConceptsByPE();
			writeBigramTrigramConcepts(path+"bigram_trigram_concepts_after_PE.csv");
		 */
			readBigramTrigramKeywordsFile(path); //System.out.println("inside 1");
                        readUnigramConceptsFile(path); // System.out.println("inside 2");
			findBigramTrigramConceptsByWNSCA();  //System.out.println("inside 3");
			writeBigramTrigramConcepts(path+"bigram_trigram_concepts_before_PE.csv"); // System.out.println("inside 4");
			makeBaseList(); // System.out.println("inside 5");
			populateConceptsByPE();  //System.out.println("inside 6");
			writeBigramTrigramConcepts(path+"bigram_trigram_concepts_after_PE.csv"); // System.out.println("inside 7");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/** readUnigramConceptsFile
	 *  read manually selected concepts file
	 *  and store them in unigramConcepts
	 *  and write file("unigram_mapping.csv") containing the mapping relation
	 * @throws IOException 
	 */
	public static void readUnigramConceptsFile(String path) throws IOException {
		unigramConcepts = new HashSet<String>();
		BufferedWriter br = new BufferedWriter(new FileWriter(path+"unigram_mapping.csv"));
		//Scanner s = new Scanner(new File("dataset/backup/listKeywordsConcepts.csv"));
		//Scanner s = new Scanner(new File("listKeywordsConcepts.csv"));
		Scanner s = new Scanner(new File(path+"first_concepts_need_manual_inspection.csv"));
		String str = "";
		//s.nextLine();
		while (s.hasNextLine()) {
			str = s.nextLine();
			String[] token = str.split(",");
			//NEW
			  token=tokenClean(token);             
              //NEW
			//if(token.length > 3) { //JY changed to 1 so that it takes all concepts obtained from wordnet, for manual inspection change back to 3
			if(token.length > 1) { //removing words which has no meanings/no hypernyms/no synonyms(concepts)
				//for(int i=4; i<token.length; i++) {
				br.write(token[0]+",");
				for(int i=1; i<token.length; i++) {
				
					//NEW BEGIN
		if(token[i].split(" ").length>1)
						{
							bigramTrigramKeywords.add(token[i]);
						}
		else if(token[i].trim().length()>1)
		{
		br.write(token[i]+",");
		unigramConcepts.add(token[i]);
		}
					//NEW END
				}
				br.write("\n");
			}
		}
		br.close();
		s.close();
	}	
	private static String[] tokenClean(String[] token) {
		// TODO Auto-generated method stub'
        File stopWordsFile = new File("stopwords.txt");
        BufferedReader in;
		try {
			 String line;
				/* make stop words hash set */
			HashSet<String> stopWords = new HashSet<String>();
			in = new BufferedReader(new FileReader(stopWordsFile));
			while ((line = in.readLine()) != null) {
				stopWords.add(line);
			} 
				 for(int i=1;i<token.length;i++) 
				 {
		       	  token[i]=token[i].toLowerCase();   
		       	  String tokenWord="";
		       	  String[] tokenStrings=token[i].split(" ");
		       	  //HashSet<String> tokenStringSet=new HashSet<String>();
				// if(token[i].contains("stopwords.txt")) token[i].);
		       	  for(String  word:tokenStrings)
		       	  		{
		       	 if(!stopWords.contains(word)) tokenWord+=word+" ";
		       	  		}
		       	  token[i]=tokenWord.trim();
				 }
				 in.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

		return token;
	}
	/** readBigramTrigramKeywordsFile
	 * read bigram and trigram keywords file
	 * and store them in bigramTrigramKeywords
	 * @throws FileNotFoundException
	 */
	public static void readBigramTrigramKeywordsFile(String path) throws FileNotFoundException {
		//bigramTrigramKeywords = new HashSet<String>();
		/*Scanner s = new Scanner(new File("BigramTrigramList.csv"));
		String str = "";
		while (s.hasNextLine()) {
			str = s.nextLine();
			bigramTrigramKeywords.add(str);
		}
		s.close();*/
                for(String s:KeywordExtraction.ListOfkeywords.bigrams.keySet()){
                    bigramTrigramKeywords.add(s);
                   // System.out.println(s+":"+s.length());
                            
                }
                for(String s:KeywordExtraction.ListOfkeywords.trigrams.keySet()){
                    bigramTrigramKeywords.add(s);
                   // System.out.println(s+":"+s.length());
                            
                }
                
               try{
                
                FileWriter fw=new FileWriter(path+"bitrikeywordws after uni concepts.csv",true);
                
                Iterator itr=bigramTrigramKeywords.iterator();
                
                while(itr.hasNext())
                    fw.write(itr.next()+"\n");
                
                
                fw.flush();
                fw.close();
                
               }
               catch(IOException e){
                   e.printStackTrace();
               }
	}	
	/** findBigramTrigramConceptsByWNSCA
	 * calculate keyword's sense count 
	 * and if it is under sense count threshold, 
	 * store the keyword in bigramTrigramConcepts 
	 */
	public static void findBigramTrigramConceptsByWNSCA() {
		bigramTrigramConcepts = new HashSet<String>();
		int i;
		String rightmost = "";
		int sct = 3; // sense count threshold 
		for(String keyword : bigramTrigramKeywords) {
			String[] words = keyword.split(" ");
			for(i=0; i<words.length; i++){
				rightmost = rightMost(words, i);
				if(KeywordExtraction.isInWordNet(rightmost)) {
					break;
				}
			}
			if(WNSCA(rightmost, i, sct)) {
				if(keyword.split(" ").length>1)
				bigramTrigramConcepts.add(keyword);
			}
		}
	}	
	/** WNSCA
	 * return true if sense count of a string is under sense count threshold
	 * @param rightmost
	 * @param i
	 * @param sct
	 * @return
	 */
	public static boolean WNSCA(String rightmost, int i, int sct) {
		System.setProperty("wordnet.database.dir", "WordNet\\dict\\"); 
		WordNetDatabase database = WordNetDatabase.getFileInstance(); 
		
		int rightmost_sc = database.getSynsets(rightmost, SynsetType.NOUN).length; 
		int sc = rightmost_sc/(i+1);
		
		if(sc<sct) return true;
		else return false;
	}	
	/** rightMost
	 * return rightmost term from index i
	 * @param words : tokenized keyword
	 * @param i 
	 * @return
	 */
	public static String rightMost(String[] words, int i) {
		int j;
		String rightmost = "";
		for(j=words.length-1; j>i-1; j--) {
			rightmost = words[j] + " " + rightmost;
		}
		return rightmost.trim();
	}	
	/** makeBaseList
	 * make base list from bigram and trigram concepts for PE
	 */
	public static void makeBaseList() {
		baseList = new HashSet<String>();
		
		/* add base from unigram concepts */
		for(String concept : unigramConcepts) {
			String[] token = concept.split(" ");
			String base = rightMost(token, token.length-1);
			baseList.add(base);
		}
		
		/* add base from bigram and trigram concepts */
		for(String concept : bigramTrigramConcepts) {
			String[] token = concept.split(" ");
			String base = rightMost(token, token.length-1);
			baseList.add(base);
		}
	}	
	/** populateConceptsByPE
	 *  select more concepts by PE method
	 */
	public static void populateConceptsByPE() {
		for(String keyword : bigramTrigramKeywords) {
			String[] token = keyword.split(" ");
			String headWord = rightMost(token, token.length-1);
			if(baseList.contains(headWord)) {
				if(keyword.split(" ").length>1)
				bigramTrigramConcepts.add(keyword);
			}
		}
	}	
	/** writeBigramTrigramConcepts
	 * write csv file containing bigram and trigram concepts
	 * @throws IOException
	 */
	public static void writeBigramTrigramConcepts(String fileName) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		for(String concept : bigramTrigramConcepts) {
			br.write(concept+"\n");
		}				
		br.close();
	}	
	public static void addConceptToTicket(){
            forms=KeywordExtraction.forms;
            for(form myform:forms){
                for(String s:myform.words.keySet()){
                    if(keywordConcept.containsKey(s)){  
                        HashSet<String> concept=keywordConcept.get(s);
                        for(String con:concept){
                            if(unigramConcepts.contains(con) || bigramTrigramConcepts.contains(con))
                                myform.addConcepts(con);
                        }
                    }
                    if(keywordHyper.containsKey(s)){  
                        HashSet<String> hyper=keywordHyper.get(s);
                        for(String hyp:hyper){
                            if(unigramConcepts.contains(hyp) || bigramTrigramConcepts.contains(hyp)) 
                                myform.addConcepts(hyp);
                        }
                    }
                    
                    
                }
                for(String s:myform.bigrams.keySet()){
                    if( bigramTrigramConcepts.contains(s)){
                         myform.addConcepts(s);
                    }
                }
                for(String s:myform.trigrams.keySet()){
                    if( bigramTrigramConcepts.contains(s)){
                         myform.addConcepts(s);
                    }
                }
            }
        }	
	public static void addHyperToTicket(){
        forms=KeywordExtraction.forms;
        for(form myform:forms){
            for(String s:myform.words.keySet()){
                if(keywordHyper.containsKey(s)){  
                    HashSet<String> hyper=keywordHyper.get(s);
                    for(String hyp:hyper){
                        myform.addConcepts(hyp);
                    }
                }
            }
        }
	}
    
public static void removeUnwantedKeywords()
    {
    	  //////////////////////////////////////////////////////////////////////////////////////////////NEW              
        /*       i=0;
               j=0;
                 HashSet<String> removeStr=new HashSet<String>();  
                   System.out.println(ListOfkeywords.words.keySet());
                    for(String word:ListOfkeywords.words.keySet()){  
                    	System.out.println("WORD"+word);
                    	if(idfUni[j]<2 && ListOfkeywords.words.containsKey(word)){ 
                    		System.out.println(idfUni[j]+"True");
                    		removeStr.add(word);
                    		}
                    	else{  
                    		idfUni[i]=idfUni[j];
                    		for(int k=0;k<ticketSize;k++)	termFrequencyUni[k][i]=termFrequencyUni[k][j];
                    		i++; }
                    	j++;
                    }
                    Iterator itr=removeStr.iterator();
                    while(itr.hasNext()) 	ListOfkeywords.words.remove(itr.next());
                    
                   i=0; j=0;
                    removeStr=new HashSet<String>();  
                      System.out.println(ListOfkeywords.bigrams.keySet());
                       for(String word:ListOfkeywords.bigrams.keySet()){  
                       	System.out.println("WORD"+word);
                       	if(idfBi[j]<2 && ListOfkeywords.bigrams.containsKey(word)){ 
                       		System.out.println(idfBi[j]+"True");
                       		removeStr.add(word);
                       		}
                       	else{ 
                       		idfBi[i]=idfBi[j]; 
                    		for(int k=0;k<ticketSize;k++)	termFrequencyBi[k][i]=termFrequencyBi[k][j];
                       		i++; }
                       	j++;
                       }
                        itr=removeStr.iterator();
                       while(itr.hasNext()) 	ListOfkeywords.bigrams.remove(itr.next());
                       
                      i=0; j=0;
                       removeStr=new HashSet<String>();  
                         System.out.println(ListOfkeywords.trigrams.keySet());
                          for(String word:ListOfkeywords.trigrams.keySet()){  
                          	System.out.println("WORD"+word);
                          	if(idfTri[j]<2 && ListOfkeywords.trigrams.containsKey(word)){ 
                          		System.out.println(idfTri[j]+"True");
                          		removeStr.add(word);
                          		}
                          	else{ 
                          		idfTri[i]=idfTri[j]; 
                        		for(int k=0;k<ticketSize;k++)	termFrequencyTri[k][i]=termFrequencyTri[k][j];
                          		i++; }
                          	j++;
                          }
                           itr=removeStr.iterator();
                          while(itr.hasNext()) 	ListOfkeywords.trigrams.remove(itr.next());
                    
        
      /////////////////////////////////////////////////////////////////////////////////////////////
    }
       /*       	   for(String biword:KeywordExtraction.ListOfkeywords.bigrams.keySet()){
                	   present=0;
                	   for(form myform: forms){   
                		   for(String s:myform.bigrams.keySet()){
                			   if(biword.equalsIgnoreCase(s)) present=present+1;
                		   }
                	   }
                	   j++;
                	   if(present<2) KeywordExtraction.ListOfkeywords.bigrams.remove(biword);
            	   }
                	   
                	   for(String triword:KeywordExtraction.ListOfkeywords.trigrams.keySet()){
                    	   present=0;
                    	   for(form myform: forms){   
                    		   for(String s:myform.trigrams.keySet()){
                    			   if(triword.equalsIgnoreCase(s)) present=present+1;
                    		   }
                    	   }
                    	   j++;
                    	   if(present<2) KeywordExtraction.ListOfkeywords.trigrams.remove(triword);
               }	
   System.out.println("Total Keywords"+j);
   */
    }
  
    public static String removeStopwords(String word){
            
            
            //System.out.println("Before:"+word);
            
            String[] words=word.split(" ");
            String keyword="";
            
            for(String w:words){
                if(! stopWords.contains(w))
                    keyword+=w+" ";
            }
            
            keyword=keyword.trim().toLowerCase();
           // System.out.println("After:"+word);
            return keyword;
        }
    

    public static void main(String[] args) {
		/*try {
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		findUnigramConcepts();
//		findBigramTrigramConcepts();*/
	}
}
