package com.example.wearablesensorbase.ble;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BLEDeviceHandler {

	/**
	 * Discover the available services from the device
	 * <i>Execute this method first, prior to trying to set up the connection. Asynchronous method, as it uses BluetoothGatt.discoverServices()</i>
	 * @param connection The BLEConnection
	 */
	public void discoverServices(BLEConnection connection);
	
	/**
	 * Setup the predefined connections to the device
	 * <i>You will have to execute discoverServices() prior to this method</i>
	 * @param connection The connection
	 */
	public void setupDeviceConnection(BLEConnection connection);
	
	public void write(BLEConnection connection, byte[] data);
	
	public byte[] read(BLEConnection connection, BluetoothGattCharacteristic characteristic);
}
