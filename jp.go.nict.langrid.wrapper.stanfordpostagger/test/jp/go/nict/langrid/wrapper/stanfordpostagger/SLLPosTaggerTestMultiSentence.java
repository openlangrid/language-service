package jp.go.nict.langrid.wrapper.stanfordpostagger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.go.nict.langrid.service_1_2.morphologicalanalysis.Morpheme;
import junit.framework.TestCase;

public class SLLPosTaggerTestMultiSentence extends TestCase {

	protected StanfordPosTagger tagger;
	
	protected void setUp() throws Exception {
		super.setUp();
		this.tagger = new StanfordPosTagger();
	}
	
	public void testAnalyze() throws Exception {
		String source = "The Language Grid Playground offers ready-to-use language services provided by the Language Grid." +
		" *%$*. Hop, skip, and jump over the language barrier. *%$*. Have fun! *$%*.";
		/* expected = "The_DT Language_NNP Grid_NNP Playground_NNP offers_VBZ ready-to-use_JJ language_NN services_NNS" +
		" provided_VBN by_IN the_DT Language_NNP Grid_NNP ._. *%$*._XOTHER Hop_NNP ,_, skip_VBP ,_, and_CC jump_NN over_IN" +
		" the_DT language_NN barrier_NN ._. *%$*._XOTHER Have_NNP fun_NN !_. *$%*._XOTHER "; */
		
		Morpheme[] result = tagger.analyze("en", source);

		assertEquals("*%$*.", result[14].getWord());
		assertEquals("*%$*.", result[26].getWord());
		assertEquals("*$%*.", result[30].getWord());
	}

	public void testSentence1() throws IOException {
		String source = "The Language Grid Playground offers ready-to-use language services provided by the Language Grid." +
				" *%$*. Hop, skip, and jump over the language barrier. *%$*. Have fun! *$%*.";
		String expected = "The_DT Language_NNP Grid_NNP Playground_NNP offers_VBZ ready-to-use_JJ language_NN services_NNS" +
				" provided_VBN by_IN the_DT Language_NNP Grid_NNP ._. *%$*._XOTHER Hop_NNP ,_, skip_VBP ,_, and_CC jump_NN over_IN" +
				" the_DT language_NN barrier_NN ._. *%$*._XOTHER Have_NNP fun_NN !_. *$%*._XOTHER ";

		assertEquals(expected,
				tagger.sentenceDelimiterFilter(tagger.executeByServer(source)));
	}

	public void testSentence2() throws IOException {
		String source = "The Language Grid. *$%$*. Playground offers ready-to-use. *%%*." +
				" language services provided by the Language Grid. *%$*. Hop, skip, and jump" +
				" over the language barrier. *%$*. Have fun! *$%*.";
		Pattern pat = Pattern.compile("_XOTHER ");
		int expected = 3;

		Matcher mth = pat.matcher(tagger.sentenceDelimiterFilter(tagger.executeByServer(source)));
		int actual = 0;
		for (actual = 0; mth.find(); actual ++);
		assertEquals(expected, actual);
	}
	
