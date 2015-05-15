package com.imperiled.game;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a damage from a weapon and contains a rectangle
 * and a dmg variable
 * @author jonaswedin
 *
 */
public class DamageRectangle {
	public Rectangle rectangle;
	public int dmg;
	
	/**
	 * Creates a new empty rectangle with zero damage
	 */
	public DamageRectangle() {
		this.rectangle = new Rectangle();
		this.dmg = 0;
	}
	
	/**
	 * Creates a new dmgRectangle from a existing rectangle
	 * and an int as damage
	 * 
	 * @param rectangle area of damage
	 * @param dmg the amount of damage
	 */
	public DamageRectangle(Rectangle rectangle, int dmg){
		this.rectangle = rectangle;
		this.dmg = dmg;
	}
}
