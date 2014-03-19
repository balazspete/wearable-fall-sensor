package com.example.wearablesensorbase.ble;

import java.util.HashMap;

import com.example.wearablesensorbase.data.SensorMeasurement;

public abstract class IncomingDataParser {
	
	private HashMap<String, ParserHelper> helpers;
	private String leftover;
	
	public IncomingDataParser() {
		helpers = new HashMap<String, ParserHelper>();
		leftover = "";
	}

	public void parse(String connection, byte[] data) {
		ParserHelper helper = helpers.get(connection);
		if (helper == null) {
			helper = new ParserHelper(connection);
			helpers.put(connection, helper);
		}
		
		String[] chunks = (leftover + new String(data)).split("#");
		
		for (int i = 0; i < chunks.length-1; i++) {
			String chunk = chunks[i];
			if (chunk.isEmpty()) {
				continue;
			}
			
			helper.storeChunk(chunk);
			
			String[] values = helper.getValues();
			if (values != null) {
				createMeasurement(connection, values);
				helper.reset();
			}
		}
		
		leftover = chunks[chunks.length-1];
	}
	
	public abstract void handleSensorMeasurement(String connection, SensorMeasurement measurement);
	
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
	
	private class ParserHelper {
		
		public final String connection;
		private String
			ax, ay, az,
			gx, gy, gz, 
			lo;
		
		public ParserHelper(String connection) {
			this.connection = connection;
		}
		
		public synchronized void storeChunk(String chunk) {
			String value = chunk.substring(3);
			if (chunk.startsWith("A")) {
				if (chunk.startsWith("X", 1)) {
					ax = value;
				} else if (chunk.startsWith("Y", 1)) {
					ay = value;
				} else if (chunk.startsWith("Z", 1)) {
					az = value;
				}
			} else if (chunk.startsWith("G")) {
				if (chunk.startsWith("X", 1)) {
					gx = value;
				} else if (chunk.startsWith("Y", 1)) {
					gy = value;
				} else if (chunk.startsWith("Z", 1)) {
					gz = value;
				}
			} else if (chunk.startsWith("LO")) {
				lo = value;
			}
		}
		
		public boolean isReady() {
			return ax != null && ay != null && az != null &&
					gx != null && gy != null && gz != null &&
					lo != null;
		}
		
		public String[] getValues() {
			if (!isReady()) {
				return null;
			}
			
			String[] values = new String[]{
				ax, ay, az,
				gx, gy, gz, 
				lo
			};
			
			return values;
		}
		
		public void reset() {
			ax = null;
			ay = null;
			az = null;
			
			gx = null;
			gy = null;
			gz = null;
			
			lo = null;
		}
		
	}
	
}
