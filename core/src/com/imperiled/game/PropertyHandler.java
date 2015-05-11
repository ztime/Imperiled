package com.imperiled.game;

import java.util.ArrayList;
import java.util.HashMap;

public class PropertyHandler {
	public static final String[] eventReqs = {"name", "type", "action"};
	public static final String[] musicReqs = {"name", "filename"};
	public static HashMap<String, Actor> currentActors;
	
	public static void newActors(ArrayList<Actor> actors) {
		currentActors = new HashMap<String, Actor>();
		for(Actor actor : actors) {
			currentActors.put("hghghj", actor);
		}
	}
}
