package com.iwi.sso.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

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
	@PostMapping("/signup")
	@ResponseBody
	public Response signup(@RequestBody IMap body) throws Exception {
		return new Response(authService.createToken(body));
	}

	/**
	 * 토큰 갱신
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/auth/refresh")
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
	@PostMapping("/auth/valid")
	@ResponseBody
	public Response authValid(@RequestBody IMap body) throws Exception {
		return new Response(authService.validationToken(body));
	}

	/**
	 * 사이트별 사용자 키 조회
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/auth/sitekey")
	@ResponseBody
	public Response authSiteKey(@RequestBody IMap body) throws Exception {
		return new Response(authService.getTokenSiteKey(body));
	}

}
