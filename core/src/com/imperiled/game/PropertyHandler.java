package com.imperiled.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.maps.MapObjects;

/**
 * PropertyHandler is a class that organizes and manages
 * all data that needs to be changed or accessed from
 * anywhere by anyone.
 * 
 * It's main purpose is too keep track of what is currently
 * active withing the game such as actors and events.
 * 
 * @author John Wikman
 * @version 2015.05.12
 */
public class PropertyHandler {
	public static final String[] eventReqs = {"name", "action", "target"};
	public static final String[] musicReqs = {"name", "filename"};
	public static HashMap<String, Actor> currentActors;
	public static HashMap<String, MapEvent> currentEvents;
	public static MapObjects collisionObjects;
	public static Imperiled currentGame;
	public static MainGameScreen previousMainGameScreen;
	
	/**
	 * Refreshes the actors in currentActors to contain
	 * a new list of active actors specified by the
	 * ArrayList. Preferably called when loading a new
	 * map.
	 * 
	 * @param actors A list containing the new actors to
	 *               be stored in currentActors.
	 */
	public static void newActors(ArrayList<Actor> actors) {
		currentActors = new HashMap<String, Actor>();
		for(Actor actor : actors) {
			currentActors.put(actor.getName(), actor);
		}
	}
	
	/**
	 * Refreshes the events in currentEvents to contain
	 * a new list of active events specified by the
	 * ArrayList. Preferably called when loading a new
	 * map.
	 * 
	 * @param events A list containing the new events to
	 *               be stored in currentEvents.
	 */
	public static void newEvents(ArrayList<MapEvent> events) {
		currentEvents = new HashMap<String, MapEvent>();
		for(MapEvent event : events) {
			currentEvents.put(event.getName(), event);
		}
	}
}
