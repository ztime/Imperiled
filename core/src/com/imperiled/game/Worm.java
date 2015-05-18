package com.imperiled.game;

/**
 * Represents an actor Worm
 */
public class Worm extends Enemy {

	/**
	 * Creates a new worm using parameters in enemy
	 * @param x
	 * @param y
	 */
	public Worm (int x, int y){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				100, 					// Health
				3,						// Damage
				70f, 					// Speed
				100f, 					// Attacking speed
				Behaviour.AGGRESSIVE, 	// Behaviour for ai 
				140f, 					// Aggrorange for ai
				"sprites/worm.png");	// The path to sprite
	}
}
