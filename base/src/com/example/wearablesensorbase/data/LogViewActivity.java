package com.example.wearablesensorbase.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.example.wearablesensorbase.R;
import com.example.wearablesensorbase.R.layout;
import com.example.wearablesensorbase.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;

public class LogViewActivity extends Activity {

	public static String DEVICE_NAME = "com.example.wearablesensorbase.ble_device_name";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_view);
		
		String deviceName = getIntent().getStringExtra(DEVICE_NAME);
		String fileName = BufferedMeasurementSaver.getFileName(deviceName);
		
		EditText text = (EditText) findViewById(R.id.log_text);
		text.setText(getTextInFile(fileName));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_view, menu);
		return true;
	}
	
	private String getTextInFile(String fileName) {
		StringBuilder builder = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(getExternalFilesDir(null), fileName)));
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
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
