package com.iwi.sso.mng.user;

import java.util.Map;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwi.sso.api.comp.ApiCompService;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

@RequestMapping("/mng")
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ApiCompService apiCompService;

	@GetMapping("/user")
	public String user(Model model, @RequestParam Map<String, String> paramMap) throws Exception {
		model.addAttribute("deptList", new JSONArray(apiCompService.selectDept(null)));
		model.addAttribute("posiList", apiCompService.selectPosi());
		model.addAttribute("dutyList", apiCompService.selectDuty());
		return "mng/user";
	}

	@ResponseBody
	@GetMapping("/user/list")
	public Response userList(@RequestParam Map<String, String> paramMap) throws Exception {
		return new Response(userService.selectUserList(new IMap(paramMap)));
	}

	@ResponseBody
	@GetMapping("/user/{userSeq:[\\d]+}")
	public Response userInfo(@PathVariable long userSeq) throws Exception {
		return new Response(userService.selectUser(new IMap("userSeq", userSeq)));
	}

	@ResponseBody
	@PostMapping("/user/save")
	public Response userSave(@RequestParam Map<String, String> paramMap) throws Exception {
		return new Response(userService.saveUser(new IMap(paramMap)));
	}

}
