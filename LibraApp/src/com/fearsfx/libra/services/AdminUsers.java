package com.fearsfx.libra.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fearsfx.libra.models.User;
import com.google.gson.Gson;

public class AdminUsers {

	private User[] users = null;
	private String createResult = "Creating new user failed.";
	private String deleteResult = "Deleting user failed.";

	public User[] getUsers() {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "Select * from users";
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				rs.last();
				int rowCount = rs.getRow();
				rs.beforeFirst();
				users = new User[rowCount];
				int curRow = 0;

				while (rs.next()) {
					users[curRow] = new User();
					users[curRow].setId(rs.getInt("id"));
					users[curRow].setUsername(rs.getString("username"));
					users[curRow].setPassword(rs.getString("password"));
					users[curRow].setRole(rs.getString("role"));
					users[curRow].setFirstName(rs.getString("first_name"));
					users[curRow].setLastName(rs.getString("last_name"));
					users[curRow].setAddress(rs.getString("address"));
					users[curRow].setPhone(rs.getString("phone"));
					curRow++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return users;
	}

	public String createUser(String userString) {

		Gson gson = new Gson();

		User user = gson.fromJson(userString, User.class);

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "INSERT INTO users VALUES (NULL, '" + user.getUsername()
				+ "', '" + user.getPassword() + "', '" + user.getRole()
				+ "', '" + user.getFirstName() + "', '" + user.getLastName()
				+ "', '" + user.getAddress() + "', '" + user.getPhone() + "')";

		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			createResult = "Successfully created new user.";
		} catch (SQLException e) {
			e.printStackTrace();
			createResult = "SQLException";
		} catch (ClassNotFoundException e) {
			createResult = "ClassNotFound";
			e.printStackTrace();
		}

		return createResult;
	}

	public String deleteUser(int userId) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "DELETE FROM users WHERE id = " + userId;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			deleteResult = "Successfully deleted user.";
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "SQLException";
		} catch (ClassNotFoundException e) {
			deleteResult = "ClassNotFound";
			e.printStackTrace();
		}

		return deleteResult;
	}

}
