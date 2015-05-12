package com.imperiled.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

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
	//private Actor player = PropertyHandler.currentActors.get("player");
	private Actor actor;
	
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
		generateIdleInterval();
		lastTime = System.nanoTime();
		currentIdleOption = rand.nextInt(IDLE_OPTIONS);
	}
	
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
			idleMove();
			break;
		}
	}
	
	/**
	 * If the actor is attacking.
	 */
	private void attacking(MapObjects collisionObjects, Player player) {
		Vector2 originalPos = new Vector2(actor.x, actor.y);
		//rect.height;
		//rect.width;
		ArrayList<Vector2> ends = new ArrayList<Vector2>();
		ArrayList<Vector2> visited = new ArrayList<Vector2>();
		HashMap<Vector2, Vector2> previous = new HashMap<Vector2, Vector2>();
		HashMap<Vector2, Integer> distance = new HashMap<Vector2, Integer>();
		LinkedList<Vector2> queue = new LinkedList<Vector2>();
		queue.add(actor.getPosition());
		distance.put(actor.getPosition(), 0);
		visited.add(actor.getPosition());
		while(queue.size() > 0) { // Start of while-loop
			
			// Gets the first candidate in the queue.
			Vector2 pos = queue.removeFirst();
			actor.setPosition(pos);
			Rectangle rect = actor.getRectangle();
			
			// Checks if pos is colliding with the player
			if(Intersector.overlaps(rect, player.getRectangle())) {
				if(!ends.contains(pos)) {
					ends.add(pos);
				}
			}
			
			// Checks for valid neighbors.
			ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
			Vector2[] nbs = new Vector2[4];
			nbs[0] = new Vector2(pos.x + rect.width, pos.y);
			nbs[1] = new Vector2(pos.x - rect.width, pos.y);
			nbs[2] = new Vector2(pos.x, pos.y + rect.height);
			nbs[3] = new Vector2(pos.x, pos.y - rect.height);
			for(int i = 0; i < 4; i++) {
				for(Vector2 neighbor : visited) {
					if(nbs[i].equals(neighbor)) {
						nbs[i] = neighbor;
						break;
					}
				}
			}
			
			nextneighbor:
			for(Vector2 nb : nbs) {
				actor.setPosition(nb);
				rect = actor.getRectangle();
				Iterator<MapObject> iterCollision = collisionObjects.iterator();
				while(iterCollision.hasNext()){
					RectangleMapObject collRect = (RectangleMapObject) iterCollision.next();
					if(Intersector.overlaps(rect, collRect.getRectangle())){
						continue nextneighbor;
					}
				}
				neighbors.add(nb);
			}
			// Iterates over the valid neighbors.
			for(Vector2 neighbor : neighbors) {
				int alt = distance.get(pos) + 1;
				if(distance.get(neighbor) == null || alt < distance.get(neighbor)) {
					distance.put(neighbor, alt);
					previous.put(neighbor, pos);
				}
				// Adds the neighbor to the queue
				// if it has not been visited before.
				if(!visited.contains(neighbor)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		} // End of while-loop
		actor.setPosition(originalPos);
		
		// Checks the found position to move to.
		Vector2 shortestRoute = null;
		for(Vector2 pLoc : ends) {
			if(shortestRoute == null || distance.get(pLoc) < distance.get(shortestRoute)) {
				shortestRoute = pLoc;
			}
		}
		if(shortestRoute == null) {
			return;
		}
		while(!previous.get(shortestRoute).equals(actor.getPosition())) {
			shortestRoute = previous.get(shortestRoute);
		}
		float xval = shortestRoute.x - actor.getPosition().x;
		float yval = shortestRoute.y - actor.getPosition().y;
		if(yval > 0) {
			actor.currentDirection = Direction.UP;
		} else if(yval < 0) {
			actor.currentDirection = Direction.DOWN;
		} else if(xval < 0) {
			actor.currentDirection = Direction.LEFT;
		} else if(xval > 0) {
			actor.currentDirection = Direction.RIGHT;
		}
		idleMove();
	}
	
	private void generateIdleInterval() {
		idleTime = Math.round(Math.random()*1000000000);
		actor.currentDirection = actor.currentDirection.translateInt(rand.nextInt(4));
	}
	
	private void idleMove() {
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