	public void testSentence3() throws Exception {
		String source = "A Part-Of-Speech Tagger (POS Tagger) is a piece of software that reads" +
				" text in some language and assigns parts of speech to each word (and other" +
				" token), such as noun, verb, adjective, etc., although generally computational" +
				" applications use more fine-grained POS tags like 'noun-plural'.";
		Morpheme[] expected = new Morpheme[] {
				new Morpheme("A", "A", "other")
				, new Morpheme("Part-Of-Speech", "Part-Of-Speech", "noun.proper")
				, new Morpheme("Tagger", "Tagger", "noun.proper")
				, new Morpheme("(", "(", "other")
				, new Morpheme("POS", "POS", "noun.proper")
				, new Morpheme("Tagger", "Tagger", "noun.proper")
				, new Morpheme(")", ")", "other")
				, new Morpheme("is", "is", "verb")
				, new Morpheme("a", "a", "other")
				, new Morpheme("piece", "piece", "noun.common")
				, new Morpheme("of", "of", "other")
				, new Morpheme("software", "software", "noun.common")
				, new Morpheme("that", "that", "other")
				, new Morpheme("reads", "reads", "verb")
				, new Morpheme("text", "text", "noun.common")
				, new Morpheme("in", "in", "other")
				, new Morpheme("some", "some", "other")
				, new Morpheme("language", "language", "noun.common")
				, new Morpheme("and", "and", "other")
				, new Morpheme("assigns", "assigns", "verb")
				, new Morpheme("parts", "parts", "noun.common")
				, new Morpheme("of", "of", "other")
				, new Morpheme("speech", "speech", "noun.common")
				, new Morpheme("to", "to", "other")
				, new Morpheme("each", "each", "other")
				, new Morpheme("word", "word", "noun.common")
				, new Morpheme("(", "(", "other")
				, new Morpheme("and", "and", "other")
				, new Morpheme("other", "other", "adjective")
				, new Morpheme("token", "token", "adjective")
				, new Morpheme(")", ")", "other")
				, new Morpheme(",", ",", "other")
				, new Morpheme("such", "such", "adjective")
				, new Morpheme("as", "as", "other")
				, new Morpheme("noun", "noun", "noun.common")
				, new Morpheme(",", ",", "other")
				, new Morpheme("verb", "verb", "noun.common")
				, new Morpheme(",", ",", "other")
				, new Morpheme("adjective", "adjective", "noun.common")
				, new Morpheme(",", ",", "other")
				, new Morpheme("etc.", "etc.", "other")
				, new Morpheme(",", ",", "other")
				, new Morpheme("although", "although", "other")
				, new Morpheme("generally", "generally", "adverb")
				, new Morpheme("computational", "computational", "adjective")
				, new Morpheme("applications", "applications", "noun.common")
				, new Morpheme("use", "use", "verb")
				, new Morpheme("more", "more", "adverb")
				, new Morpheme("fine-grained", "fine-grained", "adjective")
				, new Morpheme("POS", "POS", "noun.proper")
				, new Morpheme("tags", "tags", "noun.common")
				, new Morpheme("like", "like", "other")
				, new Morpheme("`", "`", "other")
				, new Morpheme("noun-plural", "noun-plural", "adjective")
				, new Morpheme("'", "'", "other")
				, new Morpheme(".", ".", "other")};
		
		Morpheme[] actual = tagger.analyze("en", source);
		assertEquals(expected.length, actual.length);
		for (int i = 0; i < actual.length; i ++) {
			assertEquals("Number: " + i + ", " + actual[i].toString(), expected[i].getWord(), actual[i].getWord());
			assertEquals("Number: " + i + ", " + actual[i].toString(), expected[i].getPartOfSpeech(), actual[i].getPartOfSpeech());
		}
	}
	
