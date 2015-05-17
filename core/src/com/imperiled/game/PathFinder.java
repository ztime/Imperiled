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
	HashMap<Vector2, Float> distance;
	
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
		ArrayList<Vector2> prevs = new ArrayList<Vector2>();
		Vector2 end = null;
		queue.add(actor.getPosition());
		distance.put(actor.getPosition(), 0f);
		visited.add(actor.getPosition());
		// >>>>>>Start of while-loop<<<<<<
		while(queue.size() > 0) {
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
			
			// Checks if the position collides with anything
			// on the map. If it does it is not counted as valid.
			nextneighbor:
			for(int i = 0; i < 4; i++) { //START OF NEXTNEIGHBOR
				for(Vector2 neighbor : visited) {
					if(nbs[i].equals(neighbor)) {
						// This is used later on only to
						// avoid duplicates.
						nbs[i] = neighbor;
						break;
					}
				}
				for(Vector2 prev : prevs) {
					if(prev.equals(nbs[i])) {
						continue nextneighbor;
					}
				}
				actor.setPosition(nbs[i]);
				prevs.add(new Vector2(nbs[i]));
				rect = actor.getRectangle();
				
				// If things spiral out of control this resets the actor
				// and returns null.
				if(Math.abs(originalPos.x - actor.x) > actor.aggroRange * 2 ||
						Math.abs(originalPos.y - actor.y) > actor.aggroRange * 2) {
					actor.setPosition(originalPos);
					return null;
				}
				
				Iterator<MapObject> iterCollision = collisionObjects.iterator();
				while(iterCollision.hasNext()){
					RectangleMapObject collRect = (RectangleMapObject) iterCollision.next();
					Rectangle cr = collRect.getRectangle();
					if(Intersector.overlaps(rect, cr)){
						// Ugly-fix:
						// Earlier tests with +2 instead of +6 worked fine
						// everywhere except for the labyrinth. Why +6 works
						// but not +2 or +4 is yet unknown.
						// (Referring to nbs[i].axis increment, not the if-statement)
						if(cr.x < (nbs[i].x + rect.width) && 0 < cr.x - (pos.x + rect.width + 2) && cr.x - (pos.x + rect.width + 2) < rect.width) {
							nbs[i].x = cr.x - (rect.width + 6);
						}
						if((cr.x + cr.width) > nbs[i].x && 0 < pos.x - (cr.x + cr.width + 2) && pos.x - (cr.x + cr.width + 2) < rect.width) {
							nbs[i].x = cr.x + cr.width + 6;
						}
						if(cr.y < (nbs[i].y + rect.height) && 0 < cr.y - (pos.y + rect.height + 2) && cr.y - (pos.y + rect.height + 2) < rect.height) {
							nbs[i].y = cr.y - (rect.height + 6);
						}
						if((cr.y + cr.height) > nbs[i].y && 0 < pos.y - (cr.y + cr.height + 2) && pos.y - (cr.y + cr.height + 2) < rect.height) {
							nbs[i].y = cr.y + cr.height + 6;
						}
						i--;
						continue nextneighbor;
					}
				}
				neighbors.add(nbs[i]);
			} //END OF NEXTNEIGHBOR
			
			// Iterates over the valid neighbors.
			for(Vector2 neighbor : neighbors) {
				float alt = distance.get(pos) + Math.abs(neighbor.x - pos.x) + Math.abs(neighbor.y - pos.y);
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
			
		} // >>>>>>End of while-loop<<<<<<
		
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
		distance = new HashMap<Vector2, Float>();
	}
}
