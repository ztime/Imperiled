package com.imperiled.game;

import java.util.HashMap;

public class PropertyHandler {
	public static final String[] eventReqs = {"name", "mapObject", "action"};
	public static final String[] musicReqs = {"name", "filename"};
	
	public static MapEvent parseEvent(HashMap<String, String> properties) {
		//parse properties
		for(String property : properties.keySet()) {
			String[] parts = property.trim().split("\\;");
		}
		return new MapEvent(properties.get("name"), properties.get("mapObject"));
	}
}
