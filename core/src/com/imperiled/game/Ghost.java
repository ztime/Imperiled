package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Ghost extends Actor {

	//Places for storing the animation
	private Animation[] movement; // 0 = up, 1 = left, 2 = down, 3 = left;
	private TextureRegion[][] movementFrames;
	private Texture characterSheet;
	
	/**
	 * Creates a new ghost at x & y 
	 * Also sets some default values 
	 * @param x
	 * @param y
	 */
	public Ghost (int x, int y){
		this.setPosition(x, y);
		this.loadAnimation();
		this.health = 100;
		this.maxHP = this.health;
		this.speed = 70f;
		this.attackingSpeed = 100f;
		this.ai = new AI(this);
		this.behaviour = Behaviour.AGGRESSIVE;
		this.aggroRange = 140f;
	}
	
	/**
	 * Returns a rectangle representing the hitbox 
	 * of the ghost
	 */
	@Override
	public Rectangle getRectangle() {
		if(!this.isActive()){
			return new Rectangle();
		}
		Rectangle newRect = new Rectangle();
		newRect.x = this.x + this.getWidth() / 4;
		newRect.y = this.y;
		newRect.height = (3 * this.getHeight()) / 4;
		newRect.width = this.getWidth() / 2;
		return newRect;
	}

	/**
	 * Returs a damage rectangle representing an attack
	 * of the ghost, returns an empty rectangle
	 * if ghost is inactive
	 */
	@Override
	public DamageRectangle getDamageRectangle() {
		DamageRectangle newRect = new DamageRectangle();
		if(!isActive()){
			return newRect;
		}
		newRect.rectangle = this.getRectangle();
		newRect.rectangle.x -= 5;
		newRect.rectangle.y -= 5;
		newRect.rectangle.width += 10;
		newRect.rectangle.height += 10;
		newRect.dmg = 1;
		return newRect;
	}

	/**
	 * Clears the ghosts assets from memory
	 */
	@Override
	public void dispose() {
		this.characterSheet.dispose();

	}
	
	/**
	 * Returns the current keyframe in the animation
	 * with regards to the current state and direction
	 * @return TextureRegion current keyframe
	 */
	@Override
	public TextureRegion getKeyFrame() {
		boolean moving = true;
		if(currentState == State.DEAD || currentState == State.INACTIVE){
			moving = false;
		}
		return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, moving);
	}
	
	/**
	 * Load the animations for the ghost!
	 */
	public void loadAnimation() {
		int sheetRows = 4;
		int sheetCols = 3;
		float wAnimationSpeed = 0.1f;
		characterSheet = new Texture(Gdx.files.internal("sprites/ghost.png"));
		movement = new Animation[4];
		movementFrames = TextureRegion.split(characterSheet, characterSheet.getWidth() / sheetCols, characterSheet.getHeight() / sheetRows);
		for(int i = 0; i < sheetRows; i++){
			movement[i] = new Animation(wAnimationSpeed, movementFrames[i]);
		}
	}
	
	/**
	 * Returns the height of the key frame, only for internal use
	 * not to be used from the outside
	 * @return
	 */
	private int getHeight(){
		return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving()).getRegionHeight();
	}
	
	/**
	 * Returns the width of the key-frame , only the internal use
	 * not to be used from the outside
	 * @return
	 */
	private int getWidth(){
		return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving()).getRegionWidth();
	}


}
