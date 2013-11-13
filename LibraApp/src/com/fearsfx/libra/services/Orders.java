package com.fearsfx.libra.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.fearsfx.libra.models.Order;
import com.fearsfx.libra.models.Product;
import com.google.gson.Gson;

public class Orders {

	private String response = "Ordering failed.";
	private String statusResult = "Changing status failed.";

	public String createOrder(int userId, String prodId, String prodQn,
			String note) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String userName = "root", password = "root";
		String query;

		Gson gson = new Gson();
		int[] prodIds = gson.fromJson(prodId, int[].class);
		int[] prodQns = gson.fromJson(prodQn, int[].class);

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			query = "SELECT * FROM products";
			ResultSet rs = st.executeQuery(query);
			while (rs.next()) {
				for (int i = 0; i < prodIds.length; i++) {
					// if ( !( ( rs.getInt("id") == prodIds[i] ) && (
					// (rs.getInt("quantity") - prodQns[i]) >= 0 ) ) ) {
					if (rs.getInt("p_id") == prodIds[i]) {
						if (rs.getInt("quantity") - prodQns[i] < 0) {
							response += "\n" + rs.getString("pname")
									+ " -- only " + rs.getInt("quantity")
									+ " available.";
						}
					}
				}
			}
			if (response.equals("Ordering failed.")) {
				query = "SELECT * FROM user_orders";
				rs = st.executeQuery(query);
				int id = 1;
				if (rs.next()) {
					rs.last();
					id += rs.getRow();
				}
				query = "INSERT INTO user_orders VALUES (NULL, " + userId
						+ ", " + id + ", NOW(), '" + note + "', 'NOT READY')";
				st.executeUpdate(query);

				for (int i = 0; i < prodIds.length; i++) {
					query = "UPDATE products SET quantity = quantity - "
							+ prodQns[i] + " WHERE p_id = '" + prodIds[i] + "'";
					st.executeUpdate(query);
					query = "INSERT INTO total_orders VALUES (NULL, " + id
							+ ", " + prodIds[i] + ", " + prodQns[i] + ")";
					st.executeUpdate(query);
				}
				response = "Successfully ordered.";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return response;
	}

	public Order[] getOrders(int userId) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "SELECT * FROM user_orders";
		String userName = "root", password = "root";

		Order[] orders = null;

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				int rowCount = 0;
				rs.beforeFirst();
				while (rs.next()) {
					if (rs.getInt("user_id") == userId) {
						rowCount++;
					}
				}
				if (rowCount != 0) {
					rs.beforeFirst();
					orders = new Order[rowCount];
					int curRow = 0;
					while (rs.next()) {
						if (rs.getInt("user_id") == userId) {
							orders[curRow] = new Order();
							orders[curRow].setId(rs.getInt("order_id"));
							orders[curRow].setUserId(rs.getInt("user_id"));
							orders[curRow].setNote(rs.getString("note"));
							orders[curRow].setStatus(rs.getString("status"));
							orders[curRow].setDate(rs.getDate("date"));
							curRow++;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return orders;
	}

	public Product[] getProductsForOrder(int orderId) {
		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "SELECT * FROM total_orders "
				+ "JOIN user_orders ON user_orders.order_id = total_orders.order_id "
				+ "JOIN products ON total_orders.product_id = products.p_id "
				+ "JOIN manufacturers ON products.manufacturer_id = manufacturers.id "
				+ "JOIN groups ON products.group_id = groups.id";
		String userName = "root", password = "root";

		List<Product> prods = null;
		Product[] result = null;

		Product prod = null;

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				rs.beforeFirst();
				while (rs.next()) {
					if (rs.getInt("order_id") == orderId)
						prods = new ArrayList<Product>();
				}
				if (prods != null) {
					rs.beforeFirst();
					while (rs.next()) {
						if (rs.getInt("order_id") == orderId) {
							prod = new Product();
							prod.setId(rs.getInt("p_id"));
							prod.setBarcode(rs.getInt("barcode"));
							prod.setDescription(rs.getString("description"));
							prod.setGroup(rs.getString("gname"));
							prod.setManufacturer(rs.getString("mname"));
							prod.setName(rs.getString("pname"));
							prod.setPicture(rs.getString("picture"));
							prod.setQuantity(rs.getShort("quantity"));
							prods.add(prod);
						}
					}
					result = new Product[prods.size()];
					for (int i = 0; i < prods.size(); i++) {
						result[i] = prods.get(i);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String changeStatus(int orderId, String status) {

		String dbUrl = "jdbc:mysql://localhost:3306/libra";
		String dbDriverClass = "com.mysql.jdbc.Driver";
		String sql = "UPDATE user_orders SET status = '" + status + "' WHERE order_id = " + orderId;
		String userName = "root", password = "root";

		try {
			Class.forName(dbDriverClass);
			Connection conn = DriverManager.getConnection(dbUrl, userName,
					password);
			Statement st = conn.createStatement();

			st.executeUpdate(sql);
			statusResult = "Successfully changed order's status.";
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return statusResult;
	}
	
}
