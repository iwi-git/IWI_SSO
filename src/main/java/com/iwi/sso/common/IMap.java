package com.iwi.sso.common;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.JdbcUtils;

@SuppressWarnings("rawtypes")
public class IMap extends HashMap {

	private static final long serialVersionUID = 136187775502451078L;

	public IMap() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		String keyStr = (String) key;
		if (keyStr.indexOf("_") >= 0) {
			// camel case 적용
			return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(keyStr), value);
		} else {
			// 대문자만으로 이루어진 키는 소문자로 변경
			if (keyStr.matches("^[A-Z]*$")) {
				keyStr = keyStr.toLowerCase();
			}
			return super.put(keyStr, value);
		}
	}

	final void putMapEntries(Map<?, ?> map) {
		Iterator<?> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			this.put(key, map.get(key));
		}
	}

	/**
	 * 객체 변환 Map > IMap
	 * 
	 * @param map
	 */
	public IMap(Map<?, ?> map) {
		this.putMapEntries(map);
	}

	/**
	 * 객체 변환 HashMap > IMap
	 * 
	 * @param map
	 */
	public IMap(HashMap<?, ?> map) {
		this.putMapEntries(map);
	}

	/**
	 * 객체 변환 IMap > IMap
	 * 
	 * @param map
	 */
	public IMap(IMap map) {
		this.putMapEntries(map);
	}

	/**
	 * List<IMap> 0번째 인덱스만 받음
	 * 
	 * @param list
	 */
	public IMap(List<IMap> list) {
		if (list != null && list.size() > 0) {
			IMap map = list.get(0);
			this.putMapEntries(map);
		}
	}

	/**
	 * String 형태로 반환
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		if (get(key) == null) {
			return "";
		} else if (get(key) instanceof String[]) {
			if (((String[]) get(key)).length == 1) {
				return ((String[]) get(key))[0];
			} else {
				StringBuilder builder = new StringBuilder();
				StringBuilder logbuilder = new StringBuilder();
				logbuilder.append("\nPlease use the getStringArry(String " + key + ").\n The values of " + key + " are\n");

				for (String str : (String[]) get(key)) {
					builder.append(str);
					logbuilder.append(str + ",");
				}
				// log.error(logbuilder.toString());

				return builder.toString();
			}
		} else if (get(key) instanceof String) {
			// return (String) get(key);
			return ((String) get(key) == null) ? "" : (String) get(key);
		} else if (get(key) instanceof Integer) {

			return Integer.toString((Integer) get(key));
		} else if (get(key) instanceof Long) {

			return Long.toString((Long) get(key));
		} else if (get(key) instanceof BigDecimal) {

			return ((BigDecimal) get(key)).toString();
		} else if (get(key) instanceof Double) {

			return Double.toString((Double) get(key));
		} else {

			return "Unidentify Type";
		}
	}
}
