package de.unisb.cs.st.javalanche.mutation.hibernate;

import static org.junit.Assert.*;

import java.sql.*;

import org.junit.Test;

public class ConnectionTest {

	@Test
	public void testConnection() {
		Connection conn = null;
		try {
			// Step 1: Load the JDBC driver.
			Class.forName("com.mysql.jdbc.Driver");
			// Step 2: Establish the connection to the database.
			String url = "jdbc:mysql://localhost:3308/mutation_test";
			conn = DriverManager
					.getConnection(url, "mutation", "mu");
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			
			e.printStackTrace();
		}
		assertNotNull(conn);
	}
}
