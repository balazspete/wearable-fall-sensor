package com.example.wearablesensorbase.ble;

import java.util.List;
import java.util.UUID;

import com.example.wearablesensorbase.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

public class BLEConnection {
	
	public static final byte[] 
		STOP_DATA = { '0' }, 
		ONE_DATA = { '1' }, 
		NON_STOP_DATA = { '2' }, 
		BUFFERED_NON_STOP_DATA = { '3' };
	
	public enum State {
		CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
	}
	
    private final static String TAG = BLEConnection.class.getSimpleName();

	private String address;
	private BluetoothGatt bluetoothGatt;
	private BluetoothGattCallback gattCallback;
	private State state;
	private byte[] lastData = new byte[]{};
	
	public BLEConnection(String address, BluetoothGattCallback gattCallback) {
		if (address == null || gattCallback == null) {
			throw new NullPointerException("Parameters cannot be null");
		}
		
		this.address = address;
		this.gattCallback = gattCallback;
		this.state = State.DISCONNECTED;
	}
	
	public boolean connect(Context context, BluetoothAdapter adapter) {
		if (adapter == null) {
			Log.w(TAG, "Input BluetoothAdapter is null");
			return false;
		}
		
		final BluetoothDevice device = adapter.getRemoteDevice(address);
		if (device == null) {
			Log.w(TAG, "The requested BLE device was not found. Cannot connect.");
			return false;
		}
		
		bluetoothGatt = device.connectGatt(context, true, gattCallback);
		Log.d(TAG, "Trying to connect to remote BLE device...");
		
		return true;
	}
	
	public void disconnect() {
		if (bluetoothGatt == null) {
			Log.w(TAG, "Bluetooth connection was not estabilished");
			return;
		}
		
		bluetoothGatt.disconnect();
	}
	
	public void close() {
		if (bluetoothGatt == null) {
			return;
		}
		
		bluetoothGatt.close();
		bluetoothGatt = null;
	}
	
	public BluetoothDevice getDevice() {
		if (bluetoothGatt == null) {
			return null;
		}
		
		return bluetoothGatt.getDevice();
	}
	
	public void discoverServices() {
		bluetoothGatt.discoverServices();
	}
	
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return;
		}
		
		bluetoothGatt.readCharacteristic(characteristic);
	}
	
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return;
		}
		
		bluetoothGatt.writeCharacteristic(characteristic);
		bluetoothGatt.executeReliableWrite();
	}
	
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return;
		}
		
		bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}
	
	public BluetoothGattService getGattService(String uuid) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return null;
		}
		
		return bluetoothGatt.getService(UUID.fromString(uuid));
	}
	
	public List<BluetoothGattService> getSupportedGattServices() {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return null;
		}
		
		return bluetoothGatt.getServices();
	}
	
	public void writeDescriptor(BluetoothGattDescriptor descriptor) {
		bluetoothGatt.writeDescriptor(descriptor);
	}
	
	public static String getBondString(int bond) {
		switch(bond) {
			case BluetoothDevice.BOND_BONDED:
				return "BONDED";
			case BluetoothDevice.BOND_BONDING:
				return "BONDING"; 
			case BluetoothDevice.BOND_NONE:
				return "NOT BONDED"; 
			default: return "";
		}
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	public int getStateAsStringID() {
		switch(state) {
			case DISCONNECTED:
				return R.string.ble_disconnected;
			case DISCONNECTING:
				return R.string.ble_disconnecting;
			case CONNECTING:
				return R.string.ble_connecting;
			case CONNECTED:
				return R.string.ble_connected;
		}
		return -1;
	}

	/**
	 * @return the lastData
	 */
	public byte[] getLastData() {
		return lastData;
	}

	/**
	 * @param lastData the lastData to set
	 */
	public void setLastData(byte[] lastData) {
		this.lastData = lastData;
	}
	
}
