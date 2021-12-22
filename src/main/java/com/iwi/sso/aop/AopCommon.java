package com.iwi.sso.aop;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.util.SecureUtil;
import com.iwi.sso.util.StringUtil;

@Aspect
@Component
public class AopCommon {

	@Autowired
	private AopService aopService;

	/**
	 * Auth AOP
	 */
	@Pointcut("execution(* com.iwi.sso.auth.*Controller.*(..))")
	public void authPointcut() {
	}

	/**
	 * Api AOP
	 */
	@Pointcut("execution(* com.iwi.sso.api..*Controller.*(..))")
	public void apiPointcut() {
	}

	/**
	 * Before auth/api 헤더 인증 정보 확인
	 * 
	 * @throws Exception
	 */
	@Before("authPointcut() || apiPointcut()")
	public void checkAuthorization() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		// String remoteAddr = request.getRemoteAddr();
		// System.out.println("##### remoteAddr ::: " + remoteAddr);

		// String requestURI = request.getRequestURI();
		// System.out.println("##### requestURI ::: " + requestURI);

		//header 정보 확인
		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
		String name = (String) headerNames.nextElement();
		String value = request.getHeader(name);
		System.out.println("##### request header ::: " + name + " = " + value);
		}

		String authKey = request.getHeader("Authorization");
		if (StringUtils.isEmpty(authKey)) {
			throw new IException("헤더 정보를 찾을수 없습니다.");
		} else {
			authKey = authKey.replace("Bearer ", "");

			// refere 체크
			String referer = request.getHeader("referer");
			if (StringUtils.isEmpty(referer)) {
				throw new IException("유효하지 않은 요청입니다.");
			}

			// referer 에서 domain 추출
			String domain = StringUtil.getDomainInfo(referer);

			// 인증 허용 정보 조회
			IMap authMap = aopService.selectApiAuthInfo(authKey, domain);
			if (authMap == null) {
				throw new IException("유효하지 않은 헤더 정보 입니다.");
			}

			// request 에 인증 허용 도메인 추가
			request.setAttribute("authAllowDomain", authMap.getString("domain"));
		}
	}

	/**
	 * After auth/api TB_API_ACCESS_LOG 작성
	 * 
	 * @param joinPoint
	 * @throws Exception
	 */
	@After("authPointcut() || apiPointcut()")
	public void insertLog(JoinPoint joinPoint) throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		IMap map = new IMap();

		map.put("accUrl", request.getRequestURI());
		map.put("accIp", request.getRemoteAddr());

		map.put("authorization", request.getHeader("authorization"));
		map.put("contentType", request.getHeader("content-type"));
		map.put("userAgent", request.getHeader("user-agent"));
		map.put("referer", request.getHeader("referer"));

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		String methodName = method.getName();

		String bodyJson = "";
		Object[] args = joinPoint.getArgs();
		for (Object obj : args) {
			if (obj instanceof Map) {
				Map<String, String> body = (Map<String, String>) obj;
				if ("signin".equals(methodName)) {
					if (body.containsKey("password") && body.containsKey("email")) {
						// 보안을 위해 password 암호화
						body.put("password", SecureUtil.getEncPassword(new IMap(body)));
					}
				}
				bodyJson += new JSONObject(body).toString();
			} else if (obj instanceof List) {
				List<Map<String, String>> body = (List<Map<String, String>>) obj;
				bodyJson += new JSONArray(body).toString();
			}
		}
		map.put("reqBodyJson", bodyJson);

		String paramJson = "";
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Map<String, String[]> paramMap = request.getParameterMap();
		for (String key : paramMap.keySet()) {
			String[] values = paramMap.get(key);
			if (values != null && values.length == 1) {
				jsonMap.put(key, values[0]);
			} else {
				jsonMap.put(key, values);
			}
		}
		if (!jsonMap.isEmpty()) {
			paramJson = new JSONObject(jsonMap).toString();
		}
		map.put("reqParamJson", paramJson);

		aopService.insertApiAccessLog(map);
	}

}