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

/**
 * An object representing a BLE Connection
 * @author Balazs Pete
 *
 */
public class BLEConnection {
	
	public enum State {
		CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
	}
	
    private final static String TAG = BLEConnection.class.getSimpleName();

	private String address;
	private BluetoothGatt bluetoothGatt;
	private BluetoothGattCallback gattCallback;
	private State state;
	private byte[] lastData = new byte[]{};
	
	/**
	 * Create a connection
	 * @param address The MAC address of the device
	 * @param gattCallback The GattCallback to be used
	 */
	public BLEConnection(String address, BluetoothGattCallback gattCallback) {
		if (address == null || gattCallback == null) {
			throw new NullPointerException("Parameters cannot be null");
		}
		
		this.address = address;
		this.gattCallback = gattCallback;
		this.state = State.DISCONNECTED;
	}
	
	/**
	 * try to connect to the device
	 * @param context The android parent context
	 * @param adapter The Bluetooth adapter to be used
	 * @return true if successful
	 */
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
	
	/**
	 * Disconnect from the device
	 */
	public void disconnect() {
		if (bluetoothGatt == null) {
			Log.w(TAG, "Bluetooth connection was not estabilished");
			return;
		}
		
		bluetoothGatt.disconnect();
	}
	
	/**
	 * Close the connection
	 * Used to free up any resources taken up by the BLE connection
	 */
	public void close() {
		if (bluetoothGatt == null) {
			return;
		}
		
		bluetoothGatt.close();
		bluetoothGatt = null;
	}
	
	/**
	 * Get the device associated with the connection
	 * @return
	 */
	public BluetoothDevice getDevice() {
		return bluetoothGatt.getDevice();
	}
	
	/**
	 * Discover the services associated with the connection
	 * You have to execute this method prior to using services
	 */
	public void discoverServices() {
		bluetoothGatt.discoverServices();
	}
	
	/**
	 * Read the input characteristic on this connection
	 * @param characteristic The characteristic
	 */
	public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return;
		}
		
		bluetoothGatt.readCharacteristic(characteristic);
	}
	
	/**
	 * Write to a specific characteristic
	 * @param characteristic The characteristic
	 */
	public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return;
		}
		
		bluetoothGatt.writeCharacteristic(characteristic);
		bluetoothGatt.executeReliableWrite();
	}
	
	/**
	 * Enable or disable characteristics notifications
	 * @param characteristic The characteristic
	 * @param enabled True or false
	 */
	public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return;
		}
		
		bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
	}
	
	/**
	 * Get the GATT service corresponding to the input UUID
	 * @param uuid The UUID
	 * @return The service or null if BLE was not initialised
	 */
	public BluetoothGattService getGattService(String uuid) {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return null;
		}
		
		return bluetoothGatt.getService(UUID.fromString(uuid));
	}
	
	/**
	 * Get the list of all supported GATT services
	 * @return The list of services or null if BLE was not initialised
	 */
	public List<BluetoothGattService> getSupportedGattServices() {
		if (bluetoothGatt == null) {
			Log.w(TAG, "BLE connection was not initialised");
			return null;
		}
		
		return bluetoothGatt.getServices();
	}
	
	/**
	 * Write the specified descriptor to the gatt profile
	 * @param descriptor The descriptor
	 */
	public void writeDescriptor(BluetoothGattDescriptor descriptor) {
		bluetoothGatt.writeDescriptor(descriptor);
	}
	
	/**
	 * Get a string representation of the connection bond
	 * @param bond The bond status number
	 * @return The correcponding string 
	 */
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
	 * Get the state of the connection
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Set the state of the connection
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * Return the state of the connection as a string identifier
	 * @return The ID
	 */
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
