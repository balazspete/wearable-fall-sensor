package com.example.wearablesensorbase.ble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.wearablesensorbase.WearableSensorBase;
import com.example.wearablesensorbase.events.BLEConnectionEvent;
import com.example.wearablesensorbase.events.BLEConnectionEvent.Type;
import com.example.wearablesensorbase.events.BLEConnectionEventListener;
import com.example.wearablesensorbase.events.ListenerManager;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BLEService extends Thread {
	
    private final static String TAG = BLEConnection.class.getSimpleName();

	private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			
			BLEConnection connection = getConnection(gatt.getDevice().getAddress());
			if (connection == null) {
				connection = _connections.get(gatt.getDevice().getAddress());
			}
			
			if (connection == null) {
				return;
			}
			
			if (newState == BluetoothProfile.STATE_CONNECTING) {
				connection.setState(com.example.wearablesensorbase.ble.BLEConnection.State.CONNECTING);
				listenerManager.send(new BLEConnectionEvent(connection, Type.CONNECTION_STATE_CHANGE));
				
				Log.d(TAG, "Connecting to BLE device...");
			} else if (newState == BluetoothProfile.STATE_CONNECTED) {
				connection.setState(com.example.wearablesensorbase.ble.BLEConnection.State.CONNECTED);
				listenerManager.send(new BLEConnectionEvent(connection, Type.CONNECTION_STATE_CHANGE));
				
				connections.put(gatt.getDevice().getAddress(), connection);
				discoverBLEServices(connection);

				application.addSensor(connection.getDevice().getAddress());
				
				Log.d(TAG, "Connected to BLE device...");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
				connection.setState(com.example.wearablesensorbase.ble.BLEConnection.State.DISCONNECTING);
				listenerManager.send(new BLEConnectionEvent(connection, Type.CONNECTION_STATE_CHANGE));
				
				Log.d(TAG, "Disconnecting from BLE device...");
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				connection.setState(com.example.wearablesensorbase.ble.BLEConnection.State.DISCONNECTED);
				listenerManager.send(new BLEConnectionEvent(connection, Type.CONNECTION_STATE_CHANGE));
				
				connections.remove(gatt.getDevice().getAddress());
				
				Log.d(TAG, "Disconnected from BLE device...");
			} else {
				Log.e(TAG, "What sorta state is this?!");
			}
		}
		
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			BLEConnection connection = BLEService.getInstance().getConnection(gatt.getDevice().getAddress());
			
			if (status == BluetoothGatt.GATT_SUCCESS) {
				listenerManager.send(new BLEConnectionEvent(connection, Type.CHARACTERISTIC_READ));
				
				Log.d(TAG, "Read successfully?");
			} else {
				Log.e(TAG, "Failed to read characteristic");
			}
		}
		
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			BLEConnection connection = BLEService.getInstance().getConnection(gatt.getDevice().getAddress());
			
			if (status == BluetoothGatt.GATT_SUCCESS) {
				listenerManager.send(new BLEConnectionEvent(connection, Type.CHARACTERISTIC_WRITE));
				
				Log.d(TAG, "Wrote successfully");
			} else {
				Log.e(TAG, "Failed to write characteristic");
			}
		}
		
		public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			BLEConnection connection = BLEService.getInstance().getConnection(gatt.getDevice().getAddress());
			
			byte[] data = handler.read(connection, characteristic);
			BLEConnectionEvent event = null;
			if (data == null || data.length == 0) {
				listenerManager.send(new BLEConnectionEvent(connection, Type.CHARACTERISTIC_CHANGE));
				Log.d(TAG, "WOOT! Characteristic changed! :O");
			} else {
				event = new BLEConnectionEvent(connection, Type.INCOMING_DATA);
				event.putData(data);
				Log.d(TAG, "WOOT! DATA! :O");
			}

			listenerManager.send(event);
		}
		
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			BLEConnection connection = BLEService.getInstance().getConnection(gatt.getDevice().getAddress());
			
			if (status == BluetoothGatt.GATT_SUCCESS) {
				listenerManager.send(new BLEConnectionEvent(connection, Type.SERVICE_DISCOVERY));
				
				Log.d(TAG, "Services discovered!");
				for(BluetoothGattService s : connection.getSupportedGattServices()) {
					Log.d(TAG, "SERVICE: " + s.getUuid().toString());
				}
				
				setupDeviceBLEConnection(connection);
			} else {
				Log.e(TAG, "Failed to discover services");
			}
		}
	};

	private WearableSensorBase application;
	private HashMap<String, BLEConnection> connections;
	private HashMap<String, BLEConnection> _connections;
	
	private ListenerManager<BLEConnectionEventListener, BLEConnectionEvent> listenerManager;
	private BLEDeviceHandler handler;
	
	private BLEService(WearableSensorBase application, BLEDeviceHandler handler) {
		this.application = application;
		this.connections = new HashMap<String, BLEConnection>();
		this._connections = new HashMap<String, BLEConnection>();
		this.handler = handler;
		this.listenerManager = new ListenerManager<BLEConnectionEventListener, BLEConnectionEvent>() {
			@Override
			protected void eventHandlerHelper(BLEConnectionEventListener listener, BLEConnectionEvent event) {
				switch(event.type) {
					case CONNECTION_STATE_CHANGE:
						listener.onConnectionStateChange(event);
						break;
					case CHARACTERISTIC_CHANGE:
						listener.onConnectionCharacteristicChange(event);
						break;
					case CHARACTERISTIC_READ:
						listener.onConnectionCharacteristicRead(event);
						break;
					case CHARACTERISTIC_WRITE:
						listener.onConnectionCharacteristicWrite(event);
						break;
					case SERVICE_DISCOVERY:
						listener.onConnectionServiceDiscovery(event);
						break;
					case INCOMING_DATA:
						listener.onIncomingData(event);
						break;
					default:
						break;
				}
			}
		};
	}
	
	public BLEConnection createConnection(String address) {
		BLEConnection connection = new BLEConnection(address, gattCallback);
		_connections.put(address, connection);
		return connection;
	}
	
	public BLEConnection getConnection(String address) {
		return connections.get(address);
	}
	
	public void deleteConnection(String address) {
		BLEConnection connection = connections.get(address);
		connection.disconnect();
		connection.close();
	}
	
	public List<BLEConnection> getConnections() {
		return new ArrayList<BLEConnection>(connections.values());
	}
	
	private static BLEService instance;
	public static BLEService getInstance() {
		return instance;
	}
	
	public static BLEService createInstance(WearableSensorBase application, BLEDeviceHandler handler) {
		return instance = new BLEService(application, handler);
	}

	public void run() {
		while(true) {
			try {
				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addEventListener(BLEConnectionEventListener listener) {
		listenerManager.addEventListener(listener);
	}
	
	public void removeEventListener(BLEConnectionEventListener listener) {
		listenerManager.removeEventListener(listener);
	}
	
	private void discoverBLEServices(BLEConnection connection) {
		handler.discoverServices(connection);
	}
	
	private void setupDeviceBLEConnection(BLEConnection connection) {
		handler.setupDeviceConnection(connection);
	}
	
	public void writeDataToBLEConnection(BLEConnection connection, byte[] data) {
		handler.write(connection, data);
	}
	
	public void writeDataToAllBLEConnections(byte[] data) {
		for (BLEConnection connection : connections.values()) {
			handler.write(connection, data);
		}
	}
	
}
