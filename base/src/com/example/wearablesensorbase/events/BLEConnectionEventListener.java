package com.example.wearablesensorbase.events;

import java.util.EventListener;

/**
 * An event listener for BLEConnectionEvents
 * @author Balazs Pete
 *
 */
public abstract class BLEConnectionEventListener implements EventListener {

	/**
	 * Handle a connection state change event
	 * @param event The event
	 */
	public abstract void onConnectionStateChange(BLEConnectionEvent event);
	
	/**
	 * Handle a connection characteristic change event
	 * @param event The event
	 */
	public abstract void onConnectionCharacteristicChange(BLEConnectionEvent event);
	
	/**
	 * Handle a connection characteristic read event
	 * @param event The event
	 */
	public abstract void onConnectionCharacteristicRead(BLEConnectionEvent event);
	
	/**
	 * Handle a connection characteristic write event
	 * @param event The event
	 */
	public abstract void onConnectionCharacteristicWrite(BLEConnectionEvent event);
	
	/**
	 * Handle a service discovery event
	 * @param event the event
	 */
	public abstract void onConnectionServiceDiscovery(BLEConnectionEvent event);
	
	/**
	 * Handle an incoming data event
	 * @param event The event
	 */
	public abstract void onIncomingData(BLEConnectionEvent event);
	
}
