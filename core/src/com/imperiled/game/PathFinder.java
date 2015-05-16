package com.imperiled.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Dedicated class for finding the shortest path for
 * any actor.
 * 
 * @author John Wikman
 * @version 2015.05.13
 */
public class PathFinder {

	Vector2 position;
	Actor actor;
	ArrayList<Vector2> visited;
	HashMap<Vector2, Vector2> previous;
	HashMap<Vector2, Integer> distance;
	
	/**
	 * Constructor for PathFinder. Assigns a specific
	 * actor to this pathfinder that is the actor which
	 * is to find a path from.
	 * 
	 * @param actor The actor to find the path from.
	 */
	public PathFinder(Actor actor) {
		this.actor = actor;
		recreateArrays();
	}
	
	/**
	 * Uses a hybrid between BFS and dijkstras algorithm to find
	 * the shortest path between the pathfinders actor and a target
	 * actor.
	 * 
	 * @param collisionObjects Collision objects on the current map.
	 * @param target The target in which to find a path to.
	 */
	public Direction findPath(MapObjects collisionObjects, Actor target) {
		Vector2 originalPos = new Vector2(actor.x, actor.y);
		recreateArrays();
		LinkedList<Vector2> queue = new LinkedList<Vector2>();
		Vector2 end = null;
		queue.add(actor.getPosition());
		distance.put(actor.getPosition(), 0);
		visited.add(actor.getPosition());
		while(queue.size() > 0) { // >>Start of while-loop<<
			// Gets the first candidate in the queue.
			Vector2 pos = queue.removeFirst();
			actor.setPosition(pos);
			Rectangle rect = actor.getRectangle();
			
			// Checks if pos is colliding with the player
			if(Intersector.overlaps(rect, target.getRectangle())) {
				end = pos;
				break;
				// Ends the pathfinder since BFS properies
				// ensures that this will give the shortest
				// route to the target.
			}
			
			// Gets reference to position if it has already been
			// created. Otherwise it creates a new one.
			ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
			Vector2[] nbs = new Vector2[4];
			nbs[0] = new Vector2(pos.x + rect.width, pos.y);
			nbs[1] = new Vector2(pos.x - rect.width, pos.y);
			nbs[2] = new Vector2(pos.x, pos.y + rect.height);
			nbs[3] = new Vector2(pos.x, pos.y - rect.height);
			for(int i = 0; i < 4; i++) {
				for(Vector2 neighbor : visited) {
					if(nbs[i].equals(neighbor)) {
						// This is used later on only to
						// avoid duplicates.
						nbs[i] = neighbor;
						break;
					}
				}
			}
			
			// Checks if the position collides with anything
			// on the map. If it does it is not counted as valid.
			nextneighbor:
			for(int i = 0; i < 4; i++) {
				if(visited.contains(nbs[i])) {
					continue;
				}
				actor.setPosition(nbs[i]);
				if(Math.abs(originalPos.x - actor.x) > actor.aggroRange ||
						Math.abs(originalPos.y - actor.y) > actor.aggroRange) {
					continue;
				}
				Iterator<MapObject> iterCollision = collisionObjects.iterator();
				while(iterCollision.hasNext()){
					RectangleMapObject collRect = (RectangleMapObject) iterCollision.next();
					if(Intersector.overlaps(rect, collRect.getRectangle())){
						continue nextneighbor;
					}
				}
				neighbors.add(nbs[i]);
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
			
		} // >>End of while-loop<<
		
		actor.setPosition(originalPos);
		
		if(end == null || previous.get(end) == null) {
			return null;
		}
		while(!previous.get(end).equals(actor.getPosition())) {
			end = previous.get(end);
		}
		
		float xval = end.x - actor.getPosition().x;
		float yval = end.y - actor.getPosition().y;
		if(yval > 0) {
			return Direction.UP;
		}
		if(yval < 0) {
			return Direction.DOWN;
		}
		if(xval < 0) {
			return Direction.LEFT;
		}
		if(xval > 0) {
			return Direction.RIGHT;
		}
		return null;
	}
	
	/**
	 * Creates the neccesary arrays for the pathfinding.
	 */
	private void recreateArrays() {
		visited = new ArrayList<Vector2>();
		previous = new HashMap<Vector2, Vector2>();
		distance = new HashMap<Vector2, Integer>();
	}
}
