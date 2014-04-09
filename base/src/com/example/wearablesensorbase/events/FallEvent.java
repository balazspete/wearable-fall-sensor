package com.example.wearablesensorbase.events;

import com.example.wearablesensorbase.detector.FallType;

public class FallEvent extends Event {

	public final FallType fallType;
	
	public FallEvent(FallType type) {
		fallType = type;
	}
	
}
