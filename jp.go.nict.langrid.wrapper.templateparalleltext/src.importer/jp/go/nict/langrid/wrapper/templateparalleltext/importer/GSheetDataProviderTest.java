package jp.go.nict.langrid.wrapper.templateparalleltext.importer;

import java.util.Arrays;

public class GSheetDataProviderTest {
	public static void main(String[] args) throws Throwable{
		int c = 0;
		for(String[] s : new GSheetDataProvider(
				"ACCESS-TOKEN",
				"YMC Category", new String[]{"YMC Category"})){
			c++;
			System.out.println(Arrays.toString(s));
		}
		System.out.println(c + " records found.");
	}
}
