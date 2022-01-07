package com.iwi.sso.api.comp;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IMap;

@Service
public class ApiCompServiceImpl implements ApiCompService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.api.comp.ApiComp.";

	@SuppressWarnings("unchecked")
	@Override
	public List<IMap> selectDept(IMap map) throws Exception {
		return dao.list(NAMESPACE + "selectDept", map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IMap> selectPosi() throws Exception {
		return dao.list(NAMESPACE + "selectPosi", null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IMap> selectDuty() throws Exception {
		return dao.list(NAMESPACE + "selectDuty", null);
	}
}
