package com.example.wearablesensorbase.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import android.content.Context;

import com.example.wearablesensorbase.WearableSensorBase;
import com.example.wearablesensorbase.events.MeasurementEvent;
import com.example.wearablesensorbase.events.MeasurementEventListener;

public class BufferedMeasurementSaver {

	private WearableSensorBase app;
	private MeasurementEventListener listener;
	
	private HashMap<String, File> files;
	
	public BufferedMeasurementSaver(WearableSensorBase app) {
		this.app = app;
		files = new HashMap<String, File>();
		listener = new MeasurementEventListener() {
			@Override
			public void measurement(MeasurementEvent event) {
				writemeasurementToFile(event.getSensorId(), event.getMeasurement());
			}
		};
	}

	public MeasurementEventListener getMeasurementEventListener() {
		return listener;
	}
	
	public void writemeasurementToFile(String sensor, SensorMeasurement measurement) {
		File file = files.get(sensor);
		if (file == null) {
			file = new File(app.getExternalFilesDir(null), getFileName(sensor));
			file.setReadable(true, false);
			files.put(sensor, file);
		}
		
		writeToFile(file, measurement.toString());
	}
	
	public void writeStatementToFile(String sensor, String statement) {
		File file = files.get(sensor);
		if (file == null) {
			file = new File(app.getExternalFilesDir(null), getFileName(sensor));
			file.setReadable(true, false);
			files.put(sensor, file);
		}
		
		writeToFile(file, statement);
	}
	
	public static String getFileName(String sensor) {
		return sensor.replaceAll("(:|\\s)", "_");
	}
	
	private synchronized void writeToFile(File file, String out) {
		try {
			System.out.println(file);
			FileOutputStream output = new FileOutputStream(file);
			output.write(out.getBytes());
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
