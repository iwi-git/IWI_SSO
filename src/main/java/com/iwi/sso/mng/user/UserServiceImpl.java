package com.iwi.sso.mng.user;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IMap;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.mng.user.User.";

	@SuppressWarnings("unchecked")
	@Override
	public List<IMap> selectUserList(IMap map) throws Exception {
		return dao.list(NAMESPACE + "selectUser", map);
	}

	@Override
	public IMap selectUser(IMap map) throws Exception {
		return new IMap(dao.select(NAMESPACE + "selectUser", map));
	}

	@Override
	public IMap saveUser(IMap map) throws Exception {
		String userSeq = map.getString("userSeq");
		if (StringUtils.isEmpty(userSeq)) {
			System.out.println("신규사용자");
		} else {
			System.out.println("기존사용자 : " + userSeq);
		}
		return new IMap();
	}

}
