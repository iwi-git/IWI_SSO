package com.iwi.sso.api.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

@RequestMapping("/api/user")
@RestController
public class ApiUserController {

	@Autowired
	private ApiUserService userService;

	@PostMapping
	@ResponseBody
	public Response user(@RequestBody(required = false) IMap body) throws Exception {
		return userService.getUserInfo(body);
	}

	@PostMapping("/link")
	@ResponseBody
	public Response link(@RequestBody Object body, HttpServletRequest request) throws Exception {
		return userService.setUserLink(body, request);
	}
}
