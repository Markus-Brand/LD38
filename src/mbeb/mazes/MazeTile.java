package mbeb.mazes;

import java.util.*;

public class MazeTile {
	int column;
	int row;
	int index;

	ArrayList<MazeTile> connectedDirections;
	MazeGrid maze;

	final Random random;

	public MazeTile(final MazeGrid maze, final int column, final int row) {
		this.maze = maze;
		this.column = column;
		this.row = row;
		this.connectedDirections = new ArrayList<>();
		this.index = 0;
		random = new Random();
	}

	ArrayList<MazeTile> getPossibleNeighbours() {
		return maze.getNeighbours(column, row);
	}

	public void bidirectionalConnectTo(final MazeTile mazeTile) {
		connectTo(mazeTile);
		mazeTile.connectTo(this);
	}

	private void connectTo(final MazeTile mazeTile) {
		connectedDirections.add(mazeTile);
	}

	public boolean isConnectedTo(final int column, final int row) {
		for (final MazeTile cell : connectedDirections) {
			if (cell.row == row && cell.column == column) {
				return true;
			}
		}
		return false;
	}

	public boolean isDeadEnd() {
		return connectedDirections.size() == 1;
	}

	public void addRandomConnection() {
		final ArrayList<MazeTile> possibleConnection = getPossibleNeighbours();
		possibleConnection.removeAll(connectedDirections);
		final int connectionID = random.nextInt(possibleConnection.size());
		bidirectionalConnectTo(possibleConnection.get(connectionID));
	}
}
