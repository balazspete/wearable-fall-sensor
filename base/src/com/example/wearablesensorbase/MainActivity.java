package com.example.wearablesensorbase;

import java.util.Locale;

import com.example.wearablesensorbase.ble.BLEConnection;
import com.example.wearablesensorbase.ble.BLEService;
import com.example.wearablesensorbase.ble.ConnectedDeviceActivity;
import com.example.wearablesensorbase.calibration.DeviceCalibrationActivity;
import com.example.wearablesensorbase.data.BufferedMeasurementSaver;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

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
			case R.id.action_logs:
				showLogs();
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}
	
	private void showLogs() {
		Intent intent = new Intent(this, LogListActivity.class);
		startActivity(intent);
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

	private String statement;
	
	@Override
	public void onClick(View v) {
		boolean begin = false;
		switch (v.getId()) {
			case R.id.button_fall:
				begin = true;
				statement = "FALL";
				BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.NON_STOP_DATA);
				break;
			case R.id.button_jump:
				begin = true;
				statement = "JUMP";
				BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.NON_STOP_DATA);
				break;
			case R.id.button_walk:
				begin = true;
				statement = "WALK";
				BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.NON_STOP_DATA);
				break;
			case R.id.button_stop:
				begin = false;
				BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.STOP_DATA);
				break;
			case R.id.button_start:
				begin = true;
				BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.NON_STOP_DATA);
				Toast.makeText(this, "Starting sensors...", Toast.LENGTH_SHORT).show();
				break;
			default:
				return;
		}
		
		BufferedMeasurementSaver saver = ((WearableSensorBase) getApplication()).getMeasurementSaver();
		if (saver != null) {
			String output = String.format(Locale.UK, "#STATEMENT(%d)|%S|%S#", System.currentTimeMillis(), begin ? "START" : "END", statement).toString();
			saver.writeStatementsToFile(output);
			Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
		}
	}
}
