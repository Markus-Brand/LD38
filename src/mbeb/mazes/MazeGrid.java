package mbeb.mazes;

import java.util.*;

import org.joml.*;

/**
 * Datastructure for Mazes
 *
 * @author Merlin
 */
public class MazeGrid {
	private final MazeTile[][] tiles;
	private final int height;
	private final int width;
	private final int mazetype;

	public MazeGrid(final int mazetype, final int height, final int width) {
		this.mazetype = mazetype;
		this.height = height;
		this.width = width;
		this.tiles = new MazeTile[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				this.tiles[i][j] = new MazeTile(this, new Vector2i(i, j));
			}
		}
	}

	ArrayList<MazeTile> getNeighbours(final Vector2i coordinates) {
		//neighbour logic according to type
		final ArrayList<MazeTile> result = new ArrayList<>();
		for(final int)
		final int neighbourcount = 0;
		final MazeTile[] result = new MazeTile[neighbourcount];
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

	public MazeTile getTile(final Vector2i entrancePos) {
		if (entrancePos.x < 0 || entrancePos.x >= height || entrancePos.y < 0 || entrancePos.y >= width) {
			return null;
		}

		return tiles[entrancePos.x][entrancePos.y];
	}
}
