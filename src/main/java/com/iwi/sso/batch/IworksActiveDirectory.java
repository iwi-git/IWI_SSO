package com.iwi.sso.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.springframework.util.StringUtils;

import com.iwi.sso.common.SystemConst;

public class IworksActiveDirectory {

	protected static String RETURNED_ATTS[] = { "distinguishedName", "displayName", "sn", "givenName", "cn", "name",
			"co", "st", "l", "streetAddress", "c", "postalCode", "mobile", "facsimileTelephoneNumber", "title",
			"company", "department", "userPrincipalName" };

	public static void main(String[] args) {

		try {

			LdapContext ctx = new InitialLdapContext(setEnvironment(), null);

			String accountName = "kjg";

			getAccountInfo(ctx, accountName);

//			onlyOneChangeData(ctx, accountName, "title", "대리");

		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 단건 프로필 변경 oldData is not null and newData is not null -> 변경 oldData is not
	 * null and newData is null -> 신규 oldData is null and newData is not null -> 삭제
	 * 
	 * @param ctx
	 * @param accountName
	 * @param attr
	 * @param newData     변경값
	 */
	private static void onlyOneChangeData(LdapContext ctx, String accountName, String attr, String newData) {

		try {

			String paramString = accountInfoGetReturnAttrData(ctx, accountName, "distinguishedName");

			ModificationItem[] paramArrayOfModificationItem = setModifiItem(attr,
					accountInfoGetReturnAttrData(ctx, accountName, attr), newData);

			ctx.modifyAttributes(paramString, paramArrayOfModificationItem);

		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 회원 경로 검색
	 * 
	 * @param ctx
	 * @param accountName
	 * @param returnAttr
	 * @return
	 */
	private static String accountInfoGetReturnAttrData(LdapContext ctx, String accountName, String returnAttr) {

		List<Map> accInfo = getAccountInfo(ctx, accountName);

		String attrData = "";

		if (accInfo != null) {
			Map accMap = accInfo.get(0);
			attrData = (String) accMap.get(returnAttr);
		}

		return attrData;
	}

	/**
	 * 아이템 설정
	 * 
	 * @param attr
	 * @param oldData
	 * @param newData
	 * @return
	 */
	private static ModificationItem[] setModifiItem(String attr, String oldData, String newData) {

		ModificationItem oldItem = null;
		ModificationItem newItem = null;

		if (!StringUtils.isEmpty(oldData)) {

			Attribute oldAttr = new BasicAttribute(attr, oldData);
			oldItem = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, oldAttr);
		}

		if (!StringUtils.isEmpty(newData)) {

			Attribute newAttr = new BasicAttribute(attr, newData);
			newItem = new ModificationItem(DirContext.ADD_ATTRIBUTE, newAttr);
		}

		ModificationItem[] paramArrayOfModificationItem = new ModificationItem[] { oldItem, newItem };

		return paramArrayOfModificationItem;
	}

	/**
	 * 계정정보 조회
	 * 
	 * @param ctx
	 * @param accountName
	 */
	private static List<Map> getAccountInfo(LdapContext ctx, String accountName) {

		List<Map> list = new ArrayList<Map>();

		try {

			String searchBase = "dc=iwi,dc=co,dc=kr";

			String searchFilter = "";
			searchFilter += "(&(objectClass=user)";
			if (!StringUtils.isEmpty(accountName)) {
				searchFilter += "(sAMAccountName=" + accountName + ")";
			}
			searchFilter += ")";

			SearchControls searchCtls = new SearchControls();
//			searchCtls.setReturningAttributes(RETURNED_ATTS);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration answer = ctx.search(searchBase, searchFilter, searchCtls);

			list = getAnswer(answer);

		} catch (AuthenticationException e) {
			exceptionMessage(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 기초 정보 입력
	 * 
	 * @return
	 */
	private static Hashtable<String, String> setEnvironment() {

		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, SystemConst.AD_SERVER);
		env.put(Context.SECURITY_PRINCIPAL, SystemConst.AD_ID + "@" + SystemConst.AD_DOMAIN);
		env.put(Context.SECURITY_CREDENTIALS, SystemConst.AD_PW);

		return env;
	}

	/**
	 * 조회 정보 List<Map> 에 저장
	 * 
	 * @param answer
	 * @return
	 * @throws Exception
	 */
	private static List<Map> getAnswer(NamingEnumeration answer) throws Exception {

		List<Map> list = new ArrayList<Map>();

		while (answer.hasMoreElements()) {

			SearchResult sr = (SearchResult) answer.next();
			Attributes attrs = sr.getAttributes();
			Map amap = null;

			if (attrs != null) {

				amap = new HashMap();
				NamingEnumeration ne = attrs.getAll();

				while (ne.hasMore()) {

					Attribute attr = (Attribute) ne.next();
					amap.put(attr.getID(), attr.get());
				}

				list.add(amap);
				ne.close();
			}
		}
		answerPrint(list);

		return list;
	}

	/**
	 * 조회된 정보 출력
	 * 
	 * @param list
	 */
	private static void answerPrint(List<Map> list) {

		System.out.println("################################ AD DATA ################################");

		for (Map map : list) {
			map.forEach((key, value) -> System.out.println("##[key] : " + key + ", [value] : " + value));
		}

		System.out.println("############################## //AD DATA ################################");

	}

	/**
	 * 에러 메세지 출력
	 * 
	 * @param msg
	 */
	private static void exceptionMessage(String msg) {
		if (msg.indexOf("data 525") > 0)
			System.out.println("사용자를 찾을 수 없음");
		if (msg.indexOf("data 773") > 0)
			System.out.println("사용자는 암호를 재설정 필요");
		if (msg.indexOf("data 52e") > 0)
			System.out.println("ID와 비밀번호가 불일치");
		if (msg.indexOf("data 533") > 0)
			System.out.println("입력한 ID는 비활성화");
		if (msg.indexOf("data 532") > 0)
			System.out.println("암호만료");
		if (msg.indexOf("data 701") > 0)
			System.out.println("AD에서 계정이 만료됨");
	}
}
