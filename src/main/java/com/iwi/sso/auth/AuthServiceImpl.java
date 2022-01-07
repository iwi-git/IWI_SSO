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
		// resMap.put("acsTime", PropsUtil.getLong("ACS_TOKEN_VALID_MINUTES"));
		// resMap.put("refTime", PropsUtil.getLong("REF_TOKEN_VALID_MINUTES"));
		resMap.put("tokenTime", PropsUtil.getLong("REF_TOKEN_VALID_MINUTES"));

		// ------------------ set cookie start

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

		String cookieDomain = PropsUtil.getString("DOMAIN_IWI");

		String setCookie = "I-REFRESH=" + refToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("AUTH_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
		if (!"Y".equals(PropsUtil.getString("DEV_YN"))) {
			setCookie += "domain=" + cookieDomain;
		}
		// System.out.println(setCookie);
		response.addHeader("Set-Cookie", setCookie);

		setCookie = "I-ACCESS=" + acsToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("AUTH_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
		if (!"Y".equals(PropsUtil.getString("DEV_YN"))) {
			setCookie += "domain=" + cookieDomain;
		}
		// System.out.println(setCookie);
		response.addHeader("Set-Cookie", setCookie);

		// ------------------ set cookie end

		return resMap;
	}

	@Override
	public boolean validateToken() throws Exception {
		return this.validateToken(null);
	}

	@Override
	public boolean validateToken(String acsToken) throws Exception {
		IMap map = new IMap();

		// 엑세스 토큰이 없으면 request 쿠키에서 가져옴
		if (StringUtils.isEmpty(acsToken)) {
			try {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpServletRequest request = requestAttributes.getRequest();
				Cookie[] cookies = request.getCookies();
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					if ("I-ACCESS".equals(name)) {
						acsToken = cookie.getValue();
						break;
					}
				}
			} catch (Exception e) {
				throw new IException("인증 토큰 필요");
			}

			if (StringUtils.isEmpty(acsToken)) {
				throw new IException("인증 토큰 필요");
			}
		}

		map.put("acsToken", acsToken);

		// 토큰 만료 체크
		boolean isExpired = TokenUtil.isTokenExpired(acsToken);
		if (isExpired) {
			throw new ExpiredJwtException(null, null, null);
		}

		// 토큰 검증 체크
		String email = TokenUtil.getSubjectFromToken(acsToken);
		if (StringUtils.isEmpty(email)) {
			throw new SignatureException(null);
		}

		return true;
	}

	@Override
	public IMap refreshToken() throws Exception {
		return this.refreshToken(null, null);
	}

	@Override
	public IMap refreshToken(String acsToken, String refToken) throws Exception {
		IMap map = new IMap();

		String email = null;

		// 엑세스/리프레시 토큰이 없으면 request 쿠키에서 가져옴
		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(refToken)) {
			try {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpServletRequest request = requestAttributes.getRequest();
				Cookie[] cookies = request.getCookies();
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					if ("I-ACCESS".equals(name)) {
						acsToken = cookie.getValue();
					} else if ("I-REFRESH".equals(name)) {
						refToken = cookie.getValue();
					}
				}
			} catch (Exception e) {
				throw new IException("인증 토큰 필요");
			}

			if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(refToken)) {
				throw new IException("인증 토큰 필요");
			}
		}

		// 리프레쉬 토큰 만료 체크
		boolean isRefExpired = TokenUtil.isTokenExpired(refToken);
		if (isRefExpired) {
			// 리프레쉬 토큰 만료 시 인증 종료
			throw new ExpiredJwtException(null, null, null);
		} else {
			// 엑세스 토큰에서 이메일 추출
			email = TokenUtil.getSubjectFromToken(acsToken);
			map.put("email", email);
			map.put("acsToken", acsToken);
			map.put("refToken", refToken);
			// System.out.println("###### email : " + email);

			// 이메일/리프레쉬 토큰 교차 검증
			int cnt = this.selectCountEmailAndToken(map);
			// System.out.println("###### cnt : " + cnt);

			if (StringUtils.isEmpty(email) || cnt < 1) {
				// 이메일이 비어있거나 교차 검증 실패 시 오류 반환
				throw new SignatureException(null);
			}

			// 토큰 DB 교차 검증 성공 시 토큰 재발급 (갱신)
			acsToken = TokenUtil.createAccessToken(email);
			refToken = TokenUtil.createRefreshToken();

			// 토큰 DB 저장 (신규 리프레시 토큰)
			map.put("refToken", refToken);
			this.updateUserRefreshToken(map);
		}

		// 토큰 반환
		IMap resMap = new IMap();
		resMap.put("acsToken", acsToken);
		resMap.put("refToken", refToken);
		// resMap.put("acsTime", PropsUtil.getLong("ACS_TOKEN_VALID_MINUTES"));
		// resMap.put("refTime", PropsUtil.getLong("REF_TOKEN_VALID_MINUTES"));
		resMap.put("tokenTime", PropsUtil.getLong("REF_TOKEN_VALID_MINUTES"));

		// ------------------ set cookie start

		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();

		String cookieDomain = PropsUtil.getString("DOMAIN_IWI");

		String setCookie = "I-REFRESH=" + refToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("AUTH_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
		if (!"Y".equals(PropsUtil.getString("DEV_YN"))) {
			setCookie += "domain=" + cookieDomain;
		}
		// System.out.println(setCookie);
		response.addHeader("Set-Cookie", setCookie);

		setCookie = "I-ACCESS=" + acsToken + "; Path=/; Max-Age=" + (PropsUtil.getLong("AUTH_TOKEN_VALID_MINUTES") * 60) + "; HttpOnly;";
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
		return this.getUserInfo(null);
	}

	@Override
	public IMap getUserInfo(String acsToken) throws Exception {
		IMap map = new IMap();

		// 엑세스 토큰이 없으면 request 쿠키에서 가져옴
		if (StringUtils.isEmpty(acsToken)) {
			try {
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
				HttpServletRequest request = requestAttributes.getRequest();
				Cookie[] cookies = request.getCookies();
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					if ("I-ACCESS".equals(name)) {
						acsToken = cookie.getValue();
						break;
					}
				}
			} catch (Exception e) {
				throw new IException("인증 토큰 필요");
			}

			if (StringUtils.isEmpty(acsToken)) {
				throw new IException("인증 토큰 필요");
			}
		}

		// 엑세스 토큰 만료 시 오류 반환
		boolean isExpired = TokenUtil.isTokenExpired(acsToken);
		if (isExpired) {
			throw new ExpiredJwtException(null, null, null);
		}

		String email = TokenUtil.getSubjectFromToken(acsToken);
		map.put("email", email);
		map.put("acsToken", acsToken);

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

	public int selectCountEmailAndToken(IMap map) throws Exception {
		return (int) dao.select(NAMESPACE + "selectCountEmailAndToken", map);
	}

	@Override
	public IMap selectUserInfo(IMap map) throws Exception {
		return (IMap) dao.select(NAMESPACE + "selectUserInfo", map);
	}

}
