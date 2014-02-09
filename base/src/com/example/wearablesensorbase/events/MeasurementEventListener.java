package com.example.wearablesensorbase.events;

import java.util.EventListener;

/**
 * An event listener for measurements
 * @author Balazs Pete
 *
 */
public abstract class MeasurementEventListener implements EventListener {

	/**
	 * Implement to listen for new measurements
	 * @param event The measurement event
	 */
	public abstract void measurement(MeasurementEvent event);
	
}
