package com.iwi.sso.user;

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
import com.iwi.sso.common.SystemConst;
import com.iwi.sso.util.StringUtil;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.user.User.";

	@Override
	public List<IMap> getUserInfo() throws Exception {
		return this.getUserInfo(null);
	}

	@Override
	public List<IMap> getUserInfo(IMap map) throws Exception {
		if (map != null) {
			if (!map.isEmpty() && (StringUtils.isEmpty(map.getString("id")) || StringUtils.isEmpty(map.getString("name")))) {
				throw new IException("필수 파라미터 누락");
			}

			map.put("email", map.getString("id") + "@" + SystemConst.DOMAIN_IWI);
		}

		List<IMap> userList = this.selectUser(map);
		if (userList == null || userList.size() == 0) {
			throw new IException("사원 정보 없음");
		}

		return userList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int setUserLink(Object obj, HttpServletRequest request) throws Exception {
		List<Map<String, String>> bodyList = null;

		if (obj instanceof List) {
			bodyList = (List<Map<String, String>>) obj;
		} else if (obj instanceof Map) {
			bodyList = new ArrayList<Map<String, String>>();
			bodyList.add((Map<String, String>) obj);
		}

		if (bodyList == null || bodyList.size() < 1 || bodyList.get(0).isEmpty()) {
			throw new IException("유효하지 않은 요청입니다.");
		}

		int resCnt = 0;

		String domain = StringUtil.getDomainInfo(request);
		String allowDomain = (String) request.getAttribute("authAllowDomain");

		if (StringUtils.isEmpty(domain) || !domain.toLowerCase().endsWith(allowDomain)) {
			throw new IException("유효하지 않은 헤더 정보 입니다.");
		} else {
			for (Map<String, String> bodyMap : bodyList) {
				IMap imap = new IMap(bodyMap);
				if (!StringUtils.isEmpty(imap.getString("seq")) && !StringUtils.isEmpty(imap.getString("email"))) {
					String site = domain;
					if (domain.indexOf(".") >= 0) {
						site = domain.substring(0, domain.indexOf("."));
					}

					List<IMap> userList = this.selectUser(imap);
					if (userList != null && userList.size() > 0) {
						imap.put("site", site);
						imap.put("uniqueKey", imap.getString("seq"));
						dao.update(NAMESPACE + "mergeUserSiteKey", imap);
						resCnt++;
					}
				}
			}
		}

		return resCnt;
	}

	@SuppressWarnings("unchecked")
	public List<IMap> selectUser(IMap map) throws Exception {
		return dao.list(NAMESPACE + "selectUser", map);
	}

}
