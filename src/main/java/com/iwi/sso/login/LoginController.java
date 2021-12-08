package com.iwi.sso.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/login")
@Controller
public class LoginController {

	@GetMapping("/login.do")
	public String home() throws Exception {

		// System.out.println(SecureUtil.getEncPassword("supark@iwi.co.kr", "01030950409"));
		// System.out.println(SecureUtil.getEncPassword("jgs@iwi.co.kr", "01059534237"));
		// System.out.println(SecureUtil.getEncPassword("bsy@iwi.co.kr", "01091559504"));
		// System.out.println(SecureUtil.getEncPassword("cysong@iwi.co.kr", "01020509259"));
		// System.out.println(SecureUtil.getEncPassword("youjh@iwi.co.kr", "01040362193"));
		// System.out.println(SecureUtil.getEncPassword("heonil@iwi.co.kr", "01041757452"));
		// System.out.println(SecureUtil.getEncPassword("jgkim@iwi.co.kr", "01056686827"));
		// System.out.println(SecureUtil.getEncPassword("msko@iwi.co.kr", "01091096031"));
		// System.out.println(SecureUtil.getEncPassword("jsj@iwi.co.kr", "01045449346"));
		// System.out.println(SecureUtil.getEncPassword("giwon@iwi.co.kr", "01063117816"));
		// System.out.println(SecureUtil.getEncPassword("kdu@iwi.co.kr", "01084400032"));
		// System.out.println(SecureUtil.getEncPassword("jinwoo@iwi.co.kr", "01072729323"));
		// System.out.println(SecureUtil.getEncPassword("thjo@iwi.co.kr", "01054995070"));
		// System.out.println(SecureUtil.getEncPassword("mina@iwi.co.kr", "01040745369"));
		// System.out.println(SecureUtil.getEncPassword("thkim@iwi.co.kr", "01096649026"));
		// System.out.println(SecureUtil.getEncPassword("ceh@iwi.co.kr", "01051122805"));
		// System.out.println(SecureUtil.getEncPassword("kyy@iwi.co.kr", "01053789317"));
		// System.out.println(SecureUtil.getEncPassword("sykim@iwi.co.kr", "01072255416"));
		// System.out.println(SecureUtil.getEncPassword("shlee@iwi.co.kr", "01027677894"));
		// System.out.println(SecureUtil.getEncPassword("garam@iwi.co.kr", "01064305870"));
		// System.out.println(SecureUtil.getEncPassword("dlrbgh1119@iwi.co.kr", "01041736662"));
		// System.out.println(SecureUtil.getEncPassword("nmh@iwi.co.kr", "01033099866"));
		// System.out.println(SecureUtil.getEncPassword("sbr@iwi.co.kr", "01091799336"));
		// System.out.println(SecureUtil.getEncPassword("kjg@iwi.co.kr", "01091663305"));
		// System.out.println(SecureUtil.getEncPassword("yukikait@iwi.co.kr", "01022859701"));
		// System.out.println(SecureUtil.getEncPassword("dklee@iwi.co.kr", "01033831718"));
		// System.out.println(SecureUtil.getEncPassword("stattis@iwi.co.kr", "01051607404"));
		// System.out.println(SecureUtil.getEncPassword("shpark@iwi.co.kr", "01074975557"));
		// System.out.println(SecureUtil.getEncPassword("hhjung@iwi.co.kr", "01095573458"));
		// System.out.println(SecureUtil.getEncPassword("sbkim32@iwi.co.kr", "01095097593"));
		// System.out.println(SecureUtil.getEncPassword("hjkang@iwi.co.kr", "01028461149"));
		// System.out.println(SecureUtil.getEncPassword("theo@iwi.co.kr", "01042274622"));
		// System.out.println(SecureUtil.getEncPassword("shw@iwi.co.kr", "01063352542"));
		// System.out.println(SecureUtil.getEncPassword("msshin@iwi.co.kr", "01023387354"));
		// System.out.println(SecureUtil.getEncPassword("karminea@iwi.co.kr", "01086656029"));
		// System.out.println(SecureUtil.getEncPassword("kjs@iwi.co.kr", "01048105864"));
		// System.out.println(SecureUtil.getEncPassword("jikim@iwi.co.kr", "01036684857"));
		// System.out.println(SecureUtil.getEncPassword("lee@iwi.co.kr", "01053980010"));

		// System.out.println(StringUtil.getFormatTel("02059532"));
		// System.out.println(StringUtil.getFormatTel("01091559504"));
		// System.out.println(StringUtil.getFormatTel("01020509259"));
		// System.out.println(StringUtil.getFormatTel("01040362193"));
		// System.out.println(StringUtil.getFormatTel("01041757452"));
		// System.out.println(StringUtil.getFormatTel("01056686827"));
		// System.out.println(StringUtil.getFormatTel("01091096031"));
		// System.out.println(StringUtil.getFormatTel("01045449346"));
		// System.out.println(StringUtil.getFormatTel("01063117816"));
		// System.out.println(StringUtil.getFormatTel("01084400032"));
		// System.out.println(StringUtil.getFormatTel("01072729323"));
		// System.out.println(StringUtil.getFormatTel("01054995070"));
		// System.out.println(StringUtil.getFormatTel("01040745369"));
		// System.out.println(StringUtil.getFormatTel("01096649026"));
		// System.out.println(StringUtil.getFormatTel("01051122805"));
		// System.out.println(StringUtil.getFormatTel("01053789317"));
		// System.out.println(StringUtil.getFormatTel("01072255416"));
		// System.out.println(StringUtil.getFormatTel("01027677894"));
		// System.out.println(StringUtil.getFormatTel("01064305870"));
		// System.out.println(StringUtil.getFormatTel("01041736662"));
		// System.out.println(StringUtil.getFormatTel("01033099866"));
		// System.out.println(StringUtil.getFormatTel("01091799336"));
		// System.out.println(StringUtil.getFormatTel("01091663305"));
		// System.out.println(StringUtil.getFormatTel("01022859701"));
		// System.out.println(StringUtil.getFormatTel("01033831718"));
		// System.out.println(StringUtil.getFormatTel("01051607404"));
		// System.out.println(StringUtil.getFormatTel("01074975557"));
		// System.out.println(StringUtil.getFormatTel("01095573458"));
		// System.out.println(StringUtil.getFormatTel("01095097593"));
		// System.out.println(StringUtil.getFormatTel("01028461149"));
		// System.out.println(StringUtil.getFormatTel("01042274622"));
		// System.out.println(StringUtil.getFormatTel("01063352542"));
		// System.out.println(StringUtil.getFormatTel("01023387354"));
		// System.out.println(StringUtil.getFormatTel("01086656029"));
		// System.out.println(StringUtil.getFormatTel("01048105864"));
		// System.out.println(StringUtil.getFormatTel("01036684857"));
		// System.out.println(StringUtil.getFormatTel("01053980010"));

		return "login/login";
	}

}
