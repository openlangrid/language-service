package jp.go.nict.langrid.wrapper.vlsppostagger;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.vi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;

public class VLSPPosTagger extends AbstractMorphologicalAnalysisService {
	
	/**
	 * constructor
	 */
	public VLSPPosTagger() {
		super.setSupportedLanguageCollection(Arrays.asList(vi));
	}
	
	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		try {
			// マークアップタグの削除
			String stripedText = stripTags(text);
			
			String normalizedText = Normalizer.normalize(stripedText, Normalizer.Form.NFC);
			
			List<TokenizeResult> tokenizedList = (new Tokenizer()).invoke(normalizedText);

			String taggeredStr = "";
			for(TokenizeResult tr: tokenizedList) {
				taggeredStr += (new Tagger()).invoke(tr.tokenized);
			}
			
			return toMorphemeList(taggeredStr);
			
		} catch (IOException e) {
			
			throw new ProcessFailedException("unexpected error occurred", e);
			
		}
	}
	
	protected String stripTags(String text) {
		return text.replaceAll("<\\/?[^>]+>", "");
	}
	
	protected static Morpheme createMorpheme(String word, String posStr) {
		word = word.replaceAll("_", " ");
		
		PartOfSpeech pos = PartOfSpeech.other;
		if(partOfSpeechTable.containsKey(posStr)){
			pos = partOfSpeechTable.get(posStr);
		} else if(word.startsWith("N")) {
			pos = PartOfSpeech.noun_other;
		}
		return new Morpheme(word, word, pos);
	}

	protected List<Morpheme> toMorphemeList(String response) {
		List <Morpheme> results = new LinkedList<Morpheme>();
		
		for(String line: response.split(System.getProperty("line.separator"))) {
			String[] values = line.split("\t");
			if(values.length >= 2) {
				results.add(VLSPPosTagger.createMorpheme(values[0], values[1]));
			}
		}
		
		return results;
	}
	
	static final Map<String, PartOfSpeech> partOfSpeechTable;
	
	static {
		partOfSpeechTable = new HashMap<String, PartOfSpeech>();
		partOfSpeechTable.put("A", PartOfSpeech.adjective); // 形容詞
		partOfSpeechTable.put("R", PartOfSpeech.adverb); // 副詞
		partOfSpeechTable.put("V", PartOfSpeech.verb); // 動詞
		partOfSpeechTable.put("N", PartOfSpeech.noun); // 名詞
		partOfSpeechTable.put("Nb", PartOfSpeech.noun); // 名詞
		//partOfSpeechTable.put("", PartOfSpeech.noun_common); // 普通名詞 
		partOfSpeechTable.put("P", PartOfSpeech.noun_other); // 普通名詞、固有名詞以外の名詞
		partOfSpeechTable.put("M", PartOfSpeech.noun_other); // 普通名詞、固有名詞以外の名詞
		partOfSpeechTable.put("Ny", PartOfSpeech.noun_other); // 普通名詞、固有名詞以外の名詞
		partOfSpeechTable.put("Nc", PartOfSpeech.noun_other); // 普通名詞、固有名詞以外の名詞
		partOfSpeechTable.put("Nu", PartOfSpeech.noun_other); // 普通名詞、固有名詞以外の名詞
		//partOfSpeechTable.put("", PartOfSpeech.noun_pronoun); // 代名詞
		partOfSpeechTable.put("Np", PartOfSpeech.noun_proper); // 固有名詞
		partOfSpeechTable.put("X", PartOfSpeech.unknown); // 分類情報無し
		
		partOfSpeechTable.put("E", PartOfSpeech.other); // その他
		partOfSpeechTable.put("C", PartOfSpeech.other); // その他
		partOfSpeechTable.put("L", PartOfSpeech.other); // その他
		partOfSpeechTable.put("T", PartOfSpeech.other); // その他
	}
	
	static class Tokenizer {

		public static final int PORT = 1511;
		public static final String HOST = "localhost";
		
		public List<TokenizeResult> invoke(String source) throws IOException {
			Socket socket = new Socket(HOST, PORT);
			
			try {
				List<String> strList = sendAndReceive(socket, source);
				
				return toResultList(strList);
			} finally {
				socket.close();	
			}
		}

		protected List<String> sendAndReceive(Socket socket, String text) throws IOException {

			PrintWriter out = null;
			try {
				
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				out.println(text);
				socket.shutdownOutput();

				return receive(socket);

			} finally {
				if(out != null) out.close();
			}
		}

		protected List<String> receive(Socket socket) throws IOException {

			BufferedReader in = null;
			try {
				List<String> result = new LinkedList<String>();

				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				String line = "";

				while((line = in.readLine()) != null) {
					if(!line.equals("")) {
						result.add(line);
					}
				}

				return result;

			} finally {
				if(in != null) in.close();
			}
		}
		
		protected List<TokenizeResult> toResultList(List<String> list) {
			List<TokenizeResult> result = new LinkedList<TokenizeResult>();
			for(int i = 0; i < list.size(); i = i+2) {
				TokenizeResult tr = new TokenizeResult();
				tr.original = list.get(i);
				tr.tokenized = list.get(i+1);
				result.add(tr);
			}
			return result;
		}
	}
	
	static class TokenizeResult {
		String original;
		String tokenized;
	}
	
	static class Tagger {

		public static final int PORT = 2929;
		public static final String HOST = "localhost";
		
		public String invoke(String source) throws IOException {
			Socket socket = new Socket(HOST, PORT);

			String result = sendAndReceive(socket, source);

			socket.close();

			return result;
		}

		protected String sendAndReceive(Socket socket, String text) throws IOException {

			PrintWriter out = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
				out.println(text);
				socket.shutdownOutput();
				return receive(socket);

			} finally {
				if(out != null) out.close();
			}
		}

		protected String receive(Socket socket) throws IOException {

			BufferedReader in = null;
			try {
				StringBuilder sb = new StringBuilder();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

				String line;
				while((line = in.readLine()) != null) {
					sb.append(line).append(System.getProperty("line.separator"));
				}
				return sb.toString();
			} finally {
				if(in != null) in.close();
			}
		}
	}

}
