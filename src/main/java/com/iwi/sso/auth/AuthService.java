package com.iwi.sso.auth;

import com.iwi.sso.common.IMap;

public interface AuthService {

	public IMap signinProc(IMap map) throws Exception;

	public IMap createToken(IMap map) throws Exception;

	public IMap refreshToken(IMap map) throws Exception;

	public boolean validationToken(IMap map) throws Exception;

	public IMap getTokenSiteKey(IMap imap) throws Exception;

}
