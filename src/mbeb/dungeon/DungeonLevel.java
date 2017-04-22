package mbeb.dungeon;

import mbeb.dungeon.room.Room;
import mbeb.dungeon.room.RoomParameter;
import mbeb.dungeon.room.RoomType;
import mbeb.mazes.MazeBuilder;
import mbeb.mazes.MazeGrid;
import mbeb.mazes.MazeTile;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.scene.SceneObject;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DungeonLevel extends SceneObject {

	public Room getActiveRoom() {
		return activeRoom;
	}

	private class Point {
		int x, y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Point point = (Point) o;

			if (x != point.x) return false;
			return y == point.y;
		}

		@Override
		public int hashCode() {
			int result = x;
			result = 31 * result + y;
			return result;
		}
	}

	private Map<Point, Room> rooms;
	private Room activeRoom;

	public DungeonLevel (int width, int height) {
		super();
		rooms = new HashMap<>();
		this.generate(width, height);
	}

	private void generate(int width, int height) {
		MazeGrid grid = MazeBuilder.make4Maze(width, height, 0.25f);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				RoomParameter p = new RoomParameter();
				for (RoomParameter.Type type : RoomParameter.Type.values()) {
					p.set(type, grid.getTile(x, y).hasNeighbour(type));
				}
				Room r = determineRoomType(null, grid, grid.getTile(x, y)).construct(p);
				float o = determineOffset(x, y);
				r.setTransformation(new BoneTransformation(new Vector3f(9 * x + o, o, 9 * y + o)));
				this.addSubObject(r);
				this.rooms.put(new Point(x, y), r);
				this.activeRoom = r;
			}
		}
	}

	private float determineOffset(int x, int y) {
		return -0.0025f + (0.00125f * ((x % 2) + ((y % 2) * 2)));
	}

	private RoomType determineRoomType(Random r, MazeGrid grid, MazeTile tile) {
		if(tile == grid.getEntrance()) {
			return RoomType.getEntranceRoom();
		}
		if (tile == grid.getExit()){
			return RoomType.getExitRoom();
		}
		return RoomType.getNormalRoom();
	}

}
