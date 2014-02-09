package com.example.wearablesensorbase.data;

import android.util.Log;

import com.example.wearablesensorbase.WearableSensorBase;

/**
 * An object to generate random double values periodically 
 * @author Balazs Pete
 *
 */
public class DataSimulator extends Thread {

	public static final int SLEEP_TIME = 500;
	
	private WearableSensorBase app;
	private String sensorId;
	private boolean simulate = false;
	private long time;
	
	public DataSimulator(WearableSensorBase app, String sensor, long start) {
		this.app = app;
		sensorId = sensor.intern();
		this.time = start;
	}
	
	public void simulate() {
		while (simulate) {
			SensorMeasurement dummyData = new SensorMeasurement(time++, 
					getRandom(), getRandom(), getRandom(), 
					getRandom(), getRandom(), getRandom(), 
					getRandom());
			app.addMeasurement(sensorId, dummyData);
			Log.i("DataSimulator", "Adding data for " + sensorId);
			try {
				sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				Log.e("DataSimulator", "Failed to go to sleep");
			}
		}
	}
	
	@Override
	public void run() {
		simulate = true;
		simulate();
	}
	
	public void stopSimulator() {
		simulate = false;
	}
	
	public boolean isRunning() {
		return simulate;
	}
	
	public double getRandom() {
		return Math.random();
	}
}
