package com.example.wearablesensorbase;

import com.example.wearablesensorbase.data.SensorData;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;
import com.example.wearablesensorbase.events.MeasurementEvent;
import com.example.wearablesensorbase.events.MeasurementEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class GrapherActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	public static final int 
		COLOR_ACCELERATION_X = Color.rgb(52, 152, 219),
		COLOR_ACCELERATION_Y = Color.rgb(41, 124, 165),
		COLOR_ACCELERATION_Z = Color.rgb(31,  93, 124),
		COLOR_ORIENTATION_X = Color.rgb(155, 89, 182),
		COLOR_ORIENTATION_Y = Color.rgb(114, 69, 136),
		COLOR_ORIENTATION_Z = Color.rgb(89, 54, 105),
		COLOR_LOUDNESS = Color.rgb(230, 126, 34);
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grapher);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, ((WearableSensorBase) getApplication()).getSensorNames()), 
						this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grapher, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new SensorGraphFragment();
		Bundle args = new Bundle();
		args.putInt(SensorGraphFragment.SENSOR_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class SensorGraphFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String SENSOR_NUMBER = "com.example.wearablesensorbase.sensor_number";
		public static final int MAX_MEASUREMENTS = 100; 

		private MeasurementEventListener listener;
		private GraphViewSeries accelerationX, accelerationY, accelerationZ,
			orientationX, orientationY, orientationZ, loudness;
		
		public SensorGraphFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_grapher_sensor, container, false);
			rootView.setBackgroundColor(Color.BLACK);
			
			GraphView summaryGraph = new LineGraphView(
				      container.getContext(), 
				      getString(R.string.graph_summary));
			GraphView accelerationGraph = new LineGraphView(
				      container.getContext(), 
				      getString(R.string.graph_acceleration));
			GraphView orientationGraph = new LineGraphView(
				      container.getContext(), 
				      getString(R.string.graph_orientation));
			GraphView loudnessGraph = new LineGraphView(
				      container.getContext(), 
				      getString(R.string.graph_loudness));
			
			String sensor = getArguments().getString(SENSOR_NUMBER);
			setupSeries(sensor);
			
			summaryGraph.addSeries(accelerationX);
			summaryGraph.addSeries(accelerationY);
			summaryGraph.addSeries(accelerationZ);
			summaryGraph.addSeries(orientationX);
			summaryGraph.addSeries(orientationY);
			summaryGraph.addSeries(orientationZ);
			summaryGraph.addSeries(loudness);
			addGraph(rootView, summaryGraph, R.id.graph_summary);

			accelerationGraph.addSeries(accelerationX);
			accelerationGraph.addSeries(accelerationY);
			accelerationGraph.addSeries(accelerationZ);
			addGraph(rootView, accelerationGraph, R.id.graph_acceleration);
			
			orientationGraph.addSeries(orientationX);
			orientationGraph.addSeries(orientationY);
			orientationGraph.addSeries(orientationZ);
			addGraph(rootView, orientationGraph, R.id.graph_orientation);
			
			loudnessGraph.addSeries(loudness);
			addGraph(rootView, loudnessGraph, R.id.graph_loudness);
			
			listener = new MeasurementEventListener(){
				@Override
				public void measurement(MeasurementEvent event) {
					// TODO Auto-generated method stub
					SensorMeasurement m = event.getMeasurement();
					boolean scrollToEnd = true;
					m.appendAcceleration(accelerationX, accelerationY, accelerationZ, scrollToEnd, MAX_MEASUREMENTS);
					m.appendOrientation(orientationX, orientationY, orientationZ, scrollToEnd, MAX_MEASUREMENTS);
					m.appendLoudness(loudness, scrollToEnd, MAX_MEASUREMENTS);
				}
			};
			addNewMeasurementListener();
			return rootView;
		}
		
		@Override
		public void onDestroyView() {
			super.onDestroyView();
			
			removeNewMeasurementListener();
		}
		
		private void setupSeries(String sensor) {
			createEmptySeries();
			
			SensorMeasurementSeries measurements = 
					((WearableSensorBase) getActivity().getApplication()).getSensorData(sensor);
			if (measurements != null) {
				populateSeries(measurements);
			}
		}
		
		private void populateSeries(SensorMeasurementSeries measurements) {
			for (SensorMeasurement measurement : measurements) {
				accelerationX.appendData(measurement.acceleration.x, true, MAX_MEASUREMENTS);
				accelerationY.appendData(measurement.acceleration.y, true, MAX_MEASUREMENTS);
				accelerationZ.appendData(measurement.acceleration.z, true, MAX_MEASUREMENTS);
				
				orientationX.appendData(measurement.orientation.x, true, MAX_MEASUREMENTS);
				orientationY.appendData(measurement.orientation.y, true, MAX_MEASUREMENTS);
				orientationZ.appendData(measurement.orientation.z, true, MAX_MEASUREMENTS);
				
				loudness.appendData(measurement.loudness, true, MAX_MEASUREMENTS);
			}
		}
		
		private void createEmptySeries() {
			accelerationX = getEmptySerie(R.string.acceleration_x, COLOR_ACCELERATION_X);
			accelerationY = getEmptySerie(R.string.acceleration_y, COLOR_ACCELERATION_Y);
			accelerationZ = getEmptySerie(R.string.acceleration_z, COLOR_ACCELERATION_Z);
			
			orientationX = getEmptySerie(R.string.orientation_x, COLOR_ORIENTATION_X);
			orientationY = getEmptySerie(R.string.orientation_y, COLOR_ORIENTATION_Y);
			orientationZ = getEmptySerie(R.string.orientation_z, COLOR_ORIENTATION_Z);
			
			loudness = getEmptySerie(R.string.loudness, COLOR_LOUDNESS);
		}
		
		private GraphViewSeries getEmptySerie(int id, int colour) {
			return new GraphViewSeries(
					getString(id), 
					new GraphViewSeriesStyle(colour, 1), 
					new SensorData[]{});
		}
		
		private void setupViewPort(GraphView graph) {
			graph.setViewPort(0, 20);
			graph.setScrollable(true);
			graph.setScalable(true);
		}
		
		private void addNewMeasurementListener() {
			((WearableSensorBase) getActivity().getApplication()).addMeasurementEventListener(listener);
		}
		
		private void removeNewMeasurementListener() {
			((WearableSensorBase) getActivity().getApplication()).removeMeasurementEventListener(listener);
		}
		
		private void styleGraph(GraphView graph) {
			graph.setShowLegend(false);
			GraphViewStyle style = graph.getGraphViewStyle();
			style.setTextSize(getResources().getDimension(R.dimen.small));
			style.setNumHorizontalLabels(4);
			style.setNumVerticalLabels(3);
			style.setGridColor(Color.GRAY);
			style.setVerticalLabelsWidth(20);
			style.setHorizontalLabelsColor(Color.WHITE);
			style.setVerticalLabelsColor(Color.WHITE);
		}
		
		private void addGraph(View root, GraphView graph, int layoutId) {
			styleGraph(graph);
			setupViewPort(graph);
			LinearLayout layout = (LinearLayout) root.findViewById(layoutId);
			layout.addView(graph);
		}
	}

}
