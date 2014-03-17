package com.example.wearablesensorbase.ble;

import com.example.wearablesensorbase.R;
import com.example.wearablesensorbase.ble.BLEConnection.State;
import com.example.wearablesensorbase.data.LogViewActivity;
import com.example.wearablesensorbase.events.BLEConnectionEvent;
import com.example.wearablesensorbase.events.BLEConnectionEventListener;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class DeviceControlActivity extends Activity {

	public static final String 
		INTENT_EXTRA_BLE_DEVICE_NAME = "com.example.wearablesensorbase.ble_device_name",
		INTENT_EXTRA_BLE_DEVICE_ADDR = "com.example.wearablesensorbase.ble_device_addr";
	
	private String name, address, bond;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice device;
	private BLEConnection connection;
	private BLEConnectionEventListener bleServiceListener;
	private byte[] result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		setupActionBar();
		
		final Intent intent = getIntent();
		
		name = intent.getStringExtra(INTENT_EXTRA_BLE_DEVICE_NAME);
		address = intent.getStringExtra(INTENT_EXTRA_BLE_DEVICE_ADDR);

		setTitle(name);
		
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
		
		device = bluetoothAdapter.getRemoteDevice(address);
		bond = BLEConnection.getBondString(device.getBondState());
		
		connection = BLEService.getInstance().getConnection(address);
		
		drawUI();
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        BLEService.getInstance().addEventListener(getListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        BLEService.getInstance().removeEventListener(getListener());
    }
    
    private BLEConnectionEventListener getListener() {
		if (bleServiceListener == null) {
	    	bleServiceListener = new BLEConnectionEventListener() {
				@Override
				public void onConnectionStateChange(BLEConnectionEvent event) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							drawUI();
						}
					});
				}
				
				@Override
				public void onConnectionCharacteristicWrite(BLEConnectionEvent event) {}
				@Override
				public void onConnectionCharacteristicRead(BLEConnectionEvent event) {}
				@Override
				public void onConnectionCharacteristicChange(BLEConnectionEvent event) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							drawUI();
						}
					});
				}

				@Override
				public void onConnectionServiceDiscovery(BLEConnectionEvent event) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							drawUI();
						}
					});
				}

				@Override
				public void onIncomingData(BLEConnectionEvent event) {
					if (connection.equals(event.getConnection())) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								drawUI();
							}
						});
					}
				}
			};
		}
		
		return bleServiceListener;
    }

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.device_control, menu);
		if (connection != null) {
            menu.findItem(R.id.ble_connect).setVisible(false);
            menu.findItem(R.id.ble_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.ble_connect).setVisible(true);
            menu.findItem(R.id.ble_disconnect).setVisible(false);
        }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	        case R.id.ble_connect:
	            connectToDevice();
	            return true;
	        case R.id.ble_disconnect:
	        	disconnectFromDevice();
	            return true;
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_open_logs:
				openLog();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void drawUI() {
		((TextView) findViewById(R.id.ble_device_name)).setText(name);
		((TextView) findViewById(R.id.ble_device_addr)).setText(address);
		((TextView) findViewById(R.id.ble_bond_status)).setText(bond);
		
		if (connection == null || connection.getState() == State.DISCONNECTED) {
			((TextView) findViewById(R.id.device_connected)).setVisibility(View.INVISIBLE);
			((TextView) findViewById(R.id.device_not_connected)).setVisibility(View.VISIBLE);
		} else {
			TextView view = (TextView) findViewById(R.id.device_connected);
			view.setVisibility(View.VISIBLE);
			view.setText(getResources().getString(connection.getStateAsStringID()));
			((TextView) findViewById(R.id.device_not_connected)).setVisibility(View.INVISIBLE);
		}
		
		if (connection != null && connection.getLastData() != null) {
			((EditText) findViewById(R.id.last_data)).setText(new String(connection.getLastData()));
		}
		
		invalidateOptionsMenu();
	}
	
	private void connectToDevice() {
		connection = BLEService.getInstance().createConnection(address);
		connection.connect(this, bluetoothAdapter);
	}
	
	private void disconnectFromDevice() {
		if (connection == null) {
			Log.w("DeviceControlActivity", "Waat R U doin'? No previous connection here");
			return;
		}
		
		connection.disconnect();
	}
	
	public void sendMessage(View view) {
		EditText box = (EditText) findViewById(R.id.message_box);
		byte[] message = box.getText().toString().getBytes();
		BLEService.getInstance().writeDataToBLEConnection(connection, message);
	}
	
	private void openLog() {
		Intent intent = new Intent(this, LogViewActivity.class);
		intent.putExtra(LogViewActivity.DEVICE_NAME, connection.getDevice().getAddress());
		startActivity(intent);
	}
	
}
