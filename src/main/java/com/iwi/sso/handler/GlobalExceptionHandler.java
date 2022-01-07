package com.iwi.sso.handler;

import javax.naming.AuthenticationException;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.iwi.sso.common.IException;
import com.iwi.sso.common.Response;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IException.class)
	public Response iException(IException e) {
		// e.printStackTrace();
		if (StringUtils.isEmpty(e.getMessage())) {
			return this.exception(e);
		}
		return new Response("99", e.getMessage());
	}

	@ExceptionHandler(SignatureException.class)
	public Response signatureException(SignatureException e) {
		// e.printStackTrace();
		return new Response("10", "토큰 검증 실패");
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public Response expiredJwtException(ExpiredJwtException e) {
		// e.printStackTrace();
		return new Response("11", "인증 토큰 만료");
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public Response httpMessageNotReadableException(HttpMessageNotReadableException e) {
		// e.printStackTrace();
		return new Response("12", "필수 파라미터 누락");
	}

	@ExceptionHandler(AuthenticationException.class)
	public Response authenticationException(AuthenticationException e) {
		e.printStackTrace();

		String errorMessage = e.getMessage();
		String responseMessage = "LDAP 처리 중 오류가 발생하였습니다.\\n관리자에게 문의해주세요.";

		if (errorMessage.indexOf("data 525") > 0) {
			responseMessage = "LDAP 사용자를 찾을 수 없습니다.";
		} else if (errorMessage.indexOf("data 52e") > 0) {
			responseMessage = "LDAP 비밀번호가 일치하지 않습니다.";
		} else if (errorMessage.indexOf("data 532") > 0) {
			responseMessage = "LDAP 암호가 만료되었습니다.";
		} else if (errorMessage.indexOf("data 533") > 0) {
			responseMessage = "LDAP 비활성화된 계정입니다.";
		} else if (errorMessage.indexOf("data 701") > 0) {
			responseMessage = "LDAP 계정이 만료되었습니다.";
		} else if (errorMessage.indexOf("data 773") > 0) {
			responseMessage = "LDAP 계정 비밀번호가 만료되었습니다.";
		}

		return new Response("20", responseMessage);
	}

	@ExceptionHandler(NamingException.class)
	public Response namingException(NamingException e) {
		e.printStackTrace();
		return new Response("21", "LDAP 처리 중 오류가 발생하였습니다.\n관리자에게 문의해주세요.");
	}

	@ExceptionHandler(Exception.class)
	public Response exception(Exception e) {
		e.printStackTrace();
		return new Response("22", "처리 중 오류가 발생하였습니다.\n관리자에게 문의해주세요.");
	}
}