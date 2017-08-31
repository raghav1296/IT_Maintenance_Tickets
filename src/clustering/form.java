package clustering;
import java.util.HashMap;


/** form.java
 *  this is a class to store a free form in a ticket
 *  the hashmap "words" contains all the keywords/potential keywords 
 *  of the tickets with the frequency in this ticket
 *  the hashmap "bigranms" is the same thing with bigrams
 *  addWord is a fucntion to add easily a new word in the Hashmap
 * @author Fabien_524445
 *
 */


public class form {
	
		 	public HashMap<String,Integer> words;
			public HashMap<String,Integer> bigrams;
			public HashMap<String,Integer> trigrams;
			 public HashMap<String,Integer> concepts;
                 
		public form(HashMap<String,Integer> words,HashMap<String,Integer> bigrams,HashMap<String,Integer> trigrams,HashMap<String,Integer> concepts){
			this.words=words;
			this.bigrams=bigrams;
			this.trigrams=trigrams;
            this.concepts=concepts;
		}
		
		public form(){
			this.words=new HashMap<String,Integer>();
			this.bigrams=new HashMap<String,Integer>();
			this.trigrams=new HashMap<String,Integer>();
			this.concepts=new HashMap<String,Integer>();
		}
		
		public void addWord(String w){
			if (words.containsKey(w))
			{
				words.put(w,words.get(w)+1);
			//	System.out.println(words.get(w)+"hey");
			}
			else {
				words.put(w,1);
				
			}
		}
		
		public void addBigrams(String w){
			if (bigrams.containsKey(w)){
				bigrams.put(w,bigrams.get(w)+1);
			}
			else {
				bigrams.put(w,1);
			}
		}
		
		public void addTrigrams(String w){
			if (trigrams.containsKey(w)){
				trigrams.put(w,trigrams.get(w)+1);
			}
			else {
				trigrams.put(w,1);
			}
		}
		
                public void addConcepts(String w){
                    if(concepts.containsKey(w)){
                        concepts.put(w, concepts.get(w) );
                    }
                    else{
                        concepts.put(w, 1);
                    }
                }
                
		public void print(){
			System.out.println(words);
			System.out.println(bigrams);
			System.out.println(trigrams);
		}
                
                
                 public HashMap<String,Integer> getWords(){
                    return words;
                }
                
                public HashMap<String,Integer> getBigram(){
                    return bigrams;
                }
                 
                public HashMap<String,Integer> getTrigram(){
                    return trigrams;
                }
                 public HashMap<String,Integer> getConcepts(){
                    return concepts;
                }
}
