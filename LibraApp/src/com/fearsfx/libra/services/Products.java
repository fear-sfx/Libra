package com.fearsfx.libra.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fearsfx.libra.models.Product;
import com.google.gson.Gson;

public class Products {
	private Product[] result = null;
	private String editResult = "Editing failed.";
	private String createResult = "Creating failed.";
	private String deleteResult = "Deleting failed.";
	private boolean write = false, delete = false;

	public Product[] getProducts() {
		generateItems();
		return result;
	}

	private void generateItems() {
		String dbUrl = "jdbc:mysql://localhost/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "SELECT * FROM products "
				+ "JOIN manufacturers ON products.manufacturer_id = manufacturers.id "
				+ "JOIN groups ON products.group_id = groups.id";
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass).newInstance();
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				rs.last();
				int rowCount = rs.getRow();
				rs.beforeFirst();
				result = new Product[rowCount];
				int curRow = 0;
				while (rs.next()) {
					result[curRow] = new Product();
					result[curRow].setId(rs.getInt("p_id"));
					result[curRow].setBarcode(rs.getInt("barcode"));
					result[curRow].setDescription(rs.getString("description"));
					result[curRow].setGroup(rs.getString("gname"));
					result[curRow].setManufacturer(rs.getString("mname"));
					result[curRow].setName(rs.getString("pname"));
					result[curRow].setPicture(rs.getString("picture"));
					result[curRow].setQuantity(rs.getShort("quantity"));
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

	public String editProduct(String product, String picture) {

		Gson gson = new Gson();

		Product prod = gson.fromJson(product, Product.class);
		byte[] array = gson.fromJson(picture, byte[].class);

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sqlGroups = "SELECT id FROM groups WHERE gname = '"
				+ prod.getGroup() + "' ";
		int idGroup = 0;
		String sqlMans = "SELECT id FROM manufacturers WHERE mname = '"
				+ prod.getManufacturer() + "' ";
		int idMan = 0;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery(sqlGroups);
			rs.next();
			idGroup = rs.getInt("id");

			rs = st.executeQuery(sqlMans);
			rs.next();
			idMan = rs.getInt("id");

			String sqlMain = "UPDATE products SET pname = '" + prod.getName()
					+ "', barcode = " + prod.getBarcode() + ", description = '"
					+ prod.getDescription() + "', picture = '"
					+ prod.getPicture() + "', group_id = " + idGroup
					+ ", manufacturer_id = " + idMan + ", quantity = "
					+ prod.getQuantity() + " WHERE p_id = " + prod.getId();

			st.executeUpdate(sqlMain);

			if (array != null) {
				String fileName = prod.getPicture();
				writeToFile(array, fileName);
				if (write == true) {
					editResult = "Successfully edited product.";
				}
			} else {
				editResult = "Successfully edited product.";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return editResult;
	}

	public String createProduct(String product, String picture) {

		Gson gson = new Gson();

		Product prod = gson.fromJson(product, Product.class);
		byte[] array = gson.fromJson(picture, byte[].class);

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sqlGroups = "SELECT id FROM groups WHERE gname = '"
				+ prod.getGroup() + "' ";
		int idGroup = 0;
		String sqlMans = "SELECT id FROM manufacturers WHERE mname = '"
				+ prod.getManufacturer() + "' ";
		int idMan = 0;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery(sqlGroups);
			rs.next();
			idGroup = rs.getInt("id");

			rs = st.executeQuery(sqlMans);
			rs.next();
			idMan = rs.getInt("id");

			String sqlMain = "INSERT INTO products VALUES (NULL, '"
					+ prod.getName() + "', " + prod.getBarcode() + ", '"
					+ prod.getDescription() + "', '" + prod.getPicture()
					+ "', " + idGroup + ", " + idMan
					+ ", " + prod.getQuantity() + ")";

			st.executeUpdate(sqlMain);

			if (array != null) {
				String fileName = prod.getPicture();
				writeToFile(array, fileName);
				if (write == true) {
					createResult = "Successfully created product.";
				}
			} else {
				createResult = "Successfully created product.";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return createResult;
	}

	public String deleteProduct(String product) {

		Gson gson = new Gson();

		Product prod = gson.fromJson(product, Product.class);

		String dbUrl = "jdbc:mysql://localhost/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "DELETE FROM products WHERE p_id = " + prod.getId();
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass).newInstance();
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			
			deleteFile(prod.getPicture());
			if(delete)
				deleteResult = "Successfully deleted product.";
		} catch (SQLException e) {
			e.printStackTrace();
			deleteResult = "SQLException";
		} catch (ClassNotFoundException e) {
			deleteResult = "ClassNotFound";
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return deleteResult;
	}

	private void writeToFile(byte[] array, String fileName) {
		try {
			String path = "/var/lib/tomcat7/webapps/LibraApp/images/"
					+ fileName;
			FileOutputStream stream = new FileOutputStream(path);
			stream.write(array);
			write = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteFile(String fileName) {
		try {
			boolean success = (new File("/var/lib/tomcat7/webapps/LibraApp/images/" + fileName)).delete();
			if (success) {
				delete = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
