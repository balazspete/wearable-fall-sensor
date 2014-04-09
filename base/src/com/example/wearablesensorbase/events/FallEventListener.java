package com.example.wearablesensorbase.events;

import java.util.EventListener;

public abstract class FallEventListener implements EventListener {

	public abstract void onForwardFall(FallEvent event);
	
	public static void handleFallEvent(FallEventListener listener, FallEvent event) {
		switch(event.fallType) {
			case FORWARD_FALL:
				listener.onForwardFall(event);
				break;
		}
	}
	
}
