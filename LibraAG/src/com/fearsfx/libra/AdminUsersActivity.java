package com.fearsfx.libra;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fearsfx.libra.models.Order;
import com.fearsfx.libra.models.OrderParser;
import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.models.ProductParser;
import com.fearsfx.libra.models.User;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint({ "NewApi", "SimpleDateFormat", "SetJavaScriptEnabled" })
public class AdminUsersActivity extends Activity {

	private AsyncService service;
	private List<User> users;
	private List<String> usersNames;
	private List<Integer> usersIds;
	private List<Order> orders;
	private List<String> ordersIds;
	private List<Product> products;
	private ArrayList<String> productNames;

	private int youCanCallMeTheChosenOne;
	private Context context = this;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private SimpleDateFormat sdf = new SimpleDateFormat("E - dd.MM.yyyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_users);

		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_users, menu);
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

		generateInfo();

		listView = (ListView) findViewById(R.id.usersList);
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_products, usersNames));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {
				// TODO Auto-generated method stub

				final Dialog dialog = new Dialog(context, R.style.cust_dialog);

				dialog.setContentView(R.layout.dialog_user_options);
				dialog.setTitle(usersNames.get(position));

				TextView userInfo = (TextView) dialog
						.findViewById(R.id.user_info);

				userInfo.setText(" User: " + users.get(position).getUsername()
						+ "\n Role: " + users.get(position).getRole()
						+ "\n First Name: "
						+ users.get(position).getFirstName() + "\n Last Name: "
						+ users.get(position).getLastName() + "\n Address: "
						+ users.get(position).getAddress() + "\n Phone: "
						+ users.get(position).getPhone());

				Button show = (Button) dialog
						.findViewById(R.id.dialogButtonShow);
				Button delete = (Button) dialog
						.findViewById(R.id.dialogButtonDelete);

				show.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						youCanCallMeTheChosenOne = position;
						getUserOrders(usersIds.get(position));
					}
				});

				delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						deleteUser(usersIds.get(position));
						removeUser(usersIds.get(position));
						dialog.dismiss();
					}
				});

				dialog.show();
			}

		});
	}

	private void refreshList() {
		// TODO Auto-generated method stub
		adapter = new ArrayAdapter<String>(this, R.layout.list_item_products,
				usersNames);

		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.invalidateViews();
	}

	private void removeUser(int id) {
		List<User> newUsers = new ArrayList<User>();
		usersIds = new ArrayList<Integer>();
		usersNames = new ArrayList<String>();
		for (User u : users) {
			if (u.getId() != id) {
				newUsers.add(u);
				usersIds.add(u.getId());
				usersNames.add(u.getUsername());
			}
		}
		users = newUsers;
		refreshList();
	}

	private void deleteUser(final int id) {
		// TODO Auto-generated method stub
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doDeleteUser(id, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 4, null);
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

	private void getUserOrders(final int id) {
		// TODO Auto-generated method stub
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetOrders(id, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 1, null);
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

	private void processResult(Object content, int caller, Object data) {
		// TODO Auto-generated method stub
		switch (caller) {
		case 1: {
			if (content != null) {
				OrderParser parser = new OrderParser();
				orders = new ArrayList<Order>();
				parser.parse(content.toString(), orders);
				String value = "";
				ordersIds = new ArrayList<String>();
				for (Order o : orders) {
					value = "Id : ";
					value += String.valueOf(o.getId());
					ordersIds.add(value);
				}
				showUserOrders();
			} else {
				System.out.println("null");
			}
		}
			break;
		case 2: {
			ProductParser parser = new ProductParser();
			products = new ArrayList<Product>();
			parser.parse(content.toString(), products);

			productNames = new ArrayList<String>();
			for (Product p : products) {
				productNames.add(p.getName());
			}
		}
			break;
		case 3: {
			User user = (User) data;
			users.add(user);
			usersIds.add(user.getId());
			usersNames.add(user.getUsername());
			refreshList();
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();
		}
			break;
		case 4: {
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();
		}
			break;
		case 5: {
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();
		}
			break;
		}
	}

	private void notifyError(Object message) {
		// TODO Auto-generated method stub
		if (message != null) {
			Toast.makeText(this,
					"An error has occurred: " + message.toString(),
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "The selected user has no previous orders.",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private Dialog allOrdersDialog;

	private void showUserOrders() {
		// TODO Auto-generated method stub

		allOrdersDialog = new Dialog(context, R.style.cust_dialog);

		allOrdersDialog.setContentView(R.layout.dialog_orders_list);
		allOrdersDialog.setTitle(usersNames.get(youCanCallMeTheChosenOne));

		ListView listView2;
		listView2 = (ListView) allOrdersDialog.findViewById(R.id.ordersList);
		listView2.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_orders, R.id.order_id, ordersIds) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				TextView dateView = (TextView) view
						.findViewById(R.id.order_date);
				dateView.setText("Ordered on : "
						+ sdf.format(orders.get(position).getDate()));

				TextView orderView = (TextView) view
						.findViewById(R.id.order_status);
				orderView.setText(Html.fromHtml("Status : <b>"
						+ orders.get(position).getStatus() + "</b>"));

				TextView noteView = (TextView) view
						.findViewById(R.id.order_note);
				noteView.setText("Note : " + orders.get(position).getNote());

				return view;
			}
		});
		listView2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				showOrderInfo(position + 1);
			}
		});

		allOrdersDialog.show();
	}

	private void showOrderInfo(final int position) {
		Scanner in = new Scanner(ordersIds.get(position - 1))
				.useDelimiter("[^0-9]+");
		final int id = in.nextInt();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetProductsForOrder(id, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 2, null);
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

		final Dialog orderDialog = new Dialog(context, R.style.cust_dialog);

		orderDialog.setContentView(R.layout.dialog_admin_order);
		orderDialog.setTitle(ordersIds.get(position - 1));

		listView = (ListView) orderDialog.findViewById(R.id.productsList);
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_products, productNames));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// ---------------------------------------
				showProductInfo(position);
			}
		});

		Button dialogButton = (Button) orderDialog
				.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				orderDialog.dismiss();
			}
		});

		Button dialogButtonStatus = (Button) orderDialog
				.findViewById(R.id.dialogButtonStatus);

		dialogButtonStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final Dialog statusDialog = new Dialog(context,
						R.style.cust_dialog);

				statusDialog.setContentView(R.layout.dialog_change_status);
				statusDialog.setTitle(R.string.change_order_status);

				final Spinner statusSpinner = (Spinner) statusDialog
						.findViewById(R.id.status_spinner);

				Button dialogButton = (Button) statusDialog
						.findViewById(R.id.dialogButton);

				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						statusDialog.dismiss();
						orderDialog.dismiss();
						allOrdersDialog.dismiss();
						changeStatus(id, statusSpinner.getSelectedItem().toString());
					}
				});
				statusDialog.show();
			}
		});
		orderDialog.show();
	}

	private void changeStatus(final int orderId, final String status) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doStatusChange(orderId, status, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 5, null);
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

	private void showProductInfo(int position) {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_view_product);
		dialog.setTitle(products.get(position).getName());

		WebView webview = (WebView) dialog.findViewById(R.id.picture);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.loadUrl("http://188.254.195.75/LibraApp/images/"
				+ products.get(position).getPicture());

		TextView desc = (TextView) dialog.findViewById(R.id.description);
		desc.setText(products.get(position).getDescription());

		TextView group = (TextView) dialog.findViewById(R.id.group);
		group.setText(products.get(position).getGroup());

		TextView man = (TextView) dialog.findViewById(R.id.manufacturer);
		man.setText(products.get(position).getManufacturer());

		TextView code = (TextView) dialog.findViewById(R.id.barcode);
		code.setText(String.valueOf(products.get(position).getBarcode()));

		TextView quan = (TextView) dialog.findViewById(R.id.quantity);
		quan.setText(String.valueOf(products.get(position).getQuantity()));

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void add(View view) {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_new_user);
		dialog.setTitle("\t\t\t\t\t\tNew user\t\t\t\t\t\t");

		final EditText username = (EditText) dialog.findViewById(R.id.username);

		final EditText password = (EditText) dialog.findViewById(R.id.password);

		final Spinner role = (Spinner) dialog.findViewById(R.id.role_spinner);

		final EditText firstName = (EditText) dialog
				.findViewById(R.id.first_name);

		final EditText lastName = (EditText) dialog
				.findViewById(R.id.last_name);

		final EditText address = (EditText) dialog.findViewById(R.id.address);

		final EditText phone = (EditText) dialog.findViewById(R.id.phone);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@SuppressLint("DefaultLocale")
			@Override
			public void onClick(View v) {
				if (username.getText().toString().equals("")
						|| password.getText().toString().equals("")
						|| role.getSelectedItem().toString().equals("")
						|| firstName.getText().toString().equals("")
						|| lastName.getText().toString().equals("")
						|| address.getText().toString().equals("")
						|| phone.getText().toString().equals("")) {
					Toast.makeText(AdminUsersActivity.this,
							"Please fill all fields.", Toast.LENGTH_LONG)
							.show();
				} else {
					User user = new User();
					user.setUsername(username.getText().toString());
					user.setPassword(password.getText().toString());
					user.setRole(role.getSelectedItem().toString()
							.toLowerCase());
					user.setFirstName(firstName.getText().toString());
					user.setLastName(lastName.getText().toString());
					user.setAddress(address.getText().toString());
					user.setPhone(phone.getText().toString());

					createUser(user);
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	private void createUser(final User user) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doCreateUser(user, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 3, user);
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

	@SuppressWarnings("unchecked")
	private void generateInfo() {
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		users = (List<User>) bundle.getSerializable("users");
		usersNames = new ArrayList<String>();
		usersIds = new ArrayList<Integer>();
		for (User u : users) {
			usersNames.add(u.getUsername());
			usersIds.add(u.getId());
		}
	}

}
