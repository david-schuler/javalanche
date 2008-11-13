package de.unisb.cs.st.javalanche.mutation.util;

import java.net.URL;

public class MutationUtil {

	private static final String LOG4J_PROPERTIES = "log4j.properties";

	public static URL getLog4jPropertiesLocation() {
		URL systemResource = ClassLoader.getSystemResource(LOG4J_PROPERTIES);
		return systemResource;
	}
}
