package de.unisb.cs.st.javalanche.mutation;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

public class ReleaseTest {

	@Test
	public void testHibernatePropertiesForRelease() {
		Configuration configure = new AnnotationConfiguration().configure();
		String propertyDriver = configure
				.getProperty("hibernate.connection.driver_class");
		assertThat(propertyDriver, is("org.hsqldb.jdbcDriver"));
		String propertyDialect = configure
				.getProperty("hibernate.dialect");
		assertThat(propertyDialect, is("org.hibernate.dialect.HSQLDialect"));
		String propertyShowSql = configure.getProperty("show_sql");
		propertyShowSql = propertyShowSql == null ? "false" : propertyShowSql;
		assertThat(propertyShowSql, is("false"));
	}
	
	@Test
	public void testLogLevel() {
		Level level = Logger.getRootLogger().getLevel();
		assertThat(level, is(Level.INFO));
	}
}
