package mbeb.mazes;

public class MazeGame {

	public static void main(final String[] args) {
		System.out.println("start");
		final MazeGrid maze = MazeBuilder.makeMaze(4, 35, 14, 0, 0, 0.25f);
		System.out.println("end");
		System.out.println(maze.toString());

	}
}
