package com.example.wearablesensorbase;

import java.util.HashMap;

import com.example.wearablesensorbase.ble.BLEService;
import com.example.wearablesensorbase.ble.IncomingDataParser;
import com.example.wearablesensorbase.ble.XadowBLEHandler;
import com.example.wearablesensorbase.data.BufferedMeasurementSaver;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;
import com.example.wearablesensorbase.events.BLEConnectionEvent;
import com.example.wearablesensorbase.events.BLEConnectionEventListener;
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
	
	private BLEService bleService;
	private ListenerManager<MeasurementEventListener, MeasurementEvent> measurementListenerManager;
	private HashMap<String, SensorMeasurementSeries> sensorData;
	private IncomingDataParser parser;
	private BufferedMeasurementSaver measurementSaver;
	
	public void onCreate() {
		super.onCreate();
		
		bleService = BLEService.createInstance(this, new XadowBLEHandler());
		bleService.start();
		
		measurementSaver = new BufferedMeasurementSaver(this);
		
		setupSensorData();
		setupMeasurementEventListener();
		setupIncomingDataParser();
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
		SensorMeasurementSeries series = sensorData.get(sensor);
		if (series != null) {
			series.add(measurement);
			MeasurementEvent event = new MeasurementEvent(sensor, measurement);
			measurementListenerManager.send(event);
		}
	}

	protected void setupSensorData() {
		sensorData = new HashMap<String, SensorMeasurementSeries>();
	}
	
	public void addSensor(String connectionID) {
		if (sensorData.get(connectionID) != null) {
			return;
		}
		
		sensorData.put(connectionID, new SensorMeasurementSeries(MAX_SERIES_LENGTH));
	}
	
	public void removeSensor(String connectionID) {
		sensorData.remove(connectionID);
	}
	
	protected void setupMeasurementEventListener() {
		measurementListenerManager = new ListenerManager<MeasurementEventListener, MeasurementEvent>(){
			@Override
			protected void eventHandlerHelper(MeasurementEventListener listener, MeasurementEvent event) {
				listener.measurement(event);
			}
		};
		measurementListenerManager.addEventListener(measurementSaver.getMeasurementEventListener());
	}
	
	protected void setupIncomingDataParser() {
		// Create a message parser
		parser = new IncomingDataParser() {
			@Override
			public void handleSensorMeasurement(String connection, SensorMeasurement measurement) {
				// forward measurements to the measurement manager
				measurementListenerManager.send(new MeasurementEvent(connection, measurement));
			}
		};
		
		bleService.addEventListener(new BLEConnectionEventListener() {
			@Override
			public void onIncomingData(BLEConnectionEvent event) {
				// parse incoming data...
				parser.parse(event.getConnection().getDevice().getAddress(), event.getData());
			}
			
			@Override
			public void onConnectionStateChange(BLEConnectionEvent event) { }
			@Override
			public void onConnectionServiceDiscovery(BLEConnectionEvent event) { }
			@Override
			public void onConnectionCharacteristicWrite(BLEConnectionEvent event) { }
			@Override
			public void onConnectionCharacteristicRead(BLEConnectionEvent event) { }
			@Override
			public void onConnectionCharacteristicChange(BLEConnectionEvent event) { }
		});
	}
	
}
