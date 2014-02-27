package com.example.wearablesensorbase.events;

import com.example.wearablesensorbase.ble.BLEConnection;

/**
 * An event describing a change in the contained BLEConnection
 * @author Balazs Pete
 *
 */
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
	
	/**
	 * Create a new event
	 * @param connection The associated connection
	 * @param type The type of the event
	 */
	public BLEConnectionEvent(BLEConnection connection, Type type) {
		this.connection = connection;
		this.type = type;
	}
	
	/**
	 * Get the associated connection
	 * @return The connection
	 */
	public BLEConnection getConnection() {
		return connection;
	}
	
}
