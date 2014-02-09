package com.example.wearablesensorbase.events;

import com.example.wearablesensorbase.data.SensorMeasurement;

/**
 * An event describing a measurement
 * @author Balazs Pete
 *
 */
public class MeasurementEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7200672165587355503L;
	
	private final String sensorID;
	private final SensorMeasurement measurement;
	
	/**
	 * Create a new measurement event
	 * @param sensorID The sensor from which the measurement originates from
	 * @param measurement The measurement
	 */
	public MeasurementEvent(String sensorID, SensorMeasurement measurement) {
		this.sensorID = sensorID.intern();
		this.measurement = measurement;
	}
	
	/**
	 * Get the ID of the sensor
	 * @return The ID
	 */
	public String getSensorId() {
		return sensorID;
	}
	
	/**
	 * Get the measurement
	 * @return The measurement
	 */
	public SensorMeasurement getMeasurement() {
		return measurement;
	}
	
	/**
	 * Get the timestamp of the event
	 * @return the Timestamp
	 */
	public long getTime() {
		return measurement.time;
	}
	
}
