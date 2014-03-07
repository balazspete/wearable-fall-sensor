package com.example.wearablesensorbase.events;

import com.example.wearablesensorbase.ble.BLEConnection;

public class BLEConnectionEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 479992694114267994L;

	public enum Type {
		CONNECTION_STATE_CHANGE, CHARACTERISTIC_READ, CHARACTERISTIC_WRITE, CHARACTERISTIC_CHANGE, SERVICE_DISCOVERY,
		INCOMING_DATA
	}
	
	private BLEConnection connection;
	public final Type type;
	private byte[] data;
	
	public BLEConnectionEvent(BLEConnection connection, Type type) {
		this.connection = connection;
		this.type = type;
	}
	
	public BLEConnection getConnection() {
		return connection;
	}
	
	public void putData(byte[] data) {
		this.data = data;
	}
	
	public byte[] getData() {
		return data;
	}
	
}
