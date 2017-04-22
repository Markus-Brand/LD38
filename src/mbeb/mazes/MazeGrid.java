package mbeb.mazes;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Datastructure for Mazes
 *
 * @author Merlin
 */
public class MazeGrid {
	private final MazeTile[][] tiles;
	private final int width;
	private final int height;
	private final int mazetype;

	private final MazeTile entrance;
	private MazeTile exit;

	public MazeGrid(final int mazetype, final int width, final int height, final int entranceX, final int entranceY) {
		this.mazetype = mazetype;
		this.width = width;
		this.height = height;
		this.tiles = new MazeTile[width][height];
		for (int column = 0; column < width; column++) {
			for (int row = 0; row < height; row++) {
				this.tiles[column][row] = new MazeTile(this, column, row);
			}
		}
		entrance = tiles[entranceX][entranceY];
	}

	public void determineExit(final int xFurthestAwayTile) {
		final AtomicInteger totalNumTiles = new AtomicInteger(width * height);
		final LinkedList<MazeTile> queue = new LinkedList<>();
		queue.add(entrance);
		final boolean[] visited = new boolean[totalNumTiles.get()];
		visited[entrance.index] = true;
		totalNumTiles.decrementAndGet();
		while(!queue.isEmpty()) {
			final MazeTile currentTile = queue.removeFirst();
			currentTile.connectedTiles.forEach(tile -> {
				if (!visited[tile.index]) {
					visited[tile.index] = true;
					totalNumTiles.decrementAndGet();
					if (totalNumTiles.get() <= xFurthestAwayTile) {
						exit = tile;
						return;
					}
					queue.add(tile);

				}
			});
			if (totalNumTiles.get() < xFurthestAwayTile) {
				//return;
			}
		}
		System.out.println(totalNumTiles.get() + " fasgawgasfg");
	}

	ArrayList<MazeTile> getNeighbours(final int column, final int row) {
		//neighbour logic according to type
		switch(mazetype) {
			case 3:
				return null;
			case 6:
				return null;
			case 4:
			default:
				return getQuadraticNeighbours(column, row);
		}
	}

	private ArrayList<MazeTile> getQuadraticNeighbours(final int column, final int row) {
		final ArrayList<MazeTile> result = new ArrayList<>();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i * j == 0) {
					if (i + j != 0) {
						final MazeTile temp = getTile(column + i, row + j);
						if (temp != null) {
							result.add(temp);
						}
					}
				}
			}
		}
		return result;
	}

	ArrayList<MazeTile> getTilesList() {
		final ArrayList<MazeTile> result = new ArrayList<>();

		for (final MazeTile[] gridTiles : tiles) {
			for (final MazeTile gridTile : gridTiles) {
				result.add(gridTile);
			}
		}

		return result;
	}

	public MazeTile getTile(final int column, final int row) {
		if (column < 0 || column >= width || row < 0 || row >= height) {
			return null;
		}

		return tiles[column][row];
	}

	@Override
	public String toString() {
		String result = "";
		result += makeHorizontalLine();

		for (int row = 0; row < height; row++) {
			result += makeHorizontalRoomLines(row);
			result += makeVerticalRoomLines(row);
		}
		result += "\n";
		/*for (final MazeTile[] mazeLines : tiles) {
			for (final MazeTile tile : mazeLines) {
				result += tile.column + " " + tile.row + " is connected to: ";
				for (final MazeTile connection : tile.connectedDirections) {
					result += "(" + connection.column + " " + connection.row + "),";
				}
				result += "\n";
			}
		}*/
		return result;
	}

	private String makeHorizontalRoomLines(final int row) {
		String result = "|";

		for (int column = 0; column < width; column++) {
			result += makeHorizontalRoomLine(row, column);
		}

		return result + "\n";
	}

	private String makeHorizontalRoomLine(final int row, final int column) {
		final MazeTile currentTile = tiles[column][row];
		String result = "";

		if (entrance == currentTile) {
			result += "e";
		} else if (exit == currentTile) {
			result += "x";
		} else {
			result += tiles[column][row].connectedTiles.size();
		}
		//final String result = tiles[column][row].index + "";
		if (tiles[column][row].isConnectedTo(column + 1, row)) {
			return result + " ";
		} else {
			return result + "|";
		}
	}

	private String makeVerticalRoomLines(final int row) {
		String result = "+";

		for (int column = 0; column < width; column++) {
			result += makeVerticalRoomLine(row, column);
		}

		return result + "\n";
	}

	private String makeVerticalRoomLine(final int row, final int column) {
		final String result = "";
		if (tiles[column][row].isConnectedTo(column, row + 1)) {
			return result + " +";
		} else {
			return result + "-+";
		}
	}

	private String makeHorizontalLine() {
		String result = "+";
		for (int i = 0; i < width; i++) {
			result += "-+";
		}
		return result + "\n";
	}

	public MazeTile[][] getTiles() {
		return tiles;
	}

	public MazeTile getEntrance() {
		return entrance;
	}

	public MazeTile getExit() {
		return exit;
	}
}
