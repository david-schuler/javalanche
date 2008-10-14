package de.unisb.cs.st.javalanche.mutation.run;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tests if we can connect to the mysql database.
 *
 * @author David Schuler
 *
 */
public class MYSQLConnectionTest {

	public static void main(String args[]) {
		Connection con = null;

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3308/mutation_test", "mutation",
					"mu");

			if (!con.isClosed())
				System.out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
	}
}
