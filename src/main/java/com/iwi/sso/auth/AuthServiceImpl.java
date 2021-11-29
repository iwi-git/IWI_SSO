package com.iwi.sso.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iwi.sso.common.CommonDao;
import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.SystemConst;
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
	public IMap createToken(IMap imap) throws Exception {

		// 멤버 검증 - email
		String email = imap.getString("email");
		if (StringUtils.isEmpty(email)) {
			// System.out.println("####### createToken imap : " + imap);
			throw new Exception("필수 파라미터 누락");
		} else {
			if (this.selectUser(imap) == null) {
				throw new Exception("사용자 정보 없음");
			}
		}

		// access 토큰 발급
		String acsToken = TokenUtil.createAccessToken(email);
		String refToken = null;

		try {
			// email - refresh 토큰 조회
			refToken = this.selectUserRefreshToken(imap);
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

		// System.out.println("###### " + TokenUtil.getExpirationDateFromToken(acsToken));
		// System.out.println("###### " + TokenUtil.getExpirationDateFromToken(refToken));

		return resMap;
	}

	/**
	 * 토큰 갱신
	 */
	@Override
	public IMap refreshToken(IMap imap) throws Exception {
		String acsToken = imap.getString("acsToken");
		String refToken = imap.getString("refToken");

		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(refToken)) {
			// System.out.println("####### refreshToken imap : " + imap);
			throw new Exception("필수 파라미터 누락");
		}

		String email = null;

		// 엑세스 토큰 검증
		boolean isAcsExpired = TokenUtil.isTokenExpired(acsToken);
		// System.out.println("###### isAcsExpired : " + isAcsExpired);
		if (!isAcsExpired) {
			// System.out.println("###### acsExpirationDate : " + TokenUtil.getExpirationDateFromToken(acsToken));
		}

		boolean isRefExpired = TokenUtil.isTokenExpired(refToken);
		// System.out.println("###### isRefExpired : " + isRefExpired);
		if (!isRefExpired) {
			// System.out.println("###### refExpirationDate : " + TokenUtil.getExpirationDateFromToken(refToken));
		}

		if (isRefExpired) {
			// 리프레쉬 토큰 만료 시 인증 종료, 추가 작업 없음
			throw new ExpiredJwtException(null, null, null);
		} else if (isAcsExpired) {
			// 엑세스토큰 만료 시 리프레쉬토큰으로 사용자 조회
			email = this.selectEmailByToken(imap);
			// System.out.println("###### email : " + email);

			// 토큰 DB 교차 검증 성공 시 엑세스 토큰 재발급
			if (!StringUtils.isEmpty(email)) {
				acsToken = TokenUtil.createAccessToken(email);
			} else {
				throw new SignatureException(null);
			}
		} else {
			// TODO : 엑세스토큰 유효시 추가 작업 없이 반환

			// 엑세스토큰에서 email 추출
			// email = TokenUtil.getSubjectFromToken(acsToken);
			// System.out.println("###### email : " + email);

			// 엑세스토큰 갱신
			// acsToken = TokenUtil.createAccessToken(email);
		}

		// 갱신된 토큰 DB 저장
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
	 * 토큰 검증
	 */
	@Override
	public boolean validationToken(IMap imap) throws Exception {
		String acsToken = imap.getString("acsToken");
		String refToken = imap.getString("refToken");

		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(refToken)) {
			// System.out.println("####### validationToken imap : " + imap);
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
			email = this.selectEmailByToken(imap);
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
	public IMap getTokenSiteKey(IMap imap) throws Exception {
		String acsToken = imap.getString("acsToken");
		String site = imap.getString("site");

		if (StringUtils.isEmpty(acsToken) || StringUtils.isEmpty(site)) {
			// System.out.println("####### getTokenSiteKey imap : " + imap);
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
			imap.put("email", TokenUtil.getSubjectFromToken(acsToken));
			uniqueKey = this.selectUserSiteKey(imap);
		}

		IMap resMap = new IMap();
		resMap.put("uniqueKey", uniqueKey);

		return resMap;
	}

	public IMap selectUser(IMap imap) throws Exception {
		return (IMap) dao.select(NAMESPACE + "selectUser", imap);
	}

	public String selectUserRefreshToken(IMap imap) throws Exception {
		return (String) dao.select(NAMESPACE + "selectUserRefreshToken", imap);
	}

	public String selectEmailByToken(IMap imap) throws Exception {
		return (String) dao.select(NAMESPACE + "selectEmailByToken", imap);
	}

	public String selectUserSiteKey(IMap imap) throws Exception {
		return (String) dao.select(NAMESPACE + "selectUserSiteKey", imap);
	}

	public void updateUserRefreshToken(IMap imap) throws Exception {
		dao.update(NAMESPACE + "updateUserRefreshToken", imap);
	}

}
