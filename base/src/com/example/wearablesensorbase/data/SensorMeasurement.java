package com.example.wearablesensorbase.data;

import java.io.Serializable;

import com.jjoe64.graphview.GraphViewSeries;

public class SensorMeasurement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5403317119072695604L;
	
	public final TriaxialSensorData acceleration, orientation;
	public final SensorData loudness;
	public final long time;
	
	public SensorMeasurement(long time, 
			double accelerationX, double accelerationY, double accelerationZ,
			double orientationX, double orientationY, double orientationZ, 
			double loudness) {
		acceleration = new TriaxialSensorData(time, accelerationX, accelerationY, accelerationZ);
		orientation = new TriaxialSensorData(time, orientationX, orientationY, orientationZ);
		this.loudness = new SensorData(time, loudness);
		this.time = time;	
	}
	
	public void appendAcceleration(GraphViewSeries x, GraphViewSeries y, GraphViewSeries z, boolean scrollToEnd, int maxDataCount) {
		x.appendData(acceleration.x, scrollToEnd, maxDataCount);
		y.appendData(acceleration.y, scrollToEnd, maxDataCount);
		z.appendData(acceleration.z, scrollToEnd, maxDataCount);
	}
	
	public void appendOrientation(GraphViewSeries x, GraphViewSeries y, GraphViewSeries z, boolean scrollToEnd, int maxDataCount) {
		x.appendData(orientation.x, scrollToEnd, maxDataCount);
		y.appendData(orientation.y, scrollToEnd, maxDataCount);
		z.appendData(orientation.z, scrollToEnd, maxDataCount);
	}
	
	public void appendLoudness(GraphViewSeries loudness, boolean scrollToEnd, int maxDataCount) {
		loudness.appendData(this.loudness, scrollToEnd, maxDataCount);
	}
}
