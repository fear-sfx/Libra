package com.fearsfx.libra;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fearsfx.libra.models.User;
import com.fearsfx.libra.models.UserParser;
import com.fearsfx.libra.services.AsyncService;
import com.fearsfx.libra.services.AsyncService.AsyncServiceCallback;

@SuppressLint("NewApi")
public class LoginActivity extends Activity {

	private AsyncService service;
	private User result;
	private EditText usernameField;
	private EditText passwordField;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			trimCache(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
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

	public void signIn(View view) {
		result = null;
		final String username = this.usernameField.getText().toString();
		final String password = this.passwordField.getText().toString();

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				service.doLogin(username, password, new AsyncServiceCallback() {
					@Override
					public void onResult(Object content) {
						processResult(content);
					}

					@Override
					public void onError(Object message) {
						notifyError(message);
					}
				});
			}
		});
		thread.run();
	}

	private void init() {
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		this.service = new AsyncService();
		this.usernameField = (EditText) findViewById(R.id.username);
		this.passwordField = (EditText) findViewById(R.id.password);
	}

	private void notifyError(Object message) {
		Toast.makeText(this,
				"Incorrect username or password.\n\t\t\t\t\tPlease try again.",
				Toast.LENGTH_LONG).show();
	}

	private void processResult(Object content) {
		// System.out.println("Result : " + content.toString());
		UserParser parser = new UserParser();
		result = new User();
		parser.parse(content.toString(), result);
		startAccountPanel();
	}

	private void startAccountPanel() {
		Intent intent = null;
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", result);
		if (result.getRole().equals("user")) {
			intent = new Intent(this, UserActivity.class);
		} else if (result.getRole().equals("admin")) {
			intent = new Intent(this, AdminActivity.class);
		}
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
