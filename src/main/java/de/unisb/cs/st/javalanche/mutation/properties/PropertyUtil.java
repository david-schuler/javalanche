package de.unisb.cs.st.javalanche.mutation.properties;

import org.apache.log4j.Logger;

public class PropertyUtil {

	private static final Logger logger = Logger.getLogger(PropertyUtil.class);

	public static final int getPropertyOrDefault(String key, int defaultValue) {
		String result = getPropertyOrDefault(key, defaultValue + "");
		return Integer.parseInt(result);
	}

	public static boolean getPropertyOrDefault(String key, boolean defaultValue) {
		String property = getProperty(key);
		if (property != null) {
			String propertyTrimmed = property.trim().toLowerCase();
			if (propertyTrimmed.equals("true") || propertyTrimmed.equals("yes")) {
				return true;
			} else if (propertyTrimmed.equals("false")
					|| propertyTrimmed.equals("no")) {
				return false;
			}
		}
		return defaultValue;
	}

	static final String getPropertyOrDefault(String key,
			String defaultValue) {
		String result = getProperty(key);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	static String getProperty(String key) {
		String result = null;
		if (System.getProperty(key) != null) {
			result = System.getProperty(key);
		}
		logger.info(String.format("Got property: key=%s  ,  value=%s", key,
				result));
		return result;
	}

	public static void checkProperty(String key) {
		String property = System.getProperty(key);
		if (property == null) {
			throw new IllegalStateException("Property not specified. Key: "
					+ key);
		}
	}
}
