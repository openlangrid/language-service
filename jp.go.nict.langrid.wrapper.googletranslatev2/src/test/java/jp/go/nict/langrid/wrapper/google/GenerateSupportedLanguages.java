package jp.go.nict.langrid.wrapper.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.go.nict.langrid.commons.transformer.TransformationException;
import jp.go.nict.langrid.commons.transformer.Transformer;
import jp.go.nict.langrid.commons.util.CollectionUtil;
import jp.go.nict.langrid.language.InvalidLanguageTagException;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.language.LanguagePair;
import jp.go.nict.langrid.language.LanguagePath;
import jp.go.nict.langrid.language.util.LanguagePairUtil;
import jp.go.nict.langrid.language.util.LanguagePathUtil;

public class GenerateSupportedLanguages {
	public static void main(String[] args) throws Exception{
		List<LanguagePair> pairs = new ArrayList<LanguagePair>();
		LanguagePairUtil.addBidirectionalRoundrobinformedPairs(
				pairs, supportedLangs.toArray(new Language[]{}));
		List<LanguagePath> paths = CollectionUtil.collect(pairs
				, new Transformer<LanguagePair, LanguagePath>(){
			public LanguagePath transform(LanguagePair value)
					throws TransformationException {
				return new LanguagePath(value.getSource(), value.getTarget());
			}
		});
		System.out.println(
				LanguagePathUtil.encodeLanguagePathArray(
						paths.toArray(new LanguagePath[]{}))
				);
		System.out.println(paths.size());
	}

	private static List<Language> supportedLangs = new ArrayList<Language>();
	static{
		InputStream is = GoogleTranslationTest.class.getResourceAsStream("supportedLanguages.txt");
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String line = null;
			int i = 1;
			while((line = r.readLine()) != null){
				System.out.println(i++ + ": " + line);
				supportedLangs.add(Language.parse(line.split("\\*")[0].trim()));
			}
		} catch(IOException e){
			throw new RuntimeException(e);
		} catch (InvalidLanguageTagException e) {
			throw new RuntimeException(e);
		} finally{
			try{
				is.close();
			} catch(IOException e){
				throw new RuntimeException(e);
			}
		}
	}
}
