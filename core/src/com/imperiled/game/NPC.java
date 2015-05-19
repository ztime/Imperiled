package com.imperiled.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * NPC class.
 * 
 * @author John Wikman
 * @version 2015.05.18
 */
public class NPC extends Actor {
	//Places for storing the animation
			protected Animation[] movement; // 0 = up, 1 = left, 2 = down, 3 = left;
			protected TextureRegion[][] movementFrames;
			protected Texture characterSheet;
			protected String spritePath;
			protected HashMap<String, String> npcText;
			
			/**
			 * Creates a new NPC at x & y 
			 * Also sets some default values 
			 * @param x
			 * @param y
			 */
			protected void setData(int x, int y, int health, float speed, float attackingSpeed, Behaviour behaviour , float aggroRange, String spritePath){
				this.health = health;
				this.maxHP = this.health;
				this.invulnerable = true;
				this.speed = speed;
				this.attackingSpeed = attackingSpeed;
				this.ai = new AI(this);
				this.behaviour = Behaviour.PASSIVE;
				this.aggroRange = aggroRange;
				this.spritePath = spritePath;
				this.setPosition(x, y);
				this.loadAnimation();
				this.initX = this.x;
				this.initY = this.y;
			}
			
			/**
			 * Returns a rectangle representing the hitbox 
			 * of the NPC.
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
			 * of the NPC, returns an empty rectangle
			 * if NPC is inactive or set to passive.
			 */
			@Override
			public DamageRectangle getDamageRectangle() {
				DamageRectangle newRect = new DamageRectangle();
				if(!isActive() || !isAlive() || this.behaviour != Behaviour.AGGRESSIVE){
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
			 * Clears the NPC's assets from memory
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
				if(currentState == State.DEAD || currentState == State.INACTIVE || currentState == State.IDLE){
					moving = false;
				}
				return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, moving);
			}
			
			/**
			 * Returns all the texts for this NPC.
			 */
			public HashMap<String, String> getNPCText() {
				return this.npcText;
			}
			
			/**
			 * Load the animations for the NPC!
			 */
			protected void loadAnimation() {
				int sheetRows = 4;
				int sheetCols = 9;
				float wAnimationSpeed = 0.1f;
				characterSheet = new Texture(Gdx.files.internal(this.spritePath));
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
			protected int getHeight(){
				return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving()).getRegionHeight();
			}
			
			/**
			 * Returns the width of the key-frame , only the internal use
			 * not to be used from the outside
			 * @return
			 */
			protected int getWidth(){
				return movement[translateCurrentDirection()].getKeyFrame(elapsedTime, isMoving()).getRegionWidth();
			}
}
