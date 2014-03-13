package com.example.wearablesensorbase.detector;

import java.util.HashMap;

import com.example.wearablesensorbase.callibration.Callibration;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;

public class Detector {

	private HashMap<String, SensorMeasurementSeries> data;
	private Callibration callibration;
	
	public Detector(Callibration callibration, HashMap<String, SensorMeasurementSeries> data) {
		this.callibration = callibration;
		this.data = data;
	}
	
	
	
	public void newMeasurement(String sensor, int index) {
		
//		SensorMeasurement m = data.get(sensor).get(index);
//		SensorMeasurement m2 = data.get(sensor).getLast();
//		m.acceleration.x;
//		m.acceleration.y;
//		m.acceleration.z;
//		m.orientation.x;
//		m.orientation.y;
//		m.orientation.z;
//		m.loudness;
		
		
	}
	
	
	
}
