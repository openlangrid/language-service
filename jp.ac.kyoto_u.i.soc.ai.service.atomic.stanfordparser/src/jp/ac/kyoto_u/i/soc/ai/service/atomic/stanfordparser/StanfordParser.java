/*
 * This is a program to wrap language resources as Web services.
 * 
 * Copyright (C) 2012 Department of Social Informatics, Kyoto University.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 2.1 of the License, or (at 
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This is a program to wrap language resources as Web services.
 * 
 * Copyright (C) 2012 Department of Social Informatics, Kyoto University.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 2.1 of the License, or (at 
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package jp.ac.kyoto_u.i.soc.ai.service.atomic.stanfordparser;

import static jp.go.nict.langrid.language.ISO639_1LanguageTags.en;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.commons.io.FileUtil;
import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.nio.charset.CharsetUtil;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.dependencyparser.Dependency;
import jp.go.nict.langrid.service_1_2.dependencyparser.DependencyParserService;
import jp.go.nict.langrid.service_1_2.dependencyparser.typed.DependencyLabel;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import jp.go.nict.langrid.wrapper.common.pos.PennTreebank;
import jp.go.nict.langrid.wrapper.ws_1_2.dependencyparser.AbstractDependencyParserService;

public class StanfordParser
extends AbstractDependencyParserService
implements DependencyParserService{
	public StanfordParser() {
		setSupportedLanguageCollection(Arrays.asList(en));
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	@Override
	protected Collection<Chunk> doParseDependency(Language language,
			String sentence) throws InvalidParameterException,
			ProcessFailedException {
		File tmp = null;
		try{
			tmp = FileUtil.createUniqueFile(getWorkPathFile(), "temp");
			OutputStream os = new FileOutputStream(tmp);
			try{
				StreamUtil.writeString(os, sentence, CharsetUtil.newUTF8Encoder());
			} finally{
				os.close();
			}
			ProcessBuilder pb = new ProcessBuilder(
					"java", "-mx150m", "-cp", "stanford-parser.jar"
					, "edu.stanford.nlp.parser.lexparser.LexicalizedParser"
					, "-outputFormat", "wordsAndTags,typedDependencies"
					, "-outputFormatOptions", "basicDependencies"
					, "-maxLength", Integer.toString(getMaxSourceLength())
					, "grammar/englishPCFG.ser.gz", tmp.getAbsolutePath());
			pb.directory(new File(path));
			Process process = pb.start();
			try{
				int code = process.waitFor();
				if(code == 0){
					return convert(process.getInputStream());
				} else{
					throw new ProcessFailedException(StreamUtil.readAsString(process.getErrorStream(), "UTF-8"));
				}
			} finally{
				process.destroy();
			}
		} catch(IOException e){
			throw new ProcessFailedException(e);
		} catch(InterruptedException e){
			throw new ProcessFailedException(e);
		} finally{
			if(tmp != null && !tmp.delete()){
				tmp.deleteOnExit();
			}
		}
	}

	private static List<Chunk> convert(InputStream is) throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;

		List<Morpheme> morphs = new ArrayList<Morpheme>();
		{	// parse token
			line = r.readLine();
			if(line == null){
				throw new IOException("invalid parser result.");
			}
			Matcher m = tokPattern.matcher(line);
			while(m.find()){
				String word = m.group(1).replace("\\/", "/");
				String pos = m.group(3);
				if(word == null || pos == null){
					throw new IOException("malformed token from StanfordParser: " + line);
				}
				morphs.add(new Morpheme(word, word, PennTreebank.ptToLg(pos).getExpression()));
			}
		}

		// skip empty line
		r.readLine();

		List<Chunk> chunks = new ArrayList<Chunk>();
		{	// parse dependency
			while((line = r.readLine()) != null){
				Matcher m = depPattern.matcher(line);
				if(!m.matches()) continue;
				String[] targets = m.group(1).split("-");
				if(targets.length != 2){
					logger.warning("malformed result: " + line);
					continue;
				}
				String[] sources = m.group(2).split("-");
				if(sources.length != 2){
					logger.warning("malformed result: " + line);
					continue;
				}
				try{
					int targetIndex = Integer.parseInt(targets[1]) - 1;
					int sourceIndex = Integer.parseInt(sources[1]) - 1;
					chunks.add(new Chunk(
							Integer.toString(sourceIndex)
							, new Morpheme[]{morphs.get(sourceIndex)}
							, new Dependency(DependencyLabel.DEPENDENCY.name(), Integer.toString(targetIndex))
							));
				} catch(NumberFormatException e){
					logger.warning("malformed result: " + line);
					continue;
				}
			}
		}
		return chunks;
	}

	private synchronized File getWorkPathFile()
	throws IOException{
		if(workPathFile == null){
			if(workPath != null){
				workPathFile = new File(workPath);
			} else{
				workPathFile = getWorkDirectory();
			}
			if(!workPathFile.exists()){
				if(!workPathFile.mkdirs()) throw new IOException("failed to create path: " + workPathFile);
			}
		}
		return workPathFile;
	}

	private String path;
	private String workPath = "/tmp";
	private File workPathFile;

	private static Pattern tokPattern = Pattern.compile("((\\\\/|[^\\/])+)\\/([^ ]+) *");
	private static Pattern depPattern = Pattern.compile(".*\\((.*), (.*)\\)");
	private static Logger logger = Logger.getLogger(StanfordParser.class.getName());
}
