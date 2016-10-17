package jp.go.nict.langrid.wrapper.stanfordpostagger;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.typed.Morpheme;
import jp.go.nict.langrid.service_1_2.typed.PartOfSpeech;
import jp.go.nict.langrid.wrapper.ws_1_2.morphologicalanalysis.AbstractMorphologicalAnalysisService;


public class StanfordPosTagger extends AbstractMorphologicalAnalysisService {
	
	static boolean BY_SERVER = true;
	private int port = 8081;
	private String host = "localhost";

	/**
	 * constructor
	 */
	public StanfordPosTagger() {
		super.setSupportedLanguageCollection(Arrays.asList(en));
	}

	/**
	 * Returns a port of Stanford POS Tagger service. Predefined value is 8081.
	 * @return a port number.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets port number for Stanfrod POS Tagger service.
	 * @param port port number.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns a host address of Stanford POS Tagger service. Predefined value is "localhost".
	 * @return a string of address.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets host address for Stanford POS Tagger service.
	 * @param host host address.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	protected Collection<Morpheme> doAnalyze(Language language, String text)
			throws InvalidParameterException, ProcessFailedException {
		
		
		try {
			String result;
			
			if(BY_SERVER) {
				result = executeByServer(text);
			} else {
				result = executeByJavaAPI(text);
			}
			
			return toMorphemes(tokenizeResultText(sentenceDelimiterFilter(result)));
					
		} catch (IOException e) {
			
			throw new ProcessFailedException("unexpected error occurred", e);
			
		} catch (ClassNotFoundException e) {
			
			throw new ProcessFailedException("unexpected error occurred", e);
		}
		
	}
	
	private static final Pattern sentenceDelimiter = 
		Pattern.compile("\\\\\\*_(?:SYM|NN) (%|\\$)_(?:NN|\\$) ((?<=\\$_\\$ )%(?=_NN)|(?<=%_NN )\\$(?=_\\$)).{2,3} \\\\\\*_NN \\._\\.");
	protected String sentenceDelimiterFilter(String text) {
		return sentenceDelimiter.matcher(text).replaceAll("*$1$2*._XOTHER");
	}

	protected String executeByServer(String text) throws IOException {
		Tagger tagger = new Tagger();
		return tagger.invoke(text); 
	}
	
	protected String executeByJavaAPI(String text) throws IOException, ClassNotFoundException {
		//MaxentTagger tagger = new MaxentTagger("jar:bidirectional-distsim-wsj-0-18.tagger");
		//return tagger.tagTokenizedString(text);
		return null;
	}
	
	protected static List<String[]> tokenizeResultText(String text) {
		List <String[]> results = new LinkedList<String[]>();
		for(String s : text.split(" ")) {
			String[] wordAndPos = s.split("_(?=[^_]+$)");
			if(wordAndPos.length == 2) {
				results.add(wordAndPos);
			}
		}
		return results;
	}
	
	protected List<Morpheme> toMorphemes(List<String[]> words) {
		List<Morpheme> results = new LinkedList<Morpheme>();
		
		for(String[] word : words) {
			if (characterAliasTable.containsKey(word[0])) {
				results.add(createMorpheme(characterAliasTable.get(word[0]), word[1]));
			} else {
				results.add(createMorpheme(word[0], word[1]));
			}
		}
		
		return results;
	}

	protected static Morpheme createMorpheme(String word, String posStr) {
		
		PartOfSpeech pos = PartOfSpeech.other;
		if(partOfSpeechTable.containsKey(posStr)){
			pos = partOfSpeechTable.get(posStr);
		}
		
		return new Morpheme(word, word, pos);
	}
	
	static final Map<String, String> characterAliasTable;
	static final Map<String, PartOfSpeech> partOfSpeechTable;
	
	static {
		characterAliasTable = new HashMap<String, String>();

		characterAliasTable.put("-LRB-", "(");
		characterAliasTable.put("-RRB-", ")");
		
		partOfSpeechTable = new HashMap<String, PartOfSpeech>();
		partOfSpeechTable.put("NNP", PartOfSpeech.noun_proper);
		partOfSpeechTable.put("NNPS", PartOfSpeech.noun_proper);
		
		partOfSpeechTable.put("NN", PartOfSpeech.noun_common);
		partOfSpeechTable.put("NNS", PartOfSpeech.noun_common);
		
		partOfSpeechTable.put("PRP", PartOfSpeech.noun_pronoun);
		partOfSpeechTable.put("PRP$", PartOfSpeech.noun_pronoun);
		
		partOfSpeechTable.put("VB", PartOfSpeech.verb);
		partOfSpeechTable.put("VBD", PartOfSpeech.verb);
		partOfSpeechTable.put("VBG", PartOfSpeech.verb);
		partOfSpeechTable.put("VBN", PartOfSpeech.verb);
		partOfSpeechTable.put("VBP", PartOfSpeech.verb);
		partOfSpeechTable.put("VBZ", PartOfSpeech.verb);
		
		partOfSpeechTable.put("JJ", PartOfSpeech.adjective);
		partOfSpeechTable.put("JJR", PartOfSpeech.adjective);
		partOfSpeechTable.put("JJS", PartOfSpeech.adjective);
		
		partOfSpeechTable.put("RB", PartOfSpeech.adverb);
		partOfSpeechTable.put("RBR", PartOfSpeech.adverb);
		partOfSpeechTable.put("RBS", PartOfSpeech.adverb);

		// For sentence delimiter and parentheses.
		// partOfSpeechTable.put("XOTHER", PartOfSpeech.other); // *%$*
		// partOfSpeechTable.put("-LRB-", PartOfSpeech.other); // (
		// partOfSpeechTable.put("-RRB-", PartOfSpeech.other); // )
	}

	class Tagger {

		public String invoke(String source) throws IOException {
			Socket socket = new Socket(getHost(), getPort());

			String result = sendAndReceive(socket, source);

			socket.close();

			return result;
		}

		protected String sendAndReceive(Socket socket, String text) throws IOException {

			PrintWriter out = null;
			try {
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
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
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

				String line;
				while((line = in.readLine()) != null) {
					sb.append(line);
				}
				return sb.toString();
			} finally {
				if(in != null) in.close();
			}
		}
	}
}
