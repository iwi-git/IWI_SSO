package com.iwi.sso.api.comp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iwi.sso.common.Response;

@RestController
@RequestMapping("/api/comp")
public class ApiCompController {

	@Autowired
	private ApiCompService apiCompService;

	@GetMapping("/dept")
	@ResponseBody
	public Response signin() throws Exception {
		return new Response(apiCompService.selectDept());
	}

}
