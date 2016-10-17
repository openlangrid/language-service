package evalMethod.similarityCalculator;

import java.util.ArrayList;

import jp.go.nict.langrid.language.Language;

public abstract class AbstractSimilarityCalculator {
	public abstract double calculateSimilarity(String candidate, ArrayList<String> references, Language lang);
}
