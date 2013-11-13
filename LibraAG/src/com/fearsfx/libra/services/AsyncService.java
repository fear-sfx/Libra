package com.fearsfx.libra.services;

import java.util.HashMap;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.models.User;
import com.google.gson.Gson;

public class AsyncService {    
	
	Map<String, String> methodNames;
	String NAMESPACE = "http://services.libra.fearsfx.com";
	String BASE_URL = "http://188.254.195.75/LibraApp/services/";
	
	public AsyncService() {
		methodNames = new HashMap<String, String>();
		methodNames.put("login", "doLogin");
		methodNames.put("users", "getUsers");
		methodNames.put("products", "getProducts");
		methodNames.put("new_prod", "createProduct");
		methodNames.put("edit_prod", "editProduct");
		methodNames.put("del_prod", "deleteProduct");
		methodNames.put("ch_pic", "changePicture");
		methodNames.put("groups", "getGroups");
		methodNames.put("manufacturers", "getManufacturers");
		methodNames.put("new_order", "createOrder");
		methodNames.put("new_user", "createUser");
		methodNames.put("del_user", "deleteUser");
		methodNames.put("new_group", "createGroup");
		methodNames.put("del_group", "deleteGroup");
		methodNames.put("group_name", "renameGroup");
		methodNames.put("new_man", "createManufacturer");
		methodNames.put("del_man", "deleteManufacturer");
		methodNames.put("man_name", "renameManufacturer");
		methodNames.put("get_orders", "getOrders");
		methodNames.put("order_products", "getProductsForOrder");
		methodNames.put("order_status", "changeStatus");
	}

    public void doLogin(String username, String password, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("login");
    	final String URL = BASE_URL + "Login" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("login"));
		request.addProperty("user", username);
		request.addProperty("pass", password);
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);
    }

    public void doCreateOrder(int userId, int[] prodIds, int[] prodQns, String note, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("new_order");
    	final String URL = BASE_URL + "Orders" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("new_order"));
		
		Gson gson = new Gson();
		String prodId = gson.toJson(prodIds);
		String prodQn = gson.toJson(prodQns);
		
		request.addProperty("userId", userId);
		request.addProperty("prodId", prodId);
		request.addProperty("prodQn", prodQn);
		request.addProperty("note", note);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doCreateUser(User user, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("new_user");
    	final String URL = BASE_URL + "AdminUsers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("new_user"));

		Gson gson = new Gson();
		String userString = gson.toJson(user);
		
		request.addProperty("userString", userString);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doCreateProduct(Product product, byte[] picArray, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("new_prod");
    	final String URL = BASE_URL + "Products" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("new_prod"));

		Gson gson = new Gson();
		String prodString = gson.toJson(product);
		String picString = gson.toJson(picArray);
		
		request.addProperty("product", prodString);
		request.addProperty("picture", picString);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doDeleteProduct(Product product, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("del_prod");
    	final String URL = BASE_URL + "Products" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("del_prod"));

		Gson gson = new Gson();
		String prodString = gson.toJson(product);
		
		request.addProperty("product", prodString);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doEditProduct(Product product, byte[] picArray, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("edit_prod");
    	final String URL = BASE_URL + "Products" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("edit_prod"));

		Gson gson = new Gson();
		String prodString = gson.toJson(product);
		String picString = gson.toJson(picArray);
		
		request.addProperty("product", prodString);
		request.addProperty("picture", picString);
		
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doCreateGroup(String groupName, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("new_group");
    	final String URL = BASE_URL + "Groups" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("new_group"));

		request.addProperty("groupName", groupName);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doCreateManufacturer(String manName, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("new_man");
    	final String URL = BASE_URL + "Manufacturers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("new_man"));

		request.addProperty("manName", manName);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doGetOrders(int userId, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("get_orders");
    	final String URL = BASE_URL + "Orders" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("get_orders"));
		
		request.addProperty("userId", userId);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doStatusChange(int orderId, String status, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("order_status");
    	final String URL = BASE_URL + "Orders" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("order_status"));

		request.addProperty("orderId", orderId);
		request.addProperty("status", status);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doRenameGroup(int groupId, String newName, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("group_name");
    	final String URL = BASE_URL + "Groups" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("group_name"));

		request.addProperty("groupId", groupId);
		request.addProperty("newName", newName);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doRenameManufacturer(int manId, String newName, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("man_name");
    	final String URL = BASE_URL + "Manufacturers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("man_name"));

		request.addProperty("manId", manId);
		request.addProperty("newName", newName);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doDeleteUser(int userId, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("del_user");
    	final String URL = BASE_URL + "AdminUsers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("del_user"));
		
		request.addProperty("userId", userId);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doDeleteGroup(int groupId, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("del_group");
    	final String URL = BASE_URL + "Groups" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("del_group"));
		
		request.addProperty("groupId", groupId);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }

    public void doDeleteManufacturer(int manId, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("del_man");
    	final String URL = BASE_URL + "Manufacturers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("del_man"));
		
		request.addProperty("manId", manId);
				
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);	
    }
    
    public void doGetGroups(final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("groups");
    	final String URL = BASE_URL + "Groups" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("groups"));
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);
    }

    public void doGetManufacturers(final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("manufacturers");
    	final String URL = BASE_URL + "Manufacturers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("manufacturers"));
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);
    }

    public void doGetUsers(final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("users");
    	final String URL = BASE_URL + "AdminUsers" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("users"));
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);
    }

    public void doGetProducts(final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("products");
    	final String URL = BASE_URL + "Products" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("products"));
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);
    }

    public void doGetProductsForOrder(int orderId, final AsyncServiceCallback callback) {
    	final String SOAP_ACTION = NAMESPACE + methodNames.get("order_products");
    	final String URL = BASE_URL + "Orders" + "?wsdl";
    	
		SoapObject request = new SoapObject(NAMESPACE, methodNames.get("order_products"));

		request.addProperty("orderId", orderId);
		
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		doRequest(URL, SOAP_ACTION, envelope, callback);
    }

    private void doRequest(String URL, String SOAP_ACTION, SoapSerializationEnvelope envelope, final AsyncServiceCallback callback) {
    	try{
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			Object result = envelope.getResponse();
			if(result != null) {
				callback.onResult(result);
			}else{
				callback.onError(result);
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public interface AsyncServiceCallback {
		void onResult(Object content);
		void onError(Object message);
    }
    
}