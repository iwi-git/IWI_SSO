package com.iwi.sso.aop;

import com.iwi.sso.common.IMap;

public interface AopService {

	public IMap selectAllowAuthInfo(String authKey, String domain) throws Exception;

}
