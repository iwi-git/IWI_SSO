package com.iwi.sso.api.auth;

import javax.servlet.http.HttpServletRequest;

import com.iwi.sso.common.IMap;

public interface AuthService {

	public IMap signinProc(IMap map) throws Exception;

	public IMap createToken(IMap map) throws Exception;

	public IMap refreshToken(IMap map) throws Exception;

	public boolean validationToken(IMap map) throws Exception;

	public IMap getTokenSiteKey(IMap map, HttpServletRequest request) throws Exception;

	public void setUserSiteKey(IMap map, HttpServletRequest request) throws Exception;

	public void setUserLastLogin(IMap map, HttpServletRequest request) throws Exception;

}
