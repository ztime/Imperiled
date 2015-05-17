package com.imperiled.game;

/**
 * Class representing a bee
 */
public class Bee extends Enemy {
	
	/**
	 * Creates a new bee using parameters in enemy
	 * @param x
	 * @param y
	 */
	public Bee(int x, int y){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				60, 					// Health
				70f, 					// Speed
				100f, 					// Attacking speed
				Behaviour.AGGRESSIVE, 	// Behaviour for ai 
				140f, 					// Aggrorange for ai
				"sprites/bee.png");		// The path to sprite
	}
	
	
}
