package clustering;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**   keywords.java
 * As form, keyWords is just a class to store data.
 * It is made to store easily a list of (potential) keywords in a hashmap so we can store the frequency too
 * There are functions to add easily words/bigrams
 * "RemoveSmallSize" can remove small keyword (less than to characters for example)
 * "removeSmallFreq" can remove keyword with too small frequency (less than 5 for example) 
 * @author Fabien_524445
 *
 */


public class keyWords {
		public HashMap<String,Integer> words;
		public HashMap<String,Integer> bigrams;
		public HashMap<String,Integer> trigrams;
                public HashMap<String,Integer> concepts;
		
		public keyWords(HashMap<String,Integer> words,HashMap<String,Integer> bigrams,HashMap<String,Integer> trigrams,HashMap<String,Integer> concepts){
			this.words=words;
			this.bigrams=bigrams;
			this.trigrams=trigrams;
                        this.concepts=concepts;
		}
		
		public keyWords(){
			this.words=new HashMap<String,Integer>();
			this.bigrams=new HashMap<String,Integer>();
			this.trigrams=new HashMap<String,Integer>();
                        this.concepts=new HashMap<String,Integer>();
		}
		
		public void addWord(String w){
			if (words.containsKey(w)){
				words.put(w,words.get(w)+1);
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
		
		/** removeSmallFreq
		 * Remove the keyword of the list if the frequency is less than i
		 * @param i is the minimum frequency to keep the keywords
		 */
		
		public void removeSmallFreq(int i){

			Set<String> wordSet = new HashSet<String>(words.keySet());
			for(String w : wordSet){
				if (words.get(w)<=i){
					words.remove(w);
				}
			}
			
			Set<String> biSet = new HashSet<String>(bigrams.keySet());
			for(String w : biSet){
				if (bigrams.get(w)<=(i/2)){
					bigrams.remove(w);
				}
			}
			
			Set<String> triSet = new HashSet<String>(trigrams.keySet());
			for(String w : triSet){
				if (trigrams.get(w)<=i/3){
					trigrams.remove(w);
				}
			}
		}
		
		/**removeSmallSize
		 * remove words smaller than i characters
		 * @param i
		 */
		
		public void removeSmallSize(int i){
			Set<String> wordSet = new HashSet<String>(words.keySet());
			for(String w : wordSet){
				if (w.length()<=i){
					words.remove(w);
				}
			}
		}
                
                public void clear(){
                    words.clear();
                    bigrams.clear();
                    trigrams.clear();
                    concepts.clear();
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