	public void testSentence4() throws IOException {
		String source = "IEICE Global Plaza. *$%*. -. *$%*. Essay. *$%*. Expectations to ICT scholarship students. *$%*." +
				" by Kok-King Kong. *$%*. EcoSensa Technologies Sdn Bhd, Malaysia. *$%*. Hot" +
				"  Topics. *$%*. Let's join the Welcome Party in annual IEICE Society Conference!" +
				" *%$*. IEICE Communications Society. *$%*. Many ISAP2008 / EUC2008 participants" +
				" visited IEICE exhibition booth. *$%*. IEICE International Affairs Committee." +
				" *$%*. Message from TFIPP Secretariat. *$%*. Global Plaza successfully starts in" +
				" IEICE Home Page (English) as well! *%$*. Essay. *$%*. Expectations to ICT" +
				" scholarship students. *$%*. by Kok-King  Kong. *$%*. EcoSensa  Technologies Sdn" +
				" Bhd, Malaysia. *$%*. Hello, all of you. *%$*. I am  one of engineers who have" +
				" pursued their Master degree in the field of ICT under  the sponsorship of Japanese" +
				" Government. I have received a call from  Prof.Takahashi of IEICE-TFIPP to contribute an" +
				" article in Global Plaza and share  my experience with international students studying ICT" +
				" in Japan. It is my great  pleasure if my sharing would be beneficial to you. *%$*." +
				" After  I graduated from Multimedia University in Malaysia, I was enrolled in University" +
				"  of Tokyo with the help from Prof.Takahashi in 2001. I passed the entrance  examination of" +
				" Graduate School of Information Technology and joined Aoyama-Morikawa Lab as a Research" +
				" Graduate. I  devoted myself to research activities on Sensor Network, RFID and Context" +
				" Aware  Middleware technologies. *%$*. I was  lucky to have access to some of the world" +
				" class technologies and also have the opportunities  to present my research papers in IEICE" +
				" conferences, under the warm-hearted  coordination of Prof. T. Aoyama, Prof. H. Morikawa" +
				" and the other Lab leaders. *%$*. Meanwhile,  because of my work in the lab, I was engaged" +
				" by IDTechEx Ltd. It is a well  known international leading firm in UK. The business of" +
				" this company is focused  on emerging technology consultancy, exhibition and publication." +
				" As part of  IDTechEx, I fortunately gained massive international experience there. I was" +
				"  the technical representative member of IDTechEx in  Auto-ID Center at MIT, Boston USA." +
				" This group was the original creator of the  first EPC (Electronic Product Code) Standard" +
				" and now the defacto RFID  Standardization Body, EPC Global. *%$*. After  I completed" +
				" Graduate Course in University of Tokyo, I returned to my country  and successfully" +
				" founded one of Malaysian ICT enterprises,\"EcoSensa  Technologies Sdn Bhd\"." +
				" The Malay word \"Sdn Bhd\" means a private company. The  primary business of this" +
				" company is focused on consultation, design and  integration of novel solutions based" +
				" on emerging technology innovation represented  by RFID (Radio Frequency Identification)," +
				" ZigBee for wireless personal  networks, UWB (Ultra Wideband radio technology) or RTLS" +
				" (Real Time Location  Systems). *%$*. As a pioneer in RFID industry  development in" +
				" Malaysia, I also have helped Governments to set up the  foundation of RFID Industries in" +
				" Malaysia. Due to my professional background and international  experience in RFID" +
				" development, I was elected as the editor for Malaysian  Communications and Multimedia" +
				" Commission (e.g., MCMC for standardization and regulation) Working Group 2.3. We have" +
				" drafted the RFID UHF Spectrum Regulation of the radio band 919~923MHz for the country." +
				" Thus, many system integrators in Malaysia now enjoy the readiness of the regulations to" +
				" deploy  and use RFID UHF technologies. I also have been involved in the planning and" +
				"  implementation of many RFID Catalyst Projects like Malaysia Microchip, KLIA (Kuala" +
				" Lumpur International Airport) RFID e-baggage  project, CD/DVD Tagging (Fig.1). I am" +
				" humbled that some peers regard me  as \"RFID Man of Malaysia\" who  always carries many" +
				" RFID tags and gadgets around. The RFID which I learned in University of Tokyo is my" +
				" passion now. I also speak regularly in RFID Conferences and Seminars as an invited" +
				"  guest. *%$*. Frankly  speaking, from my experience of cooperative work with a company" +
				" during my years  with Aoyama-Morikawa Lab, I think it will be beneficial for ICT students" +
				" or  even professionals if IEICE can bridge the gap between academic and industrial" +
				"  worlds. For example, IEICE's coordination of internship or consultancy work for" +
				" foreign scholars studying in Japan, in  collaboration with IEICE's membership" +
				" companies, would surely spark their new  ideas, blend foreign culture and thinking" +
				" into Japanese way of invention or  technological innovation in real environments." +
				" *%$*. My sincere advice to foreign scholars in Japan is to have passion on what you" +
				" do. With  passion, you will have unlimited energy to keep you ahead, and excel in" +
				" the  field of your research and work. I will  be so much delighted to share my" +
				" experience with all of you. Please reach myself at. *$%*. info@ecosensa.com." +
				" *$%*. . *%$*. Fig.1. *$%*. Showcasing  RFID e-baggage tag to Ex-Prime Minister of." +
				" *$%*. Malaysia, Tun Dr Mahathir Mohamad in  a RFID Conference. *$%*. (Ex-PM on the" +
				" left, the author on the right in the center). *$%*. Top. *$%*. Hot Topics. *$%*." +
				" Let's join the Welcome Party. *$%*. in annual IEICE Society Conference! *%$*. IEICE" +
				" Communications Society. *$%*. IEICE  holds nationwide conferences twice a year. The" +
				" four societies and one group of IEICE  co-organize the General Conference every" +
				" March to provide a forum for your  paper presentation and enhancement of your studies" +
				" through discussion with the  participants, simultaneously hold a Young Researcher's" +
				" Award ceremony. About  six months later, each of the societies organizes paper" +
				" presentation sessions  and conferment ceremony of newly authorized IEICE Fellows" +
				" according to the  strategy and planning of the society in the same Society Conference" +
				" where you  may join and have presentations. *%$*. Additionally for the Society" +
				" Conference held at Ikuta Campus of Meiji  University last September, IEICE" +
				" Communications Society held the first Welcome  Party (Fig.2). This party was planned" +
				" to provide an opportunity of free opinion  exchange to all participants in an easy" +
				" and friendly environment. In fact, 205  people covering various generations of normal" +
				" members, students, younger  engineers took part in the party and enjoyed talks without" +
				" awareness of class. *%$*. In the  party venue, 18 Technical Committees under the" +
				" Society and 11 relevant ICT  industries and 2 public organizations opened their own" +
				" exhibition booths and  introduced their activities or businesses with posters, to the" +
				" party  participants (Fig.3). They positively discussed each other. As a part of the" +
				"  party program, each of 13 younger ICT researchers presented a 3-minutes speech  and" +
				" appealed how attractive evolving ICT industries or their contributions to  the" +
				" academic progress would be for themselves. Soft drinks and appetizers  provided in" +
				" the party venue made the participants take it easy and promoted  free walking around" +
				" for talks in the venue, irrespectively of age and sex. *%$*. Considering the effect" +
				" of better human relations by the event, IEICE  Communications Society plans to hold" +
				" the second Welcome Party in 2009 Society  Conference which will be held at University" +
				" of Niigata on 15-18 in coming September. We are looking forward to seeing you  there." +
				" *%$*. Fig.2. *$%*. First Welcome Party of. *$%*. IEICE Communications Society. *$%*." +
				" Fig.3. *$%*. Poster session in the party. *$%*. Top. *$%*. Many ISAP2008 /  EUC2008" +
				" participants. *$%*. visited IEICE  exhibition booth. *$%*. IEICE International" +
				" Affairs Committee. *$%*. IEICE  set up an exhibition booth in the International" +
				" Symposium on Antennas and  Propagation (ISAP2008) held at Taipei International" +
				" Convention Center on 27-30  October 2008 and further in the International Conference" +
				" on Embedded and  Ubiquitous Computing (EUC2008) held at Courtyard by Marriott" +
				" Shanghai Pudong in  China on 17-20 December 2008. Every IEICE booth exhibited IEICE's" +
				" publications  represented by IEICE transactions to make them widely understood by the" +
				"  participants. These transactions reviewed by a project of National Institute of" +
				"  Informatics, Scholarly Publishing and Academic Resources Coalition (SPARC Japan)" +
				"  and many current volumes were displayed as a part of IEICE's SPARC project. *%$*." +
				" ISAP2008  was organized by Yuan Ze University and Oriental Institute of Technology" +
				" with  IEICE's co-sponsorship, 455 people participated in the Symposium and most of" +
				"  them visited IEICE's booth. 42 booths in total were arranged in the Exhibition  Hall" +
				" of the venue. IEICE's publications exhibited in the booth covered 70  volumes of" +
				" English transactions, 150 brochures of English transactions, 50  brochures of SPARC" +
				" Japan, 50 brochures of Call for Papers concerning Special  Issues on Communications," +
				" 100 brochures of ELEX, 10 Global News Letters by  IEICE Communications Society and" +
				" 100 souvenirs. The IEICE's exhibition was  successful with the kind support of Prof." +
				" Tzong-Li Wu of National Taiwan  University, IEICE Taipei Section Representative" +
				" and his colleagues. *%$*. EUC2008 was organized by Shanghai  Jiao Tong University" +
				" and Shanghai Computer Society, the number of participants  reached around 200. IEICE's" +
				" special booth was set up in the center of the venue  entrance, the exhibited" +
				" publications covered 85 English and 8 Japanese  transactions, 100 brochures of" +
				" English transactions, 50 brochures of SPARC  Japan, 50 brochures of Call for Papers" +
				" in English transactions. The IEICE's  exhibition was successful with the kind" +
				"  support of Prof. Rong-Hong Jin of Shanghai Jiao Tong University, IEICE Shanghai" +
				"  Section Representative and his colleagues, as well. *%$*. Top. *$%*. Message from" +
				" TFIPP Secretariat. *$%*. Global  Plaza successfully starts. *$%*. in  IEICE Home Page" +
				" (English) as well! *%$*. IEICE Task Force  for International Policy and Planning" +
				" (TFIPP) under IEICE International Affairs  Committee has discussed and decided to" +
				" use the following media:. *$%*. (1)Mail Magazine \"Global  Plaza on Line\" via" +
				" Internet. *$%*. This  is delivered in the beginning of next month just after" +
				" the edition to transfer the most updated  information. The delivery of the mail" +
				" magazine depends on your free choice. The  first issue was delivered last December" +
				" and successive issues were punctually  delivered. The coming issues will try to add" +
				" the updated recruitment information  for foreign students. *%$*. (2)IEICE Home" +
				"  Page (English). *$%*. This is delivered little later after the  delivery of Global" +
				" Plaza on Line to taking the time for the html based design  and implementation of" +
				" the articles. It started in February 2009. URL is. *$%*." +
				" http://www.ieice.org/eng/global_plaza/index.html. *$%*. . *%$*. (3)IEICE Magazine" +
				"  (English Page). *$%*. This is published two months later after the  Mail Magazine." +
				" *%$*. We are looking forward to your constructive  opinions or proposals to enhance" +
				" IEICE's global academic activities and membership  services." +
				" If you have any questions, please contact. *$%*." +
				" Prof.Kenzo Takahashi,  Chief/IEICE-TFIPP, at. *$%*. Top. *$%*. BACK." +
				" *$%*. (C). *$%*. Copyright 2009 IEICE.All rights reserved. *%$*. ";
		Pattern pat = Pattern.compile("_XOTHER ");
		int expected = 72;
		
		Matcher mth = pat.matcher(tagger.sentenceDelimiterFilter(tagger.executeByServer(source)));
		int actual = 0;
		for (actual = 0; mth.find(); actual ++);
		assertEquals(expected, actual);
	}
	
	public void testSentence5() throws IOException {
		String text = "I.C.E. *%$*. is a simulator of cell phone. *%$*. It helps you for testing your software. *%$*. ";

		Pattern pat = Pattern.compile("_XOTHER ");
		int expected = 3;
		String value = tagger.executeByServer(text);
		
		Matcher mth = pat.matcher(tagger.sentenceDelimiterFilter(value));
		int actual = 0;
		for (actual = 0; mth.find(); actual ++);
		assertEquals(expected, actual);
	}
}