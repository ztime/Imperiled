package com.imperiled.game;

/**
 * Represents a ghost enemy
 */
public class Ghost extends Enemy {

	/**
	 * Creates a new ghost using parameters in enemy
	 * @param x
	 * @param y
	 */
	public Ghost(int x, int y){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				100, 					// Health
				5,						// Damage
				70f, 					// Speed
				100f, 					// Attacking speed
				Behaviour.AGGRESSIVE, 	// Behaviour for ai 
				140f, 					// Aggrorange for ai
				"sprites/ghost.png");	// The path to sprite
	}
}
