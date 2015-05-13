package com.imperiled.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObjects;

/**
 * A class that manages the AI of an actor.
 * 
 * @author John Wikman
 * @version 2015.05.12
 */
public class AI {
	
	private static final int IDLE_OPTIONS = 3;
	private long idleTime;
	private long lastTime;
	private int currentIdleOption;
	private Random rand = new Random();
	private Actor actor;
	private PathFinder pathfinder;
	
	/**
	 * Constructor for an AI. Usually created
	 * by the constructor of the actor which
	 * this AI belongs to.
	 * 
	 * @param actor The actor which is managed
	 *              by this AI.
	 */
	public AI(Actor actor) {
		this.actor = actor;
		pathfinder = new PathFinder(actor);
		generateIdleInterval();
		lastTime = System.nanoTime();
		currentIdleOption = rand.nextInt(IDLE_OPTIONS);
	}
	
	/**
	 * Gets the current state of the actor and acts
	 * differently depending on what state it is in.
	 * 
	 * @param collisionObjects The collision objects on the
	 *                         loaded map.
	 * @param player Reference to the player.
	 */
	public void act(MapObjects collisionObjects, Player player) {
		switch(actor.currentState) {
		case IDLE:
			idling();
			break;
		case ATTACKING:
			attacking(collisionObjects, player);
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
	
	/**
	 * If the actor is idling.
	 */
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
		default:
			move();
			break;
		}
	}
	
	/**
	 * If the actor is attacking.
	 */
	private void attacking(MapObjects collisionObjects, Player player) {
		Direction dir = pathfinder.findPath(collisionObjects, player);
		if(dir == null) {
			return;
		}
		actor.currentDirection = dir;
		move();
	}
	
	/**
	 * Generates a random time interval for the idling
	 * method. The interval is used to determine the
	 * time between this idle state and the next. Also
	 * sets a random idle state.
	 */
	private void generateIdleInterval() {
		idleTime = Math.round(Math.random()*1000000000);
		actor.currentDirection = actor.currentDirection.translateInt(rand.nextInt(4));
	}
	
	/**
	 * Moves the actor in the current direction it is facing.
	 */
	private void move() {
		int x = actor.getX();
		int y = actor.getY();
		int dist = Math.round(Gdx.graphics.getDeltaTime() * actor.getSpeed());
		
		switch(actor.currentDirection) {
		case UP:
			y += dist;
			break;
		case DOWN:
			y -= dist;
			break;
		case LEFT:
			x -= dist;
			break;
		default:
			x += dist;
		}
		actor.setPosition(x, y);
	}
}
