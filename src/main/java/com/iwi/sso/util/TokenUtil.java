package com.iwi.sso.util;

import java.util.Date;
import com.iwi.sso.common.SystemConst;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class TokenUtil {

	public static String createAccessToken(String subject) {
		Claims claims = Jwts.claims().setSubject(subject);
		Date now = new Date();
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(new Date(System.currentTimeMillis() + (SystemConst.ACS_TOKEN_VALID_MINUTES * 60 * 1000)))
				.signWith(SignatureAlgorithm.HS256, SystemConst.SECRET_KEY)
				.compact();
	}

	public static String createRefreshToken() {
		Date now = new Date();
		return Jwts.builder()
				.setIssuedAt(now)
				.setExpiration(new Date(System.currentTimeMillis() + (SystemConst.REF_TOKEN_VALID_MINUTES * 60 * 1000)))
				.signWith(SignatureAlgorithm.HS256, SystemConst.SECRET_KEY)
				.compact();
	}

	// 토큰 만료 체크
	public static Boolean isTokenExpired(String token) throws SignatureException {
		try {
			Claims claims = Jwts.parser().setSigningKey(SystemConst.SECRET_KEY).parseClaimsJws(token).getBody();
			return claims.getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			// 토큰 만료
			return true;
		}
	}

	// 토큰에서 subject 추출
	public static String getSubjectFromToken(String token) throws SignatureException {
		Claims claims = Jwts.parser().setSigningKey(SystemConst.SECRET_KEY).parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	// 토큰 유효성 검증
	public static Boolean validateToken(String token, String subject) {
		final String tokenSubject = getSubjectFromToken(token);
		return (subject.equals(tokenSubject) && !isTokenExpired(token));
	}

	// 토큰 만료일
	public static Date getExpirationDateFromToken(String token) throws SignatureException {
		Claims claims = Jwts.parser().setSigningKey(SystemConst.SECRET_KEY).parseClaimsJws(token).getBody();
		return claims.getExpiration();
	}

}
