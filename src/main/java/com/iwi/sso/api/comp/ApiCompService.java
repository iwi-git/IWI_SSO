package com.iwi.sso.api.comp;

import java.util.List;

import com.iwi.sso.common.IMap;

public interface ApiCompService {

	public List<IMap> selectDept(IMap map) throws Exception;

	public List<IMap> selectPosi() throws Exception;

	public List<IMap> selectDuty() throws Exception;

}
