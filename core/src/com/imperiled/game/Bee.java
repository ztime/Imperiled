package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Bee extends Actor {

	private Animation[] movement; // 0 = up, 1 = left, 2 = down, 3 = left;
	private TextureRegion[][] movementFrames;
	private Texture characterSheet;
	
	public Bee (int x, int y){
		this.setPosition(x, y);
		this.loadAnimation();
		this.health = 100;
	}
	
	
	/**
	 * Gets the current key frame
	 * @return
	 */
	public TextureRegion getKeyFrame(){
		return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, true);
	}

	/**
	 * Returns a rectangle of where the bee is
	 * @return
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
	 * Get a damage rectangle for when the bee attacks
	 * bee attacks all the time
	 * @return
	 */
	@Override
	public DamageRectangle getDamageRectangle() {
		DamageRectangle newRect = new DamageRectangle();
		if(!isActive()){
			return newRect;
		}
		newRect.rectangle.x = this.x;
		newRect.rectangle.y = this.y;
		newRect.rectangle.height = this.getHeight();
		newRect.rectangle.width = this.getWidth();
		newRect.dmg = 20;
		return newRect;
	}


	@Override
	public void dispose() {
		this.characterSheet.dispose();

	}
	
	public void loadAnimation() {
		int sheetRows = 4;
		int sheetCols = 3;
		float wAnimationSpeed = 0.1f;
		characterSheet = new Texture(Gdx.files.internal("sprites/bee.png"));
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
