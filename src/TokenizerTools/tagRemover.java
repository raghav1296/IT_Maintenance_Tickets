package TokenizerTools;

import java.util.LinkedList;

public class tagRemover {

	public static boolean check(String tag) {

		LinkedList<String> badTag = new LinkedList<String>();
		badTag.add("DT"); // Determiner
		badTag.add("PRP$"); // PRP$ Possessive pronoun 
		badTag.add("PRP"); // PRP Personal pronoun 			
		badTag.add("WRB"); 	// WRB Wh¬adverb
		badTag.add("CC"); // Coordinating conjunction
		badTag.add("CD"); // Cardinal number
		badTag.add("IN"); // IN Preposition or subordinating conjunction 
		badTag.add("MD"); // Modal
		badTag.add("POS"); // 	POS Possessive ending 
		badTag.add("RB");// RB Adverb 
		badTag.add("RBR");// RBR Adverb, comparative 
		badTag.add("RBS");// RBS Adverb, superlative 
		badTag.add("RP");// RP Particle 
		badTag.add("SYM");// SYM Symbol 
		badTag.add("TO");// TO to 
		badTag.add("UH"); // UH Interjection
		badTag.add("WDT"); //WDT Wh¬determiner 
		badTag.add("WP"); //WP Wh¬pronoun 
		badTag.add("WP$"); //WP$ Possessive wh¬pronoun 
		badTag.add("NNP");// NNP Proper noun, singular

		// punctuation
		badTag.add(",");
		badTag.add(".");
		badTag.add(":");
		badTag.add("%");
		badTag.add("'");

		/*  not removed tags  :
		 	EX Existential there ---> what is this ?
			FW Foreign word 
			IN Preposition or subordinating conjunction 
			JJ Adjective 
			JJR Adjective, comparative 
			JJS Adjective, superlative 
			LS List item marker ---> what is this ?
			NN Noun, singular or mass 
			NNS Noun, plural 
			NNP Proper noun, singular 
			NNPS Proper noun, plural 
			PDT Predeterminer ---> what is this ?
			VB Verb, base form 
			VBD Verb, past tense 
			VBG Verb, gerund or present participle 
			VBN Verb, past participle 
			VBP Verb, non¬3rd person singular present 
			VBZ Verb, 3rd person singular present 
		 */



		for (String s : badTag){
			if (tag.startsWith(s)){
				return false;
			}	
		}
		return true;
	}

	
	
	
}
