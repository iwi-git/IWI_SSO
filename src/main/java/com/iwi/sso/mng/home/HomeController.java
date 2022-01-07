package com.iwi.sso.mng.home;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class HomeController {

	@GetMapping()
	public String home(HttpServletResponse response) throws Exception {
		return "home/home";
	}

}
