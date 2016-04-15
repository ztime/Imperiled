/*
	Copyright © 2015 Jonas Wedin jonwed@kth.se John Wikman jwikman@kth.se
	This work is free. You can redistribute it and/or modify it under the
	terms of the Do What The Fuck You Want To Public License, Version 2,
	as published by Sam Hocevar. See the COPYING file for more details.
*/

package com.imperiled.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Vector2;

public class Imperiled extends Game {
	
	//Here we can add variables available between game screens
	//and game states. Should not be used unless we must.
	
	//the map that should be loaded with mainGameClass
	public String map; 
	public Vector2 startPos;
	public Direction startDirection;
	public boolean paused;
	public int playerHealth;   //save player health inbetween maps
	
	//set this to true if we want do draw rectangels for collision and things
	public boolean debug = false;
	//Set this to start map for the game
	public String startMap = "overworld";
	
	@Override
	public void create () {		
		// we simply switch to main manu screen
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	/**
	 * This should clear any "global" game 
	 * variables if it needs to 
	 */
	public void dispose(){
		
	}
}
