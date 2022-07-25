package com.iwi.sso.auth;

import com.iwi.sso.common.IMap;

public interface AuthService {

	public IMap signinProc(IMap map) throws Exception;

	public IMap signoutProc() throws Exception;

	public IMap validateToken() throws Exception;

	public IMap validateToken(String acsToken) throws Exception;

	public IMap refreshToken() throws Exception;

	public IMap refreshToken(String acsToken, String refToken) throws Exception;

	public IMap getUserInfo() throws Exception;

	public IMap getUserInfo(String acsToken) throws Exception;

	public IMap selectUserInfo(IMap map) throws Exception;

}
