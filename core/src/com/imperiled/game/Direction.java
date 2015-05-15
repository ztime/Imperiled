package com.imperiled.game;
/**
 * Represents the direction an actor is facing
 */
public enum Direction {
	UP,DOWN,RIGHT,LEFT;
	
	private Direction opposite;
	
	static {
		UP.opposite = DOWN;
		DOWN.opposite = UP;
		LEFT.opposite = RIGHT;
		RIGHT.opposite = LEFT;
	}
	/**
	 * Returns the opposite direction of an other direction
	 * @return
	 */
	public Direction getOpposite() {
		return opposite;
	}
	/**
	 * Translates between int representing direction
	 * and the state
	 * 
	 * @param i
	 * @return State 
	 */
	public Direction translateInt(int i) {
		Direction dir;
		switch(i) {
		case 0:
			dir = UP;
			break;
		case 2:
			dir = DOWN;
			break;
		case 1:
			dir = LEFT;
			break;
		default:
			dir = RIGHT;
		}
		return dir;
	}
}
