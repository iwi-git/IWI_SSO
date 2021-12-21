package com.iwi.sso.aop;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwi.sso.common.IException;
import com.iwi.sso.common.SystemConst;
import com.iwi.sso.util.StringUtil;

@Aspect
@Component
public class AopCommon {

	// @Autowired
	// private AopService aopService;

	/**
	 * SSO 모든 기능은 *.iwi.co.kr 도메인에서만 작동함
	 */
	@Pointcut("execution(* com.iwi.sso.**.*Controller.*(..))")
	public void allPointcut() {
	}

	/**
	 * Before auth/api 헤더 인증 정보 확인
	 * 
	 * @throws Exception
	 */
	@Before("allPointcut()")
	public void checkAll() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		// refere 체크
		String referer = request.getHeader("referer");
		if (StringUtils.isEmpty(referer)) {
			throw new IException("유효하지 않은 요청입니다.");
		}

		// referer IWI 도메인 체크
		String domain = StringUtil.getDomainInfo(referer);
		if (!domain.endsWith(SystemConst.DOMAIN_IWI)) {
			throw new IException("유효하지 않은 요청입니다.");
		}
	}

	// / **
	// * After auth/api TB_API_ACCESS_LOG 작성
	// *
	// * @param joinPoint
	// * @throws Exception
	// */
	// @After("authPointcut()")
	// public void insertLog(JoinPoint joinPoint) throws Exception {
	// ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
	// HttpServletRequest request = requestAttributes.getRequest();
	//
	// IMap map = new IMap();
	//
	// map.put("accUrl", request.getRequestURI());
	// map.put("accIp", request.getRemoteAddr());
	//
	// map.put("authorization", request.getHeader("authorization"));
	// map.put("contentType", request.getHeader("content-type"));
	// map.put("userAgent", request.getHeader("user-agent"));
	// map.put("referer", request.getHeader("referer"));
	//
	// MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
	// Method method = methodSignature.getMethod();
	// String methodName = method.getName();
	//
	// String bodyJson = "";
	// Object[] args = joinPoint.getArgs();
	// for (Object obj : args) {
	// if (obj instanceof Map) {
	// Map<String, String> body = (Map<String, String>) obj;
	// if ("signin".equals(methodName)) {
	// if (body.containsKey("password") && body.containsKey("email")) {
	// // 보안을 위해 password 암호화
	// body.put("password", SecureUtil.getEncPassword(new IMap(body)));
	// }
	// }
	// bodyJson += new JSONObject(body).toString();
	// } else if (obj instanceof List) {
	// List<Map<String, String>> body = (List<Map<String, String>>) obj;
	// bodyJson += new JSONArray(body).toString();
	// }
	// }
	// map.put("reqBodyJson", bodyJson);
	//
	// String paramJson = "";
	// Map<String, Object> jsonMap = new HashMap<String, Object>();
	// Map<String, String[]> paramMap = request.getParameterMap();
	// for (String key : paramMap.keySet()) {
	// String[] values = paramMap.get(key);
	// if (values != null && values.length == 1) {
	// jsonMap.put(key, values[0]);
	// } else {
	// jsonMap.put(key, values);
	// }
	// }
	// if (!jsonMap.isEmpty()) {
	// paramJson = new JSONObject(jsonMap).toString();
	// }
	// map.put("reqParamJson", paramJson);
	//
	// aopService.insertApiAccessLog(map);
	// }

}