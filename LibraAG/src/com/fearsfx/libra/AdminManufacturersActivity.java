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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fearsfx.libra.models.Manufacturer;
import com.fearsfx.libra.models.ManufacturerParser;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint("NewApi")
public class AdminManufacturersActivity extends Activity {

	private AsyncService service;
	private List<Manufacturer> mans;
	private List<String> mansNames;
	private List<Integer> mansIds;
	
	private Context context = this;
	private ListView listView;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_manufacturers);
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_manufacturers, menu);
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

		listView = (ListView) findViewById(R.id.manufacturersList);
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_products, mansNames));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {
				// TODO Auto-generated method stub

				final Dialog dialog = new Dialog(context, R.style.cust_dialog);

				dialog.setContentView(R.layout.dialog_man_options);
				dialog.setTitle(mansNames.get(position));

				final EditText name = (EditText) dialog.findViewById(R.id.man_name);
				name.setText(mansNames.get(position));
				
				Button save = (Button) dialog.findViewById(R.id.dialogButtonSave);				
				Button delete = (Button) dialog.findViewById(R.id.dialogButtonDelete);

				save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!name.getText().toString().equals(mansNames.get(position))) {
							renameManufacturer(mansIds.get(position), name.getText().toString());
							mans.get(position).setName(name.getText().toString());
							mansNames.remove(position);
							mansNames.add(name.getText().toString());
							dialog.dismiss();
							refreshList();
						}else{
							dialog.dismiss();
						}
					}
				});

				delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						deleteManufacturer(mansIds.get(position));
						removeManufacturer(mansIds.get(position));
						dialog.dismiss();
					}
				});
				
				dialog.show();
			}

		});
	}

	public void add(View view) {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_new_man);
		dialog.setTitle("\t\t\t\t\t\tNew man\t\t\t\t\t\t");

		final EditText name = (EditText) dialog.findViewById(R.id.man_name);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name.getText().toString().equals("")) {
					Toast.makeText(context,
							"Please fill all fields.", Toast.LENGTH_LONG)
							.show();
				} else {
					createManufacturer(name.getText().toString());
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}
	
	private void renameManufacturer(final int manId, final String manName) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doRenameManufacturer(manId, manName, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						Manufacturer man = new Manufacturer();
						man.setId(manId);
						man.setName(manName);
						processResult(content, 3, man);
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

	private void createManufacturer(final String manName) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doCreateManufacturer(manName, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 1, manName);
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

	private void refreshList() {
		// TODO Auto-generated method stub
		adapter = new ArrayAdapter<String>(this, R.layout.list_item_products,
				mansNames);

		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.invalidateViews();
	}

	private void removeManufacturer(int id) {
		List<Manufacturer> newMans = new ArrayList<Manufacturer>();
		mansIds = new ArrayList<Integer>();
		mansNames = new ArrayList<String>();
		for (Manufacturer u : mans) {
			if (u.getId() != id) {
				newMans.add(u);
				mansIds.add(u.getId());
				mansNames.add(u.getName());
			}
		}
		mans = newMans;
		refreshList();
	}

	private void deleteManufacturer(final int id) {
		// TODO Auto-generated method stub
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doDeleteManufacturer(id, new AsyncServiceCallback() {

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
	}
	
	private void processResult(Object content, int caller, Object data) {
		switch(caller) {
		case 1: {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					service.doGetManufacturers(new AsyncServiceCallback() {

						@Override
						public void onResult(Object content) {
							// TODO Auto-generated method stub
							ManufacturerParser parser = new ManufacturerParser();
							mans = new ArrayList<Manufacturer>();
							mansNames = new ArrayList<String>();
							mansIds = new ArrayList<Integer>();
							parser.parse(content.toString(), mans);
							for (Manufacturer g : mans) {
								mansNames.add(g.getName());
								mansIds.add(g.getId());
							}
							refreshList();	
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
			Toast.makeText(context, content.toString(), Toast.LENGTH_LONG).show();	
		}break;
		case 2: {
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();			
		}break;
		case 3: {
			Manufacturer man = (Manufacturer) data;
			for(Manufacturer g : mans) {
				if(g.getId() == man.getId())
					g.setName(man.getName());
				refreshList();
			}
			Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();			
		}break;
		}		
	}
	
	private void notifyError(Object message) {
		
	}

	@SuppressWarnings("unchecked")
	private void generateInfo() {
		Bundle bundle = new Bundle();
		bundle = getIntent().getExtras();
		mans = (List<Manufacturer>) bundle.getSerializable("mans");
		mansNames = new ArrayList<String>();
		mansIds = new ArrayList<Integer>();
		for (Manufacturer g : mans) {
			mansNames.add(g.getName());
			mansIds.add(g.getId());
		}
	}

}
