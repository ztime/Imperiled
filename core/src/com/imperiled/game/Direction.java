package com.imperiled.game;

public enum Direction {
	UP,DOWN,RIGHT,LEFT,UP_LEFT,UP_RIGHT,DOWN_LEFT,DOWN_RIGHT;
	
	private Direction opposite;
	
	static {
		UP.opposite = DOWN;
		DOWN.opposite = UP;
		LEFT.opposite = RIGHT;
		RIGHT.opposite = LEFT;
		UP_LEFT.opposite = DOWN_RIGHT;
		UP_RIGHT.opposite = DOWN_LEFT;
		DOWN_LEFT.opposite = UP_RIGHT;
		DOWN_RIGHT.opposite = UP_LEFT;
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
		case 3:
			return RIGHT;
		case 4:
			return UP_LEFT;
		case 5:
			return UP_RIGHT;
		case 6:
			return DOWN_LEFT;
		default: // 7
			return DOWN_RIGHT;
		}
	}
}
