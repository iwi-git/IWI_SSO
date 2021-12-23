package com.iwi.sso.auth;

import com.iwi.sso.common.IMap;

public interface AuthService {

	public IMap signinProc(IMap map) throws Exception;

	public IMap refreshToken() throws Exception;

	public IMap getUserInfo() throws Exception;

}
