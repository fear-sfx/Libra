package com.fearsfx.libra;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fearsfx.libra.models.Group;
import com.fearsfx.libra.models.GroupParser;
import com.fearsfx.libra.models.Manufacturer;
import com.fearsfx.libra.models.ManufacturerParser;
import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.models.User;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class ProductsListActivity extends Activity {

	private AsyncService service;
	private List<Product> products;
	private int[] productsIds;
	private int[] productsQns;
	private List<String> productNames = new ArrayList<String>();
	private List<Group> groups;
	private List<String> groupNames;
	private List<Manufacturer> mans;
	private List<String> mansNames;
	private List<Product> cart;
	private List<Product> newProducts;
	private String note;
	private User user;
	private Context context = this;

	private ListView listView;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products_list);

		init();

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

		listView = (ListView) findViewById(R.id.productsList);
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_products, productNames));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				showProductInfo(position);
			}
		});
	}

	public void proceed(View v) {
		if (cart != null) {
			askNote();
		} else {
			Toast.makeText(this,
					"Cart empty. Add products before proceeding..",
					Toast.LENGTH_LONG).show();
		}
	}

	private void sendOrder() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int i = 0;
				productsIds = new int[cart.size()];
				productsQns = new int[cart.size()];
				for (Product p : cart) {
					productsIds[i] = p.getId();
					productsQns[i] = p.getQuantity();
					i++;
				}
				service.doCreateOrder(user.getId(), productsIds, productsQns,
						note, new AsyncServiceCallback() {

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
			}
		});
		thread.run();
	}

	public void filterByGroup(View v) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				service.doGetGroups(new AsyncServiceCallback() {
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

	private void processResult(Object content, int caller) {
		switch (caller) {
		case 1: {
			GroupParser parser = new GroupParser();
			groups = new ArrayList<Group>();
			parser.parse(content.toString(), groups);
			pickFilter(1);
		}
			break;
		case 2: {
			ManufacturerParser parser = new ManufacturerParser();
			mans = new ArrayList<Manufacturer>();
			parser.parse(content.toString(), mans);
			pickFilter(2);
		}
			break;
		case 3: {
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();
		}
			break;
		}
	}

	private void pickFilter(final int caller) {
		if (caller == 1) {
			groupNames = new ArrayList<String>();
			for (Group g : groups) {
				groupNames.add(g.getName());
			}
		} else {
			mansNames = new ArrayList<String>();
			for (Manufacturer m : mans) {
				mansNames.add(m.getName());
			}
		}
		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_filters);
		dialog.setTitle("Groups:");

		final ListView dListView = (ListView) dialog
				.findViewById(R.id.groupsList);
		if (caller == 1)
			dListView.setAdapter(new ArrayAdapter<String>(this,
					R.layout.list_item_products, groupNames));
		else
			dListView.setAdapter(new ArrayAdapter<String>(this,
					R.layout.list_item_products, mansNames));
		dListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// newProducts = new ArrayList<Product>();
				List<Product> newP = new ArrayList<Product>();
				for (Product p : newProducts) {
					if (caller == 1
							&& p.getGroup().equals(
									dListView.getItemAtPosition(position)))
						newP.add(p);
					if (caller == 2
							&& p.getManufacturer().equals(
									dListView.getItemAtPosition(position)))
						newP.add(p);
				}
				newProducts = new ArrayList<Product>();
				newProducts = newP;
				List<String> newNames = new ArrayList<String>();
				for (Product p : newProducts) {
					newNames.add(p.getName());
				}
				refreshList(newNames);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void notifyError(Object message) {
		Toast.makeText(this, "An error has occurred: " + message.toString(),
				Toast.LENGTH_LONG).show();
	}

	public void filterByManufacturer(View v) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				service.doGetManufacturers(new AsyncServiceCallback() {
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

	private void refreshList(List<String> list) {
		// TODO Auto-generated method stub
		adapter = new ArrayAdapter<String>(ProductsListActivity.this,
				R.layout.list_item_products);
		adapter.addAll(list);

		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.invalidateViews();
	}

	@SuppressWarnings("unchecked")
	private void generateInfo() {
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("user");
		products = (List<Product>) bundle.getSerializable("products");
		newProducts = products;
		for (Product p : products) {
			productNames.add(p.getName());
		}
	}

	private void showProductInfo(final int position) {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_product);
		dialog.setTitle(newProducts.get(position).getName());

		WebView webview = (WebView) dialog.findViewById(R.id.picture);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.loadUrl("http://188.254.195.75/LibraApp/images/"
				+ newProducts.get(position).getPicture());

		TextView desc = (TextView) dialog.findViewById(R.id.description);
		desc.setText(newProducts.get(position).getDescription());

		TextView group = (TextView) dialog.findViewById(R.id.group);
		group.setText(newProducts.get(position).getGroup());

		TextView man = (TextView) dialog.findViewById(R.id.manufacturer);
		man.setText(newProducts.get(position).getManufacturer());

		TextView code = (TextView) dialog.findViewById(R.id.barcode);
		code.setText(String.valueOf(newProducts.get(position).getBarcode()));

		final EditText quantity = (EditText) dialog.findViewById(R.id.quantity);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (quantity.getText().toString().equals(""))
					Toast.makeText(ProductsListActivity.this,
							"Enter product quantity before adding to cart..",
							Toast.LENGTH_LONG).show();
				else {
					Product chosen = newProducts.get(position);
					chosen.setQuantity(Integer.parseInt(quantity.getText()
							.toString()));
					addToCart(chosen);
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}

	private void askNote() {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_note);
		dialog.setTitle("Add note:");

		final EditText noteField = (EditText) dialog.findViewById(R.id.note);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				note = noteField.getText().toString();
				sendOrder();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	private void addToCart(Product product) {
		if (cart == null)
			cart = new ArrayList<Product>();
		cart.add(product);
		Toast.makeText(this, "Successfully added to cart..", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.products_list, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (listView.getAdapter().getCount() < productNames.size()) {
			newProducts = products;
			refreshList(productNames);
		} else {
			finish();
		}
	}

}
