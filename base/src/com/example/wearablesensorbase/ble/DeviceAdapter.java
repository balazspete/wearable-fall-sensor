package com.example.wearablesensorbase.ble;

import java.util.ArrayList;

import com.example.wearablesensorbase.R;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<BluetoothDevice> devices;
	
	public DeviceAdapter(Activity activity) {
		this.activity = activity;
		devices = new ArrayList<BluetoothDevice>();
	}

	@Override
	public int getCount() {
		return devices.size();
	}

	@Override
	public BluetoothDevice getItem(int position) {
		return devices.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.devices_row, null);
		}
		
		BluetoothDevice device = getItem(position);
		
		TextView name, address;
		name = (TextView) convertView.findViewById(R.id.device_name);
		
		name.setText(device.getName());
		
		address = (TextView) convertView.findViewById(R.id.device_address);
		address.setText(device.getAddress());
		
		TextView status = (TextView) convertView.findViewById(R.id.bond_status);
		int bondState = getItem(position).getBondState();
		String state;
		switch(bondState) {
			case BluetoothDevice.BOND_BONDED:
				state = "BONDED"; break;
			case BluetoothDevice.BOND_BONDING:
				state = "BONDING"; break;
			case BluetoothDevice.BOND_NONE:
				state = "NOT BONDED"; break;
			default: state = "";
		}
		status.setText(state);
		
		return convertView;
	}
	
	public void clear() {
		devices.clear();
	}
	
	public void addDevice(BluetoothDevice device) {
		devices.add(device);
	}
	
}
