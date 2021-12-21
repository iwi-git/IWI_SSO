package com.iwi.sso.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
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

import com.iwi.sso.common.IException;
import com.iwi.sso.common.IMap;
import com.iwi.sso.common.SystemConst;

public class LDAPUtil {

	/**
	 * 계정정보 조회
	 * 
	 * @return
	 * @throws Exception
	 */
	public static List<IMap> getAccountInfo() throws Exception {
		return getAccountList(null);
	}

	/**
	 * 계정정보 조회
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	public static IMap getAccountInfo(String email) throws Exception {
		return new IMap(getAccountList(email));
	}

	/**
	 * 계정 목록 조회
	 * 
	 * @param email
	 * @return
	 * @throws Exception
	 */
	private static List<IMap> getAccountList(String email) throws Exception {

		List<IMap> list = new ArrayList<IMap>();

		LdapContext ctx = new InitialLdapContext(setEnvironment(), null);

		String searchFilter = "";
		searchFilter += "(&(objectClass=user)";
		if (!StringUtils.isEmpty(email)) {
			searchFilter += "(userPrincipalName=" + email + ")";
		}
		searchFilter += ")";

		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		NamingEnumeration<SearchResult> answer = ctx.search(SystemConst.AD_BASE, searchFilter, searchCtls);

		list = getList(answer);

		return list;
	}

	public static void setAttribute(String dn, String key, String value) throws Exception {
		setAttribute(dn, new IMap(key, value));
	}

	public static void setAttribute(String dn, IMap attr) throws Exception {
		if (StringUtils.isEmpty(dn) || attr == null || attr.isEmpty()) {
			throw new IException("필수 파라미터 누락");
		}

		ModificationItem[] modifyitems = new ModificationItem[attr.size()];

		int i = 0;
		Iterator<?> keys = attr.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();

			Attribute mod = new BasicAttribute(key, attr.getString(key));
			modifyitems[i++] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod);
		}

		LdapContext ctx = new InitialLdapContext(setEnvironment(), null);
		ctx.modifyAttributes(dn, modifyitems);
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
	private static List<IMap> getList(NamingEnumeration<SearchResult> answer) throws Exception {
		List<IMap> list = new ArrayList<IMap>();
		while (answer.hasMoreElements()) {
			SearchResult sr = answer.next();
			Attributes attrs = sr.getAttributes();
			IMap amap = null;
			if (attrs != null) {
				amap = new IMap();
				NamingEnumeration<? extends Attribute> ne = attrs.getAll();
				while (ne.hasMore()) {
					Attribute attr = (Attribute) ne.next();
					amap.put(attr.getID(), attr.get());
				}
				list.add(amap);
				ne.close();
			}
		}
		return list;
	}

}
