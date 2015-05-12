package com.imperiled.game;

import java.util.ArrayList;
import java.util.HashMap;

public class PropertyHandler {
	public static final String[] eventReqs = {"name", "action"};
	public static final String[] musicReqs = {"name", "filename"};
	public static HashMap<String, Actor> currentActors;
	public static HashMap<String, MapEvent> currentEvents;
	
	public static void newActors(ArrayList<Actor> actors) {
		currentActors = new HashMap<String, Actor>();
		for(Actor actor : actors) {
			currentActors.put(actor.getName(), actor);
		}
	}
	
	public static void newEvents(ArrayList<MapEvent> events) {
		currentEvents = new HashMap<String, MapEvent>();
		for(MapEvent event : events) {
			currentEvents.put(event.getName(), event);
		}
	}
}
