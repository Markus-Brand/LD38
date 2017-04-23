package mbeb.mazes;

import java.util.*;

import mbeb.ld38.dungeon.room.Door;
import mbeb.ld38.dungeon.room.RoomParameter.*;

public class MazeTile {
	int column;
	int row;
	int index;

	ArrayList<MazeTile> connectedTiles;
	MazeGrid maze;

	final Random random;

	public MazeTile(final MazeGrid maze, final int column, final int row) {
		this.maze = maze;
		this.column = column;
		this.row = row;
		this.connectedTiles = new ArrayList<>();
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
		connectedTiles.add(mazeTile);
	}

	public boolean isConnectedTo(final int column, final int row) {
		for (final MazeTile cell : connectedTiles) {
			if (cell.row == row && cell.column == column) {
				return true;
			}
		}
		return false;
	}

	public boolean isDeadEnd() {
		return connectedTiles.size() == 1;
	}

	public void addRandomConnection() {
		final ArrayList<MazeTile> possibleConnection = getPossibleNeighbours();
		possibleConnection.removeAll(connectedTiles);
		final int connectionID = random.nextInt(possibleConnection.size());
		bidirectionalConnectTo(possibleConnection.get(connectionID));
	}

	public boolean hasNeighbour(final Door.Direction direction) {
		return isConnectedTo(this.column + direction.getX(), this.row + direction.getY());
	}
}
