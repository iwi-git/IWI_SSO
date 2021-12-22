package com.iwi.sso.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IMap;
import com.iwi.sso.util.StringUtil;

@Service
public class AopServiceImpl implements AopService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.aop.Aop.";

	@Override
	public IMap selectApiAuthInfo(String authKey, String referer) throws Exception {
		authKey = authKey.replace("Bearer ", "");
		referer = StringUtil.getDomainInfo(referer);

		IMap map = new IMap();
		map.put("authKey", authKey);
		map.put("domain", referer);
		return (IMap) dao.select(NAMESPACE + "selectApiAuthInfo", map);
	}

	@Override
	public void insertApiAccessLog(IMap map) throws Exception {
		dao.insert(NAMESPACE + "insertApiAccessLog", map);
	}

}
