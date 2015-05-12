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
}
