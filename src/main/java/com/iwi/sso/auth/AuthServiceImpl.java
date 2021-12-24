package com.iwi.sso.auth;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.util.PropsUtil;
import com.iwi.sso.util.SecureUtil;
import com.iwi.sso.util.TokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private CommonDao dao;

	private String NAMESPACE = "com.iwi.sso.auth.Auth.";

	/**
	 * 로그인 (엑세스/리프레시 토큰 발급)
	 */
	@Override
	public IMap signinProc(IMap map) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		String email = map.getString("email");
		if (StringUtils.isEmpty(email)) {
			throw new IException("이메일을 입력하세요.");
		}

		if (StringUtils.isEmpty(map.getString("password"))) {
			throw new IException("비밀번호를 입력하세요.");
		}

		IMap user = this.selectLoginUser(map);
		if (user == null) {
			throw new IException("존재하지 않는 사용자입니다.");
		}

		String encPassword = SecureUtil.getEncPassword(map);
		String dbPassword = user.getString("password");

		if (!encPassword.equals(dbPassword)) {
			throw new IException("로그인 정보가 일치하지 않습니다. ");
		}

		String acsToken = TokenUtil.createAccessToken(email);
		String refToken = TokenUtil.createRefreshToken();

		IMap userMap = new IMap();
		userMap.put("email", email);

		// 마지막 로그인 일자 업데이트
		userMap.put("remoteAddr", request.getRemoteAddr());
		this.updateUserLastLogin(userMap);

		// 토큰 DB 저장
		userMap.put("refToken", refToken);
		this.updateUserRefreshToken(userMap);

		// 토큰 반환
		IMap resMap = new IMap();
		resMap.put("acsToken", acsToken);
		resMap.put("refToken", refToken);
		resMap.put("acsTime", PropsUtil.getLong("ACS_TOKEN_VALID_MINUTES"));
		resMap.put("refTime", PropsUtil.getLong("REF_TOKEN_VALID_MINUTES"));

		// ------------------ set cookie start

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

		String cookieDomain = PropsUtil.getString("DOMAIN_IWI");

		String setCookie = "I-REFRESH=" + refToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("REF_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
		if (!"Y".equals(PropsUtil.getString("DEV_YN"))) {
			setCookie += "domain=" + cookieDomain;
		}
		// System.out.println(setCookie);
		response.addHeader("Set-Cookie", setCookie);

		setCookie = "I-ACCESS=" + acsToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("ACS_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
		if (!"Y".equals(PropsUtil.getString("DEV_YN"))) {
			setCookie += "domain=" + cookieDomain;
		}
		// System.out.println(setCookie);
		response.addHeader("Set-Cookie", setCookie);

		// ------------------ set cookie end

		return resMap;
	}

	/**
	 * 엑세스 토큰 갱신
	 */
	@Override
	public IMap refreshToken() throws Exception {
		IMap map = new IMap();

		String email = null;
		String acsToken = null;
		// String refToken = map.getString("refToken");
		String refToken = null;

		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				if ("I-REFRESH".equals(name)) {
					refToken = cookie.getValue();
					map.put("refToken", refToken);
					break;
				}
			}
		} catch (Exception e) {
			throw new IException("인증 토큰 필요");
		}

		if (StringUtils.isEmpty(refToken)) {
			// System.out.println("####### refreshToken map : " + map);
			throw new IException("필수 파라미터 누락");
		}

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
		// resMap.put("refToken", refToken);
		resMap.put("acsTime", PropsUtil.getLong("ACS_TOKEN_VALID_MINUTES"));
		// resMap.put("refTime", PropsUtil.getLong("REF_TOKEN_VALID_MINUTES"));

		// ------------------ set cookie start

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

		String cookieDomain = PropsUtil.getString("DOMAIN_IWI");

		String setCookie = "I-ACCESS=" + acsToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("ACS_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
		if (!"Y".equals(PropsUtil.getString("DEV_YN"))) {
			setCookie += "domain=" + cookieDomain;
		}
		// System.out.println(setCookie);
		response.addHeader("Set-Cookie", setCookie);

		// ------------------ set cookie end

		return resMap;
	}

	@Override
	public IMap getUserInfo() throws Exception {
		IMap map = new IMap();

		// String acsToken = map.getString("acsToken");
		String acsToken = null;

		try {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpServletRequest request = requestAttributes.getRequest();
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				if ("I-ACCESS".equals(name)) {
					acsToken = cookie.getValue();
					map.put("acsToken", acsToken);
					break;
				}
			}
		} catch (Exception e) {
			throw new IException("인증 토큰 필요");
		}

		if (StringUtils.isEmpty(acsToken)) {
			throw new IException("필수 파라미터 누락");
		}

		String email = TokenUtil.getSubjectFromToken(acsToken);
		map.put("email", email);

		IMap userInfo = new IMap(this.selectUserInfo(map));
		if (userInfo == null || userInfo.isEmpty()) {
			throw new IException("존재하지 않는 사용자입니다.");
		}

		return userInfo;
	}

	public IMap selectLoginUser(IMap map) throws Exception {
		return (IMap) dao.select(NAMESPACE + "selectLoginUser", map);
	}

	public void updateUserLastLogin(IMap map) throws Exception {
		dao.update(NAMESPACE + "updateUserLastLogin", map);
	}

	public void updateUserRefreshToken(IMap map) throws Exception {
		dao.update(NAMESPACE + "updateUserRefreshToken", map);
	}

	public String selectEmailByToken(IMap map) throws Exception {
		return (String) dao.select(NAMESPACE + "selectEmailByToken", map);
	}

	public IMap selectUserInfo(IMap map) throws Exception {
		return (IMap) dao.select(NAMESPACE + "selectUserInfo", map);
	}

}
