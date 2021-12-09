package com.iwi.sso.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

@RequestMapping("/auth")
@RestController
public class AuthController {

	@Autowired
	private AuthService authService;

	/**
	 * 토큰 발급
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping
	@ResponseBody
	public Response auth(@RequestBody IMap body, HttpServletRequest request) throws Exception {
		Response res = new Response(authService.createToken(body));
		if (res.isSuccess()) {
			authService.setUserSiteKey(body, request);
			authService.setUserLastLogin(body, request);
		}
		return res;
	}

	/**
	 * 토큰 갱신
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/refresh")
	@ResponseBody
	public Response authRefresh(@RequestBody IMap body) throws Exception {
		return new Response(authService.refreshToken(body));
	}

	/**
	 * 토큰 검증
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/valid")
	@ResponseBody
	public Response authValid(@RequestBody IMap body) throws Exception {
		return new Response(authService.validationToken(body));
	}

	/**
	 * 사이트별 사용자 키 조회
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/sitekey")
	@ResponseBody
	public Response authSiteKey(@RequestBody IMap body, HttpServletRequest request) throws Exception {
		return new Response(authService.getTokenSiteKey(body, request));
	}

	/**
	 * SSO 로그인 처리
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/signin")
	@ResponseBody
	public Response signin(@RequestBody IMap body, HttpServletResponse response) throws Exception {
		return new Response(authService.signinProc(body));
	}

}
