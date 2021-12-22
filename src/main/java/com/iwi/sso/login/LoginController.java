package com.iwi.sso.login;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iwi.sso.util.PropsUtil;

@Controller
public class LoginController {

	@GetMapping("/")
	public String home(HttpServletRequest request) throws Exception {

		// System.out.println("############################### ldap test start ###############################");
		//
		// List<IMap> userList = userService.getUserInfo();
		// System.out.println(userList);
		//
		// for (IMap user : userList) {
		// String email = user.getString("email");
		// String name = user.getString("name");
		// String dept = user.getString("dept");
		// String posi = user.getString("posi");
		// String comp = user.getString("comp");
		// IMap map = LDAPUtil.getAccountInfo(email);
		// if (map == null || map.isEmpty()) {
		// // System.out.println("$$$$$$ " + email + " : 존재하지 않는 사용자입니다.");
		// } else {
		// System.out.println("@@@@@@ " + email + " : " + name);
		// String DN = map.getString("distinguishedName");
		// IMap attr = new IMap();
		// attr.put("company", comp);
		// attr.put("department", dept);
		// attr.put("title", posi);
		// LDAPUtil.setAttribute(DN, attr);
		// }
		// System.out.println("------------------------------------------------------------------------");
		// }
		// System.out.println("############################### ldap test end ###############################");

		return "redirect:/login/login.do";
	}

	@GetMapping("/login/login.do")
	public String login() throws Exception {
		// System.out.println(SecureUtil.getEncPassword("kjg@iwi.co.kr", "01091663305"));
		// System.out.println(StringUtil.getFormatTel("01091663305"));
		return "login/login";
	}

}
