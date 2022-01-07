package com.iwi.sso.api.comp;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

@RestController
@RequestMapping("/api/comp")
public class ApiCompController {

	@Autowired
	private ApiCompService apiCompService;

	@GetMapping("/dept")
	@ResponseBody
	public Response dept(@RequestParam Map<String, String> paramMap) throws Exception {
		return new Response(apiCompService.selectDept(new IMap(paramMap)));
	}

	@GetMapping("/posi")
	@ResponseBody
	public Response posi() throws Exception {
		return new Response(apiCompService.selectPosi());
	}

	@GetMapping("/duty")
	@ResponseBody
	public Response duty() throws Exception {
		return new Response(apiCompService.selectDuty());
	}

}
