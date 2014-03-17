package com.example.wearablesensorbase.ble;

import com.example.wearablesensorbase.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class BLEScanActivity extends ListActivity {

	public static int SCAN_TIME = 10000;
	
	private boolean scanning = false;
	private Handler handler;
	
	private BluetoothAdapter bluetoothAdapter;
	private DeviceAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		
		handler = new Handler();
		
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
		
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
		
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        scanBLE(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
		
		adapter = new DeviceAdapter(this);
		setListAdapter(adapter);	
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanBLE(false);
        adapter.clear();
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
		getMenuInflater().inflate(R.menu.blescan, menu);
		
		if (!scanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.circular_progress);
        }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.menu_scan:
				adapter.clear();
				scanBLE(true);
				break;
			case R.id.menu_stop:
				scanBLE(false);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.addDevice(device);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };
	
	private void scanBLE(final boolean scan) {
		if (scan) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(bleScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_TIME);

            scanning = true;
            bluetoothAdapter.startLeScan(bleScanCallback);
        } else {
            scanning = false;
            bluetoothAdapter.stopLeScan(bleScanCallback);
        }
        invalidateOptionsMenu();
	}
	
	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		final BluetoothDevice device = adapter.getItem(position);
		if (device == null) {
			return;
		}
		
		final Intent intent = new Intent(this, DeviceControlActivity.class);
		intent.putExtra(DeviceControlActivity.INTENT_EXTRA_BLE_DEVICE_NAME, device.getName());
		intent.putExtra(DeviceControlActivity.INTENT_EXTRA_BLE_DEVICE_ADDR, device.getAddress());
		
		if (scanning) {
			bluetoothAdapter.stopLeScan(bleScanCallback);
			scanning = false;
		}
		
		startActivity(intent);
	}
	
}
