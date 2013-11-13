package com.fearsfx.libra;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fearsfx.libra.models.Group;
import com.fearsfx.libra.models.Manufacturer;
import com.fearsfx.libra.models.Product;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class ProductOptionsActivity extends Activity {

	private AsyncService service;
	private Product product;
	private String path = null;
	private List<Group> groups;
	private List<String> groupsNames;
	private List<Manufacturer> mans;
	private List<String> mansNames;

	private Context context = this;
	private InputStream is = null;
	private byte[] array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_product_options);

		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_options, menu);
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

		final WebView webview = (WebView) findViewById(R.id.picture);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.loadUrl("http://188.254.195.75/LibraApp/images/"
				+ product.getPicture());

		final EditText name = (EditText) findViewById(R.id.name);
		name.setText(product.getName());

		final EditText desc = (EditText) findViewById(R.id.description);
		desc.setText(product.getDescription());

		final Spinner group = (Spinner) findViewById(R.id.groups);
		ArrayAdapter<String> adapterG = new ArrayAdapter<String>(context,
				R.layout.list_item_products, groupsNames);
		adapterG.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		group.setAdapter(adapterG);
		final int indexG = groupsNames.indexOf(product.getGroup());
		group.setSelection(indexG);

		final Spinner man = (Spinner) findViewById(R.id.mans);
		ArrayAdapter<String> adapterM = new ArrayAdapter<String>(context,
				R.layout.list_item_products, mansNames);
		adapterM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		man.setAdapter(adapterM);
		final int indexM = mansNames.indexOf(product.getManufacturer());
		man.setSelection(indexM);

		final EditText code = (EditText) findViewById(R.id.barcode);
		code.setText(String.valueOf(product.getBarcode()));

		final EditText quantity = (EditText) findViewById(R.id.quantity);
		quantity.setText(String.valueOf(product.getQuantity()));

		Button buttonSave = (Button) findViewById(R.id.buttonSave);
		Button buttonDelete = (Button) findViewById(R.id.buttonDelete);
		Button changePic = (Button) findViewById(R.id.buttonChangePicture);

		changePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, FilePickerActivity.class);
				startActivityForResult(intent, 1);
			}
		});

		buttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (path != null
						|| !name.getText().toString().equals(product.getName())
						|| !desc.getText().toString()
								.equals(product.getDescription())
						|| !group.getItemAtPosition(
								group.getSelectedItemPosition()).equals(
								group.getItemAtPosition(indexG))
						|| !man.getItemAtPosition(man.getSelectedItemPosition())
								.equals(man.getItemAtPosition(indexM))
						|| !code.getText().toString()
								.equals(String.valueOf(product.getBarcode()))
						|| !quantity.getText().toString()
								.equals(String.valueOf(product.getQuantity()))) {
					try {
						if (path != null) {
							is = new FileInputStream(path);
							try {
								array = streamToBytes(is);
							} finally {
								is.close();
							}
						} else {
							array = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
						try {
							throw new IOException("Unable to open R.raw.");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					product.setBarcode(Integer.valueOf(code.getText()
							.toString()));
					product.setDescription(desc.getText().toString());
					product.setGroup(group.getSelectedItem().toString());
					product.setManufacturer(man.getSelectedItem().toString());
					product.setQuantity(Integer.valueOf(quantity.getText()
							.toString()));
					product.setName(name.getText().toString());
					if (path != null) {
						String picName = product.getName().replaceAll("\\s",
								"_");
						String extension = path.substring(path.lastIndexOf('.'));
						picName = picName.toLowerCase();
						picName = picName + extension;
						product.setPicture(picName);
					}
					service.doEditProduct(product, array,
							new AsyncServiceCallback() {

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
				finish();
			}
		});


		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				service.doDeleteProduct(product,
						new AsyncServiceCallback() {

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
				finish();
			}
		});


		
	}

	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		switch (requestCode) {
		case 1: {
			if (resultCode == RESULT_OK) {
				TextView filepath = (TextView) findViewById(R.id.file_path);
				filepath.setText(data
						.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
				path = data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH);
			}
		}
			break;
		}
	}

	private static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {
		}
		return os.toByteArray();
	}

	private void processResult(Object content, int caller) {
		switch (caller) {
		case 1: {
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();

			Intent extra = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("product", product);
			bundle.putBoolean("delete", false);
			extra.putExtras(bundle);
			setResult(RESULT_OK, extra);
		}
			break;
		case 2: {
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();

			Intent extra = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("product", product);
			bundle.putBoolean("delete", true);
			extra.putExtras(bundle);
			setResult(RESULT_OK, extra);
		}
			break;
		}
	}

	private void notifyError(Object message) {
		Toast.makeText(this, "An error has occurred: " + message.toString(),
				Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unchecked")
	private void generateInfo() {
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		product = (Product) bundle.getSerializable("product");
		groups = (List<Group>) bundle.getSerializable("groups");
		mans = (List<Manufacturer>) bundle.getSerializable("mans");
		groupsNames = new ArrayList<String>();
		mansNames = new ArrayList<String>();
		for (Group g : groups) {
			groupsNames.add(g.getName());
		}
		for (Manufacturer m : mans) {
			mansNames.add(m.getName());
		}
	}

}
