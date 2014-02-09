package com.example.wearablesensorbase;

import java.util.HashMap;

import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;
import com.example.wearablesensorbase.events.ListenerManager;
import com.example.wearablesensorbase.events.MeasurementEvent;
import com.example.wearablesensorbase.events.MeasurementEventListener;

import android.app.Application;

/**
 * Application for the WearableSensorBase project
 * @author Balazs Pete
 *
 */
public class WearableSensorBase extends Application {

	public static final int MAX_SERIES_LENGTH = 1000;
	
	private ListenerManager<MeasurementEventListener, MeasurementEvent> measurementListenerManager;
	private HashMap<String, SensorMeasurementSeries> sensorData;
	
	public void onCreate() {
		super.onCreate();
		
		setupSensorData();
		setupMeasurementEventListener();
	}

	/**
	 * Add an event listener for measurements
	 * @param listener The measurement event listener
	 */
	public void addMeasurementEventListener(MeasurementEventListener listener) {
		measurementListenerManager.addEventListener(listener);
	}
	
	/**
	 * Remove an event listener for measurements
	 * @param listener The measurement event listener
	 */
	public void removeMeasurementEventListener(MeasurementEventListener listener) {
		measurementListenerManager.removeEventListener(listener);
	}
	
	/**
	 * Get the names of the attaches sensors
	 * @return The names of the sensors
	 */
	public synchronized String[] getSensorNames() {
		int i = 0;
		String[] names = new String[sensorData.keySet().size()];
		for(String name : sensorData.keySet()) {
			names[i++] = name;
		}
		return names;
	}
	
	public SensorMeasurementSeries getSensorData(String sensor) {
		return sensorData.get(sensor);
	}
	
	public void addMeasurement(String sensor, SensorMeasurement measurement) {
		sensorData.get(sensor).add(measurement);
		MeasurementEvent event = new MeasurementEvent(sensor, measurement);
		measurementListenerManager.send(event);
	}

	protected void setupSensorData() {
		sensorData = new HashMap<String, SensorMeasurementSeries>();
		// TODO: make this synamic
		sensorData.put("SENSOR ONE", new SensorMeasurementSeries(MAX_SERIES_LENGTH));
		sensorData.put("SENSOR TWO", new SensorMeasurementSeries(MAX_SERIES_LENGTH));
		sensorData.put("SENSOR THREE", new SensorMeasurementSeries(MAX_SERIES_LENGTH));
	}
	
	protected void setupMeasurementEventListener() {
		measurementListenerManager = new ListenerManager<MeasurementEventListener, MeasurementEvent>(){
			@Override
			protected void eventHandlerHelper(MeasurementEventListener listener, MeasurementEvent event) {
				listener.measurement(event);
			}
		};
	}
}
