package com.example.wearablesensorbase.data;

import java.util.LinkedList;

public class SensorMeasurementSeries extends LinkedList<SensorMeasurement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -201734900464549335L;

	private int maxSize;
	
	/**
	 * Create a sensor measurement series specifying the maximum size of the series
	 * @param maxSize The maximum number of elements allowed in the series
	 */
	public SensorMeasurementSeries(int maxSize) {
		super();
		this.maxSize = maxSize;
	}
	
	/**
	 * Appends the specified element to the end of the series. Removes the head if the series is longer then allowed.
	 * @param measurement The measurement to add
	 */
	public synchronized boolean add(SensorMeasurement measurement) {
		if (this.size() >= maxSize) {
			super.remove();
		}
		
		return super.add(measurement);
	}
}
