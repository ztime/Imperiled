package com.imperiled.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

abstract public class Actor {
	//position
	int x, y;
	//old position , saved for collision checking
	int oldX, oldY;
	float speed;
	
	//time variables
	float elapsedTime;
	float elapsedTimeDeath;			//time since death
	float elapsedTimeDamage = 2f;	// - || -    damage was taken, start with 2 so player aint red
	float elapsedTimeAttack;		// - || -    attack begun
	
	//other
	int health = 100;
	boolean invulnerable = false;
	String name;
	AI ai;
	
	//keep track of current states
	State currentState = State.IDLE;
	Direction currentDirection = Direction.UP;
	
	//functions that must be implemented
	
	public abstract Rectangle getRectangle();
	public abstract DamageRectangle getDamageRectangle();
	public abstract void dispose();
	public abstract TextureRegion getKeyFrame();
	
	
	/**
	 * Draws the current key frame 
	 * @param batch
	 */
	public void draw(SpriteBatch batch) {
		if(this.elapsedTimeDamage < 1.2f && 
				this.currentState != State.INACTIVE &&
				this.currentState != State.DEAD){
			batch.setColor(1, 0, 0, 1);
		} else if(this.currentState == State.DEAD || this.currentState == State.INACTIVE){
			batch.setColor(0,0,1,1);
		}
		batch.draw(this.getKeyFrame(), x, y);
		batch.setColor(Color.WHITE);
	}
	/**
	 * Updates the current time according to state
	 * @param deltaTime the time between the last frame and this
	 */
	public void update(float elapsedTime){
		this.elapsedTime += elapsedTime;
		this.elapsedTimeDamage += elapsedTime;
		if(this.currentState == State.ATTACKING){
			this.elapsedTimeAttack += elapsedTime;
		}
		if(this.currentState == State.DEAD){
			this.elapsedTimeDeath += elapsedTime;
			if(this.elapsedTimeDeath > 2.0f){
				this.currentState = State.INACTIVE;
			}
		}
	}
	
	/**
	 * Should be called when the player takes damage 
	 */
	public void takeDamage(int dmg) {
		if(!this.invulnerable){
			if(this.elapsedTimeDamage > 1.2f && 
					this.currentState != State.DEAD &&
					this.currentState != State.INACTIVE){
				this.health -= dmg;
				elapsedTimeDamage = 0;
			}
			
			if(health <= 0 && 
					currentState != State.DEAD &&
					currentState != State.INACTIVE){
				currentState = State.DEAD;
			}
		}
	}
	/**
	 * Sets the current position to x & y
	 * @param x
	 * @param y
	 */
	public void setPosition(int x,int y){
		oldX = this.x;
		oldY = this.y;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets current position using vector
	 * @param newPos
	 */
	public void setPosition(Vector2 newPos){
		oldX = this.x;
		oldY = this.y;
		this.x = (int) newPos.x;
		this.y = (int) newPos.y;
	}
	
	/**
	 * returns the old position 
	 * @return
	 */
	public Vector2 getOldPosition(){
		Vector2 retVec = new Vector2();
		retVec.x = (float) oldX;
		retVec.y = (float) oldY;
		return retVec;
	}
	
	/**
	 * Moves actor back to it's old position
	 */
	public void revertToOldPosition(){
		this.x = this.oldX;
		this.y = this.oldY;
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
	
	/**
	 * Translates between enum and int 
	 * @param dir
	 * @return
	 */
	public int translateCurrentDirection(){
		switch(this.currentDirection){
		case UP:
			return 0;
		case DOWN:
			return 2;
		case LEFT:
			return 1;
		default: //RIGHT
			return 3;
		}
	}
	

	/**
	 * Change the direction the player is facing
	 * @param newDir
	 */
	public void setDirection(Direction newDir){
		this.currentDirection = newDir;
	}
	
	/**
	 * Returns true if the current actor is active
	 * @return
	 */
	public boolean isActive(){
		return (this.currentState != State.INACTIVE);
	}
	/**
	 * Change the state of the player
	 * @param newState
	 */
	public void setState(State newState){
		if(this.currentState != State.ATTACKING && 
				this.currentState != State.DEAD &&
				this.currentState != State.INACTIVE){
			this.currentState = newState;
		}
	}
	
	/**
	 * Returns the current speed
	 * 
	 * @return speed
	 */
	public float getSpeed(){
		return this.speed;
	}
	
	/**
	 * Returs the current direction
	 * @return
	 */
	public Direction getDirection(){
		return this.currentDirection;
	}
	
	/**
	 * Returns the current state
	 * @return
	 */
	public State getState(){
		return this.currentState;
	}
	
	/**
	 * Returns true if player is MOVEing
	 * @return
	 */
	public boolean isMoving(){
		if(this.currentState == State.MOVE){
			return true;
		} else {
			return false;
		}
	}
	
	public AI getAI() {
		return ai;
	}
	
	/**
	 * Returns the name of the actor
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
}
