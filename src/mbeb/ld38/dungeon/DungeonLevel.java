package mbeb.ld38.dungeon;

import mbeb.ld38.dungeon.room.Door;
import mbeb.ld38.dungeon.room.Room;
import mbeb.ld38.dungeon.room.RoomParameter;
import mbeb.ld38.dungeon.room.RoomType;
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
		MazeGrid grid = MazeBuilder.make4Maze(width, height, 0.11f);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				RoomParameter p = new RoomParameter();
				p.set(RoomParameter.Type.RIGHT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.RIGHT));
				p.set(RoomParameter.Type.LEFT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.LEFT));
				p.set(RoomParameter.Type.TOP_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.TOP));
				p.set(RoomParameter.Type.BOTTOM_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.BOTTOM));
				Room r = determineRoomType(null, grid, grid.getTile(x, y)).construct(p);
				float o = determineOffset(x, y);
				if(x > 0) {
					r.registerNeighbour(Door.Direction.LEFT, getRoom(x - 1, y));
				}
				if(y > 0) {
					r.registerNeighbour(Door.Direction.TOP, getRoom(x, y - 1));

				}
				r.setTransformation(new BoneTransformation(new Vector3f(9 * x + o, o, 9 * y + o)));
				this.addSubObject(r);
				this.rooms.put(new Point(x, y), r);
				this.activeRoom = r;
			}
		}
	}

	public Room getRoom(int x, int y) {
		return rooms.get(new Point(x, y));
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
