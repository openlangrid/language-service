package jp.go.nict.langrid.wrapper.google;

import jp.go.nict.langrid.commons.ws.ServiceContext;
import junit.framework.TestCase;

public class GoogleTranslationSeparatorConversionTest extends TestCase {

	protected void setUp() throws Exception {
		source = "Ishida & Matsubara Laboratory. *$%*. Search this site. *$%*. Search WWW. *$%*. English. *$%*. Japanese. *$%*. Home. *$%*. Introduction. *$%*. Research Background & Goal. *$%*. Research Issues. *$%*. Pilot Projects. *$%*. Lab and CoLab. *$%*. Laboratory Life. *$%*. Research Group. *$%*. Participatory Simulation. *$%*. Intercultural Collaboration. *$%*. Information Economy. *$%*. Past Researches. *$%*. Publications. *$%*. Selected Publications. *$%*. Thesis. *$%*. List of Publications. *$%*. Software. *$%*. Language Grid Playground. *$%*. FreeWalk. *$%*. Q. *$%*. People. *$%*. Admission. *$%*. Contact. *$%*. Link. *$%*. Home. *$%*. Announcement. *$%*. Update Information. *$%*. 2009 Group Photo. *$%*. Ishida & Matsubara Laboratory at Kyoto University pursues artificial intelligence, service computing,       and human interface technologies to realize. *$%*. collective intelligence. *$%*. for designing novel social information systems. *%$*. Unlike current research activities into forming collective intelligence,       this research aims at integrating and coordinating \"humans,\" \"agents,\" and \"knowledge,\"       in a service-oriented framework to systematically create collective intelligence for realizing social information systems.       We have two major research components. *%$*. One is. *$%*. collective decision making. *$%*. based on multiagent systems; modeling the human decision making process as agents, matching incentives to encourage humans in developing collective intelligence, and predicting the formation of collective intelligence by multiagent simulations. *%$*. The other component is. *$%*. collective knowledge structuring. *$%*. based on semantic Web; structuring service ontology by regarding knowledge as services, and supervising the execution of services in abstract workflows. *%$*. Participatory platform for collective intelligence. *$%*. is to be developed       to actually build social information systems in various fields including transportation, education, and the economy.       Especially, we have been working on intercultural collaboration since 2002,       created a practical multilingual infrastructure called the Language Grid,       and formed an international research group with students worldwide. *%$*. Announcement. *$%*. The First International Conference on Culture and Computing. *$%*. February 22 -23, 2010. *$%*. Kyoto University, Japan. *$%*. The Clock Tower Centennial Hall (and other rooms). *$%*. International Workshop on Agents in Cultural Context. *$%*. February 22 -23, 2010. *$%*. Kyoto University, Japan. *$%*. The Clock Tower Centennial Hall (Conference Room #3). *$%*. Past announcement is. *$%*. here. *$%*. Update Information. *$%*. October 9, 2009. *$%*. People. *$%*. was updated. *%$*. Jiang Huan (PhD Student) was added. *%$*. October 9, 2009. *$%*. People. *$%*. was updated. *%$*. Xia Linsi (Master Student) was added. *%$*. June 25, 2009. *$%*. List of Publications. *$%*. was updated. *%$*. Publications in 2009 were added. *%$*. June 25, 2009. *$%*. Selected Publications. *$%*. was updated. *%$*. Selected publications in 2009 were added. *%$*. 2009 Group Photo. *$%*. On May 28th, 2009, the laboratory members gathered in the lecture room#3 where they have a lab seminar and lunch meeting. *%$*. Copyright(C) 1993-2009 Ishida & Matsubara Lab. All rights reserved. *%$*. ";
		service = new GoogleTranslationV2() {
			@Override
			protected String getIDCode(
					ServiceContext serviceContext, String parameterName, String defaultValue)
			{
				return "apikey";
			}
		};
	}

	public void test_1() throws Exception {
		assertEquals("FreeWalkを。 *$%*。 問。 *$%*。 人。 *$%*。",
				service.translate("en", "ja", "FreeWalk. *$%*. Q. *$%*. People. *$%*."));
	}

	public void test_1_back() throws Exception {
		assertEquals("FreeWalk to. *$%*. Questions. *$%*. People. *$%*.", service.translate("ja", "en",
				service.translate("en", "ja", "FreeWalk. *$%*. Q. *$%*. People. *$%*.")));
	}

	public void test_2() throws Exception {
		assertEquals("一つは、。 *$%*。 集団的意思決定。 *$%*。 マルチエージェントシステムに基づいて、モデリング、集団的知性を開発し、マルチエージェントシミュレーションによる集合知の形成を予測する上で人間を奨励するインセンティブを一致させる剤としてのプロセスを作る人間の意思決定。 *%$*。",
				service.translate("en", "ja", "One is. *$%*. collective decision making. *$%*. based on multiagent systems; modeling the human decision making process as agents, matching incentives to encourage humans in developing collective intelligence, and predicting the formation of collective intelligence by multiagent simulations. *%$*."));
	}

	public void test_2_back() throws Exception {
		assertEquals("One. *$%*. Collective decision making. *$%*. Based on the multi modeling, developing a collective intelligence, human decision making process as an incentive to encourage the match in predicting the formation of human collective intelligence by Multi. *%$*.", service.translate("ja", "en",
				service.translate("en", "ja", "One is. *$%*. collective decision making. *$%*. based on multiagent systems; modeling the human decision making process as agents, matching incentives to encourage humans in developing collective intelligence, and predicting the formation of collective intelligence by multiagent simulations. *%$*.")));
	}

	public void test_3() throws Exception {
		assertEquals("文化とコンピューティングに関する第1回国際会議。 *$%*。 2月22日-23、2010。 *$%*。 京都大学、日本。 *$%*。",
				service.translate("en", "ja", "The First International Conference on Culture and Computing. *$%*. February 22 -23, 2010. *$%*. Kyoto University, Japan. *$%*."));
	}

	public void test_3_back() throws Exception {
		assertEquals("1st International Conference on Computing and culture. *$%*. February 22 -23,2010. *$%*. Kyoto University, Japan. *$%*.", service.translate("ja", "en",
				service.translate("en", "ja", "The First International Conference on Culture and Computing. *$%*. February 22 -23, 2010. *$%*. Kyoto University, Japan. *$%*.")));
	}

	public void test_speed() throws Exception {
		service.translate("en", "ja", source);
	}

	private GoogleTranslationV2 service;
	private String source;
}
