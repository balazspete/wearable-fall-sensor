package com.example.wearablesensorbase.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.example.wearablesensorbase.WearableSensorBase;
import com.example.wearablesensorbase.events.MeasurementEvent;
import com.example.wearablesensorbase.events.MeasurementEventListener;

public class BufferedMeasurementSaver {

	private WearableSensorBase app;
	private MeasurementEventListener listener;
	
	private HashMap<String, File> files;
	private HashMap<String, Integer> writeCount;
	
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
		File file = getFile(sensor);
		writeToFile(sensor, file, measurement.toString());
	}
	
	public void writeStatementToFile(String sensor, String statement) {
		File file = getFile(sensor);
		writeToFile(sensor, file, statement);
	}
	
	private File getFile(String sensor) {
		File file = files.get(sensor);
		if (file == null) {
			file = new File(app.getExternalFilesDir(null), getFileName(sensor));
			file.setReadable(true, false);
			files.put(sensor, file);
		}
		
		return file;
	}
	
	private int MAX = 10;
	private boolean shouldFlush(String sensor) {
		Integer count = writeCount.get(sensor);
		if (count == null) {
			writeCount.put(sensor, 1);
			return 1 >= MAX;
		}
		
		return count >= MAX;
	}
	
	public void writeStatementsToFile(String statement) {
		for (String sensor : files.keySet()) {
			writeStatementToFile(sensor, statement);
		}
	}
	
	private static String timestamp;
	public static String getFileName(String sensor) {
		String filename = sensor.replaceAll("(:|\\s)", "_");
		if (timestamp == null) {
			timestamp = "" + System.currentTimeMillis();
		}
		
		return filename + "_" + timestamp;
	}
	
	private synchronized void writeToFile(String sensor, File file, String out) {
		try {
			FileWriter writer = new FileWriter(file, true);
			writer.append(out);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Log.e("BufferedMeasurementSaver", "Failed to write to file");
		}
		
	}
	
}
