package com.iwi.sso.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.iwi.sso.common.IException;
import com.iwi.sso.common.Response;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IException.class)
	public Response iException(IException e) {
		// e.printStackTrace();
		if (StringUtils.isEmpty(e.getMessage())) {
			return this.exception(e);
		}
		return new Response(false, e.getMessage());
	}

	@ExceptionHandler(SignatureException.class)
	public Response signatureException(SignatureException e) {
		// e.printStackTrace();
		return new Response(false, "토큰 검증 실패");
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public Response expiredJwtException(ExpiredJwtException e) {
		// e.printStackTrace();
		return new Response(false, "인증 토큰 만료");
	}

	@ExceptionHandler(Exception.class)
	public Response exception(Exception e) {
		e.printStackTrace();
		return new Response(false, "처리 중 오류가 발생하였습니다.\n관리자에게 문의해주세요.");
	}
}