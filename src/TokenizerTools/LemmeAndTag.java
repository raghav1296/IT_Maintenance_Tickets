package TokenizerTools;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class LemmeAndTag {

	protected StanfordCoreNLP pipeline;
	Properties props;

	public LemmeAndTag() {
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}
	
	public void reset(){
		this.pipeline = new StanfordCoreNLP(props);
	}

	public List<CoreLabel> lemmatize(String documentText) {

		Annotation document = new Annotation(documentText);

		this.pipeline.annotate(document);
		List<CoreLabel> myList = new LinkedList<CoreLabel>();
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			myList.addAll(sentence.get(TokensAnnotation.class));
			//for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				//myList.add(token);
			//}
			// Iterate over all tokens in ae sentence
			/*for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
					System.out.println("word: "+token.value()+"; lemme: "+token.lemma()+"; tag: "+token.tag());
			}*/
		}
		//pipeline.clearAnnotatorPool();
		return myList;
	}
}