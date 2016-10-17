package jp.go.nict.langrid.wrapper.google;

import java.net.URL;

import jp.go.nict.langrid.commons.lang.StringUtil;
import junit.framework.TestCase;

public class URLTest extends TestCase{
	public void test() throws Exception{
		String url = "http://" + StringUtil.randomString(10000);
		System.out.println(
				new URL(url).toString().length()
				);
	}
}
