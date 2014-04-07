package com.example.wearablesensorbase.calibration;

import java.util.HashMap;
import java.util.List;

import com.example.wearablesensorbase.R;
import com.example.wearablesensorbase.WearableSensorBase;
import com.example.wearablesensorbase.ble.BLEConnection;
import com.example.wearablesensorbase.ble.BLEService;
import com.example.wearablesensorbase.ble.ConnectedDeviceAdapter;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.events.MeasurementEvent;
import com.example.wearablesensorbase.events.MeasurementEventListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class DeviceCalibrationActivity extends Activity {
	
	private WearableSensorBase app;
	private HashMap<String, SensorMeasurement> firstMeasurements;
	
	private int currentState = -1;
	private Step[] steps = { 
		Step.INITIAL,
		Step.FORWARD, 
		Step.BACKWARD, 
		Step.LEFTWARD, 
		Step.RIGHTWARD, 
		Step.DOWNWARD, 
		Step.UPWARD 
	};
	
	private DeviceCalibrationAdapter adapter;
	private boolean calibrating = false;
	private boolean nextVisible = false;
	
	private MeasurementEventListener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_callibration);
		// Show the Up button in the action bar.
		setupActionBar();

		app = (WearableSensorBase) getApplication();
		
		setupLayout();
		invalidateOptionsMenu();
		
		listener = new MeasurementEventListener() {
			@Override
			public void measurement(MeasurementEvent event) {
				handleNewMeasurement(event);
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.addMeasurementEventListener(listener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		app.removeMeasurementEventListener(listener);
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
		
		System.out.println("Invalidating options menu");
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
		TextView text = (TextView) findViewById(R.id.main_instruction);
		text.setText("Calibration");
		
		text = (TextView) findViewById(R.id.mini_instruction_above);
		text.setText("");
		
		text = (TextView) findViewById(R.id.mini_instruction_below);
		text.setText("Press START to begin");
		
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
		
		BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.NON_STOP_DATA);
		
		currentState = 0;
		beginCalibrationStep();
	}
	
	private void stopCalibration() {
		calibrating = false;
		BLEService.getInstance().writeDataToAllBLEConnections(BLEConnection.STOP_DATA);
		invalidateOptionsMenu();
	}
	
	private void beginCalibrationStep() {
		firstMeasurements = new HashMap<String, SensorMeasurement>();

		adapter.clearStatuses();
		adapter.notifyDataSetChanged();
		findViewById(R.id.sensor_list).invalidate();
		
		setCalibrationProgress(((double) currentState)/steps.length);
		showInstructionsOnScreen();
		
		nextVisible = false;
		calibrating = true;
	}
	
	private void handleNewMeasurement(MeasurementEvent event) {
		if (!calibrating) {
			return;
		}
		
		SensorMeasurement first = firstMeasurements.get(event.getSensorId());
		if (first == null) {
			firstMeasurements.put(event.getSensorId(), event.getMeasurement());
			return;
		}
		
		app.calibrateDeviceInDirection(event.getSensorId(), steps[currentState], first, event.getMeasurement());
		
		int pos = adapter.getPosition(event.getSensorId());
		System.out.println("position "+pos);
		adapter.setStatus(pos, true);
		adapter.notifyDataSetChanged();
		
		if (!isAllCalibratedInStep()) {
			return;
		}
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				doneWithStep();
			}
		}).start();
	}
	
	public void doneWithStep() {
		if (currentState +1 >= steps.length) {
			doneWithCalibration();
			return;
		}
		
		final Activity a = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(a, "DONE WITH STEP " + steps[currentState], Toast.LENGTH_LONG).show();
			}
		});
		
		calibrating = false;
		nextVisible = true;
		adapter.notifyDataSetChanged();
		invalidateOptionsMenu();
	}
	
	public void nextClick(View item) {
		System.out.println("NEXT");
		if (calibrating) {
			Toast.makeText(this, "Cannot go to next yet!", Toast.LENGTH_SHORT).show();
			return;
		}
		goToNext();
	}
	
	private void goToNext() {
		currentState++;
		beginCalibrationStep();
	}
	
	private boolean isAllCalibratedInStep() {
		boolean calibrated = true;
		for (int i = 0; i < adapter.getCount(); i++) {
			calibrated = calibrated && adapter.getStatus(i);
			System.out.println(calibrated);
		}
		
		return calibrated;
	}
	
	private void doneWithCalibration() {
		stopCalibration();
		setCalibrationProgress(1);
		adapter.notifyDataSetChanged();
		final Activity a = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((WearableSensorBase) getApplication()).initializeDetector();
				Toast.makeText(a, "Calibration complete", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void showInstructionsOnScreen() {
		if (currentState < steps.length) {
			TextView text = (TextView) findViewById(R.id.main_instruction);
			text.setText("Move " + steps[currentState].toString());
			
			text = (TextView) findViewById(R.id.mini_instruction_above);
			text.setText("Calibration step " + (currentState + 1) + " out of " + steps.length);
			
			text = (TextView) findViewById(R.id.mini_instruction_below);
			text.setText("Get all sensors to green");
		} else {
			TextView text = (TextView) findViewById(R.id.mini_instruction_above);
			text.setText("Calibration complete!");
		}
	}
	
}
