package com.fearsfx.libra.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fearsfx.libra.models.User;

public class Login {
	
	private User result = null;
	
	public User doLogin(String user, String pass) {
		checkUser(user, pass);
		return result;
	}

	private void checkUser(String user, String pass) {
		String dbUrl = "jdbc:mysql://localhost/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "SELECT * FROM users";
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass).newInstance();
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("username").equals(user)
						&& rs.getString("password").equals(pass)) {
					result = new User();
					result.setId(rs.getInt("id"));
					result.setUsername(rs.getString("username"));
					result.setPassword(rs.getString("password"));
					result.setRole(rs.getString("role"));
					result.setFirstName(rs.getString("first_name"));
					result.setLastName(rs.getString("last_name"));
					result.setAddress(rs.getString("address"));
					result.setPhone(rs.getString("phone"));
					System.out.println(rs.getString("address"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
