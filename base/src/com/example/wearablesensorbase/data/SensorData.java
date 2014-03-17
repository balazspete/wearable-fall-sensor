package com.example.wearablesensorbase.data;

import java.io.Serializable;

import com.jjoe64.graphview.GraphView.GraphViewData;

/**
 * A data object describing an x-y relationship
 * @author Balazs Pete
 *
 */
public class SensorData extends GraphViewData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3966525927272578510L;
	
	public final long time;
	public final double value;
	private double displayValue;
	
	/**
	 * Create a new SensorData
	 * @param time The timestamp
	 * @param value The value
	 */
	public SensorData(long time, double value) {
		super(time, value);
		this.time = time;
		this.value = value;
		this.displayValue = value;
	}
	
	@Override
	public double getX() {
		return time;
	}

	@Override
	public double getY() {
		return displayValue;
	}

	/**
	 * Set a new value to be used for graph display and calculations
	 * @param value The new value
	 */
	public void setDisplayValue(double value) {
		this.displayValue = value;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(value);
		out.append("(");
		out.append(displayValue);
		out.append(")");
		
		return out.toString();
	}
}
