package com.example.wearablesensorbase.ble;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

import com.example.wearablesensorbase.data.SensorMeasurement;

public abstract class IncomingDataParser {
	
	ConcurrentHashMap<String, DataParserHelper> parsers;
	
	public IncomingDataParser() {
		parsers = new ConcurrentHashMap<String, IncomingDataParser.DataParserHelper>();
	}

	public abstract void handleSensorMeasurement(String connection, SensorMeasurement measurement);
	
	public void parse(String connection, byte[] data) {
		DataParserHelper helper = getDataParserHelper(connection);
		helper.parse(this, data);
	}
	
	private DataParserHelper getDataParserHelper(String connection) {
		DataParserHelper helper = parsers.get(connection);
		if (helper == null) {
			helper = new DataParserHelper(connection);
			parsers.put(connection, helper);
		}
		
		return helper;
	}
	
	public class DataParserHelper {
		
		private String connection;
		private StringBuffer buffer;
		
		public DataParserHelper(String connection) {
			this.connection = connection;
			buffer = new StringBuffer();
		}
		
		public void parse(IncomingDataParser parser, byte[] data) {
			try {
				buffer = buffer.append(new String(data, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				System.err.println("IncomingDataParser|parse: Input data is not a string");
				return;
			}
			
			String section;
			synchronized (this) {
				int end = buffer.lastIndexOf("#");
				if (end < 0) {
					System.err.println(buffer);
					System.err.println("IncomingDataParser|parse: No measurement in buffer");
					return;
				}
				
				section = buffer.substring(0, end);
				buffer = buffer.delete(0, end);
			}
			
			String[] chunks = section.split("#");
			for (String chunk : chunks) {
				SensorMeasurement measurement = parseMeasurement(chunk);
				if (measurement != null) {
					parser.handleSensorMeasurement(connection, measurement);
				}
			}
		}
		
		private SensorMeasurement parseMeasurement(String data) {
			if (data == null || data.length() == 0) {
				return null;
			}
			
			String[] chunks = data.replaceAll("#", "").split("\\|");
			if (chunks.length < 8 || !chunks[0].startsWith("MEASUREMENT")) {
				System.err.println("IncomingDataParser|parseMeasurement: Malformed measurement");
				return null;
			}
			
			try {
				double accelerationX = Double.parseDouble(chunks[1]);
				double accelerationY = Double.parseDouble(chunks[2]);
				double accelerationZ = Double.parseDouble(chunks[3]);
				
				double orientationX = Double.parseDouble(chunks[4]);
				double orientationY = Double.parseDouble(chunks[5]);
				double orientationZ = Double.parseDouble(chunks[6]);
				
				double loudness = Double.parseDouble(chunks[7]);
				
				SensorMeasurement measurement = 
					new SensorMeasurement(System.currentTimeMillis(), 
						accelerationX, accelerationY, accelerationZ, 
						orientationX, orientationY, orientationZ, 
						loudness);
				
				return measurement;
			} catch (NumberFormatException e) {
				Log.e("IncomingDataParser", "Failed to parse measurement: " + e.getMessage());
				return null;
			}
		}
	}
}
