package com.example.wearablesensorbase.ble;

import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

public class XadowBLEHandler implements BLEDeviceHandler {
	
	public static final String SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
	
	public static final String INCOMING_CHARACTERISTIC = "0000fff1-0000-1000-8000-00805f9b34fb";
	public static final String OUTGOING_CHARACTERISTIC = "0000fff2-0000-1000-8000-00805f9b34fb";
	
	@Override
	public void discoverServices(BLEConnection connection) {
		connection.discoverServices();
	}
	
	@Override
	public void setupDeviceConnection(BLEConnection connection) {
		BluetoothGattService service = connection.getGattService(SERVICE);
		
		if (service == null) {
			Log.w("XadowBLEHandler", "No services have been discovered. Selected service is NULL");
			return;
		}
		
		BluetoothGattCharacteristic incoming = service.getCharacteristic(UUID.fromString(INCOMING_CHARACTERISTIC));
		BluetoothGattCharacteristic outgoing = service.getCharacteristic(UUID.fromString(OUTGOING_CHARACTERISTIC));
		
		if (incoming == null || outgoing == null) {
			Log.w("XadowBLEHandler", "Service does not have the specified characteristics.");
			return;
		}
		
		connection.setCharacteristicNotification(incoming, true);
		connection.setCharacteristicNotification(outgoing, true);
		
		Log.i("xadow", "descriptors: "+incoming.getDescriptors().size());
		BluetoothGattDescriptor desc = incoming.getDescriptors().get(0);
		desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		connection.writeDescriptor(desc);
	}

	@Override
	public void write(BLEConnection connection, byte[] data) {
		BluetoothGattService service = connection.getGattService(SERVICE);
		if (service == null) {
			Log.w("XadowBLEHandler", "No service disvovered. Service is NULL");
			return;
		}
		
		BluetoothGattCharacteristic outgoing = service.getCharacteristic(UUID.fromString(OUTGOING_CHARACTERISTIC));
		if (outgoing == null) {
			Log.w("XadowBLEHandler", "No such characteristic. Characteristic is null.");
			return;
		}
		
		outgoing.setValue(data);
		connection.writeCharacteristic(outgoing);
	}

	@Override
	public byte[] read(BLEConnection connection, BluetoothGattCharacteristic characteristic) {
		connection.readCharacteristic(characteristic);
		byte[] result = characteristic.getValue();
		connection.setLastData(result);
		return result;
	}

}
