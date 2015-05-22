package com.imperiled.game;

/**
 * Represents a eyeball enemy
 */
public class Eyeball extends Enemy {

	/**
	 * Creates a new eyeball using parameters in enemy
	 * @param x
	 * @param y
	 */
	public Eyeball(int x, int y){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				100, 					// Health
				4,						// Damage
				70f, 					// Speed
				100f, 					// Attacking speed
				Behaviour.AGGRESSIVE, 	// Behaviour for ai 
				140f, 					// Aggrorange for ai
				"sprites/eyeball.png");	// The path to sprite
	}
}
