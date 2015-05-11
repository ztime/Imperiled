package com.imperiled.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Bee extends Actor {

	private Animation[] movement; // 0 = up, 1 = left, 2 = down, 3 = left;
	private TextureRegion[][] movementFrames;
	private Texture characterSheet;
	
	//keep current state and direction
	private State currentState = State.IDLE;
	private Direction currentDirection = Direction.UP;
	
	public Bee (int x, int y){
		this.setPosition(x, y);
		this.loadAnimation();
		this.health = 100;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.draw(this.getKeyFrame(), x, y);
	}
	
	/**
	 * 
	 * @return
	 */
	private TextureRegion getKeyFrame(){
		return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, true);
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
	
	private boolean isMoving(){
		return (currentState == State.MOVE);
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

}
