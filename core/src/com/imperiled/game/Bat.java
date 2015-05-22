package com.imperiled.game;

/**
 * Class representing a bat
 */
public class Bat extends Enemy {
	
	/**
	 * Creates a new bat using parameters in enemy
	 * @param x
	 * @param y
	 */
	public Bat(int x, int y){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				60, 					// Health
				2,						// Damage
				70f, 					// Speed
				100f, 					// Attacking speed
				Behaviour.AGGRESSIVE, 	// Behaviour for ai 
				140f, 					// Aggrorange for ai
				"sprites/bat.png");		// The path to sprite
	}
	
	
}
