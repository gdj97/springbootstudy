package kr.gdu.shop2.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ShopUtil {
	public static String getRandomString(int count, boolean letter, boolean number) {
		//count : 필요한 문자의 갯수
		//letter : true인경우 영문자를 필요한 문자에 추가
		//number : true인 경우 숫자를 필요한 문자에 추가
		StringBuilder builder = new StringBuilder();
		List<String> list = new ArrayList<String>();
		if (letter) {
			for (char ch = 'A'; ch <= 'Z'; ch++) { // 대문자 추가
				list.add(ch + ""); 
			}
			for (char ch = 'a'; ch <= 'z'; ch++) { // 소문자 추가
				list.add(ch + "");
			}
		}
		if (number) {
			for (int n = 0; n <= 9; n++) {
				list.add(n + "");
			}
		}
		//secureRandom : Random(난수발생)클래스의 보안성을 강화한 클래스. 암호학적으로 강력한 난수 생성 클래스
		SecureRandom secureRandom = new SecureRandom();
		if (letter || number) {
			while (count > 0) { //count 갯수만큼 list객체에서 임의로 요소를 builder 추가
				builder.append(list.get(secureRandom.nextInt(list.size())));
				count--;
			}
		}
		return builder.toString();  //String 객체 리턴
	}
}
