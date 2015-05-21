package com.imperiled.game;

/**
 * 
 * @author John Wikman
 * @version 2015.05.21
 */
public class Soldier extends NPC {
	/**
	 * Creates a new soldier using parameters in npc
	 * @param x
	 * @param y
	 */
	public Soldier(int x, int y, String model){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				60, 					// Health
				30f, 					// Speed
				this.speed,				// Attacking speed
				Behaviour.PASSIVE, 	    // Behaviour for ai 
				40f, 					// Aggrorange for ai
				"sprites/soldier" + model + ".png");	// The path to sprite
	}
}
