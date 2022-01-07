package com.iwi.sso;

import java.util.Date;

import com.iwi.sso.util.TokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public class TokenTest {

	public static void main(String[] args) {
		try {
		String a = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJramdAaXdpLmNvLmtyIiwiaWF0IjoxNjQwNzY4Njc2LCJleHAiOjE2NDA3Njg3MzZ9.kj0BrU44RM9HosO3FT_vO6pWFMgfuehHMArd_H5JSDE";

		System.out.println("a : " + a);

		//Date e = TokenUtil.getExpirationDateFromToken(a);
		//System.out.println("e : " + e);

		String s = TokenUtil.getSubjectFromToken(a);
		System.out.println("s : " + s);
		} catch(ExpiredJwtException e) {
			Claims cs = e.getClaims();
			System.out.println(cs.getExpiration());
			System.out.println(cs.getSubject());
		}
	}

}
