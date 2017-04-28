package jp.go.nict.langrid.wrapper.google;

import jp.go.nict.langrid.commons.io.StreamUtil;
import jp.go.nict.langrid.commons.ws.ServiceContext;
import junit.framework.TestCase;

public class GoogleTranslationSentenceDivisionTest extends TestCase {
	protected void setUp() throws Exception{
		this.sourceJa = "石田・松原研究室。 *$%*。 このサイトを検索。 *$%*。 ＷＷＷを検索。 *$%*。 English。 *$%*。 Japanese。 *$%*。 ホーム。 *$%*。 学生募集。 *$%*。 研究室について。 *$%*。 研究の背景と目標。";// *$%*。 研究の内容。 *$%*。 パイロットプロジェクト。 *$%*。 ラボとコラボ。 *$%*。 研究室生活。 *$%*。 他大学から入学すると。 *$%*。 博士修了学生の今。 *$%*。 修士修了学生の今。 *$%*。 関連する会議。 *$%*。 研究グループ。 *$%*。 参加型シミュレーション。 *$%*。 異文化コラボレーション。 *$%*。 情報経済。 *$%*。 過去の研究。 *$%*。 研究成果。 *$%*。 主な研究成果。 *$%*。 受賞。 *$%*。 学位論文。 *$%*。 ニュースリリース。 *$%*。 研究成果一覧。 *$%*。 ソフトウェア。 *$%*。 言語グリッドプレイグラウンド。 *$%*。 多言語NOTA。 *$%*。 FreeWalk。 *$%*。 Q。 *$%*。 gumonji/Q。 *$%*。 授業科目。 *$%*。 人工知能。 *$%*。 マルチエージェントシステム。 *$%*。 ヒューマンインタフェース。 *$%*。 情報と社会。 *$%*。 情報社会論。 *$%*。 情報システム設計論Ｉ。 *$%*。 フィールド情報学。 *$%*。 メンバー。 *$%*。 アクセス。 *$%*。 リンク。 *$%*。 ホーム。 *$%*。 お知らせ。 *$%*。 更新情報。 *$%*。 2009年度 研究室 集合写真。 *$%*。 京都大学石田・松原研究室は, 人工知能, サービスコンピューティング, ヒューマンインタフェースの最新の技術を用いて,       「集合知（collective intelligence）」に基づく新しい社会情報システムのデザインを目指しています． *%$*。 これまでの集合知形成の研究活動とは異なり, 私たちは「人」「エージェント」「知識」をサービス指向の方法論によって統合し，       その連携により，社会情報システムに必要な集合知を系統的に形成することを目標とします．       この枠組みを実現するための研究課題は2つあります． *%$*。 「集合的意思決定」では, マルチエージェントシステムを基礎に研究を進めます. 人々の意思決定過程のモデリング, 人々を集合知形成に導くインセンティブマッチング, 集合知の形成を予測するマルチエージェントシミュレーションの研究を行います． *%$*。 「集合的知識構築」では, セマンティックWebに基礎を置きます. 知識をサービスとして捉え体系化するサービスオントロジーと, 抽象ワークフローの実行を管理するサービススーパビジョンを研究の中心に据えます． *%$*。 また, 集合知形成のための参加型プラットフォームを開発し, 交通, 教育, 経済の分野などで社会情報システムを実際に構築しています.       特に, 2002年から活動を始めた異文化コラボレーションは, 実用的な多言語基盤である言語グリッドを産み出し, 世界の留学生が集まる国際色豊かな研究グループが生まれています． *%$*。 お知らせ。 *$%*。 第一回 文化とコンピューティング国際会議。 *$%*。 2010年2月22日（月） - 23日（火）。 *$%*。 京都大学 吉田キャンパス構内 百周年時計台記念館 百周年記念ホール，およびその周辺。 *$%*。 International Workshop on Agents in Cultural Context。 *$%*。 2010年2月22日（月） - 23日（火）。 *$%*。 京都大学 吉田キャンパス構内 百周年時計台記念館 会議室III。 *$%*。 過去のお知らせは。 *$%*。 こちら。 *$%*。 更新情報。 *$%*。 2009年12月16日。 *$%*。 受賞。 *$%*。 のページを更新しました． *%$*。 山根昇平(博士課程)らの論文が，PRIMA2009のBest Multimedia Paper Awardを受賞しました． *%$*。 2009, Best Multimedia Paper Award。 *$%*。 Shohei Yamane, Shoichi Sawada, Hiromitsu Hattori, Marika Odagaki, Kengo Nakajima, and Toru Ishida.       Participatory Simulation Environment gumonji/Q: A Network Game Empowered by Agents.       The 12th Pacific Rim International Conference on Multi-Agents (PRIMA2009), December, 2009. *%$*。 [。 *$%*。 受賞。 *$%*。 ]。 *$%*。 2009年10月9日。 *$%*。 メンバー。 *$%*。 のページを更新しました． *%$*。 Jiang Huan(博士課程)をメンバーリストに追加しました． *%$*。 2009年10月9日。 *$%*。 メンバー。 *$%*。 のページを更新しました． *%$*。 Xia Linsi(修士課程)をメンバーリストに追加しました． *%$*。 2009年6月25日。 *$%*。 研究成果一覧。 *$%*。 のページを更新しました． *%$*。 2009年の研究成果を追加しました． *%$*。 2009年6月25日。 *$%*。 主な研究成果。 *$%*。 のページを更新しました． *%$*。 2009年の研究成果を追加しました． *%$*。 2009年度 研究室 集合写真。 *$%*。 2009年5月28日，工学部10号館 第三講義室に研究室メンバーが集まりました．この部屋で，研究会や昼食会を行っています． *%$*。 Copyright(C) 1993-2009 Ishida & Matsubara Lab. All rights reserved. *%$*。";
//		this.sourceEn = "Ishida & Matsubara Laboratory. *$%*. Search this site. *$%*. Search WWW. *$%*. English. *$%*. Japanese. *$%*. Home. *$%*. Introduction. *$%*. Research Background & Goal. *$%*. Research Issues. *$%*. Pilot Projects. *$%*. Lab and CoLab. *$%*. Laboratory Life. *$%*. Research Group. *$%*. Participatory Simulation. *$%*. Intercultural Collaboration. *$%*. Information Economy. *$%*. Past Researches. *$%*. Publications. *$%*. Selected Publications. *$%*. Thesis. *$%*. List of Publications. *$%*. Software. *$%*. Language Grid Playground. *$%*. FreeWalk. *$%*. Q. *$%*. People. *$%*. Admission. *$%*. Contact. *$%*. Link. *$%*. Home. *$%*. Announcement. *$%*. Update Information. *$%*. 2009 Group Photo. *$%*. Ishida & Matsubara Laboratory at Kyoto University pursues artificial intelligence, service computing,       and human interface technologies to realize. *$%*. collective intelligence. *$%*. for designing novel social information systems. *%$*. Unlike current research activities into forming collective intelligence,       this research aims at integrating and coordinating \"humans,\" \"agents,\" and \"knowledge,\"       in a service-oriented framework to systematically create collective intelligence for realizing social information systems.       We have two major research components. *%$*. One is. *$%*. collective decision making. *$%*. based on multiagent systems; modeling the human decision making process as agents, matching incentives to encourage humans in developing collective intelligence, and predicting the formation of collective intelligence by multiagent simulations. *%$*. The other component is. *$%*. collective knowledge structuring. *$%*. based on semantic Web; structuring service ontology by regarding knowledge as services, and supervising the execution of services in abstract workflows. *%$*. Participatory platform for collective intelligence. *$%*. is to be developed       to actually build social information systems in various fields including transportation, education, and the economy.       Especially, we have been working on intercultural collaboration since 2002,       created a practical multilingual infrastructure called the Language Grid,       and formed an international research group with students worldwide. *%$*. Announcement. *$%*. The First International Conference on Culture and Computing. *$%*. February 22 -23, 2010. *$%*. Kyoto University, Japan. *$%*. The Clock Tower Centennial Hall (and other rooms). *$%*. International Workshop on Agents in Cultural Context. *$%*. February 22 -23, 2010. *$%*. Kyoto University, Japan. *$%*. The Clock Tower Centennial Hall (Conference Room #3). *$%*. Past announcement is. *$%*. here. *$%*. Update Information. *$%*. October 9, 2009. *$%*. People. *$%*. was updated. *%$*. Jiang Huan (PhD Student) was added. *%$*. October 9, 2009. *$%*. People. *$%*. was updated. *%$*. Xia Linsi (Master Student) was added. *%$*. June 25, 2009. *$%*. List of Publications. *$%*. was updated. *%$*. Publications in 2009 were added. *%$*. June 25, 2009. *$%*. Selected Publications. *$%*. was updated. *%$*. Selected publications in 2009 were added. *%$*. 2009 Group Photo. *$%*. On May 28th, 2009, the laboratory members gathered in the lecture room#3 where they have a lab seminar and lunch meeting. *%$*. Copyright(C) 1993-2009 Ishida & Matsubara Lab. All rights reserved. *%$*.";
		this.sourceEn = StreamUtil.readAsString(
				getClass().getResourceAsStream("longtext_en.txt")
				, "UTF-8"
				);
	}

