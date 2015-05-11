package com.imperiled.game;

import com.badlogic.gdx.Game;

public class Imperiled extends Game {
	
	//Here we can add variables available between game screens
	//and game states. Should not be used unless we must.
	
	
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
