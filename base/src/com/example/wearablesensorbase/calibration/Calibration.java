package com.example.wearablesensorbase.calibration;

import com.example.wearablesensorbase.data.SensorMeasurement;

public class Calibration {

	public void initialise(SensorMeasurement initial) {
		System.out.println("Initial measurement");
	}
	
	public void callibrateLeftwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: left");
	}
	
	public void callibrateRightwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: right");
	}
	
	public void callibrateForwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: forward");
	}
	
	public void callibrateBackwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: backward");
	}
	
	public void callibrateDownwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: downward");
	}
	
	public void callibrateUpwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: upward");
	}
	
}
