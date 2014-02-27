package com.example.wearablesensorbase.ble;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * A description of a BLE device handler
 * This allows for implementation of the BLE protocol (services and characteristics) specific to each individual device type
 * @author Balazs Pete
 *
 */
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
	
	/**
	 * Write to the connection
	 * @param connection The connection to write to
	 * @param data The data to write
	 */
	public void write(BLEConnection connection, byte[] data);
	
	/**
	 * Read the specified characteristic on the input connection
	 * @param connection The connection
	 * @param characteristic The characteristic
	 */
	public void read(BLEConnection connection, BluetoothGattCharacteristic characteristic);
}
