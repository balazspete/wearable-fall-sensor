package com.example.wearablesensorbase.calibration;

import java.util.List;

import com.example.wearablesensorbase.R;
import com.example.wearablesensorbase.ble.BLEConnection;
import com.example.wearablesensorbase.ble.BLEService;
import com.example.wearablesensorbase.ble.ConnectedDeviceAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class DeviceCalibrationActivity extends Activity {

	private DeviceCalibrationAdapter adapter;
	private boolean calibrating = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_callibration);
		// Show the Up button in the action bar.
		setupActionBar();
		setupLayout();
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
		getMenuInflater().inflate(R.menu.device_callibration, menu);
		
		if (calibrating) {
			menu.findItem(R.id.action_calibration_start).setVisible(false);
			menu.findItem(R.id.action_calibration_stop).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.circular_progress);
		} else {
			menu.findItem(R.id.action_calibration_start).setVisible(true);
			menu.findItem(R.id.action_calibration_stop).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_calibration_start:
				startCalibration();
				return true;
			case R.id.action_calibration_stop:
				stopCalibration();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setupLayout() {
		ListView view = (ListView) findViewById(R.id.sensor_list);
		adapter = new DeviceCalibrationAdapter(this);
		view.setAdapter(adapter);
		
		List<BLEConnection> connections = BLEService.getInstance().getConnections();
		if (connections.size() > 0) {
			// if there are connections, show the list
			for (BLEConnection connection : connections) {
				adapter.addConnection(connection);
			}
		}

		adapter.notifyDataSetChanged();
		setCalibrationProgress(0.01);
	}

	private void setCalibrationProgress(double progress) {
		if (progress > 1 || progress < 0 ) {
			progress = 1;
		}
		
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setProgress((int)(bar.getMax() * progress));
	}
	
	private void startCalibration() {
		calibrating = true;
		invalidateOptionsMenu();
	}
	
	private void stopCalibration() {
		calibrating = false;
		invalidateOptionsMenu();
	}
	
}
