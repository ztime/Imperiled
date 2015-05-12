package com.imperiled.game;

public enum Direction {
	UP,DOWN,RIGHT,LEFT;
	
	private Direction opposite;
	
	static {
		UP.opposite = DOWN;
		DOWN.opposite = UP;
		LEFT.opposite = RIGHT;
		RIGHT.opposite = LEFT;
	}
	
	public Direction getOpposite() {
		return opposite;
	}
	
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
