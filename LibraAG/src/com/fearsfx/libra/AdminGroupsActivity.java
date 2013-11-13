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

import com.fearsfx.libra.models.Group;
import com.fearsfx.libra.models.GroupParser;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint("NewApi")
public class AdminGroupsActivity extends Activity {

	private AsyncService service;
	private List<Group> groups;
	private List<String> groupsNames;
	private List<Integer> groupsIds;
	
	private Context context = this;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_groups);
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.admin_groups, menu);
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

		listView = (ListView) findViewById(R.id.groupsList);
		listView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.list_item_products, groupsNames));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {
				// TODO Auto-generated method stub

				final Dialog dialog = new Dialog(context, R.style.cust_dialog);

				dialog.setContentView(R.layout.dialog_group_options);
				dialog.setTitle(groupsNames.get(position));

				final EditText name = (EditText) dialog.findViewById(R.id.group_name);
				name.setText(groupsNames.get(position));
				
				Button save = (Button) dialog.findViewById(R.id.dialogButtonSave);				
				Button delete = (Button) dialog.findViewById(R.id.dialogButtonDelete);

				save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(!name.getText().toString().equals(groupsNames.get(position))) {
							renameGroup(groupsIds.get(position), name.getText().toString());
							groups.get(position).setName(name.getText().toString());
							groupsNames.remove(position);
							groupsNames.add(name.getText().toString());
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
						deleteGroup(groupsIds.get(position));
						removeGroup(groupsIds.get(position));
						dialog.dismiss();
					}
				});
				
				dialog.show();
			}

		});
	}

	public void add(View view) {

		final Dialog dialog = new Dialog(context, R.style.cust_dialog);

		dialog.setContentView(R.layout.dialog_new_group);
		dialog.setTitle("\t\t\t\t\t\tNew group\t\t\t\t\t\t");

		final EditText name = (EditText) dialog.findViewById(R.id.group_name);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (name.getText().toString().equals("")) {
					Toast.makeText(AdminGroupsActivity.this,
							"Please fill all fields.", Toast.LENGTH_LONG)
							.show();
				} else {
					createGroup(name.getText().toString());
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}
	
	private void renameGroup(final int groupId, final String groupName) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doRenameGroup(groupId, groupName, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						Group group = new Group();
						group.setId(groupId);
						group.setName(groupName);
						processResult(content, 3, group);
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

	private void createGroup(final String groupName) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doCreateGroup(groupName, new AsyncServiceCallback() {

					@Override
					public void onResult(Object content) {
						// TODO Auto-generated method stub
						processResult(content, 1, groupName);
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
				groupsNames);

		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.invalidateViews();
	}

	private void removeGroup(int id) {
		List<Group> newGroups = new ArrayList<Group>();
		groupsIds = new ArrayList<Integer>();
		groupsNames = new ArrayList<String>();
		for (Group u : groups) {
			if (u.getId() != id) {
				newGroups.add(u);
				groupsIds.add(u.getId());
				groupsNames.add(u.getName());
			}
		}
		groups = newGroups;
		refreshList();
	}

	private void deleteGroup(final int id) {
		// TODO Auto-generated method stub
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				service.doDeleteGroup(id, new AsyncServiceCallback() {

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
					service.doGetGroups(new AsyncServiceCallback() {

						@Override
						public void onResult(Object content) {
							// TODO Auto-generated method stub
							GroupParser parser = new GroupParser();
							groups = new ArrayList<Group>();
							groupsNames = new ArrayList<String>();
							groupsIds = new ArrayList<Integer>();
							parser.parse(content.toString(), groups);
							for (Group g : groups) {
								groupsNames.add(g.getName());
								groupsIds.add(g.getId());
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
			Group group = (Group) data;
			for(Group g : groups) {
				if(g.getId() == group.getId())
					g.setName(group.getName());
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
		groups = (List<Group>) bundle.getSerializable("groups");
		groupsNames = new ArrayList<String>();
		groupsIds = new ArrayList<Integer>();
		for (Group g : groups) {
			groupsNames.add(g.getName());
			groupsIds.add(g.getId());
		}
	}

}
