package com.example.wearablesensorbase;

import com.example.wearablesensorbase.ble.ConnectedDeviceActivity;
import com.example.wearablesensorbase.calibration.DeviceCalibrationActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_grapher:
				openGrapher();
				return true;
			case R.id.action_devices:
				openDevices();
				return true;
			case R.id.action_calibrate:	
				calibrateDevice();
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}

	private void openGrapher() {
		Intent intent = new Intent(this, GrapherActivity.class);
		startActivity(intent);
	}
	
	private void openDevices() {
		Intent intent = new Intent(this, ConnectedDeviceActivity.class);
		startActivity(intent);
	}
	
	private void calibrateDevice() {
		Intent intent = new Intent(this, DeviceCalibrationActivity.class);
		startActivity(intent);
	}
	
	public void testDecisionTree(View view) {
		startActivity(new Intent(this, DecisionTreeTestActivity.class));
	}
	
}
