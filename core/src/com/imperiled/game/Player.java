package com.imperiled.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	float elapsedTimeDeath;			//time since death
	float elapsedTimeDamage;		// - || -    damage was taken
	float elapsedTimeAttack;		// - || -    attack begun
	
	//places for saving animations
	private Animation[] walking; 			// 0 = up, 1 = left, 2 = down, 3 = right;
	private Animation[] slashing; 			// same direction as above
	private Animation death;				// death has no direction
	private TextureRegion[][] walkFrames; 	//same order as walking
	private TextureRegion[][] slashFrames;
	private TextureRegion[][] deathFrames;
	private Texture characterSheet;			//the big sheet with animations
	
	//keep track of current states
	private State currentState = State.IDLE;
	private Direction currentDirection = Direction.UP;
	
	
	/**
	 * Constructs a new player at x and y.
	 * @param x
	 * @param y
	 */
	public Player(int x, int y){
		this.setPosition(x, y);
		
		
	}
	
	/**
	 * Updates the current time according to state
	 * @param deltaTime the time between the last frame and this
	 */
	@Override
	public void update(float deltaTime){
		this.elapsedTime += elapsedTime;
		this.elapsedTimeDamage += elapsedTime;
		if(currentState == State.ATTACKING){
			this.elapsedTimeAttack += elapsedTime;
		}
		if(currentState == State.DEAD){
			this.elapsedTimeDeath += elapsedTime;
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
	 * Change the state of the player
	 * @param newState
	 */
	public void setState(State newState){
		if(this.currentState != State.ATTACKING && this.currentState != State.DEAD){
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
	private boolean isPlayerMoving(){
		if(this.currentState == State.MOVE){
			return true;
		} else {
			return false;
		}
		
	}
	@Override
	public void draw(SpriteBatch batch) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns a rectangle surrounding the player, should be used for
	 * collision checking and position checking etc. 
	 * 
	 * This needs to be handmade :/
	 * @return Rectangle
	 */
	@Override
	public Rectangle getRectangle() {
		Rectangle newRect = new Rectangle();
		newRect.x = this.x + this.getWidth() / 4;
		newRect.y = this.y;
		newRect.height = (3 * this.getHeight()) / 4;
		newRect.width = this.getWidth() / 2;
		return newRect;
	}

	/**
	 * Returns the height of the key frame, only for internal use
	 * not to be used from the outside
	 * @return
	 */
	private int getHeight(){
		return walking[translateCurrentDirection()].getKeyFrame(elapsedTime, isPlayerMoving()).getRegionHeight();
	}
	
	/**
	 * Returns the width of the key-frame , only the internal use
	 * not to be used from the outside
	 * @return
	 */
	private int getWidth(){
		return walking[translateCurrentDirection()].getKeyFrame(elapsedTime, isPlayerMoving()).getRegionWidth();
	}
	
	@Override
	public DamageRectangle getDamageRectangle() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Should be called when the player takes damage 
	 */
	@Override
	public void takeDmg(int dmg) {
		if(elapsedTimeDamage > 1.2f && currentState != State.DEAD){
			health -= dmg;
			elapsedTimeDamage = 0;
		}
		
		if(health <= 0 && currentState != State.DEAD){
			currentState = State.DEAD;
		}

	}
	/**
	 * Translates between enum and int 
	 * @param dir
	 * @return
	 */
	private int translateCurrentDirection(){
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
	 * Clears up memory from images and stuff
	 */
	public void dispose(){
		characterSheet.dispose();
	}
}
