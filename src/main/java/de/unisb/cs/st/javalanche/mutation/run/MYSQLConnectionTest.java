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