	public void testNoneJ2E() throws Exception{
		System.setProperty("langrid.translation.sentenceDivision", "NONE");
		GoogleTranslationV2 s = createService();
		System.out.println(s.translate("ja", "en", sourceJa));
	}

	public void testFullJ2E() throws Exception{
		System.setProperty("langrid.translation.sentenceDivision", "FULL");
		GoogleTranslationV2 s = createService();
		System.out.println(s.translate("ja", "en", sourceJa));
	}

	public void testWordJ2E() throws Exception{
		System.setProperty("langrid.translation.sentenceDivision", "WORD");
		GoogleTranslationV2 s = createService();
		System.out.println(s.translate("ja", "en", sourceJa));
	}

	public void testNoneE2J() throws Exception{
		System.setProperty("langrid.translation.sentenceDivision", "NONE");
		GoogleTranslationV2 s = createService();
		System.out.println(s.translate("en", "ja", sourceEn));
	}

	public void testFullE2J() throws Exception{
		System.setProperty("langrid.translation.sentenceDivision", "FULL");
		GoogleTranslationV2 s = createService();
		System.out.println(s.translate("en", "ja", sourceEn));
	}

	public void testWordE2J() throws Exception{
		System.setProperty("langrid.translation.sentenceDivision", "WORD");
		GoogleTranslationV2 s = createService();
		System.out.println(s.translate("en", "ja", sourceEn));
	}

