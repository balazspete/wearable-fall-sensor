package com.example.wearablesensorbase.data;

/**
 * An object to describe a triaxial measurement sample
 * @author Balazs Pete
 *
 */
public class TriaxialSensorData {

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
