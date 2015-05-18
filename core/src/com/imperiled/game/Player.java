package com.imperiled.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


/**
 * Class for the player!
 * 
 * @author jonas wedin
 */
public class Player extends Actor {
	
	//places for saving animations
	private Animation[] walking; 			// 0 = up, 1 = left, 2 = down, 3 = right;
	private Animation[] slashing; 			// same direction as above
	private Animation death;				// death has no direction
	private TextureRegion[][] walkFrames; 	//same order as walking
	private TextureRegion[][] slashFrames;
	private TextureRegion[][] deathFrames;
	private Texture characterSheet;			//the big sheet with animations
	
	//keep track of other stuff
	private ArrayList<Weapon> weapons;
	private Weapon currentWeapon;
	
	/**
	 * Constructs a new player at x and y.
	 * @param x
	 * @param y
	 */
	public Player(int x, int y, int health){
		this.setPosition(x, y);
		weapons = new ArrayList<Weapon>();
		this.loadAnimation();
		this.speed = 200f;
		this.name = "player";
		this.health = PropertyHandler.currentGame.playerHealth;
		this.maxHP = health;
		this.initX = this.x;
		this.initY = this.y;
		//Temporarly add a sword as the only weapon
		Weapon sword = new Sword();
		weapons.add(sword);
		currentWeapon = sword;
		
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
		newRect.height = (3 * this.getHeight()) / 9;
		newRect.width = this.getWidth() / 2;
		return newRect;
	}

	/**
	 * Returns the height of the key frame, only for internal use
	 * not to be used from the outside
	 * @return
	 */
	private int getHeight(){
		return walking[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving()).getRegionHeight();
	}
	
	/**
	 * Returns the width of the key-frame , only the internal use
	 * not to be used from the outside
	 * @return
	 */
	private int getWidth(){
		return walking[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving()).getRegionWidth();
	}
	
	/**
	 * Returns a DamageRectangle based on the current weapon and positioned
	 * around the player depending on the current direction
	 */
	@Override
	public DamageRectangle getDamageRectangle() {
		if(currentWeapon == null || currentState != State.ATTACKING){
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
	 * Returns the current keyframe in
	 * the relevant animation
	 * 
	 * @return
	 */
	public TextureRegion getKeyFrame(){
		if(this.currentState != State.ATTACKING && 
				this.currentState != State.DEAD &&
				this.currentState != State.INACTIVE){
			return walking[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving());
		} else if(this.currentState == State.ATTACKING){
			if(slashing[translateCurrentDirection()].isAnimationFinished(elapsedTimeAttack)){
				this.elapsedTimeAttack = 0;
				this.currentState = State.IDLE;
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
