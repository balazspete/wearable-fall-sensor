package com.example.wearablesensorbase.ble;

import java.util.Arrays;
import java.util.HashMap;

import com.example.wearablesensorbase.data.SensorMeasurement;

public abstract class IncomingDataParser {
	
	private HashMap<String, StringBuffer> buffers;
	
	public IncomingDataParser() {
		buffers = new HashMap<String, StringBuffer>();
	}

	public synchronized void parse(String connection, byte[] data) {
		String message = new String(data);
		
		if (message.startsWith("#")) {
			System.out.println("BEGIN MESSAGE");
			beginMessage(connection, message);
		} else if (message.endsWith("#")) {
			System.out.println("END MESSAGE");
			endMessage(connection, message);
		} else {
			appendToMessage(connection, message);
		}
	}
	
	public abstract void handleSensorMeasurement(String connection, SensorMeasurement measurement);
	
	
	private void beginMessage(String connection, String message) {
		buffers.put(connection, new StringBuffer(message));
		
		if (message.toString().endsWith("#")) {
			endMessage(connection, "");
		}
	}
	
	private void endMessage(String connection, String message) {
		StringBuffer str = buffers.remove(connection);
		str.append(message);
		
		handleMessage(connection, str.toString());
	}
	
	private void appendToMessage(String connection, String message) {
		buffers.get(connection).append(message);
	}
	
	private void handleMessage(String connection, String message) {
		String[] splitMessage = message.replace("#", "").split("\\|");
		if (splitMessage[0].equalsIgnoreCase("MEASUREMENT")) {
			createMeasurement(connection, splitMessage);
		}// other statements here...
		
		
	}
	
	private void createMeasurement(String connection, String[] message) {
		double[] values = new double[7];
		
		for (int i = 0; i < 7; i++) {
			try {
				values[i] = Double.parseDouble(message[i+1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				values[i] = 0;
			}
		}
		
		SensorMeasurement measurement = new SensorMeasurement(System.currentTimeMillis(), 
				values[0], values[1], values[2],
				values[3], values[4], values[5], 
				values[6]);
		
		handleSensorMeasurement(connection, measurement);
	}
	
}
