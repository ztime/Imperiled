package com.imperiled.game;

/**
 * 
 * @author John Wikman
 * @version 2015.05.18
 */
public class Soldier extends NPC {
	/**
	 * Creates a new bee using parameters in enemy
	 * @param x
	 * @param y
	 */
	public Soldier(int x, int y){
		this.setData(x, 				// x - coordinate
				y, 						// y - coordinate
				60, 					// Health
				30f, 					// Speed
				this.speed,				// Attacking speed
				Behaviour.PASSIVE, 	    // Behaviour for ai 
				40f, 					// Aggrorange for ai
				"sprites/soldier.png");	// The path to sprite
		this.npcText = "Hejsan morsan, hejsan stabben. Här är brev från älsklingsgrabben.";
	}
}
