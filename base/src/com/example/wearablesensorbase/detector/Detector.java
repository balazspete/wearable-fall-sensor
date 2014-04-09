package com.example.wearablesensorbase.detector;

import java.util.HashMap;
import java.util.Set;

import quickdt.HashMapAttributes;
import quickdt.Instance;
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
		
//		detectionAlert(FallType.FORWARD_FALL);
		private void testDecisionTree() {
			final Set<Instance> instancesA = Sets.newHashSet();
			final Set<Instance> instancesO = Sets.newHashSet();
			final Set<Instance> instancesL = Sets.newHashSet();
			
			//find the fall based or sensor readings
					//add data into the decision tree
					//acceleration DT
					instancesA.add(HashMapAttributes.create("x", 15, "y", 40, "z", 40, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 60, "y", 46, "z", 15, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 20, "y", 49, "z", 50, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 21, "y", 168, "z", 15, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 25, "y", 168, "z", 20, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 52, "y", 168, "z", 0, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 62, "y", 168, "z", 0, "gender", "male").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 2, "y", 168, "z", 0, "gender", "female").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 2, "y", 168, "z", 0, "gender", "female").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 2, "y", 168, "z", "", "gender", "female").classification("NOT FALL"));
					instancesA.add(HashMapAttributes.create("x", 2, "y", 168, "z", "", "gender", "female").classification("NOT FALL"));
					
					//Orientation DT
					instancesO.add(HashMapAttributes.create("x", 0, "y", 0, "z", 0).classification("NOT FALL"));
					instancesO.add(HashMapAttributes.create("x", 30, "y", 10, "z", 0).classification("NOT FALL"));
					instancesO.add(HashMapAttributes.create("x", 10, "y", 4, "z", 5).classification("NOT FALL"));
					instancesO.add(HashMapAttributes.create("x", 2, "y", 5, "z", 10).classification("NOT FALL"));
					instancesO.add(HashMapAttributes.create("x", 20, "y", 20, "z", 15).classification("FALL"));
					
					//loudness DT
					instancesL.add(HashMapAttributes.create("value", 15).classification("NOT FALL"));
					instancesL.add(HashMapAttributes.create("value", 20).classification("NOT FALL"));
					instancesL.add(HashMapAttributes.create("value", 35).classification("FALL"));
					
					TreeBuilder treeBuilder = new TreeBuilder();
					Tree treeA = treeBuilder.buildPredictiveModel(instancesA);
					Tree treeO = treeBuilder.buildPredictiveModel(instancesO);
					Tree treeL = treeBuilder.buildPredictiveModel(instancesL);


					//Leaf leafA = treeA.getLeaf(HashMapAttributes.create("x", m.acceleration.x, "y", m.acceleration.y, "z", m.acceleration.z, "gender", "female"));
					//Leaf leafA = treeA.getLeaf(HashMapAttributes.create("x", , "y", m.acceleration.y, "z", m.acceleration.z, "gender", "female"));

					//Leaf leafO = treeO.getLeaf(HashMapAttributes.create("x", m.orientation.x, "y", m.orientation.y, "z", m.orientation.z));
					//Leaf leafO = treeO.getLeaf(HashMapAttributes.create("x", m.orientation.x, "y", m.orientation.y, "z", m.orientation.z));

					//Leaf leafL = treeL.getLeaf(HashMapAttributes.create("value", 36));
					//if (leafA.getBestClassification().equals("FALL") && leafO.getBestClassification().equals("FALL") && leafL.getBestClassification().equals("FALL") ) {
					//	System.out.println(FallType.FORWARD_FALL);
					//} else {
						
					//}
		
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
