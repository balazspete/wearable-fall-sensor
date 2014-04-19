package com.example.wearablesensorbase.detector;

import java.util.HashMap;
import java.util.Set;

import quickdt.HashMapAttributes;
import quickdt.Instance;
import quickdt.Leaf;
import quickdt.Tree;
import quickdt.TreeBuilder;

import com.example.wearablesensorbase.calibration.Calibration;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;
import com.example.wearablesensorbase.events.FallEvent;
import com.example.wearablesensorbase.events.FallEventListener;
import com.example.wearablesensorbase.events.ListenerManager;
import com.google.common.collect.Sets;

public class Detector {

	private HashMap<String, SensorMeasurementSeries> data;
	private HashMap<String, Calibration> callibrations;
	
	ListenerManager<FallEventListener, FallEvent> manager;
	
	public Detector(HashMap<String, Calibration> callibrations, HashMap<String, SensorMeasurementSeries> data) {
		this.callibrations = callibrations;
		this.data = data;
		
		manager = new ListenerManager<FallEventListener, FallEvent>() {
			@Override
			protected void eventHandlerHelper(FallEventListener listener, FallEvent data) {
				FallEventListener.handleFallEvent(listener, data);
			}
		};
	}
	
	public void newMeasurement(String sensor, int index, SensorMeasurement measurement) {
		
//		SensorMeasurement m = data.get(sensor).get(index);
//		SensorMeasurement m2 = data.get(sensor).getLast();
//		m.acceleration.x;
//		m.acceleration.y;
//		m.acceleration.z;
//		m.orientation.x;
//		m.orientation.y;
//		m.orientation.z;
//		m.loudness;
		
		final Set<Instance> instancesA = Sets.newHashSet();
		final Set<Instance> instancesO = Sets.newHashSet();
		final Set<Instance> instancesL = Sets.newHashSet();
		
		//find the fall based or sensor readings
		//add data into the decision tree
		//acceleration DT
		instancesA.add(HashMapAttributes.create("accVector", 1 ).classification("NOT FALL"));
		instancesA.add(HashMapAttributes.create("accVector", 1.5 ).classification("NOT FALL"));
		instancesA.add(HashMapAttributes.create("accVector", 2 ).classification("FALL"));
		instancesA.add(HashMapAttributes.create("accVector", 2.5 ).classification("FALL"));

		
		//Orientation DT
		instancesO.add(HashMapAttributes.create("oriSum", 350).classification("FALL"));
		instancesO.add(HashMapAttributes.create("oriSum", 250).classification("NOT FALL"));
		instancesO.add(HashMapAttributes.create("oriSum", 280).classification("NOT FALL"));
		instancesO.add(HashMapAttributes.create("oriSum", 330).classification("FALL"));


		//loudness DT
		instancesL.add(HashMapAttributes.create("dbValue", 15).classification("NOT FALL"));
		instancesL.add(HashMapAttributes.create("dbValue", 20).classification("NOT FALL"));
		instancesL.add(HashMapAttributes.create("dbValue", 35).classification("FALL"));
				
		TreeBuilder treeBuilder = new TreeBuilder();
		Tree treeA = treeBuilder.buildPredictiveModel(instancesA);
		Tree treeO = treeBuilder.buildPredictiveModel(instancesO);
		Tree treeL = treeBuilder.buildPredictiveModel(instancesL);
		
		SensorMeasurement m1;
		//SensorMeasurement m2;
		//SensorMeasurement m3;
		//SensorMeasurement m4;
		//SensorMeasurement m5;
		double acc1,acc2,acc3,acc4,acc5;
		double ori1;
		double ld1;
		Leaf leafA, leafO, leafL;
		int percent;

		boolean check = true;
		while (check) {
			m1 = data.get(sensor).get(index);
			//m2 = data.get(sensor).get(index);
			//m3 = data.get(sensor).get(index);
			//m4 = data.get(sensor).get(index);
			//m5 = data.get(sensor).get(index);
			acc1 = Math.sqrt((m1.acceleration.x.getY() * m1.acceleration.x.getY())+ (m1.acceleration.y.getY() * m1.acceleration.y.getY())+(m1.acceleration.z.getY() * m1.acceleration.z.getY()));
			//acc2 = Math.sqrt((m2.acceleration.x.getY() * m2.acceleration.x.getY())+ (m2.acceleration.y.getY() * m2.acceleration.y.getY())+(m2.acceleration.z.getY() * m2.acceleration.z.getY()));
			//acc3 = Math.sqrt((m3.acceleration.x.getY() * m3.acceleration.x.getY())+ (m3.acceleration.y.getY() * m3.acceleration.y.getY())+(m3.acceleration.z.getY() * m3.acceleration.z.getY()));
			//acc4 = Math.sqrt((m4.acceleration.x.getY() * m4.acceleration.x.getY())+ (m4.acceleration.y.getY() * m4.acceleration.y.getY())+(m4.acceleration.z.getY() * m4.acceleration.z.getY()));
			//acc5 = Math.sqrt((m5.acceleration.x.getY() * m5.acceleration.x.getY())+ (m5.acceleration.y.getY() * m5.acceleration.y.getY())+(m5.acceleration.z.getY() * m5.acceleration.z.getY()));
			
			ori1 = m1.orientation.x.getY() + m1.orientation.y.getY() + m1.orientation.z.getY();
			
			ld1 = m1.loudness.getY();
			
			leafA = treeA.getLeaf(HashMapAttributes.create("accVector", acc1));
			leafO = treeO.getLeaf(HashMapAttributes.create("accVector", ori1));
			leafL = treeL.getLeaf(HashMapAttributes.create("dbValue", ld1 ));
			
			if (leafA.getBestClassification().equals("FALL") && leafO.getBestClassification().equals("FALL") && leafL.getBestClassification().equals("FALL") ) {
				//System.out.println(FallType.FALL);
				detectionAlert(FallType.FORWARD_FALL);
				percent = 100;  	//this data can be used to get the accuracy of fall combining 2 sensor
			} else if (leafA.getBestClassification().equals("FALL") && leafO.getBestClassification().equals("FALL")){
				percent = 66;
			}
			else if (leafA.getBestClassification().equals("FALL") || leafO.getBestClassification().equals("FALL")) {
				percent = 33;
			}
			if (data.get(sensor).size() < index-1) {
				index++;
			}
		}
		
/*		
			final Set<Instance> instancesA = Sets.newHashSet();
			final Set<Instance> instancesO = Sets.newHashSet();
			final Set<Instance> instancesL = Sets.newHashSet();
			
			//find the fall based or sensor readings
			//add data into the decision tree
			//acceleration DT
			instancesA.add(HashMapAttributes.create("v1", 15, "v2", 40, "v3", 40, "v4", ,"v5", ).classification("NOT FALL"));
					
			//Orientation DT
			instancesO.add(HashMapAttributes.create("x", 0, "y", 0, "z", 0).classification("NOT FALL"));
			
			//loudness DT
			instancesL.add(HashMapAttributes.create("value", 15).classification("NOT FALL"));
			instancesL.add(HashMapAttributes.create("value", 20).classification("NOT FALL"));
			instancesL.add(HashMapAttributes.create("value", 35).classification("FALL"));
					
			TreeBuilder treeBuilder = new TreeBuilder();
			Tree treeA = treeBuilder.buildPredictiveModel(instancesA);
			Tree treeO = treeBuilder.buildPredictiveModel(instancesO);
			Tree treeL = treeBuilder.buildPredictiveModel(instancesL);
			*/

//
//			//Leaf leafA = treeA.getLeaf(HashMapAttributes.create("x", m.acceleration.x, "y", m.acceleration.y, "z", m.acceleration.z, "gender", "female"));
//			Leaf leafA = treeA.getLeaf(HashMapAttributes.create("x", , "y", m.acceleration.y, "z", m.acceleration.z, "gender", "female"));
//
//			//Leaf leafO = treeO.getLeaf(HashMapAttributes.create("x", m.orientation.x, "y", m.orientation.y, "z", m.orientation.z));
//			Leaf leafO = treeO.getLeaf(HashMapAttributes.create("x", m.orientation.x, "y", m.orientation.y, "z", m.orientation.z));
//
//			Leaf leafL = treeL.getLeaf(HashMapAttributes.create("value", 36));
					
//			if (leafA.getBestClassification().equals("FALL") && leafO.getBestClassification().equals("FALL") && leafL.getBestClassification().equals("FALL") ) {
//				System.out.println(FallType.FORWARD_FALL);
//			} else {
//					
//			}
					
			//chairs testing in a different location
			//combining the two decision from the multiple sensor.
//		
	}
	
	
	private void detectionAlert(FallType type) {
		manager.send(new FallEvent(type));
	}
	
	public void addFallEventListener(FallEventListener listener) {
		manager.addEventListener(listener);
	}
	
	public void removeFallEventListener(FallEventListener listener) {
		manager.removeEventListener(listener);
	}
	
}
