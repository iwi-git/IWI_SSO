package com.iwi.sso.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IMap;

@Service
public class AopServiceImpl implements AopService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.aop.Aop.";

	@Override
	public IMap selectAllowAuthInfo(String authKey, String domain) throws Exception {
		IMap map = new IMap();
		map.put("authKey", authKey);
		map.put("domain", domain);
		return (IMap) dao.select(NAMESPACE + "selectAllowAuthInfo", map);
	}

	@Override
	public void insertApiAccessLog(IMap map) throws Exception {
		dao.insert(NAMESPACE + "insertApiAccessLog", map);
	}

}
