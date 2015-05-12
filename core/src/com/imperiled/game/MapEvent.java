package com.imperiled.game;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.Event;

public class MapEvent extends Event {
	private HashMap<String, String> props;
	
	/*
	 * Shit will happen.	
	 */
	public MapEvent(HashMap<String, String> props) {
		this.props = props;
	}
	
	public String getName() {
		return props.get("name");
	}
	
	/*
	 * In dev
	 */
	public void action() {
		String act = props.get("action");
		if(act.equalsIgnoreCase("dodamage")) {
			String target = props.get("target");
			String sAmount = props.get("amount");
			if(target == null) {
				eventError("No target specified in event.", act, "dodamage");
			}
			if(sAmount == null) {
				eventError("No damage amount specified.", act, "dodamage");
			}
			if(!sAmount.matches("^\\d+$")) {
				eventError("Amount is not a number.", act, "dodamage");
			}
			int amount = Integer.parseInt(sAmount);
			PropertyHandler.currentActors.get(target).takeDamage(amount);
		}
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
