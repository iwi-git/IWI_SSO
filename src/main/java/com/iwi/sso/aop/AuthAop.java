package com.iwi.sso.aop;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwi.sso.auth.AuthService;
import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.util.StringUtil;

@Aspect
@Component
public class AuthAop {

	@Autowired
	private AuthService authService;

	@Pointcut("execution(* com.iwi.sso.auth.AuthController.*(..))")
	public void authPointcut() {
	}

	@Before("authPointcut()")
	public void beforeAuthPointcut() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		// String remoteAddr = request.getRemoteAddr();
		// System.out.println("##### remoteAddr ::: " + remoteAddr);

		// String requestURI = request.getRequestURI();
		// System.out.println("##### requestURI ::: " + requestURI);

		// header 정보 확인
		// Enumeration<?> headerNames = request.getHeaderNames();
		// while (headerNames.hasMoreElements()) {
		// String name = (String) headerNames.nextElement();
		// String value = request.getHeader(name);
		// System.out.println("##### request header ::: " + name + " = " + value);
		// }

		String authKey = request.getHeader("Authorization");
		if (StringUtils.isEmpty(authKey)) {
			throw new IException("헤더 정보를 찾을수 없습니다.");
		} else {
			authKey = authKey.replace("Bearer ", "");

			// 20211207 DB 교차 검증 방식으로 변경됨
			// if (!authKey.equals(SystemConst.API_KEY)) {
			// throw new IException("유효하지 않은 헤더 정보 입니다.");
			// }

			// refere 체크
			String referer = request.getHeader("referer");
			if (StringUtils.isEmpty(referer)) {
				throw new IException("유효하지 않은 요청입니다.");
			}

			// referer 에서 domain 추출
			String domain = StringUtil.getDomainInfo(referer);

			// 인증 허용 정보 조회
			IMap authMap = authService.selectAllowAuthInfo(authKey, domain);
			if (authMap == null) {
				throw new IException("유효하지 않은 헤더 정보 입니다.");
			}
		}
	}

}