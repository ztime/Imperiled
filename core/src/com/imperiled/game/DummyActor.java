package com.imperiled.game;

/**
 * DummyActor used as a placeholder where an
 * actor is needed.
 * 
 * @author John Wikman
 * @version 2015.05.17
 */
public class DummyActor extends Enemy {
	/**
	 * Creates a new bee using parameters in enemy
	 * @param x
	 * @param y
	 */
	public DummyActor(int x, int y){
		this.setData(x, 					// x - coordinate
				y, 							// y - coordinate
				60, 						// Health
				0,							// Damage
				70f, 						// Speed
				100f, 						// Attacking speed
				Behaviour.AGGRESSIVE, 		// Behaviour for ai 
				140f, 					    // Aggrorange for ai
				"sprites/dummyActor.png");  // The path to sprite
	}
}
