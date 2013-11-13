package com.fearsfx.libra;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FilePickerActivity  extends ListActivity {
	
	public final static String EXTRA_FILE_PATH = "file_path";
	public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";
	public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";
	@SuppressLint("SdCardPath")
	private final static String DEFAULT_INITIAL_DIRECTORY = "/sdcard/";
	
	protected File mDirectory;
	protected ArrayList<File> mFiles;
	protected FilePickerListAdapter mAdapter;
	protected boolean mShowHiddenFiles = false;
	protected String[] acceptedFileExtensions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflator = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View emptyView = inflator.inflate(R.layout.activity_empty_picker, null);
		((ViewGroup)getListView().getParent()).addView(emptyView);
		getListView().setEmptyView(emptyView);
		
		mDirectory = new File(DEFAULT_INITIAL_DIRECTORY);
		
		mFiles = new ArrayList<File>();
		
		mAdapter = new FilePickerListAdapter(this, mFiles);
		setListAdapter(mAdapter);
		
		acceptedFileExtensions = new String[] {};
		
		if(getIntent().hasExtra(EXTRA_FILE_PATH)) {
			mDirectory = new File(getIntent().getStringExtra(EXTRA_FILE_PATH));
		}
		if(getIntent().hasExtra(EXTRA_SHOW_HIDDEN_FILES)) {
			mShowHiddenFiles = getIntent().getBooleanExtra(EXTRA_SHOW_HIDDEN_FILES, false);
		}
		if(getIntent().hasExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS)) {
			ArrayList<String> collection = getIntent().getStringArrayListExtra(EXTRA_ACCEPTED_FILE_EXTENSIONS);
			acceptedFileExtensions = (String[]) collection.toArray(new String[collection.size()]);
		}
	}
	
	@Override
	protected void onResume() {
		refreshFilesList();
		super.onResume();
	}
	
	protected void refreshFilesList() {
		mFiles.clear();
		ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);
		
		File[] files = mDirectory.listFiles(filter);
		if(files != null && files.length > 0) {
			for(File f : files) {
				if(f.isHidden() && !mShowHiddenFiles) {
					continue;
				}
				mFiles.add(f);
			}
			
			Collections.sort(mFiles, new FileComparator());
		}
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {
		if(mDirectory.getParentFile() != null) {
			mDirectory = mDirectory.getParentFile();
			refreshFilesList();
			return;
		}
		super.onBackPressed();
	}

	@Override
	public void onDestroy() {
		finish();
		super.onDestroy();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File newFile = (File)l.getItemAtPosition(position);
		
		if(newFile.isFile()) {
			Intent extra = new Intent();
			extra.putExtra(EXTRA_FILE_PATH, newFile.getAbsolutePath());
			setResult(RESULT_OK, extra);
			finish();
		} else {
			mDirectory = newFile;
			refreshFilesList();
		}
		super.onListItemClick(l, v, position, id);
	}
	
	private class FilePickerListAdapter extends ArrayAdapter<File> {
		private List<File> mObjects;
		
		public FilePickerListAdapter(Context context, List<File> objects) {
			super(context, R.layout.activity_file_picker, android.R.id.text1, objects);
			mObjects = objects;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = null;
			
			if(convertView == null) { 
				LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.activity_file_picker, parent, false);
			} else {
				row = convertView;
			}

			File object = mObjects.get(position);

			ImageView imageView = (ImageView)row.findViewById(R.id.file_picker_image);
			TextView textView = (TextView)row.findViewById(R.id.file_picker_text);
			textView.setSingleLine(true);
			
			textView.setText(object.getName());
			if(object.isFile()) {
				imageView.setImageResource(R.drawable.file);
			} else {
				imageView.setImageResource(R.drawable.folder);
			}
			return row;
		}

	}
	
	private class FileComparator implements Comparator<File> {
	    public int compare(File f1, File f2) {
	    	if(f1 == f2) {
	    		return 0;
	    	}
	    	if(f1.isDirectory() && f2.isFile()) {
	        	return -1;
	        }
	    	if(f1.isFile() && f2.isDirectory()) {
	        	return 1;
	        }
	    	return f1.getName().compareToIgnoreCase(f2.getName());
	    }
	}
	
	private class ExtensionFilenameFilter implements FilenameFilter {
		private String[] mExtensions;
		
		public ExtensionFilenameFilter(String[] extensions) {
			super();
			mExtensions = extensions;
		}
		
		public boolean accept(File dir, String filename) {
			if(new File(dir, filename).isDirectory()) {
				return true;
			}
			if(mExtensions != null && mExtensions.length > 0) {
				for(int i = 0; i < mExtensions.length; i++) {
					if(filename.endsWith(mExtensions[i])) {
						return true;
					}
				}
				return false;
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file_picker, menu);
		return true;
	}

}
