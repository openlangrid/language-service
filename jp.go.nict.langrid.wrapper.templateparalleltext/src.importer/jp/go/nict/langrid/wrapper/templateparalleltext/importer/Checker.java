package jp.go.nict.langrid.wrapper.templateparalleltext.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import au.com.bytecode.opencsv.CSVReader;

public class Checker {
	public static void main(String[] args) throws Exception{
		importTemplates();
	}

	private static int importTemplates()
	throws IOException, SQLException{
		int c = 0;
		String[] files = {
				"data/Expert Answer Parallel Text.csv"
				, "data/Expert Answer Parallel Text(1).csv"
				, "data/Expert Answer Parallel Text(2).csv"
				, "data/Expert Answer Parallel Text(3).csv"
				, "data/Expert Answer Parallel Text(4).csv"
				, "data/Expert Answer Parallel Text(5).csv"
				, "data/Expert Answer Parallel Text(6).csv"
				, "data/Expert Answer Parallel Text(7).csv"
				, "data/Expert Answer Parallel Text(8).csv"
		};
		Set<String> duplicatedIds = new TreeSet<String>();
		for(String f : files){
			InputStream is = new FileInputStream(f); 
			try{
				CSVReader r = new CSVReader(new InputStreamReader(is, "UTF-8"));
				r.readNext();
				for(String[] values; (values = r.readNext()) != null;){
					if(values.length < 7) continue;
					String tid = values[1].trim();
					if(tid.length() == 0) continue;
					if(duplicatedIds.contains(tid)) continue;

					String en = values[4].trim();
					String vi = values[5].trim();
					StringBuilder buff = new StringBuilder();
					for(char ch : en.toCharArray()){
						UnicodeBlock b = UnicodeBlock.of(ch);
						if(!enBlocks.contains(b)){
							if(buff.length() > 0){
								buff.append(",");
							}
							buff.append("'")
								.append(ch)
								.append("'(")
								.append(b)
								.append(")");
						}
					}
					if(buff.length() > 0){
						System.out.println(String.format(
								"\"%s\",en,\"%s\",\"%s\""
								, tid, buff
								, en.replaceAll("\r\n", "\\n")
									.replaceAll("\r", "\\n")
									.replaceAll("\n", "\\n")
								));
					}
					buff = new StringBuilder();
					for(char ch : vi.toCharArray()){
						UnicodeBlock b = UnicodeBlock.of(ch);
						if(!viBlocks.contains(b)){
							if(buff.length() > 0){
								buff.append(",");
							}
							buff.append("'")
								.append(ch)
								.append("'(")
								.append(b)
								.append(")");
						}
					}
					if(buff.length() > 0){
						System.out.println(String.format(
								"\"%s\",vi,\"%s\",\"%s\""
								, tid, buff
								, vi.replaceAll("\r\n", "\\n")
									.replaceAll("\r", "\\n")
									.replaceAll("\n", "\\n")
								));
					}
				}
				c++;
			} finally{
				is.close();
			}
		}
		return c;
	}

	@SuppressWarnings("serial")
	private static Set<UnicodeBlock> enBlocks = new HashSet<Character.UnicodeBlock>(){{
		add(UnicodeBlock.ALPHABETIC_PRESENTATION_FORMS);
		add(UnicodeBlock.BASIC_LATIN);
		add(UnicodeBlock.LATIN_1_SUPPLEMENT);
		add(UnicodeBlock.LATIN_EXTENDED_A);
		add(UnicodeBlock.LATIN_EXTENDED_ADDITIONAL);
		add(UnicodeBlock.LATIN_EXTENDED_B);
	}};
	@SuppressWarnings("serial")
	private static Set<UnicodeBlock> viBlocks = new HashSet<Character.UnicodeBlock>(){{
		addAll(enBlocks);
		add(UnicodeBlock.THAI);
		add(UnicodeBlock.COMBINING_DIACRITICAL_MARKS);
	}};
}
