package com.iwi.sso.mng.user;

import java.util.List;

import com.iwi.sso.common.IMap;

public interface UserService {

	public List<IMap> selectUserList(IMap map) throws Exception;

	public IMap selectUser(IMap map) throws Exception;

	public IMap saveUser(IMap map) throws Exception;

}
