package com.iwi.sso.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

@SuppressWarnings("deprecation")
public class PropsUtil extends PropertyPlaceholderConfigurer {

	private static Map<String, String> propertiesMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {

		super.processProperties(beanFactoryToProcess, props);

		propertiesMap = new HashMap<String, String>();

		for (Object key : props.keySet()) {

			try {
				String rawValue = props.getProperty((String) key);
				String value = new String(rawValue.getBytes("utf-8"), "utf-8");
				propertiesMap.put((String) key, value);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static int getInt(String name) {
		int re_val = 0;
		try {
			re_val = Integer.parseInt(String.valueOf(propertiesMap.get(name)));
		} catch (Exception e) {
			re_val = 0;
		}

		return re_val;
	}

	public static long getLong(String name) {
		long re_val = 0;
		try {
			re_val = Long.parseLong(String.valueOf(propertiesMap.get(name)));
		} catch (Exception e) {
			re_val = 0;
		}

		return re_val;
	}

	public static String getString(String name) {
		return String.valueOf(propertiesMap.get(name));
	}
}
