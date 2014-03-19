package com.example.wearablesensorbase.events;

import java.util.ArrayList;
import java.util.EventListener;

/**
 * An object managing event listeners
 * @author Balazs Pete
 *
 * @param <LISTENER> The type of the listener to manage
 * @param <DATA> The type of object the listeners manager
 */
public abstract class ListenerManager<LISTENER extends EventListener, DATA> {

	private ArrayList<LISTENER> listeners;
	
	/**
	 * Create a new manager
	 */
	public ListenerManager() {
		listeners = new ArrayList<LISTENER>();
	}
	
	/**
	 * Add an event listener
	 * @param listener The listener to add
	 */
	public synchronized void addEventListener(LISTENER listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove an event listener
	 * @param listener The listener to remove
	 */
	public synchronized void removeEventListener(LISTENER listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Send an object to the listeners
	 * @param data The object to send
	 */
	public synchronized void send(DATA data) {
		for (LISTENER listener : listeners) {
			eventHandlerHelper(listener, data);
		}
	}
	
	/**
	 * Method to determine if data object is to be sent to the listener
	 * @param listener The listener
	 * @param data The data object
	 */
	protected abstract void eventHandlerHelper(LISTENER listener, DATA data);
	
}
