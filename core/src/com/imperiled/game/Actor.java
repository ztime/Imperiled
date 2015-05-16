package com.imperiled.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A base class that represents an monster or player
 * on the screen , i.e something that should be rendered
 * controlled and take / give damage
 * 
 * @author Jonas Wedin
 * @version 2015-05-15
 */
abstract public class Actor {
	//position
	int x, y;
	//old position , saved for collision checking
	int oldX, oldY;
	float speed;
	
	//for actors controlled by AI
	float attackingSpeed;
	float aggroRange;
	
	//time variables
	float elapsedTime;
	float elapsedTimeDeath;			//time since death
	float elapsedTimeDamage = 2f;	// - || -    damage was taken
	float elapsedTimeAttack;		// - || -    attack begun
	
	//other
	int health = 100;
	int maxHP = health;
	boolean invulnerable = false;
	String name;
	AI ai;
	
	//keep track of current states
	State currentState = State.IDLE;
	Direction currentDirection = Direction.UP;
	Behaviour behaviour;
	
	//functions that must be implemented
	public abstract Rectangle getRectangle();
	public abstract DamageRectangle getDamageRectangle();
	public abstract void dispose();
	public abstract TextureRegion getKeyFrame();
	
	/**
	 * Draws the current key frame from animation.
	 * Also colors the animation red if the actor has taken damage
	 * in the last 1.2 seconds and blue if the actor is inactive
	 * 
	 * Does not call batch.begin() or batch.end()!
	 * 
	 * @param batch SpriteBatch to paint with 
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
	 * Also keeps track on how long the actor has been dead and if he should
	 * switch state to inactive.
	 * 
	 * @param deltaTime the time between the last frame and the one to be drawn
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
	 * Makes the actor take damage. Also keeps track of
	 * health and sets the actor to death if health is <= 0
	 * 
	 * @param dmg the amount of damage to take
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
	 * Sets the current position to x & y.
	 * Also saves the old position should we need to back up
	 * with revertToOldPosition()
	 * 
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
	 * Returns the players old position through a vector
	 * Does not change the position back!
	 *  
	 * @return Vector2 the old position
	 */
	public Vector2 getOldPosition(){
		Vector2 retVec = new Vector2();
		retVec.x = (float) oldX;
		retVec.y = (float) oldY;
		return retVec;
	}
	
	/**
	 * Moves actor back to it's old position
	 * This only works once! If it is called again 
	 * without setting a new position , it will return 
	 * the same position 
	 */
	public void revertToOldPosition(){
		this.x = this.oldX;
		this.y = this.oldY;
	}
	
	/**
	 * Returns current position as a vector2
	 * @return Vector2 the position
	 */
	public Vector2 getPosition(){
		Vector2 vector = new Vector2();
		vector.x = (float) this.x;
		vector.y = (float) this.y;
		return vector; 
	}
	
	/**
	 * Gets the current x position
	 * @return int x
	 */
	public int getX(){
		return x;
	}
	
	/**
	 * Gets the current y position
	 * @return int y
	 */
	public int getY(){
		return y;
	}
	
	/**
	 * Returns the current health
	 * @return int current health
	 */
	public int getHealth(){
		return this.health;
	}
	
	/**
	 * Translates between enum and int. 
	 * For use with the animation sprites
	 * @return int Number representing the current direction
	 */
	public int translateCurrentDirection(){
		switch(this.currentDirection){
		case UP:
			return 0;
		case DOWN:
			return 2;
		case LEFT:
			return 1;
		case RIGHT:
			return 3;
		case UP_LEFT:
			return 4;
		case UP_RIGHT:
			return 5;
		case DOWN_LEFT:
			return 6;
		default: //DOWN_RIGHT
			return 7;
		}
	}
	

	/**
	 * Change the direction the actor is facing
	 * using the enum Direction
	 * @param newDir the new direction
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
	 * Change the state of the actor
	 * will not change state if the actor is attacking, dead
	 * or inactive
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
	 * @return float speed
	 */
	public float getSpeed(){
		return this.speed;
	}
	
	/**
	 * Returns the current attack speed
	 * @return float attack speed
	 */
	public float getAttackSpeed(){
		return this.attackingSpeed;
	}
	
	/**
	 * Returs the current direction
	 * @return Direction current direction
	 */
	public Direction getDirection(){
		return this.currentDirection;
	}
	
	/**
	 * Returns the current state
	 * @return State current State
	 */
	public State getState(){
		return this.currentState;
	}
	
	/**
	 * Returns true if the actor is moving
	 * @return
	 */
	public boolean isMoving(){
		if(this.currentState == State.MOVE){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns an instance of the current AI for the actor
	 * 
	 * @return AI ai
	 */
	public AI getAI() {
		return ai;
	}
	
	/**
	 * Returns the name of the actor
	 * @return String name
	 */
	public String getName() {
		return this.name;
	}
	
}
