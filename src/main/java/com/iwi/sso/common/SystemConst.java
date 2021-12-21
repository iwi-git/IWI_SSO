package com.iwi.sso.common;

public class SystemConst {

	// IWI Default Domain
	public static final String DOMAIN_IWI = "iwi.co.kr";

	// SSO API
	public static final String SECRET_KEY = "IWORKSINTERACTIVE2021"; // 토큰 생성 암호 키

	// SSO TOKEN
	public static final long ACS_TOKEN_VALID_MINUTES = 10; // 엑세스 토큰 생성 시간 (10분)
	public static final long REF_TOKEN_VALID_MINUTES = 60 * 24; // 리프레쉬 토큰 생성 시간 (1일)

	// Active Directory
	public static final String AD_SERVER = "LDAP://rainbow.iwi.co.kr:389";
	public static final String AD_DOMAIN = "iwi.co.kr";
	public static final String AD_ID = "administrator";
	public static final String AD_PW = "iworks2021!";
	public static final String AD_BASE = "OU=iworks,dc=iwi,dc=co,dc=kr";

}
