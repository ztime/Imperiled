package com.imperiled.game;

import com.badlogic.gdx.scenes.scene2d.Event;

public class MapEvent extends Event {
	String name;
	String mapObject;
	
	/*
	 * Shit will happen.	
	 */
	public MapEvent(String name, String mapObject) {
		this.name = name;
		this.mapObject = mapObject;
	}
}
