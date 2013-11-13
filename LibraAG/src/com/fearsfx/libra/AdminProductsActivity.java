package com.fearsfx.libra;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fearsfx.libra.models.Group;
import com.fearsfx.libra.models.Manufacturer;
import com.fearsfx.libra.models.Product;

import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class AdminProductsActivity extends Activity {

	private List<Product> products;
	private List<String> productNames = new ArrayList<String>();
	private List<Product> newProducts;
	private List<Group> groups;
	private List<Manufacturer> mans;

	private Context context = this;

	private ListView listView;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_products);

		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_products, menu);
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

	private void showProductInfo(final int position) {

		Intent intent = new Intent(context, ProductOptionsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("product",
				(Serializable) newProducts.get(position));
		bundle.putSerializable("groups", (Serializable) groups);
		bundle.putSerializable("mans", (Serializable) mans);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		switch (requestCode) {
		case 1: {
			if (resultCode == RESULT_OK) {
				boolean delete = data.getExtras().getBoolean("delete");
				if (delete) {
					Product product = (Product) data.getExtras().getSerializable("product");
					int index = 0;
					for (Product p : newProducts) {
						if (p.getId() == product.getId()) {
							break;
						}
						index++;
					}
					newProducts.remove(index);
					productNames.remove(index);
					refreshList(productNames);
				} else {
					Product changed = (Product) data.getExtras().getSerializable("product");
					int index = 0;
					for (Product p : newProducts) {
						if (p.getId() == changed.getId()) {
							break;
						}
						index++;
					}
					newProducts.remove(index);
					newProducts.add(changed);
					productNames = new ArrayList<String>();
					for (Product p : newProducts) {
						productNames.add(p.getName());
					}
					refreshList(productNames);
				}
			}
		}
			break;
		case 2: {
			if (resultCode == RESULT_OK) {
				Product changed = (Product) data
						.getSerializableExtra("product");
				newProducts.add(changed);
				productNames = new ArrayList<String>();
				for (Product p : newProducts) {
					productNames.add(p.getName());
				}
				refreshList(productNames);
			}
			// }
		}
			break;
		}
	}

	public void add(View view) {
		Intent intent = new Intent(context, ProductNewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("groups", (Serializable) groups);
		bundle.putSerializable("mans", (Serializable) mans);
		intent.putExtras(bundle);
		startActivityForResult(intent, 2);
	}

	private void refreshList(List<String> list) {
		// TODO Auto-generated method stub
		adapter = new ArrayAdapter<String>(AdminProductsActivity.this,
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
		products = (List<Product>) bundle.getSerializable("products");
		groups = (List<Group>) bundle.getSerializable("groups");
		mans = (List<Manufacturer>) bundle.getSerializable("mans");
		newProducts = products;
		for (Product p : products) {
			productNames.add(p.getName());
		}
	}

}
