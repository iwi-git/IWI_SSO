package com.iwi.sso.auth;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.SystemConst;
import com.iwi.sso.util.StringUtil;
import com.iwi.sso.util.TokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.auth.Auth.";

	/**
	 * 토큰 생성
	 * 
	 * @throws Exception
	 */
	@Override
	public IMap createToken(IMap map) throws Exception {

		// 멤버 검증 - email
		String email = map.getString("email");
		if (StringUtils.isEmpty(email)) {
			// System.out.println("####### createToken map : " + map);
			throw new IException("필수 파라미터 누락");
		} else {
			if (this.selectUser(map) == null) {
				throw new IException("사용자 정보 없음");
			}
		}

		// access 토큰 발급
		String acsToken = TokenUtil.createAccessToken(email);
		String refToken = null;

		try {
			// email - refresh 토큰 조회
			refToken = this.selectUserRefreshToken(map);
			if (StringUtils.isEmpty(refToken)) {
				// refresh 토큰 없으면 신규 발급 - DB 저장
				refToken = TokenUtil.createRefreshToken();
			} else {
				// refresh 토큰 있으면 만료 체크
				if (TokenUtil.isTokenExpired(refToken)) {
					// refresh 토큰 만료 시 재발급 - DB 저장
					refToken = TokenUtil.createRefreshToken();
				}
			}
		} catch (SignatureException e) {
			// SECRET_KEY 변경 등으로 인한 refresh 토큰 DB 교차 검증 실패 시 재발급
			refToken = TokenUtil.createRefreshToken();
		}

		// 토큰 DB 저장
		IMap userMap = new IMap();
		userMap.put("email", email);
		userMap.put("refToken", refToken);
		this.updateUserRefreshToken(userMap);

		// 토큰 반환
		IMap resMap = new IMap();
		resMap.put("acsToken", acsToken);
		resMap.put("refToken", refToken);
		resMap.put("acsTime", SystemConst.ACS_TOKEN_VALID_MINUTES);
		resMap.put("refTime", SystemConst.REF_TOKEN_VALID_MINUTES);

		return resMap;
	}

	/**
	 * 토큰 갱신
	 */
	@Override
	public IMap refreshToken(IMap map) throws Exception {
		String acsToken = map.getString("acsToken");
		String refToken = map.getString("refToken");

		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(refToken)) {
			// System.out.println("####### refreshToken map : " + map);
			throw new IException("필수 파라미터 누락");
		}

		String email = null;

		// 리프레쉬 토큰 만료 체크
		boolean isRefExpired = TokenUtil.isTokenExpired(refToken);
		if (isRefExpired) {
			// 리프레쉬 토큰 만료 시 인증 종료
			throw new ExpiredJwtException(null, null, null);
		} else {
			// 리프레쉬 토큰으로 사용자 조회
			email = this.selectEmailByToken(map);
			// System.out.println("###### email : " + email);

			if (StringUtils.isEmpty(email)) {
				// 토큰 DB 교차 검증 실패 시 오류 반환
				throw new SignatureException(null);
			}

			// 토큰 DB 교차 검증 성공 시 엑세스 토큰 재발급 (갱신)
			acsToken = TokenUtil.createAccessToken(email);
		}

		// 토큰 반환
		IMap resMap = new IMap();
		resMap.put("acsToken", acsToken);
		resMap.put("refToken", refToken);
		resMap.put("acsTime", SystemConst.ACS_TOKEN_VALID_MINUTES);
		resMap.put("refTime", SystemConst.REF_TOKEN_VALID_MINUTES);

		return resMap;
	}

	/**
	 * 토큰 검증
	 */
	@Override
	public boolean validationToken(IMap map) throws Exception {
		String acsToken = map.getString("acsToken");
		String refToken = map.getString("refToken");

		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(refToken)) {
			// System.out.println("####### validationToken map : " + map);
			throw new IException("필수 파라미터 누락");
		}

		String email = null;

		// 엑세스 토큰 만료 체크
		boolean isAcsExpired = TokenUtil.isTokenExpired(acsToken);
		// System.out.println("###### isAcsExpired : " + isAcsExpired);

		// 리프레쉬 토큰 만료 체크
		boolean isRefExpired = TokenUtil.isTokenExpired(refToken);
		// System.out.println("###### isRefExpired : " + isRefExpired);

		if (isRefExpired) {
			// 리프레쉬 토큰 만료
			throw new ExpiredJwtException(null, null, null);
		} else if (isAcsExpired) {
			// 엑세스토큰 만료 시 리프레쉬토큰으로 사용자 조회
			email = this.selectEmailByToken(map);
			// System.out.println("###### email : " + email);

			// 토큰 DB 교차 검증
			if (StringUtils.isEmpty(email)) {
				// 토큰 검증 실패
				throw new SignatureException(null);
			}
		}

		return true;
	}

	@Override
	public IMap getTokenSiteKey(IMap map, HttpServletRequest request) throws Exception {
		String acsToken = map.getString("acsToken");
		// String site = map.getString("site");

		if (StringUtils.isEmpty(acsToken)/* || StringUtils.isEmpty(site) */) {
			// System.out.println("####### getTokenSiteKey map : " + map);
			throw new IException("필수 파라미터 누락");
		}

		// 엑세스 토큰 검증
		boolean isAcsExpired = TokenUtil.isTokenExpired(acsToken);
		// System.out.println("###### isAcsExpired : " + isAcsExpired);

		String uniqueKey = null;

		if (isAcsExpired) {
			// 엑세스 토큰 만료
			throw new ExpiredJwtException(null, null, null);
		} else {
			// 엑세스토큰에서 email 추출
			map.put("email", TokenUtil.getSubjectFromToken(acsToken));

			String domain = StringUtil.getDomainInfo(request);
			if (StringUtils.isEmpty(domain)) {
				// referer 없음
				throw new IException("유효하지 않은 요청입니다.");
			} else {
				String site = domain.substring(0, domain.indexOf("."));
				map.put("site", site);

				uniqueKey = this.selectUserSiteKey(map);
				if (StringUtils.isEmpty(uniqueKey)) {
					throw new IException("사이트 사용자 키 없음");
				}
			}
		}

		IMap resMap = new IMap();
		resMap.put("uniqueKey", uniqueKey);

		return resMap;
	}

	public IMap selectUser(IMap map) throws Exception {
		return (IMap) dao.select(NAMESPACE + "selectUser", map);
	}

	public String selectUserRefreshToken(IMap map) throws Exception {
		return (String) dao.select(NAMESPACE + "selectUserRefreshToken", map);
	}

	public String selectEmailByToken(IMap map) throws Exception {
		return (String) dao.select(NAMESPACE + "selectEmailByToken", map);
	}

	public String selectUserSiteKey(IMap map) throws Exception {
		return (String) dao.select(NAMESPACE + "selectUserSiteKey", map);
	}

	public void updateUserRefreshToken(IMap map) throws Exception {
		dao.update(NAMESPACE + "updateUserRefreshToken", map);
	}

	@Override
	public IMap selectAllowAuthInfo(String authKey, String domain) throws Exception {
		IMap map = new IMap();
		map.put("authKey", authKey);
		map.put("domain", domain);
		return (IMap) dao.select(NAMESPACE + "selectAllowAuthInfo", map);
	}

	@Override
	public void setUserSiteKey(IMap map, HttpServletRequest request) throws Exception {
		try {
			// refere, uniqueKey - 서비스별 사용자 번호 merge
			String uniqueKey = map.getString("uniqueKey");
			String domain = StringUtil.getDomainInfo(request);

			if (!StringUtils.isEmpty(uniqueKey) && !StringUtils.isEmpty(domain) && domain.toLowerCase().endsWith("iwi.co.kr")) {
				String site = domain.substring(0, domain.indexOf("."));
				if (!StringUtils.isEmpty(site)) {
					map.put("site", site);
					dao.update(NAMESPACE + "mergeUserSiteKey", map);
				}
			}
		} catch (Exception e) {
			// 오류 발생 시 작업 무시 후 종료
		}
	}

	@Override
	public void setUserLastLogin(IMap map, HttpServletRequest request) throws Exception {
		map.put("remoteAddr", request.getRemoteAddr());
		dao.update(NAMESPACE + "updateUserLastLogin", map);
	}

}
