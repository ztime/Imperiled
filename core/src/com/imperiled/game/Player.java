package com.imperiled.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
/**
 * Class for the player!
 * 
 * @author jonas wedin
 */
public class Player extends Actor {
	/**
	 * Represents the current state the player is in
	 * this controls what player can do 
	 * For ex. cant move if the player is attacking or dead
	 */
	public enum State{
		IDLE, MOVE, ATTACKING, DEAD;
	}
	/**
	 * Represents the current direction the player is facing
	 */
	public enum Direction{
		UP,DOWN,RIGHT,LEFT;
	}
	
	//more time variables
	float elapsedTimeDeath;
	float elapsedTimeDamage;
	
	//places for saving animations
	private Animation[] walking; 	// 0 = up, 1 = left, 2 = down, 3 = right;
	private Animation[] slashing; 	// same direction as above
	private Animation death;		// death has no direction

	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub

	}

	@Override
	public Rectangle getRectangle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DamageRectangle getDamageRectangle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void takeDmg(int dmg) {
		// TODO Auto-generated method stub

	}

}
