package com.iwi.sso.aop;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AopManage {

	/**
	 * Manage AOP
	 */
	@Pointcut("execution(* com.iwi.sso.manage.*.*(..)) || execution(* com.iwi.sso.manage.*.*())")
	public void managePointcut() {
	}

	/**
	 * Before auth/api 헤더 인증 정보 확인
	 * 
	 * @throws Exception
	 */
	@Around("managePointcut()")
	public Object checkAdmin() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String remoteAddr = request.getRemoteAddr();
		System.out.println("##### remoteAddr ::: " + remoteAddr);

		String requestURI = request.getRequestURI();
		System.out.println("##### requestURI ::: " + requestURI);

		// header 정보 확인
		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String name = (String) headerNames.nextElement();
			String value = request.getHeader(name);
			System.out.println("##### request header ::: " + name + " = " + value);
		}

		String acsToken = null;
		String refToken = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			if ("t".equals(name)) {
				acsToken = cookie.getValue();
			} else if ("t1".equals(name)) {
				refToken = cookie.getValue();
			}
		}

		System.out.println("##### [cookie] access token  : " + acsToken);
		System.out.println("##### [cookie] refresh token : " + refToken);

		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(acsToken)) {
			return "redirect:/login/login.do";
		}

		return null;

	}

}
