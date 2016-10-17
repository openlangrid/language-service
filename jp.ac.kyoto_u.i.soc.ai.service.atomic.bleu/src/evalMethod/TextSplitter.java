package evalMethod;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;
import static jp.go.nict.langrid.language.ISO639_1LanguageTags.ja;

import java.util.Arrays;
import java.util.List;

import jp.go.nict.langrid.language.Language;

public class TextSplitter {
	public List<String> split(String text, Language lang){
		if(lang == en){
			return this.splitEn(text);
		}else if(lang == ja){
			return this.splitJa(text);
		}else{
			return null;
		}
	}

	private List<String> splitEn(String text){
		text = text.replace("-\n", "");
		text = text.replace("\n", "");

		text = " " + text + " ";
		text = text.toLowerCase();
		text = text.replaceAll("([\\{-\\~\\[-\\` -\\&\\(-\\+\\:-\\@\\/])", " $1 ");
		text = text.replaceAll("([^0-9])([\\.,])", "$1 $2 ");
		text = text.replaceAll("([\\.,])([^0-9])", " $1 $2");
		text = text.replaceAll("([0-9])(-)", "$1 $2 ");
		text = text.replaceAll("\\s+", " ");
		text = text.replaceAll("^\\s+", "");
		text = text.replaceAll("\\s+$", "");

		text = text.replaceAll("\\s*\\.$", "");
		text = text.replaceAll("\\s*\\?$", "");

		return Arrays.asList(text.split(" "));
	}

	private List<String> splitJa(String text){
		text = " " + text + " ";
		text = text.toLowerCase();
		text = text.replaceAll("([\\{-\\~\\[-\\` -\\&\\(-\\+\\:-\\@\\/])", " $1 ");
		text = text.replaceAll("([^0-9])([\\.,])", "$1 $2 ");
		text = text.replaceAll("([\\.,])([^0-9])", " $1 $2");
		text = text.replaceAll("([0-9])(-)", "$1 $2 ");
		text = text.replaceAll("(.)", " $1 ");
		text = text.replaceAll(" ([a-z0-9]) ", "$1");
		text = text.replaceAll("\\s+", " ");
		text = text.replaceAll("^\\s+", "");
		text = text.replaceAll("\\s+$", "");

		text = text.replaceAll("\\s*。$", "");
		text = text.replaceAll("\\s*？$", "");
		text = text.replaceAll("\\s*．$", "");
		text = text.replaceAll("\\s*\\.$", "");
		text = text.replaceAll("\\s*\\?$", "");

		return Arrays.asList(text.split(" "));
	}

	public static void main(String[] args){
		TextSplitter ts = new TextSplitter();
		ts.split("this is a tes  .", en);
	}
}
