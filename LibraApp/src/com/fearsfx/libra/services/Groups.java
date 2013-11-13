package com.fearsfx.libra.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fearsfx.libra.models.Group;

public class Groups {
	
	private Group[] result;
	private String createResult = "Creating new group failed.";
	private String deleteResult = "Deleting group failed.";
	private String renameResult = "Renaming the group failed.";
	
	public Group[] getGroups(){
		generateItems();
		return result;
	}

	private void generateItems() {
		String dbUrl = "jdbc:mysql://localhost/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "SELECT * FROM groups";
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass).newInstance();
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()){
				rs.last();
				int rowCount = rs.getRow();
				rs.beforeFirst();
				result = new Group[rowCount];
				int curRow = 0;
				while (rs.next()) {				
					result[curRow] = new Group();
					result[curRow].setId(rs.getInt("id"));
					result[curRow].setName(rs.getString("gname"));
					curRow++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}		
	}

	public String createGroup(String groupName) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "INSERT INTO groups VALUES (NULL, '" + groupName + "')";

		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			createResult = "Successfully created new group.";
		} catch (SQLException e) {
			e.printStackTrace();
			createResult = "SQLException";
		} catch (ClassNotFoundException e) {
			createResult = "ClassNotFound";
			e.printStackTrace();
		}

		return createResult;
	}

	public String deleteGroup(int groupId) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "DELETE FROM groups WHERE id = " + groupId;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			deleteResult = "Successfully deleted group.";
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "SQLException";
		} catch (ClassNotFoundException e) {
			deleteResult = "ClassNotFound";
			e.printStackTrace();
		}

		return deleteResult;
	}	

	public String renameGroup(int groupId, String newName) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "UPDATE groups SET gname = '" + newName + "' WHERE id = " + groupId;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			renameResult = "Successfully renamed the group.";
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return renameResult;
	}
	
}
