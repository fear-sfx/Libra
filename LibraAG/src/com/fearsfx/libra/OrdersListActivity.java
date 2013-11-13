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
import android.widget.ListView;
import android.widget.TextView;

import com.fearsfx.libra.models.Order;
import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.models.ProductParser;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint({ "SimpleDateFormat", "NewApi", "SetJavaScriptEnabled" })
public class OrdersListActivity extends Activity {

	private AsyncService service;
	private List<String> ordersIds = new ArrayList<String>();
	private List<Order> orders;
	private List<Product> result;
	private List<String> productNames = new ArrayList<String>();
	private Context context = this;

	private ListView listView;
	private SimpleDateFormat sdf = new SimpleDateFormat("E - dd.MM.yyyy");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orders_list);

		init();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.orders_list, menu);
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

		generateInfo();

		listView = (ListView) findViewById(R.id.ordersList);
		listView.setAdapter(new ArrayAdapter<String>(this,
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
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				showOrderInfo(position + 1);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void generateInfo() {
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		orders = (List<Order>) bundle.getSerializable("orders");
		String value = "";
		for (Order o : orders) {
			value = "Id : ";
			value += String.valueOf(o.getId());
			ordersIds.add(value);
		}
	}

	private void showOrderInfo(final int position) {
//		System.out.println(position);
		Scanner in = new Scanner(ordersIds.get(position - 1)).useDelimiter("[^0-9]+");
		final int id = in.nextInt();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doGetProductsForOrder(id, new AsyncServiceCallback() {
					
					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub

//						System.out.println(content.toString());
						
						ProductParser parser = new ProductParser();
						result = new ArrayList<Product>();
						parser.parse(content.toString(), result);
						
						productNames = new ArrayList<String>();
						for(Product p : result){
							productNames.add(p.getName());
						}
					}
					
					@Override
					public void onError(Object message) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		thread.run();

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_order);
//		System.out.println(ordersIds.size());
		dialog.setTitle(ordersIds.get(position - 1));

		listView = (ListView) dialog.findViewById(R.id.productsList);
		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_products,	productNames));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				//---------------------------------------
				showProductInfo(position);
			}
		});
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void showProductInfo(int position) {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_view_product);
		dialog.setTitle(result.get(position).getName());

		WebView webview = (WebView) dialog.findViewById(R.id.picture);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.loadUrl("http://188.254.195.75/LibraApp/images/"
				+ result.get(position).getPicture());

		TextView desc = (TextView) dialog.findViewById(R.id.description);
		desc.setText(result.get(position).getDescription());

		TextView group = (TextView) dialog.findViewById(R.id.group);
		group.setText(result.get(position).getGroup());

		TextView man = (TextView) dialog.findViewById(R.id.manufacturer);
		man.setText(result.get(position).getManufacturer());

		TextView code = (TextView) dialog.findViewById(R.id.barcode);
		code.setText(String.valueOf(result.get(position).getBarcode()));

		TextView quan = (TextView) dialog.findViewById(R.id.quantity);
		quan.setText(String.valueOf(result.get(position).getQuantity()));
		
		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					dialog.dismiss();
			}
		});
		dialog.show();
	}
	
}
