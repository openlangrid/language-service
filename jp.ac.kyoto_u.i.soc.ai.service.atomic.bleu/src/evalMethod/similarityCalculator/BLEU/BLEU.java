package evalMethod.similarityCalculator.BLEU;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguageNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguageException;
import jp.go.nict.langrid.service_1_2.similaritycalculation.SimilarityCalculationService;
import evalMethod.TextSplitter;


public class BLEU implements SimilarityCalculationService {
	private int firstMaxNgram = 4;

	public BLEU(){
	}

	public BLEU(int firstMaxNgram){
		this.firstMaxNgram = firstMaxNgram;
	}

	@Override
	public double calculate(String language, String text1, String text2)
			throws AccessLimitExceededException, InvalidParameterException,
			LanguageNotUniquelyDecidedException, NoAccessPermissionException,
			NoValidEndpointsException, ProcessFailedException,
			ServerBusyException, ServiceNotActiveException,
			ServiceNotFoundException, UnsupportedLanguageException {
		Language langTag;
		if(language.equals("ja")){
			langTag = jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;
		}else if(language.equals("en")){
			langTag = jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
		}else{
			throw new InvalidParameterException("language", language);
		}
		return calculateSimilarity(text1, text2, langTag);

	}

	public double calculateSimilarity(String candidate,
			String reference, Language lang) {
		ArrayList<String> refs = new ArrayList<String>();
		refs.add(reference);
		return calculateSimilarity(candidate,refs, lang);
	}

	public double calculateSimilarity(String candidate,
			ArrayList<String> references, Language lang) {
		if(references.size()==0) return 0;

		TextSplitter splitter = new TextSplitter();

		List<String> candidateWords = splitter.split(candidate, lang);
		int maxNgram;
		if(candidateWords.size() < this.firstMaxNgram){
			maxNgram = candidateWords.size();
		}else{
			maxNgram = firstMaxNgram;
		}

		ArrayList<List<String>> referenceWordsList = new ArrayList<List<String>>();
		for(String reference : references){
			List<String> referenceWords = splitter.split(reference, lang);
			referenceWordsList.add(referenceWords);

			if(referenceWords.size() < maxNgram){
				maxNgram = referenceWords.size();
			}
		}

		double sumOfBLEU = 0.0;
		for(int ngram = 1; ngram <= maxNgram; ngram++){
			double p = this.calcBLEU(candidateWords, referenceWordsList, ngram);
			sumOfBLEU += Math.log(p) / (double)maxNgram;
		}

		double bp = this.getBP(candidateWords, referenceWordsList);

		return bp * Math.exp(sumOfBLEU);
	}


	private double getBP(List<String> candidateWords,
			ArrayList<List<String>> referenceWordsList) {
		int c = candidateWords.size();
		int minDistance = Math.abs(referenceWordsList.get(0).size()-c);
		int minDistanceIndex = 0;
		for(int i = 1; i < referenceWordsList.size(); i++){
			int length = referenceWordsList.get(i).size();
			int distance = Math.abs(length-c);
			if(distance < minDistance){
				minDistance = distance;
				minDistanceIndex = i;
			}
		}
		int r = referenceWordsList.get(minDistanceIndex).size();

		if(c >= r){
			return 1.0;
		}else{
			return Math.exp(1.0-(double)r/(double)c);
		}
	}


	private double calcBLEU(List<String> candidateWords,
			ArrayList<List<String>> referenceWordsList, int ngram) {
		if(referenceWordsList.size()==0) return 0;

		HashMap<String, Integer> candidateGrams = this.words2gram(candidateWords,ngram);
		ArrayList<HashMap<String, Integer>> referenceGramsList = new ArrayList<HashMap<String, Integer>>();

		for(List<String> referenceWords : referenceWordsList){
			referenceGramsList.add(words2gram(referenceWords, ngram));
		}

		int sumOfCountClip = 0;
		int sumOfCount = 0;
		for(String cGram : candidateGrams.keySet()){
			int count = candidateGrams.get(cGram);
			sumOfCount += count;
			int maxRefCount = 0;
			for(HashMap<String, Integer> referenceGrams : referenceGramsList){
				int refCount = 0;
				if(referenceGrams.containsKey(cGram)){
					refCount = referenceGrams.get(cGram);
				}
				if( refCount > maxRefCount){
					maxRefCount = refCount;
				}
			}

			if(count > maxRefCount){
				sumOfCountClip += maxRefCount;
			}else{
				sumOfCountClip += count;
			}
		}

		return (double)sumOfCountClip / (double)sumOfCount;
	}

	private HashMap<String, Integer> words2gram(List<String> words, int ngram) {
		HashMap<String, Integer> resGrams = new HashMap<String, Integer>();
		for(int i = 0; i <= words.size() - ngram; i++){
			String resGram = words.get(i);
			for(int j = 1; j < ngram; j++){
				resGram += " " + words.get(i+j);
			}
			if(resGrams.containsKey(resGram)){
				resGrams.put(resGram, resGrams.get(resGram)+1);
			}else{
				resGrams.put(resGram, 1);
			}
		}

		return resGrams;
	}


}
