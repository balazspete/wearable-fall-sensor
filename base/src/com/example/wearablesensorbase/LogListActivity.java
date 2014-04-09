package com.example.wearablesensorbase;

import java.io.File;

import com.example.wearablesensorbase.data.LogViewActivity;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class LogListActivity extends Activity {

	private LogListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_list);
		// Show the Up button in the action bar.
		setupActionBar();
		
		adapter = new LogListAdapter(this);
		
		ListView list = (ListView) findViewById(R.id.log_list);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(LogListActivity.this, LogViewActivity.class);
				intent.putExtra(LogViewActivity.FILE_NAME, adapter.getItem(arg2).getName());
				startActivity(intent);
			}
		});
		
		list.setAdapter(adapter);
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class LogListAdapter extends BaseAdapter {

		private LogListActivity activity;
		private File[] files;
		
		public LogListAdapter(LogListActivity activity) {
			this.activity = activity;
			File directory = activity.getExternalFilesDir(null); 
			files = directory.listFiles();
		}
		
		@Override
		public int getCount() {
			return files.length;
		}

		@Override
		public File getItem(int position) {
			return files[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = activity.getLayoutInflater().inflate(R.layout.log_list_entry, null);
			}
			
			File file = getItem(position);
			String fileName = file.getName();
			int split = fileName.lastIndexOf("_");
			
			TextView text = (TextView) convertView.findViewById(R.id.log_entry_name);
			text.setText((split > 0 ? fileName.substring(0, split) : fileName));

			text = (TextView) convertView.findViewById(R.id.log_entry_sub);
			text.setText(split > 1 ? (fileName.substring(split)+1) : "" );
			
			return convertView;
		}
	}
	
}
