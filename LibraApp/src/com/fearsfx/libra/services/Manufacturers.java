package com.fearsfx.libra.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fearsfx.libra.models.Manufacturer;

public class Manufacturers {
	private Manufacturer[] result;
	private String createResult = "Creating new manufacturer failed.";
	private String deleteResult = "Deleting manufacturer failed.";
	private String renameResult = "Renaming the manufacturer failed.";
	
	public Manufacturer[] getManufacturers(){
		generateItems();
		return result;
	}

	private void generateItems() {
		String dbUrl = "jdbc:mysql://localhost/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "SELECT * FROM manufacturers";
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
				result = new Manufacturer[rowCount];
				int curRow = 0;
				while (rs.next()) {				
					result[curRow] = new Manufacturer();
					result[curRow].setId(rs.getInt("id"));
					result[curRow].setName(rs.getString("mname"));
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

	public String createManufacturer(String manName) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "INSERT INTO manufacturers VALUES (NULL, '" + manName + "')";

		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			createResult = "Successfully created new manufacturer.";
		} catch (SQLException e) {
			e.printStackTrace();
			createResult = "SQLException";
		} catch (ClassNotFoundException e) {
			createResult = "ClassNotFound";
			e.printStackTrace();
		}

		return createResult;
	}

	public String deleteManufacturer(int manId) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "DELETE FROM manufacturers WHERE id = " + manId;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			deleteResult = "Successfully deleted manufacturer.";
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "SQLException";
		} catch (ClassNotFoundException e) {
			deleteResult = "ClassNotFound";
			e.printStackTrace();
		}

		return deleteResult;
	}	

	public String renameManufacturer(int manId, String newName) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "UPDATE manufacturers SET mname = '" + newName + "' WHERE id = " + manId;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			renameResult = "Successfully renamed the manufacturer.";
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return renameResult;
	}
}
