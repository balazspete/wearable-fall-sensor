package com.example.wearablesensorbase.data;

import java.io.Serializable;

import com.jjoe64.graphview.GraphViewSeries;

/**
 * An object describing a measurement sample
 * @author Balazs Pete
 *
 */
public class SensorMeasurement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5403317119072695604L;
	
	public final TriaxialSensorData acceleration, orientation;
	public final SensorData loudness;
	public final long time;
	
	/**
	 * Create a measurement
	 * @param time The timestamp
	 * @param accelerationX The X axis value of the acceleration
	 * @param accelerationY The Y axis value of the acceleration
	 * @param accelerationZ The Z axis value of the acceleration
	 * @param orientationX The X axis value of the orientation
	 * @param orientationY The Y axis value of the orientation
	 * @param orientationZ The Z axis value of the orientation
	 * @param loudness The loudness value
	 */
	public SensorMeasurement(long time, 
			double accelerationX, double accelerationY, double accelerationZ,
			double orientationX, double orientationY, double orientationZ, 
			double loudness) {
		acceleration = new TriaxialSensorData(time, accelerationX, accelerationY, accelerationZ);
		orientation = new TriaxialSensorData(time, orientationX, orientationY, orientationZ);
		this.loudness = new SensorData(time, loudness);
		this.time = time;	
	}
	
	/**
	 * Append the acceleration values to the input {@link GraphViewSeries}
	 * @param x The series for the X axis
	 * @param y The series for the Y axis
	 * @param z The series for the Z axis
	 * @param scrollToEnd scroll to the end of graph?
	 * @param maxDataCount Max data items on graph
	 */
	public void appendAcceleration(GraphViewSeries x, GraphViewSeries y, GraphViewSeries z, boolean scrollToEnd, int maxDataCount) {
		x.appendData(acceleration.x, scrollToEnd, maxDataCount);
		y.appendData(acceleration.y, scrollToEnd, maxDataCount);
		z.appendData(acceleration.z, scrollToEnd, maxDataCount);
	}
	
	/**
	 * Append the orientation values to the input {@link GraphViewSeries}
	 * @param x The series for the X axis
	 * @param y The series for the Y axis
	 * @param z The series for the Z axis
	 * @param scrollToEnd scroll to the end of graph?
	 * @param maxDataCount Max data items on graph
	 */
	public void appendOrientation(GraphViewSeries x, GraphViewSeries y, GraphViewSeries z, boolean scrollToEnd, int maxDataCount) {
		x.appendData(orientation.x, scrollToEnd, maxDataCount);
		y.appendData(orientation.y, scrollToEnd, maxDataCount);
		z.appendData(orientation.z, scrollToEnd, maxDataCount);
	}
	
	/**
	 * Append the loudness value to the input {@link GraphViewSeries}
	 * @param loudness The loudness value
	 * @param scrollToEnd scroll to the end of graph?
	 * @param maxDataCount Max data items on the graph
	 */
	public void appendLoudness(GraphViewSeries loudness, boolean scrollToEnd, int maxDataCount) {
		loudness.appendData(this.loudness, scrollToEnd, maxDataCount);
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder("#MEASUREMENT(");
		out.append(time);
		out.append(")|");
		out.append(acceleration.x);
		out.append("|");
		out.append(acceleration.y);
		out.append("|");
		out.append(acceleration.z);
		out.append("|");
		out.append(orientation.x);
		out.append("|");
		out.append(orientation.y);
		out.append("|");
		out.append(orientation.z);
		out.append("|");
		out.append(loudness);
		out.append("#");
		
		return out.toString();
	}
	
}
