package com.imperiled.game;

import com.badlogic.gdx.math.Rectangle;

public class Sword implements Weapon {

	int damage = 20;
	int boxHeight = 20;
	int boxWidth = 20;
	
	@Override
	public int getDamage() {
		return 20;
	}

	@Override
	public DamageRectangle getRectangle() {
		Rectangle rect = new Rectangle();
		rect.height = this.boxHeight;
		rect.width = this.boxWidth;
		
		DamageRectangle dmg = new DamageRectangle(rect, this.damage);
		return dmg;
	}

}
