package com.example.wearablesensorbase.events;

import java.util.EventListener;

public abstract class BLEConnectionEventListener implements EventListener {

	public abstract void onConnectionStateChange(BLEConnectionEvent event);
	
	public abstract void onConnectionCharacteristicChange(BLEConnectionEvent event);
	
	public abstract void onConnectionCharacteristicRead(BLEConnectionEvent event);
	
	public abstract void onConnectionCharacteristicWrite(BLEConnectionEvent event);
	
	public abstract void onConnectionServiceDiscovery(BLEConnectionEvent event);
	
	public abstract void onIncomingData(BLEConnectionEvent event);
	
}
