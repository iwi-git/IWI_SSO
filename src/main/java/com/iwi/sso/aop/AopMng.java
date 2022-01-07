package com.iwi.sso.aop;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwi.sso.auth.AuthService;
import com.iwi.sso.common.IMap;

import io.jsonwebtoken.ExpiredJwtException;

@Aspect
@Component
public class AopMng {

	@Autowired
	private AuthService authService;

	@Pointcut("execution(* com.iwi.sso.mng.**.*Controller.*(..)) && @annotation(org.springframework.web.bind.annotation.ResponseBody)")
	public void mngPointcutResponseBody() {
	}

	@Pointcut("execution(* com.iwi.sso.mng.**.*Controller.*(..)) && !@annotation(org.springframework.web.bind.annotation.ResponseBody)")
	public void mngPointcut() {
	}

	@Around("mngPointcutResponseBody()")
	public Object beforeMngResponseBody(ProceedingJoinPoint pjp) throws Throwable {
		if (!this.checkAuth()) {
			throw new ExpiredJwtException(null, null, null);
		}
		return pjp.proceed();
	}

	@Around("mngPointcut()")
	public Object beforeMng(ProceedingJoinPoint pjp) throws Throwable {
		if (!this.checkAuth()) {
			return "redirect:/login";
		}
		return pjp.proceed();
	}

	private boolean checkAuth() throws Exception {
		boolean isAuthProc = true;

		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String currURI = request.getRequestURI();
		if (!currURI.startsWith("/login")) {
			String acsToken = null;
			String refToken = null;
			IMap sessionUser = null;

			try {

				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						String name = cookie.getName();
						if ("I-ACCESS".equals(name)) {
							acsToken = cookie.getValue();
						} else if ("I-REFRESH".equals(name)) {
							refToken = cookie.getValue();
						}
					}
				}

				isAuthProc = (!StringUtils.isEmpty(acsToken) && !StringUtils.isEmpty(refToken));

				// 토큰 검증
				IMap map = new IMap();
				if (isAuthProc) {
					try {
						authService.validateToken(acsToken);
					} catch (ExpiredJwtException ea) {
						// 엑세스 토큰 만료 시 토큰 갱신
						try {
							map = authService.refreshToken(acsToken, refToken);
							acsToken = map.getString("acsToken");
							refToken = map.getString("refToken");
						} catch (ExpiredJwtException er) {
							// 리프레시 토큰 만료 시 인증 종료
							isAuthProc = false;
						}
					}
				}

				// 토큰 사용자 정보 추출
				if (isAuthProc) {
					sessionUser = authService.getUserInfo(acsToken);
					if (sessionUser == null) {
						isAuthProc = false;
					}
				}
			} catch (Exception e) {
				isAuthProc = false;
			}

			if (isAuthProc) {
				request.setAttribute("session", sessionUser);
			} else {
				request.setAttribute("session", null);
			}
		}

		return isAuthProc;
	}

}