package com.imperiled.game;

import com.badlogic.gdx.math.Rectangle;

/**
 * Represents a sword the player can have equipped
 */
public class Sword implements Weapon {
	//states
	int damage = 20;
	int boxHeight = 30;
	int boxWidth = 30;
	
	/**
	 * Returns the damage of the weapon
	 * @return int damage
	 */
	@Override
	public int getDamage() {
		return 20;
	}

	/**
	 * Returs a damage rectangle based on the weapon
	 */
	@Override
	public DamageRectangle getRectangle() {
		Rectangle rect = new Rectangle();
		rect.height = this.boxHeight;
		rect.width = this.boxWidth;
		
		DamageRectangle dmg = new DamageRectangle(rect, this.damage);
		return dmg;
	}

}
