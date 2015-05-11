package com.imperiled.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
	
	//more time variables
	float elapsedTimeDeath;			//time since death
	float elapsedTimeDamage = 2f;	// - || -    damage was taken, start with 2 so player aint red
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
	
	//keep track of other stuff
	private ArrayList<Weapon> weapons;
	private Weapon currentWeapon;
	
	/**
	 * Constructs a new player at x and y.
	 * @param x
	 * @param y
	 */
	public Player(int x, int y){
		this.setPosition(x, y);
		weapons = new ArrayList<Weapon>();
		this.loadAnimation();
		
		//temporarly
		Weapon sword = new Sword();
		weapons.add(sword);
		currentWeapon = sword;
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
	/**
	 * Draws the player using the SpriteBatch 
	 * DOES NOT CALL batch.begin() or batch.end()
	 * draws the player red if it has taken damage
	 */
	@Override
	public void draw(SpriteBatch batch) {
		if(elapsedTimeDamage < 1.2f){
			batch.setColor(1, 0, 0, 0.7f);
		}
		batch.draw(this.getKeyFrame(), x, y);
		batch.setColor(Color.WHITE);

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
		if(currentWeapon != null){
			return new DamageRectangle();
		}
		//position the rectangle
		DamageRectangle returnRec = currentWeapon.getRectangle();
		//we need to position it relative to the player
		Rectangle playerRect = this.getRectangle();
		if(currentDirection == Direction.UP){
			returnRec.rectangle.x = playerRect.x;
			returnRec.rectangle.y = playerRect.y + playerRect.height;
		} else if (currentDirection == Direction.DOWN){
			returnRec.rectangle.x = playerRect.x;
			returnRec.rectangle.y = playerRect.y - returnRec.rectangle.height;
		} else if (currentDirection == Direction.LEFT){
			returnRec.rectangle.x = playerRect.x - playerRect.width;
			returnRec.rectangle.y = playerRect.y + playerRect.height / 4;
		} else { //Direction.RIGHT
			returnRec.rectangle.x = playerRect.x + playerRect.width;
			returnRec.rectangle.y = playerRect.y + playerRect.height / 4;
		}
		
		return returnRec;
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
	 * Returns the current keyframe in
	 * the relevant animation
	 * 
	 * @return
	 */
	private TextureRegion getKeyFrame(){
		if(currentState != State.ATTACKING && currentState != State.DEAD){
			return walking[translateCurrentDirection()].getKeyFrame(elapsedTime, isPlayerMoving());
		} else if(currentState == State.ATTACKING){
			if(slashing[translateCurrentDirection()].isAnimationFinished(elapsedTimeAttack)){
				elapsedTimeAttack = 0;
				currentState = State.IDLE;
			}
			return slashing[translateCurrentDirection()].getKeyFrame(elapsedTimeAttack, false);
		} else {
			return death.getKeyFrame(elapsedTimeDeath);
		}	
	}
	
	/**
	 * Load all the animations!
	 */
	private void loadAnimation(){
		int bigSheetRows = 21;
		int bigSheetCols = 13;
		characterSheet = new Texture(Gdx.files.internal("sprites/player.png"));
		TextureRegion[][] allFrames = TextureRegion.split(characterSheet, characterSheet.getWidth() / bigSheetCols, characterSheet.getHeight() / bigSheetRows);
		
		//walking
		int walkCols = 9;
		int walkRows = 4;
		int walkStart = 8;
		int walkStop = 11;
		walking = new Animation[4];
		walkFrames = new TextureRegion[walkRows][walkCols];
		
		float wAnimationSpeed = 0.05f;
		int index = 0;
		for(int i = walkStart; i < walkStop + 1; i++){
			for(int j = 0; j < walkCols; j++){
				walkFrames[index][j] = allFrames[i][j];
			}
			walking[index] = new Animation(wAnimationSpeed, walkFrames[index]);
			index++;
		}
		
		//Attacking
		
		int slashCols = 6;
		int slashRows = 4;
		int slashStart = 12;
		int slashStop = 15;
		float aAnimationSpeed = 0.07f;
		slashing = new Animation[4];
		slashFrames = new TextureRegion[slashRows][slashCols];
		index = 0;
		for(int i = slashStart; i < slashStop + 1; i++){
			for(int j = 0; j < slashCols; j++){
				slashFrames[index][j] = allFrames[i][j];
			}
			slashing[index] = new Animation(aAnimationSpeed, slashFrames[index]);
			index++;
		}
		
		//death
		int deathCols = 6;
		int deathRows = 1;
		int deathStart = 20;
		float dAnimationSpeed = 0.1f;

		deathFrames = new TextureRegion[deathRows][deathCols];
		for(int i = 0; i < deathCols ; i++){
			deathFrames[0][i] = allFrames[deathStart][i];
		}
		death = new Animation(dAnimationSpeed, deathFrames[0]);
		death.setPlayMode(Animation.PlayMode.NORMAL);
	}
	
	/**
	 * Clears up memory from images and stuff
	 */
	public void dispose(){
		characterSheet.dispose();
	}
}
