package com.iwi.sso.mng.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/login")
@Controller
public class LoginController {

	@GetMapping()
	public String login(HttpServletRequest request, Model model) throws Exception {
		String loginReferer = (String) request.getAttribute("loginReferer");
		model.addAttribute("loginReferer", loginReferer);
		return "login/login";
	}

}
