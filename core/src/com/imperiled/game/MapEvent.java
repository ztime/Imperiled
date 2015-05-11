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
	public void action(String act) {
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
			doDamage(target, amount);
		}
	}
	
	/**
	 * 
	 * @param target
	 * @param amount
	 */
	private void doDamage(String target, int amount) {
		//
	}
	
	private void eventError(String type, String atFrom, String atTo) {
		System.err.printf("Event error: %s%nFrom %s to %s.%n", type, atFrom, atTo);
		System.exit(1);
	}
}