package com.example.wearablesensorbase.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.example.wearablesensorbase.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class LogViewActivity extends Activity {

	public static String 
		DEVICE_NAME = "com.example.wearablesensorbase.ble_device_name",
		FILE_NAME = "com.example.wearablesensorbase.file_name";
	
	private String fileName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_view);
		
		String fileName = getIntent().getStringExtra(FILE_NAME);
		if (fileName != null) {
			this.fileName = fileName;
		} else {
			String deviceName = getIntent().getStringExtra(DEVICE_NAME);
			this.fileName = BufferedMeasurementSaver.getFileName(deviceName);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_refresh:
				load();
				return true;
			case R.id.action_share:
				share();
				return true;
			case R.id.action_delete:
				delete();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void load() {
		EditText text = (EditText) findViewById(R.id.log_text);
		text.setText(getTextInFile(fileName));
	}
	
	private void delete() {
		
	}
	
	private void share() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getRealFile(fileName)));
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, fileName));
	}
	
	private File getRealFile(String fileName) {
		return new File(getExternalFilesDir(null), fileName);
	}
	
	private String getTextInFile(String fileName) {
		StringBuilder builder = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(getRealFile(fileName)));
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
				builder.append('\n');
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return builder.toString();
	}

}
