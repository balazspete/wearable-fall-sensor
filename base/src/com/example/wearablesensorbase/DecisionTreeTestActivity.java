package com.example.wearablesensorbase;

import java.util.Set;

import com.google.common.collect.Sets;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import quickdt.*;

public class DecisionTreeTestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_decision_tree_test);
		
		testDecisionTree();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.decision_tree_test, menu);
		return true;
	}
	
	private void testDecisionTree() {
		final Set<Instance> instances = Sets.newHashSet();
		// A male weighing 168lb that is 55 inches tall, they are overweight
		instances.add(HashMapAttributes.create("height", 55, "weight", 168, "gender", "male").classification("overweight"));
		instances.add(HashMapAttributes.create("height", 75, "weight", 168, "gender", "female").classification("healthy"));
		instances.add(HashMapAttributes.create("height", 74, "weight", 143, "gender", "male").classification("underweight"));
		instances.add(HashMapAttributes.create("height", 49, "weight", 144, "gender", "female").classification("underweight"));
		instances.add(HashMapAttributes.create("height", 83, "weight", 223, "gender", "male").classification("healthy"));
		
		TreeBuilder treeBuilder = new TreeBuilder();
		Tree tree = treeBuilder.buildPredictiveModel(instances);
		
		Leaf leaf = tree.node.getLeaf(HashMapAttributes.create("height", 62, "weight", 201, "gender", "female"));
		if (leaf.getBestClassification().equals("healthy")) {
		    System.out.println("They are healthy!");
		} else if (leaf.getBestClassification().equals("underweight")) {
		    System.out.println("They are underweight!");
		} else {
		    System.out.println("They are overweight!");
		}
		
		tree.dump(System.out);
	}

}
