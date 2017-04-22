package mbeb.mazes;

import java.util.*;

public class MazeBuilder {

	static MazeGrid makeMaze(final int mazetype, final int width, final int height, final int entranceX, final int entranceY, final float resolveDeadendProbability) {
		final MazeGrid maze = new MazeGrid(mazetype, width, height, entranceX, entranceY);
		final ArrayList<MazeTile> unvisited = maze.getTilesList();

		final Random random = new Random();
		int id = 1;
		unvisited.remove(maze.getTile(entranceX, entranceY));

		while(unvisited.size() > 0) {
			final ArrayList<MazeTile> path = new ArrayList<>();
			MazeTile cell = unvisited.get(random.nextInt(unvisited.size()));
			path.add(cell);

			//find path
			while(unvisited.contains(cell)) {
				final ArrayList<MazeTile> neighbours = cell.getPossibleNeighbours();
				cell = neighbours.get(random.nextInt(neighbours.size()));

				final int cellIndex = path.indexOf(cell);
				if (cellIndex == -1) {
					//add new cell
					path.add(cell);
				} else {
					//loop-erase
					for (int i = cellIndex + 1; i < path.size(); i++) {
						path.remove(i);
						i--;
					}
				}
			}
			//carve Path to Maze
			for (int i = 0; i < path.size() - 1; i++) {
				cell = path.get(i);
				if (cell.index == 0) {
					cell.index = id++;
				}
				System.out.println("Added connection:" + cell.index + " to " + path.get(i + 1).index);
				cell.bidirectionalConnectTo(path.get(i + 1));
				unvisited.remove(cell);
			}
		}

		for (final MazeTile[] mazeLines : maze.getTiles()) {
			for (final MazeTile tile : mazeLines) {
				if (tile.isDeadEnd()) {
					if (random.nextFloat() < resolveDeadendProbability) {
						tile.addRandomConnection();
					}
				}
			}
		}

		maze.determineExit(width * height / 4);

		return maze;
	}
}
