package com.example.wearablesensorbase.ble;

import java.util.List;

import com.example.wearablesensorbase.R;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class ConnectedDeviceActivity extends Activity {

	private ConnectedDeviceAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connected_devices);
		// Show the Up button in the action bar.
		setupActionBar();
		setupContentUI();
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
		getMenuInflater().inflate(R.menu.connected_devices, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_scan_devices:
				showScan();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setupContentUI() {
		ListView list = ((ListView) findViewById(R.id.sensor_list));
		adapter = new ConnectedDeviceAdapter(this);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
				final BluetoothDevice device = adapter.getItem(position);
				if (device == null) {
					return;
				}
				
				final Intent intent = new Intent(ConnectedDeviceActivity.this, DeviceControlActivity.class);
				intent.putExtra(DeviceControlActivity.INTENT_EXTRA_BLE_DEVICE_NAME, device.getName());
				intent.putExtra(DeviceControlActivity.INTENT_EXTRA_BLE_DEVICE_ADDR, device.getAddress());
				
				startActivity(intent);
			}
		});
		
		reloadUI();
	}
	
	public void reloadUI() {
		adapter.clear();
		
		List<BLEConnection> connections = BLEService.getInstance().getConnections();
		if (connections.size() > 0) {
			// if there are connections, show the list
			findViewById(R.id.no_sensors_text).setVisibility(View.INVISIBLE);
			
			for (BLEConnection connection : connections) {
				adapter.addConnection(connection);
			}
		}
		
		adapter.notifyDataSetChanged();
	}

	private void showScan() {
		Intent intent = new Intent(this, BLEScanActivity.class);
		startActivity(intent);
	}
	
}
