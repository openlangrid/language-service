package jp.go.nict.langrid.wrapper.google;

import org.junit.Assert;
import org.junit.Test;

public class ZWSPTest {
	@Test
	public void test() throws Throwable{
		String src = "Hello XX\u200bXbrn​​bumX\u200bXX and XX\u200bXasf\u200bdbrnX\u200bXX.";
		String b = GoogleTranslationV2.restoreCodes(src);
		System.out.println(b);
		Assert.assertTrue(b.indexOf("xxxbrnbumxxx") != -1);
		Assert.assertTrue(b.indexOf("xxxasfdbrnxxx") != -1);
		Assert.assertEquals("Hello xxxbrnbumxxx and xxxasfdbrnxxx.", b);
	}

	@Test
	public void test2() throws Throwable{
		String src = "수업 (course) 의미 측면에서 나타납니다이 스토리 텔링, 통찰력 개념 (concepts) , 공기 (air) 공간 도메인 (domains)에 사용되는 방법 (methods) 및 도구 (tools) . 복잡한에 대한 인간-컴퓨터 상호작용 (HCI)는 인간-컴퓨터 상호작용 (HCI)에게 복잡한 문제를 해결하기위한 매우 유용한 것으로 판명 XXXbrn​​bumXXX 새로운 종류의 제안 체계 (systems) 어려움 (challenges) 기존 인간-컴퓨터 상호작용 (HCI) 솔루션 (solutions)을 설계. 참가자 (Participants)는 항공 우주 경험 (experience)에서 축적 된 지식 (knowledge) 및 팁을 사용하여 지구 (Earth)에 사용할 수 장치 (devices)을 설계합니다. 합성 및 통합, 디자인 (design) 생각의 감각 (sense)의 창의성, 참가자 (participants)는 상태와 복잡한 디자인 (design) 문제를 해결하는 방법에 대해 설명이 수업 (course)의 중심에, 그 결과 제품 (product)을 제공합니다.";
		int c = 0;
		int s = src.indexOf('\u200b');
		while(s != -1){
			c++;
			s = src.indexOf('\u200b', s + 1);
		}
		System.out.println(c);
	}
}
