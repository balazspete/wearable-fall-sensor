package com.example.wearablesensorbase.calibration;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wearablesensorbase.R;
import com.example.wearablesensorbase.ble.BLEConnection;
import com.example.wearablesensorbase.ble.ConnectedDeviceAdapter;

public class DeviceCalibrationAdapter extends ConnectedDeviceAdapter {

	private List<Boolean> statuses;
	
	public DeviceCalibrationAdapter(Activity activity) {
		super(activity);
		statuses = new ArrayList<Boolean>();
	}
	
	@Override
	public void addConnection(BLEConnection connection) {
		super.addConnection(connection);
		statuses.add(false);
	}
	
	public void clear() {
		super.clear();
		statuses.clear();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		
		if (statuses.get(position)) {
			view.setBackgroundColor(Color.argb(50, 0, 255, 0));
		} else {
			view.setBackgroundColor(Color.argb(50, 255, 0, 0));
		}
		
		return view;
	}

}
