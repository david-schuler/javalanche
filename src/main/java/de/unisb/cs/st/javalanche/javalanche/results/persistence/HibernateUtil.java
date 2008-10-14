package de.unisb.cs.st.javalanche.mutation.results.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

/**
 * Startup Hibernate and provide access to the singleton SessionFactory.
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;

	static {
		try {
			sessionFactory = new AnnotationConfiguration().configure()
					.buildSessionFactory();
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		// Alternatively, we could look up in JNDI here
		return sessionFactory;
	}

	public static Session openSession() {
		return sessionFactory.openSession();
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
}
