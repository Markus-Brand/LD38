package mbeb.mazes;

import java.util.*;

import org.joml.*;

public class MazeBuilder {

	MazeGrid makeMaze(final int mazetype, final int height, final int width, final Vector2i entrancePos) {
		final MazeGrid maze = new MazeGrid(mazetype, height, width);
		final ArrayList<MazeTile> unvisited = maze.getTilesList();

		final Random random = new Random();

		unvisited.remove(maze.getTile(entrancePos));

		while(unvisited.size() > 0) {
			final ArrayList<MazeTile> path = new ArrayList<>();
			MazeTile cell = unvisited.get(random.nextInt(unvisited.size()));
			path.add(cell);

			//find path
			while(unvisited.contains(cell)) {
				final MazeTile[] neighbours = cell.getPossibleNeighbours();
				cell = neighbours[random.nextInt(neighbours.length)];

				final int cellIndex = path.indexOf(cell);
				if (cellIndex == -1) {
					//add new cell
					path.add(cell);
				} else {
					//loop-erase
					for (int i = cellIndex + 1; i < path.size(); i++) {
						path.remove(i);
					}
				}
			}

			//carve Path to Maze
			for (int i = 0; i < path.size() - 1; i++) {
				cell = path.get(i);
				cell.bidirectionalConnectTo(path.get(i + 1));
				unvisited.remove(cell);
			}

		}
		return maze;
	}
}
