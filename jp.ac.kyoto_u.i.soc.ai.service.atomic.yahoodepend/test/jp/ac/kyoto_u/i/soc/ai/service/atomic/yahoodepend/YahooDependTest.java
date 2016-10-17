package jp.ac.kyoto_u.i.soc.ai.service.atomic.yahoodepend;

import jp.go.nict.langrid.service_1_2.dependencyparser.Chunk;
import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.Assert;

import org.junit.Test;

public class YahooDependTest {
	
	@Test
	public void test1() throws Exception {
		String text = "庭には二羽にわとりがいる。";
		YahooDepend yahoo = new YahooDepend();
		yahoo.setAppId("APPID");
		Chunk[] ret = yahoo.parseDependency("ja", text);
		Assert.assertEquals(ret.length, 3);
		for (Chunk m : ret) {
			System.out.println(m.getChunkId());
			System.out.println(m.getDependency().getHeadChunkId());
			System.out.println(m.getDependency().getLabel());
			for (Morpheme mor : m.getMorphemes()) {
				System.out.println(mor);
			}
		}
	}

}
