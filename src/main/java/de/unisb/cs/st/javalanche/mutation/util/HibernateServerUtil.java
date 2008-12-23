package de.unisb.cs.st.javalanche.mutation.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class HibernateServerUtil {

	private static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";

	public static enum Server {
		LOCALHOST("localhost"), KUBRICK("kubrick.cs.uni-sb.de"), HOBEL(
				"hobel.cs.uni-sb.de");

		Server(String name) {
			this.connection = "jdbc:mysql://" + name + ":3308/mutation_test";
		}

		public String connection;

		public String getConnection() {
			return connection;
		}
	};

	public static Map<Server, SessionFactory> sessionFactories = new HashMap<Server, SessionFactory>();

	static {
		for (Server s : Server.values()) {
			try {
				AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
				Configuration configuration = annotationConfiguration
						.configure();
				String property = configuration.getProperty(s.getConnection());
				System.err.println(property);
				configuration.setProperty(HIBERNATE_CONNECTION_URL, s
						.getConnection());
				SessionFactory sessionFactory = configuration
						.buildSessionFactory();
				sessionFactories.put(s, sessionFactory);
			} catch (Throwable ex) {
				ex.printStackTrace();
				throw new ExceptionInInitializerError(ex);
			}
		}

	}

	public static SessionFactory getSessionFactory(Server s) {
		// Alternatively, we could look up in JNDI here
		return sessionFactories.get(s);
	}

	public static Session openSession(Server s) {
		return getSessionFactory(s).openSession();
	}

	public static void shutdown(Server s) {
		// Close caches and connection pools
		getSessionFactory(s).close();
	}
}
