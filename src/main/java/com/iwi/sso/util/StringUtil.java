package com.iwi.sso.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

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
	 * request referer > domain 추출
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomainInfo(HttpServletRequest request) {
		String referer = request.getHeader("referer");
		if (StringUtils.isEmpty(referer)) {
			return null;
		}
		return getDomainInfo(referer, 2);
	}

	/**
	 * url > domain 추출
	 * 
	 * @param url
	 * @return
	 */
	public static String getDomainInfo(String url) {
		return getDomainInfo(url, 2);
	}

	/**
	 * url > 각 항목 추출
	 * 
	 * @param url : ex. https://sso.iwi.co.kr:1234/qa/aaa/bbb.do?p1=v1&p2=v2#hash
	 * @param num : 0 = https://sso.iwi.co.kr:1234/qa/aaa/bbb.do?p1=v1&p2=v2#hash
	 * @param num : 1 = https
	 * @param num : 2 = sso.iwi.co.kr
	 * @param num : 3 = :1234
	 * @param num : 4 = 1234
	 * @param num : 5 = /qa/aaa
	 * @param num : 6 = /aaa
	 * @param num : 7 = bbb.do
	 * @param num : 8 = ?p1=v1&p2=v2
	 * @param num : 9 = p1=v1&p2=v2
	 * @param num : 10 = #hash
	 * @param num : 11 = hash
	 */
	public static String getDomainInfo(String url, int num) {

		if (num < 0 || num > 11) {
			return url;
		}

		Pattern urlPattern = Pattern.compile("^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?((\\/[^\\s/\\/]+)*)?\\/([^#\\s\\?]*)(\\?([^#\\s]*))?(#(\\w*))?$");
		Matcher mc = urlPattern.matcher(url);

		if (!mc.matches()) {
			return url;
		}

		// for (int i = 0; i <= mc.groupCount(); i++) {
		// System.out.println("group(" + i + ") = " + mc.group(i));
		// }

		return mc.group(num);
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