	public void test_word_allWithTerminalSymbol_en_ja() throws Exception {
		System.setProperty("langrid.translation.sentenceDivision", "WORD");
		GoogleTranslationV2 s = createService();
		assertEquals("こんにちは。 *%$*。 さようなら。 *%$*。 ", s.translate("en", "ja", "Hello. *%$*. Good bye. *%$*."));
	}

	public void test_word_allWithoutTerminalSymbol_en_ja() throws Exception {
		System.setProperty("langrid.translation.sentenceDivision", "WORD");
		GoogleTranslationV2 s = createService();
		assertEquals("こんにちは。 *$%*。 さようなら。 *$%*。 ", s.translate("en", "ja", "Hello. *$%*. Good bye. *$%*."));
	}

	public void test_word_allWithExclamation_en_ja() throws Exception {
		System.setProperty("langrid.translation.sentenceDivision", "WORD");
		GoogleTranslationV2 s = createService();
		assertEquals("こんにちは！ *%$*。 さようなら。 *%$*。 ", s.translate("en", "ja", "Hello! *%$*. Good bye. *%$*."));
	}

	public void test_word_allWithQuestion_en_ja() throws Exception {
		System.setProperty("langrid.translation.sentenceDivision", "WORD");
		GoogleTranslationV2 s = createService();
		assertEquals("もしもし？ *%$*。 さようなら。 *$%*。 ", s.translate("en", "ja", "Hello? *%$*. Good bye. *$%*."));
	}

	GoogleTranslationV2 createService() {
		return new GoogleTranslationV2() {
			@Override
			protected String getIDCode(
					ServiceContext serviceContext, String parameterName, String defaultValue)
			{
				return "apikey";
			}
		};
	}

	private String sourceJa;
	private String sourceEn;
}
