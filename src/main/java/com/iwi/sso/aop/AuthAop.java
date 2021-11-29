package com.iwi.sso.aop;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.iwi.sso.common.IException;
import com.iwi.sso.common.SystemConst;

@Aspect
@Component
public class AuthAop {

	@Pointcut("execution(* com.iwi.sso.auth.AuthController.*(..))")
	public void authPointcut() {}

	@Before("authPointcut()")
	public void beforeAuthPointcut() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		String apiKey = request.getHeader("Authorization");
		if (StringUtils.isEmpty(apiKey)) {
			throw new IException("헤더 정보를 찾을수 없습니다.");
		} else {
			apiKey = apiKey.replace("Bearer ", "");
			if (!apiKey.equals(SystemConst.API_KEY)) {
				throw new IException("유효하지 않은 헤더 정보 입니다.");
			}
		}
	}

}