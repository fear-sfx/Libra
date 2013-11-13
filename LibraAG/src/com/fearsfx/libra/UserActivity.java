package com.fearsfx.libra;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fearsfx.libra.models.Order;
import com.fearsfx.libra.models.OrderParser;
import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.models.ProductParser;
import com.fearsfx.libra.models.User;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint("NewApi")
public class UserActivity extends Activity {

	private AsyncService service;
	private List<Product> result;
	private List<Order> orders;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		init();

		TextView tv = (TextView) findViewById(R.id.user_info);

		tv.setText(" ID: " + user.getId() + "\n User: " + user.getUsername()
				+ "\n Role: " + user.getRole() + "\n First Name: "
				+ user.getFirstName() + "\n Last Name: " + user.getLastName()
				+ "\n Address: " + user.getAddress() + "\n Phone: "
				+ user.getPhone());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_exit:
		{
			Intent intent = new Intent(getApplicationContext(),	LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("EXIT", true);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.service = new AsyncService();
		getInfo();
	}

	private void getInfo() {
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("user");
	}

	public void newOrder(View view) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetProducts(new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 1);
					}

					@Override
					public void onError(Object message) {
						// TODO Auto-generated method stub
						notifyError(message);
					}
				});
			}
		});
		thread.run();
	}

	public void listOrders(View view) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetOrders(user.getId(), new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 2);
					}

					@Override
					public void onError(Object message) {
						// TODO Auto-generated method stub
						notifyError(message);
					}
				});
			}
		});
		thread.run();
	}

	private void notifyError(Object message) {
		if(message == null)
			message = "No information found.";
		Toast.makeText(this, "An error has occurred: " + message.toString(),
				Toast.LENGTH_LONG).show();
	}

	private void processResult(Object content, int caller) {
		switch (caller) {
			case 1: {
				ProductParser parser = new ProductParser();
				result = new ArrayList<Product>();
				parser.parse(content.toString(), result);
				startProductsList();
				}break;
			case 2: {
				System.out.println(content.toString());
				OrderParser parser = new OrderParser();
				orders = new ArrayList<Order>();
				parser.parse(content.toString(), orders);
				startOrdersList();
				}break;
		}
	}

	private void startProductsList() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(UserActivity.this,
				ProductsListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("products", (Serializable) result);
		bundle.putSerializable("user", user);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void startOrdersList() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(UserActivity.this, OrdersListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("orders", (Serializable) orders);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
