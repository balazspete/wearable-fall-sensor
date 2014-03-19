package com.example.wearablesensorbase.ble;

import java.util.ArrayList;
import java.util.List;

import com.example.wearablesensorbase.R;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConnectedDeviceAdapter extends DeviceAdapter {

	private List<BLEConnection> connections;
	
	public ConnectedDeviceAdapter(Activity activity) {
		super(activity);
		this.connections = new ArrayList<BLEConnection>();
	}
	
	@Override
	public int getCount() {
		return connections.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		TextView status = (TextView) view.findViewById(R.id.device_status);
		int bondState = getItem(position).getBondState();
		String state = BLEConnection.getBondString(bondState);
		
		status.setText(state);
		return view;
	}

	public BLEConnection getConnection(int arg0) {
		return connections.get(arg0);
	}
	
	public int getPosition(String connectionID) {
		int index = -1;
		for (BLEConnection c : connections) {
			index++;
			if (c.getDevice().getAddress().equals(connectionID)) {
				return index;
			}
		}
		return -1;
	}
	
	public void addConnection(BLEConnection connection) {
		this.connections.add(connection);
		super.addDevice(connection.getDevice());
	}
	
	@Override
	public void addDevice(BluetoothDevice device) {
		throw new UnsupportedOperationException();
	}
	
	public void clear() {
		super.clear();
		connections.clear();
	}
	
}
