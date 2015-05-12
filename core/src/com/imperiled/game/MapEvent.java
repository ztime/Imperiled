package com.imperiled.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Event;

/**
 * A class for the events on a map. An instance
 * of this class should never be created directly.
 * It should be created by the class FileParser which
 * reads in all the relevant events for the map.
 * 
 * @author John Wikman
 * @version 2015.05.12
 */
public class MapEvent extends Event {
	private HashMap<String, String> props;
	
	/**
	 * Constructor for a MapEvent. This should only
	 * be called by the class FileParser.
	 */
	public MapEvent(HashMap<String, String> props) {
		this.props = props;
	}
	
	/**
	 * Returns the name of this event.
	 */
	public String getName() {
		return props.get("name");
	}
	
	/**
	 * The method that triggers the event. It does different
	 * things depending on what the action property is.
	 */
	public void action() {
		// Loads the action-type and sets the event as handled.
		if(isHandled()) {
			return;
		}
		handle();
		String act = props.get("action");
		
		/*
		 * This action-type does damage to a specific
		 * target. An amount specifies the amount of
		 * damage to deal to the target.
		 * 
		 * Causes eventError if "target" is not
		 * specified or is not loaded on map. Causes
		 * eventError if "amount" is not specified or
		 * if "amount" is not a number.
		 */
		if(act.equalsIgnoreCase("dodamage")) {
			String target = props.get("target");
			String sAmount = props.get("amount");
			if(PropertyHandler.currentActors.get(target) == null) {
				eventError("Target is not loaded on to this map.", act, target);
			}
			if(sAmount == null) {
				eventError("No damage amount specified.", act, target);
			}
			if(!sAmount.matches("^\\d+$")) {
				eventError("Amount is not a number.", act, target);
			}
			int amount = Integer.parseInt(sAmount);
			PropertyHandler.currentActors.get(target).takeDamage(amount);
		}
		
		/*
		 * This action-type changes the current map. It
		 * also specifies what coordinates the player
		 * will spawn on the new map and the direction
		 * the player will be facing.
		 * 
		 * 
		 */
		else if(act.equalsIgnoreCase("changeMap")) {
			String target = props.get("target");
			String xcor = props.get("xcor");
			String ycor = props.get("ycor");
			String direction = props.get("direction");
			if(!mapExists(target)) {
				eventError("Target map does not exist.", act, target);
			}
			Imperiled game = PropertyHandler.currentGame; 
			game.map = target;
			if(xcor != null && ycor != null) {
				if(!xcor.matches("^\\d+$") || !ycor.matches("^\\d+$")) {
					eventError("Coordinates not integers.", act, target);
				}
				game.startPos.x = Integer.parseInt(xcor);
				game.startPos.y = Integer.parseInt(ycor);
			}
			if(direction == null) {
				//Do nothing
			} else if(direction.equalsIgnoreCase("UP")) {
				game.startDirection = Direction.UP;
			} else if(direction.equalsIgnoreCase("DOWN")) {
				game.startDirection = Direction.DOWN;
			} else if(direction.equalsIgnoreCase("LEFT")) {
				game.startDirection = Direction.LEFT;
			} else if(direction.equalsIgnoreCase("RIGHT")) {
				game.startDirection = Direction.RIGHT;
			} else {
				eventError("Specified direction not valid.", act, target);
			}
			game.setScreen(new MainGameScreen(game));
		}
	}
	
	/**
	 * Returns the target of this event.
	 * Returns null of there is no target.
	 */
	public String eventTarget() {
		return props.get("target");
	}
	
	/**
	 * Prints out a list of properties for an event.
	 * Used for debugging.
	 * 
	 * @return A String containing all the properties of the event.
	 */
	public String listOfProperties() {
		StringBuilder s = new StringBuilder();
		for(String key : props.keySet()) {
			s.append("\n" + key + ": " + props.get(key));
		}
		return s.toString();
	}
	
	/**
	 * Help method to check if a map exists.
	 * @return True if the map exists. Else false;
	 */
	private boolean mapExists(String mapName) {
		if (Gdx.app.getType() == ApplicationType.Android) {
			return Gdx.files.internal("map/" + mapName).exists();
		}
		return Gdx.files.internal("./bin/map/" + mapName).exists();
	}
	
	/**
	 * 
	 * @param type
	 * @param atFrom
	 * @param atTo
	 */
	private void eventError(String type, String atFrom, String atTo) {
		System.err.printf("Event error: %s%nFrom %s to %s.%n", type, atFrom, atTo);
		System.exit(1);
	}
}
