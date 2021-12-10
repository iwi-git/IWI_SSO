package com.iwi.sso.api.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.Response;
import com.iwi.sso.util.StringUtil;

@Service
public class ApiUserServiceImpl implements ApiUserService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.api.user.ApiUser.";

	@Override
	public Response getUserInfo(IMap map) throws Exception {
		if (map != null && (StringUtils.isEmpty(map.getString("id")) || StringUtils.isEmpty(map.getString("name")))) {
			throw new IException("필수 파라미터 누락");
		}

		List<IMap> userList = this.selectUser(map);
		if (userList == null || userList.size() == 0) {
			throw new IException("사원 정보 없음");
		}

		if (map == null) {
			return new Response(userList);
		} else {
			return new Response(new IMap(userList));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response setUserLink(Object obj, HttpServletRequest request) throws Exception {
		List<Map<String, String>> list = null;

		if (obj instanceof List) {
			list = (List<Map<String, String>>) obj;
		} else if (obj instanceof Map) {
			list = new ArrayList<Map<String, String>>();
			list.add((Map<String, String>) obj);
		}

		if (list == null || list.size() < 1 || list.get(0).isEmpty()) {
			throw new IException("유효하지 않은 요청입니다.");
		}

		int resCnt = 0;

		String domain = StringUtil.getDomainInfo(request);
		String allowDomain = (String) request.getAttribute("authAllowDomain");

		if (!StringUtils.isEmpty(domain) && domain.toLowerCase().endsWith(allowDomain)) {
			for (Map<String, String> map : list) {
				IMap imap = new IMap(map);
				if (!StringUtils.isEmpty(imap.getString("seq")) && !StringUtils.isEmpty(imap.getString("email"))) {
					String site = domain;
					if (domain.indexOf(".") >= 0) {
						site = domain.substring(0, domain.indexOf("."));
					}

					List<IMap> userList = this.selectUser(imap);
					if (userList != null && userList.size() > 0) {
						imap.put("site", site);
						dao.update(NAMESPACE + "mergeUserSiteKey", imap);
						resCnt++;
					}
				}
			}
		} else {
			throw new IException("유효하지 않은 헤더 정보 입니다.");
		}

		return new Response(resCnt);
	}

	@SuppressWarnings("unchecked")
	public List<IMap> selectUser(IMap map) throws Exception {
		return dao.list(NAMESPACE + "selectUser", map);
	}

}
