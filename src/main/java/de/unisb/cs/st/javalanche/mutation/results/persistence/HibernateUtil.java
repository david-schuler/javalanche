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
