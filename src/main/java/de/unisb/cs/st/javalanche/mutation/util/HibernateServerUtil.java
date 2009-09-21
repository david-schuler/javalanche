package de.unisb.cs.st.javalanche.mutation.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class HibernateServerUtil {

	private static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";

	public static enum Server {
		LOCALHOST("localhost"), KUBRICK("kubrick.cs.uni-saarland.de"), HOBEL(
				"hobel.cs.uni-sb.de"), QUENTIN("quentin.cs.uni-saarland.de");

		Server(String name) {
			this.connection = "jdbc:mysql://" + name + ":3308/mutation_test";
		}

		public String connection;

		public String getConnection() {
			return connection;
		}
	};

	private static Map<Server, SessionFactory> sessionFactories = new HashMap<Server, SessionFactory>();

	private static SessionFactory getServerConnection(Server s) {
		SessionFactory sessionFactory = null;
		try {
			AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
			Configuration configuration = annotationConfiguration.configure();
			// String property1 = configuration
			// .getProperty(HIBERNATE_CONNECTION_URL);
			configuration.setProperty(HIBERNATE_CONNECTION_URL, s
					.getConnection());
			// String property2 = configuration
			// .getProperty(HIBERNATE_CONNECTION_URL);
			// System.err.println(property2);
			// System.err.println(property1.equals(property2));
			sessionFactory = configuration.buildSessionFactory();
			sessionFactories.put(s, sessionFactory);
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory(Server s) {
		SessionFactory sessionFactory = sessionFactories.get(s);
		if (sessionFactory == null) {
			sessionFactory = getServerConnection(s);
			sessionFactories.put(s, sessionFactory);
		}
		return sessionFactory;
	}

	public static void shutdown(Server s) {
		// Close caches and connection pools
		getSessionFactory(s).close();
	}

}
