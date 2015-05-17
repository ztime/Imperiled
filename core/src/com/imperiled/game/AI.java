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
			idling(collisionObjects, player);
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
		default:
			break;
		}
	}
	
	/**
	 * If the actor is idling.
	 */
	private void idling(MapObjects collisionObjects, Player player) {
		checkAggroRange(player);
		if(System.nanoTime() - lastTime > idleTime) {
			currentIdleOption = rand.nextInt(IDLE_OPTIONS);
			generateIdleInterval();
			lastTime = System.nanoTime();
		}
		
		// If an enemy strays too far from its
		// spawn point.
		if(Math.abs(actor.x - actor.initX) > actor.aggroRange ||
				Math.abs(actor.y - actor.initY) > actor.aggroRange) {
			Direction prevDir = actor.currentDirection;
			Actor dummy = new DummyActor(actor.initX, actor.initY);
			actor.currentDirection = pathfinder.findPath(collisionObjects, dummy);
			dummy.dispose();
			currentIdleOption = IDLE_OPTIONS - 1;
			if(actor.currentDirection == null) {
				actor.currentDirection = prevDir;
			}
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
			actor.currentState = State.IDLE;
			return;
		}
		actor.currentDirection = dir;
		move();
		checkAggroRange(player);
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
		float speed = actor.getSpeed();
		if(actor.currentState == State.ATTACKING) {
			speed = actor.getAttackSpeed();
		}
		int dist = Math.round(Gdx.graphics.getDeltaTime() * speed);
		if(dist > actor.getRectangle().width) {
			dist = Math.round(actor.getRectangle().width);
		}
		if(dist > actor.getRectangle().height) {
			dist = Math.round(actor.getRectangle().height);
		}
		
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
	
	/**
	 * Checks the aggro range of the actor. If the player is
	 * within the actors aggro range both for x and y it sets
	 * the actors state to attacking.
	 * 
	 * Only checks aggro range if the actor is set to be of
	 * aggressive behaviour.
	 * 
	 * @param player Reference to the player object.
	 */
	private void checkAggroRange(Player player) {
		if(actor.behaviour == Behaviour.AGGRESSIVE) {
			float xdist = Math.abs(player.getX() - actor.getX());
			float ydist = Math.abs(player.getY() - actor.getY());
			if(ydist <= actor.aggroRange && xdist <= actor.aggroRange) {
				actor.currentState = State.ATTACKING;
				return;
			}
			actor.currentState = State.IDLE;
		}
	}
}
