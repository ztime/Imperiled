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
	
	public DamageRectangle() {
		this.rectangle = new Rectangle();
		this.dmg = 0;
	}
	
	public DamageRectangle(Rectangle rectangle, int dmg){
		this.rectangle = rectangle;
		this.dmg = dmg;
	}
}
