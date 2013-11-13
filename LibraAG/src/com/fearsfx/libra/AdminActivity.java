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
import android.widget.Toast;

import com.fearsfx.libra.models.Group;
import com.fearsfx.libra.models.GroupParser;
import com.fearsfx.libra.models.Manufacturer;
import com.fearsfx.libra.models.ManufacturerParser;
import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.models.ProductParser;
import com.fearsfx.libra.models.User;
import com.fearsfx.libra.models.UsersParser;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint("NewApi")
public class AdminActivity extends Activity {

	private AsyncService service;
	private List<User> users;
	private List<Group> groups;
	private List<Manufacturer> mans;
	private List<Product> products;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);

		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_exit: {
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
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
	}

	public void showUsers(View view) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetUsers(new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 1);
						startUsersActivity();
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

	public void showGroups(View view) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetGroups(new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 2);
						startGroupsActivity();
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

	public void showManufacturers(View view) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetManufacturers(new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 3);
						startManufacturersActivity();
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

	public void showProducts(View view) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetGroups(new AsyncServiceCallback() {

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
				service.doGetManufacturers(new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 3);
					}

					@Override
					public void onError(Object message) {
						// TODO Auto-generated method stub
						notifyError(message);
					}

				});
				service.doGetProducts(new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 4);
						startProductsActivity();
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

	private void processResult(Object content, int caller) {
		// TODO Auto-generated method stub
		switch (caller) {
		case 1: {
			UsersParser parser = new UsersParser();
			users = new ArrayList<User>();
			parser.parse(content.toString(), users);
		}
			break;
		case 2: {
			GroupParser parser = new GroupParser();
			groups = new ArrayList<Group>();
			parser.parse(content.toString(), groups);
		}
			break;
		case 3: {
			ManufacturerParser parser = new ManufacturerParser();
			mans = new ArrayList<Manufacturer>();
			parser.parse(content.toString(), mans);
		}
			break;
		case 4: {
			ProductParser parser = new ProductParser();
			products = new ArrayList<Product>();
			parser.parse(content.toString(), products);
		}
			break;
		}
	}

	private void startUsersActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AdminUsersActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("users", (Serializable) users);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void startGroupsActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AdminGroupsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("groups", (Serializable) groups);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void startManufacturersActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AdminManufacturersActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("mans", (Serializable) mans);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void startProductsActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, AdminProductsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("groups", (Serializable) groups);
		bundle.putSerializable("mans", (Serializable) mans);
		bundle.putSerializable("products", (Serializable) products);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void notifyError(Object message) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "An error has occurred: " + message.toString(),
				Toast.LENGTH_LONG).show();
	}

}
