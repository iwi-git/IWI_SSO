package com.iwi.sso.manage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/manage")
@Controller
public class ManageController {

	@GetMapping("/userList.do")
	public String userList() throws Exception {
		return null;
	}

}
