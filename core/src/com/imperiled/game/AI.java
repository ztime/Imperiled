package com.imperiled.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;

public class AI {
	
	private static final int IDLE_OPTIONS = 2;
	private long idleTime;
	private long lastTime;
	private int currentIdleOption;
	private Random rand = new Random();
	//private Actor player = PropertyHandler.currentActors.get("player");
	private Actor actor;
	
	public AI(Actor actor) {
		this.actor = actor;
		generateIdleInterval();
		lastTime = System.nanoTime();
		currentIdleOption = rand.nextInt(IDLE_OPTIONS);
	}
	
	public void act() {
		switch(actor.currentState) {
		case IDLE:
			idling();
			break;
		case ATTACKING:
			//
			break;
		case DEAD:
			//
			break;
		case MOVE:
			//
			break;
		case INACTIVE:
			//
			break;
		}
	}
	
	private void idling() {
		if(System.nanoTime() - lastTime > idleTime) {
			currentIdleOption = rand.nextInt(IDLE_OPTIONS);
			generateIdleInterval();
			lastTime = System.nanoTime();
		}
		switch(currentIdleOption) {
		case 0:
			//do nothing
			break;
		case 1:
			moveActor();
			break;
		}
	}
	
	private void generateIdleInterval() {
		idleTime = Math.round(Math.random()*2000000000);
		actor.currentDirection = actor.currentDirection.translateInt(rand.nextInt(4));
	}
	
	private void moveActor() {
		int x = actor.getX();
		int y = actor.getY();
		
		switch(actor.currentDirection) {
		case UP:
			y += Gdx.graphics.getDeltaTime() * actor.getSpeed();
			break;
		case DOWN:
			y -= Gdx.graphics.getDeltaTime() * actor.getSpeed();
			break;
		case LEFT:
			x -= Gdx.graphics.getDeltaTime() * actor.getSpeed();
			break;
		default:
			x += Gdx.graphics.getDeltaTime() * actor.getSpeed();
		}
		
		actor.setPosition(x, y);
	}
}
