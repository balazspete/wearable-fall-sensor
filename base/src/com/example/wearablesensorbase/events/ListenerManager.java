package com.example.wearablesensorbase.events;

import java.util.ArrayList;
import java.util.EventListener;

public abstract class ListenerManager<LISTENER extends EventListener, DATA> {

	private ArrayList<LISTENER> listeners;
	
	public ListenerManager() {
		listeners = new ArrayList<LISTENER>();
	}
	
	public synchronized void addEventListener(LISTENER listener) {
		listeners.add(listener);
	}
	
	public synchronized void removeEventListener(LISTENER listener) {
		listeners.remove(listener);
	}
	
	public synchronized void send(DATA data) {
		for (LISTENER listener : listeners) {
			eventHandlerHelper(listener, data);
		}
	}
	
	protected abstract void eventHandlerHelper(LISTENER listener, DATA data);
	
}
