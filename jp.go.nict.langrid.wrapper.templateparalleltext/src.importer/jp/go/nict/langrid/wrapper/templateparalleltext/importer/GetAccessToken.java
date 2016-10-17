package jp.go.nict.langrid.wrapper.templateparalleltext.importer;

/**
 * Get AccessToken for Google SpreadSheet.
 * You need to run GetConfirmationPageURL and get confirmation code before run this class.
 * @author Takao Nakaguchi
 *
 */
public class GetAccessToken {
	public static void main(String[] args) throws Throwable{
		System.out.println(GetConfirmationPageURL.buildFlow()
				.newTokenRequest(code)  
				.setRedirectUri(GetConfirmationPageURL.redirectUri)
				.execute()
				.getAccessToken());
	}

	static String code = "CODE"; // place CODE here that you got by accessing URL got by running GetConfirmationPageURL.
}
