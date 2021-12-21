package com.iwi.sso.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;

public interface UserService {

	public List<IMap> getUserInfo() throws Exception;

	public List<IMap> getUserInfo(IMap map) throws Exception;

	public int setUserLink(Object obj, HttpServletRequest request) throws Exception;

}
