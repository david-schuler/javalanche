package de.unisb.cs.st.javalanche.mutation.properties;

public class ConfigurationLocator {

	private static JavalancheConfiguration jvlConfiguration = new PropertyConfiguration();

	public static void setJavalancheConfiguration(
			JavalancheConfiguration javalancheConfiguration) {
		jvlConfiguration = javalancheConfiguration;
	}

	public static JavalancheConfiguration getJavalancheConfiguration() {
		return jvlConfiguration;
	}

}
