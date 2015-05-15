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
		switch(i) {
		case 0:
			return UP;
		case 2:
			return DOWN;
		case 1:
			return LEFT;
		default: // 3
			return RIGHT;
		}
	}
}
