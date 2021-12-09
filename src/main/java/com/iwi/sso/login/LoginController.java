package com.iwi.sso.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/login")
@Controller
public class LoginController {

	@GetMapping("/login.do")
	public String home() throws Exception {

		// System.out.println(SecureUtil.getEncPassword("kjg@iwi.co.kr", "01091663305"));
		// System.out.println(StringUtil.getFormatTel("01091663305"));

		return "login/login";
	}

}
