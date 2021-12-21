package com.iwi.sso.auth;

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
	 * SSO 로그인 처리
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/signin")
	@ResponseBody
	public Response signin(@RequestBody IMap body) throws Exception {
		return new Response(authService.signinProc(body));
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
	public Response refresh(@RequestBody IMap body) throws Exception {
		return new Response(authService.refreshToken(body));
	}

	/**
	 * 내 정보 조회
	 * 
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/me")
	@ResponseBody
	public Response me(@RequestBody IMap body) throws Exception {
		return new Response(authService.getUserInfo(body));
	}

}
