package com.iwi.sso.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.JdbcUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

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
		try {
			if (list != null && list.size() > 0) {
				IMap map = list.get(0);
				this.putMapEntries(map);
			}
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * 객체 변환 Object(Map type) > IMap
	 * 
	 * @param map
	 */
	public IMap(Object obj) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<?, ?> map = objectMapper.convertValue(obj, Map.class);
			this.putMapEntries(map);
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public IMap(String key, Object value) {
		this.put(key, value);
	}

	public String getString(String key) {
		Object obj = this.get(key);
		if (obj instanceof String) {
			return (String) obj;
		}
		return null;
	}

	public int getInt(String key) {
		Object obj = this.get(key);
		return Integer.valueOf((String) obj);
	}

	public long getLong(String key) {
		Object obj = this.get(key);
		return Long.valueOf((String) obj);
	}
}
