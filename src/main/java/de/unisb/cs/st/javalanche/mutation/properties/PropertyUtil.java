/*
* Copyright (C) 2011 Saarland University
* 
* This file is part of Javalanche.
* 
* Javalanche is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* Javalanche is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser Public License for more details.
* 
* You should have received a copy of the GNU Lesser Public License
* along with Javalanche.  If not, see <http://www.gnu.org/licenses/>.
*/
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

	public static String getProperty(String key) {
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
