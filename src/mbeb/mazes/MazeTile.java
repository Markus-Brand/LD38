package mbeb.mazes;

import java.util.*;

import org.joml.*;

public class MazeTile {
	Vector2i coordinates;
	ArrayList<MazeTile> connectedDirections;
	MazeGrid maze;

	public MazeTile(final MazeGrid maze, final Vector2i coordinates) {
		this.maze = maze;
		this.coordinates = coordinates;
	}

	MazeTile[] getPossibleNeighbours() {
		return maze.getNeighbours(coordinates);
	}

	public void bidirectionalConnectTo(final MazeTile mazeTile) {
		connectTo(mazeTile);
		mazeTile.connectTo(this);
	}

	private void connectTo(final MazeTile mazeTile) {
		connectedDirections.add(mazeTile);
	}
}
