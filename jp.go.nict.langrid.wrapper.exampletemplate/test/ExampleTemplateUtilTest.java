import java.util.List;

import jp.go.nict.langrid.service_1_2.AccessLimitExceededException;
import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.LanguagePairNotUniquelyDecidedException;
import jp.go.nict.langrid.service_1_2.NoAccessPermissionException;
import jp.go.nict.langrid.service_1_2.NoValidEndpointsException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;
import jp.go.nict.langrid.service_1_2.ServerBusyException;
import jp.go.nict.langrid.service_1_2.ServiceNotActiveException;
import jp.go.nict.langrid.service_1_2.ServiceNotFoundException;
import jp.go.nict.langrid.service_1_2.UnsupportedLanguagePairException;
import jp.go.nict.langrid.service_1_2.UnsupportedMatchingMethodException;
import jp.go.nict.langrid.wrapper.exampletemplate.ExampleTemplateUtilService;
import jp.go.nict.langrid.wrapper.exampletemplate.service.ExampleTemplate;
import jp.go.nict.langrid.wrapper.exampletemplate.service.Segment;
import jp.go.nict.langrid.wrapper.exampletemplate.service.SeparateExample;
import junit.framework.TestCase;


public class ExampleTemplateUtilTest extends TestCase {

	protected void setUp() throws Exception {
	}
/*
	ExampleTemplateUtilService exampleTemplate = new ExampleTemplateUtilService();
	int level = 1;
	public void testDoSeparateExamples() {
//			List<String> results = dissolution(str);
//			for (String s : results) {
//				System.out.println(s);
//			}
//		}
		ExampleTemplate[] exampleTemplates = new ExampleTemplate[5];
		exampleTemplates[0] = new ExampleTemplate("1",  "今日は<CHANGE_SENTENCE>いい天気ですね。<CHANGE_SENTENCE>明日はどこに<PID NO=\"1\">2,3</PID>行きますか？");
		exampleTemplates[1] = new ExampleTemplate("2",  "今日は<CHANGE_SENTENCE>天気が悪いですね。<CHANGE_SENTENCE>明日はどこに<PID NO=\"1\">2,3</PID>行きますか？");
		exampleTemplates[2] = new ExampleTemplate("3",  "今日は<CHANGE_SENTENCE>天気が悪いですね。<CHANGE_SENTENCE>明日はどこに<PID NO=\"1\">2,3</PID>行きますか？");
		exampleTemplates[3] = new ExampleTemplate("4",  "明日は<CHANGE_SENTENCE>天気が悪いですね。<CHANGE_SENTENCE>明後日はどこに<PID NO=\"1\">2,3</PID>行きますか？");
		exampleTemplates[4] = new ExampleTemplate("5",  "明後日は<CHANGE_SENTENCE>天気が悪いですね。<CHANGE_SENTENCE>明後日は<CHANGE_SENTENCE>どこに<CHANGE_SENTENCE>行きますか？");
		try {
			SeparateExample[] separateExamples = exampleTemplate.separateExamples("ja", exampleTemplates);
			Segment[] segments = exampleTemplate.margeExamples(separateExamples);
			for (Segment segment : segments) {
				level = 1;
				
				System.out.println(createIndent(level) + "Level:" + level + " text:" + segment.getText()+ "exampleId:" + segment.getExampleId());
				if (segment.getChildSegments() != null && segment.getChildSegments().length != 0) {
					printSegment(segment.getChildSegments());
				}
			}
		} catch (LanguagePairNotUniquelyDecidedException e) {
			e.printStackTrace();
		} catch (UnsupportedLanguagePairException e) {
			e.printStackTrace();
		} catch (UnsupportedMatchingMethodException e) {
			e.printStackTrace();
		} catch (AccessLimitExceededException e) {
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			e.printStackTrace();
		} catch (NoAccessPermissionException e) {
			e.printStackTrace();
		} catch (NoValidEndpointsException e) {
			e.printStackTrace();
		} catch (ProcessFailedException e) {
			e.printStackTrace();
		} catch (ServerBusyException e) {
			e.printStackTrace();
		} catch (ServiceNotActiveException e) {
			e.printStackTrace();
		} catch (ServiceNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String createIndent(int count) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < count; i++) {
			buf.append("  ");
		}
		return buf.toString();
	}
	public void printSegment(Segment[] segments) {
		level++;
		for (Segment segment : segments) {
			System.out.println(createIndent(level) + "Level:" + level + " text:" + segment.getText() + "exampleId:" + segment.getExampleId());
			if (segment.getChildSegments() != null && segment.getChildSegments().length != 0) {
				printSegment(segment.getChildSegments());
			}
		}
		level--;
	}
	public void testDoMargeExamples() {
//		fail("まだ実装されていません。");
	}
*/
}
