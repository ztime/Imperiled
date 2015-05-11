package com.imperiled.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

abstract public class Actor {
	//position
	int x, y;
	float speed;
	
	//time variables
	float elapsedTime;
	
	//other
	int health;
	
	//functions that must be implemented
	public abstract void draw(SpriteBatch batch);
	public abstract Rectangle getRectangle();
	public abstract DamageRectangle getDamageRectangle();
	public abstract void takeDmg(int dmg);
	public abstract void dispose();
	
	/**
	 * Moves deltaTime forward
	 * @param deltaTime
	 */
	public void update(float deltaTime){
		this.elapsedTime += deltaTime;
	}
	
	/**
	 * Sets the current position to x & y
	 * @param x
	 * @param y
	 */
	public void setPosition(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets current position using vector
	 * @param newPos
	 */
	public void setPosition(Vector2 newPos){
		this.x = (int) newPos.x;
		this.y = (int) newPos.y;
	}
	
	/**
	 * Returns current position as a vector2
	 * @return
	 */
	public Vector2 getPosition(){
		Vector2 vector = new Vector2();
		vector.x = (float) this.x;
		vector.y = (float) this.y;
		return vector; 
	}
	
	/**
	 * Gets the current x position
	 * @return
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * Gets the current y position
	 * @return
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * Returns the current health
	 * @return
	 */
	public int getHealth(){
		return this.health;
	}
}
