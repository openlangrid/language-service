/*
 * $Id: ExampleTemplateUtilService.java 2009/03/01 koyama $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
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

package jp.go.nict.langrid.wrapper.exampletemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import jp.go.nict.langrid.language.Language;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate;
import jp.go.nict.langrid.wrapper.exampletemplate.service.Segment;
import jp.go.nict.langrid.wrapper.exampletemplate.service.SeparateExample;
import jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateUtil;

/**
 * @author koyama
 * @author $Author$
 * @version $Revision$
 */
public class ExampleTemplateUtilService extends AbstractExampleTemplateUtil {

	/**
	 * <#if locale="ja">
	 * デフォルトコンストラクタ．
	 * <#elseif locale="en">
	 * </#if>
	 */
	public ExampleTemplateUtilService() {
		super();
	}
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param serviceContext
	 * <#elseif locale="en">
	 * </#if>
	 */
	public ExampleTemplateUtilService(ServiceContext serviceContext) {
		super(serviceContext);
	}


	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateUtil#doMargeExamples(jp.go.nict.langrid.wrapper.exampletemplate.service.SeparateExample[])
	 */
	@Override
	protected Collection<Segment> doMargeExamples(
			SeparateExample[] separateExamples)
			throws InvalidParameterException, ProcessFailedException {
		Map<String, Segment> maps = new HashMap<String, Segment>();
		for (SeparateExample example : separateExamples) {
			String[] texts = example.getTexts();
			String exampleId = example.getExampleId();
			Segment segment = null;
			if (texts == null || texts.length == 0) {
				continue;
			}
			// 親要素の取得
			segment = maps.get(texts[0]);
			String[] categoryIds = null;
			if (texts[0] != null && texts[0].indexOf("PID") > -1) {
				categoryIds = getPid(texts[0]);
			}
			if (segment == null) {
				segment = new Segment(exampleId, texts[0], categoryIds, null);
			}
			Segment parentSegment = segment;
			// ２番目の分節からループ
			for (int i = 1; i < texts.length; i++) {
				parentSegment.setChildSegments(margeChildSegment(exampleId, texts[i], parentSegment));
				Segment[] childSegments = parentSegment.getChildSegments();
				if (childSegments != null) {
					for (Segment child : childSegments) {
						if (child.getText().equals(texts[i]) && child.getExampleId().equals(child.getExampleId())) {
							parentSegment = child;
						}
					}
				}
			}
			maps.put(texts[0], segment);
		}
		return maps.values();
	}

	/**
	 * 子要素のセグメントをマージする．
	 * @param exampleId 用例ID
	 * @param text テキスト
	 * @param parentSegment 親セグメント
	 * @return 子要素セグメント
	 */
	private Segment[] margeChildSegment(String exampleId, String text, Segment parentSegment) {
		List<Segment> segments = new ArrayList<Segment>();
		Segment[] childs = parentSegment.getChildSegments();
		Segment child = null;
		if (childs == null || childs.length == 0) {
			;
		} else {
			for (Segment c : childs) {
				if (c.getText().equals(text) && text.indexOf("PID") < 0) {
					child = c;
				}
				segments.add(c);
			}
		}
		if (child == null) {
			String[] categoryIds = null;
			if (text != null && text.indexOf("PID") > -1) {
				categoryIds = getPid(text);
			}						
			child = new Segment(exampleId, text, categoryIds, null);
			segments.add(child);
		}
		return segments.toArray(new Segment[]{});
	}
	
	/* (non-Javadoc)
	 * @see jp.go.nict.langrid.wrapper.exampletemplate.ws.AbstractExampleTemplateUtil#doSeparateExamples(jp.go.nict.langrid.language.Language, jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate[])
	 */
	@Override
	protected Collection<SeparateExample> doSeparateExamples(Language language,
			ExampleTemplate[] exampleTemplates)
			throws InvalidParameterException, ProcessFailedException {
		List<SeparateExample> separateExamples = new ArrayList<SeparateExample>();
		for (ExampleTemplate template : exampleTemplates) {
			String[] texts = dissolution(template.getExample()).toArray(new String[]{});
			SeparateExample separate = new SeparateExample(template.getExampleId(), texts);
			separateExamples.add(separate);
		}
		return separateExamples;
	}
	
	/**
	 * <#if locale="ja">
	 * CHANGE_SENTENSEかPIDタグで区切られた文節単位で返す．<br>
	 * CHANGE_SENTENSEは含まない．
	 * @param exampleTemplate
	 * @return 解析後の文字配列
	 * <#elseif locale="en">
	 * </#if>
	 */
	private List<String> dissolution(String exampleTemplate) {
		List<String> list = new ArrayList<String>();
		Scanner scanner = new Scanner(exampleTemplate);
		scanner.useDelimiter(Pattern.compile(PATTERN_CHANGE_SENTENCE));
		int in = 0;
		String text = "";
		while (scanner.hasNext()) {
			text = scanner.next();
			int out = exampleTemplate.indexOf(text, in);
			if (out != in) {
				// 1文章
				String sentence = exampleTemplate.substring(in, out);
				if (sentence != null && !sentence.equals(CHANGE_SENTENCE)) {
					list.add(sentence);
				}
			}
			in = out + text.length();			
			if (StringUtils.isNotBlank(text)) {
				if (text != null && !text.equals(CHANGE_SENTENCE)) {
					list.add(text);
				}
				text = "";
			}
		}
		if (in != exampleTemplate.length()) {
			String sentence = exampleTemplate.substring(in);
			if (sentence != null && !sentence.equals(CHANGE_SENTENCE)) {
				list.add(sentence);
			}
		}
		return list;
	}
	
	private String[] getPid(String text) {
		String tempStr = text.replaceAll(PATTERN_PID, "");
		String[] num = tempStr.trim().split(",");
		return num;
	}
	
	/*
	private String getPidNo(String text) {
		Pattern pattern = Pattern.compile(PATTERN_PID_VALUE);
		Matcher matcher = pattern.matcher(text);
		String result = "";
		while (matcher.find()) {
			result = matcher.group(1);
			break;
		}
		return result;
	
	}
	*/
	private static final String CHANGE_SENTENCE = "<CHANGE_SENTENCE>";
	/**
	 * 文節単位正規表現
	 * <(CHANGE_SENTENCE|PID.+?>[^</>]*</PID)>
	 */
	private static final String PATTERN_CHANGE_SENTENCE = "<(CHANGE_SENTENCE|PID.+?>[^</>]*</PID)>";
	/**
	 * PID中正規表現
	 * <(PID.+?|/PID)>
	 */
	private static final String PATTERN_PID = "<(PID.+?|/PID)>";
	/**
	 * PID正規表現
	 * <PID\sNO=\"(\d)\">
	 *
	private static final String PATTERN_PID_VALUE = "<PID\\sNO=\\\"(\\d)\\\">";
	*/
}
