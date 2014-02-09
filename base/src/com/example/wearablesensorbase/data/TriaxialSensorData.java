package com.example.wearablesensorbase.data;

import java.io.Serializable;

/**
 * An object to describe a triaxial measurement sample
 * @author Balazs Pete
 *
 */
public class TriaxialSensorData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4186318170645362274L;
	
	public final SensorData x, y, z;
	
	/**
	 * Create a new triaxial sensor measurement
	 * @param time The timestamp of the measurement
	 * @param x The x value
	 * @param y The y value
	 * @param z The z value
	 */
	public TriaxialSensorData(long time, double x, double y, double z) {
		this.x = new SensorData(time, x);
		this.y = new SensorData(time, y);
		this.z = new SensorData(time, z);
	}
	
}
