package com.example.wearablesensorbase.detector;

import java.util.HashMap;

import com.example.wearablesensorbase.calibration.Calibration;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;
import com.example.wearablesensorbase.events.FallEvent;
import com.example.wearablesensorbase.events.FallEventListener;
import com.example.wearablesensorbase.events.ListenerManager;

public class Detector {

	private HashMap<String, SensorMeasurementSeries> data;
	private HashMap<String, Calibration> callibrations;
	
	ListenerManager<FallEventListener, FallEvent> manager;
	
	public Detector(HashMap<String, Calibration> callibrations, HashMap<String, SensorMeasurementSeries> data) {
		this.callibrations = callibrations;
		this.data = data;
		
		manager = new ListenerManager<FallEventListener, FallEvent>() {
			@Override
			protected void eventHandlerHelper(FallEventListener listener, FallEvent data) {
				FallEventListener.handleFallEvent(listener, data);
			}
		};
	}
	
	public void newMeasurement(String sensor, int index, SensorMeasurement measurement) {
		
//		SensorMeasurement m = data.get(sensor).get(index);
//		SensorMeasurement m2 = data.get(sensor).getLast();
//		m.acceleration.x;
//		m.acceleration.y;
//		m.acceleration.z;
//		m.orientation.x;
//		m.orientation.y;
//		m.orientation.z;
//		m.loudness;
		
//		detectionAlert(FallType.FORWARD_FALL);
		
		
	}
	
	
	private void detectionAlert(FallType type) {
		manager.send(new FallEvent(type));
	}
	
	public void addFallEventListener(FallEventListener listener) {
		manager.addEventListener(listener);
	}
	
	public void removeFallEventListener(FallEventListener listener) {
		manager.removeEventListener(listener);
	}
	
}
