package com.iwi.sso.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	/**
	 * 전화번호 형태로 반환 (000-000-0000)
	 * 
	 * @param tel
	 * @return
	 */
	public static String getFormatTel(String tel) {
		if (StringUtils.isEmpty(tel)) {
			return null;
		}

		if (tel.length() == 8) {
			// 0000-0000
			tel = tel.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
		} else if (tel.length() == 12) {
			// 0000-0000-0000
			tel = tel.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
		} else {
			// 02-000-0000, 02-0000-0000
			// 000-0n-0000
			tel = tel.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
		}

		return tel;

	}

	/**
	 * String 소문자 유효성 여부 반환
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLowerCase(String str) {
		return str.matches("^[a-z]*$");
	}

	/**
	 * String 대문자 유효성 여부 반환
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isUpperCase(String str) {
		return str.matches("^[A-Z]*$");
	}

}
