package com.example.wearablesensorbase;

import java.util.HashMap;

import com.example.wearablesensorbase.data.DataSimulator;
import com.example.wearablesensorbase.data.SensorData;
import com.example.wearablesensorbase.data.SensorMeasurement;
import com.example.wearablesensorbase.data.SensorMeasurementSeries;
import com.example.wearablesensorbase.events.MeasurementEvent;
import com.example.wearablesensorbase.events.MeasurementEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GrapherActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	private boolean simulatorOn = false;
	private HashMap<String, DataSimulator> simulators;
	
	private String currentSensor;
	private long timestamp = System.currentTimeMillis();
	
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
		setupActionBar();

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		simulators = new HashMap<String, DataSimulator>();

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
		getMenuInflater().inflate(R.menu.grapher, menu);
		
		if (simulatorOn) {
			menu.getItem(0).setVisible(false);
		} else {
			menu.getItem(1).setVisible(false);
		}

		return true;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.simulator_start:
				startSimulator();
				return true;
			case R.id.simulator_stop:
				stopSimulator();
				return true;
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			default: return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		currentSensor = ((WearableSensorBase) getApplication()).getSensorNames()[position];
		
		Fragment fragment = new SensorGraphFragment();
		Bundle args = new Bundle();
		args.putString(SensorGraphFragment.SENSOR_ID, currentSensor);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		
		DataSimulator simulator = simulators.get(currentSensor);
		simulatorOn = simulator == null ? false : simulator.isRunning();
		
		invalidateOptionsMenu();
		
		return true;
	}
	
	private void startSimulator() {
		simulatorOn = true;
		DataSimulator simulator = new DataSimulator((WearableSensorBase) getApplication(), currentSensor, timestamp);
		simulators.put(currentSensor, simulator);
		simulator.start();
		invalidateOptionsMenu();
		Toast.makeText(getApplicationContext(), R.string.simulator_starting, Toast.LENGTH_SHORT).show();
	}
	
	private void stopSimulator() {
		simulatorOn = false;
		simulators.get(currentSensor).stopSimulator();
		invalidateOptionsMenu();
		Toast.makeText(getApplicationContext(), R.string.simulator_stopping, Toast.LENGTH_SHORT).show();
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
		public static final int VIEWPORT_SIZE = 30000; // 30 seconds
		public static final String SENSOR_ID = "com.example.wearablesensorbase.sensor_id";
		public static final String SENSOR_DATA = "com.example.wearablesensorbase.sensor_data";
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
			
			final String sensor = getArguments().getString(SENSOR_ID);
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
			
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					MeasurementEvent e = ((MeasurementEvent) message.getData().getSerializable(SENSOR_DATA));
					SensorMeasurement m = e.getMeasurement();
					boolean scrollToEnd = true;
					
					Log.d("test", e.getSensorId() + " " + sensor);
					if (e.getSensorId().equals(sensor)) {
						m.appendAcceleration(accelerationX, accelerationY, accelerationZ, scrollToEnd, MAX_MEASUREMENTS);
						m.appendOrientation(orientationX, orientationY, orientationZ, scrollToEnd, MAX_MEASUREMENTS);
						m.appendLoudness(loudness, scrollToEnd, MAX_MEASUREMENTS);
					}
				}
			};
			listener = new MeasurementEventListener(){
				@Override
				public void measurement(MeasurementEvent event) {
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable(SENSOR_DATA, event);
					message.setData(bundle);
					handler.sendMessage(message);
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
			graph.setViewPort(0, VIEWPORT_SIZE);
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
