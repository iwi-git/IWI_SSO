package com.iwi.sso.api.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;
import com.iwi.sso.user.UserService;

@RequestMapping("/api/user")
@RestController
public class ApiUserController {

	@Autowired
	private UserService userService;

	@PostMapping
	@ResponseBody
	public Response user(@RequestBody(required = false) IMap body) throws Exception {
		List<IMap> list = userService.getUserInfo(body);
		if (list.size() == 1) {
			return new Response(new IMap(list));
		}
		return new Response(list);
	}

	@PostMapping("/link")
	@ResponseBody
	public Response link(@RequestBody Object body, HttpServletRequest request) throws Exception {
		return new Response(userService.setUserLink(body, request));
	}
}
