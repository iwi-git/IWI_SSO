package com.iwi.sso.api.user;

import javax.servlet.http.HttpServletRequest;

import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

public interface UserService {

	public Response getUserInfo(IMap map) throws Exception;

	public Response setUserLink(Object obj, HttpServletRequest request) throws Exception;

}
