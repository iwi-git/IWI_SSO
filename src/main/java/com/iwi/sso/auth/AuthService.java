package com.iwi.sso.auth;

import com.iwi.sso.common.IMap;

public interface AuthService {

	public IMap signinProc(IMap map) throws Exception;

	public IMap refreshToken(IMap map) throws Exception;

	public IMap getUserInfo(IMap map) throws Exception;

}
