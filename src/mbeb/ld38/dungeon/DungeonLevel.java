package mbeb.ld38.dungeon;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import mbeb.opengldefault.light.LightManager;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import mbeb.ld38.dungeon.room.Door;
import mbeb.ld38.dungeon.room.Room;
import mbeb.ld38.dungeon.room.RoomParameter;
import mbeb.ld38.dungeon.room.RoomType;
import mbeb.mazes.MazeBuilder;
import mbeb.mazes.MazeGrid;
import mbeb.mazes.MazeTile;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.IHeightSource;
import mbeb.opengldefault.scene.entities.IEntity;

public class DungeonLevel extends SceneObject implements IHeightSource {

	public Room getActiveRoom() {
		return activeRoom;
	}

	public class Point {
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
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Point point = (Point) o;

			if (x != point.x)
				return false;
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
	private Room entrance;
	private Room exit;
	private IEntity player;

	public DungeonLevel(int width, int height, LightManager manager) {
		super();
		rooms = new HashMap<>();
		this.generate(width, height, manager);
	}

	private void generate(int width, int height, LightManager manager) {
		MazeGrid grid = MazeBuilder.make4Maze(width, height, 0.11f);
		this.addSubObject(new SceneObject(RoomType.getCORNER(), new BoneTransformation(null, new Quaternionf(new AxisAngle4f((float) Math.PI / -2, 0, 1, 0)))));
		for (int x = 0; x < width; x++) {
			float o = determineOffset(x, 0);
			this.addSubObject(new SceneObject(RoomType.getSEGMENT(), new BoneTransformation(new Vector3f(9 * x + o, o, o), new Quaternionf(new AxisAngle4f((float) Math.PI / -2, 0, 1, 0)))));
		}
		for (int y = 0; y < width; y++) {
			float o = determineOffset(0, y);
			this.addSubObject(
					new SceneObject(RoomType.getSEGMENT(), new BoneTransformation(new Vector3f(o, o, 9 * y + o), new Quaternionf(new AxisAngle4f((float) Math.PI, 0, 1, 0)), new Vector3f(-1f, 1, 1))));
		}

		Consumer<Room> closeDoors = room -> {
			if(!room.wasVisited()) {
				room.setVisited();
				room.close();
			}
		};

		Consumer<Room> exitRoom = closeDoors.andThen(room -> {
			room.getSlotObject("exit").setVisible(true);
		});

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				RoomParameter p = new RoomParameter();
				p.set(RoomParameter.Type.RIGHT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.RIGHT));
				p.set(RoomParameter.Type.LEFT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.LEFT));
				p.set(RoomParameter.Type.TOP_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.TOP));
				p.set(RoomParameter.Type.BOTTOM_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.BOTTOM));
				float o = determineOffset(x, y);
				Vector3f pos = new Vector3f(9 * x + o, o, 9 * y + o);
				Room r = determineRoomType(null, grid, grid.getTile(x, y)).construct(p, manager, pos);
				if (x > 0) {
					r.registerNeighbour(Door.Direction.LEFT, getRoom(x - 1, y));
				}
				if (y > 0) {
					r.registerNeighbour(Door.Direction.TOP, getRoom(x, y - 1));

				}
				r.setTransformation(new BoneTransformation(pos));
				Point pt = new Point(x, y);
				r.setPosition(pt);
				this.addSubObject(r);
				this.rooms.put(pt, r);
				if (grid.getTile(x, y) == grid.getEntrance()) {
					this.entrance = r;
				} else if(grid.getTile(x, y) == grid.getExit()) {
					this.exit = r;
					r.setEntryListener(exitRoom);
				} else {
					r.setEntryListener(closeDoors);
				}
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
		if (tile == grid.getEntrance()) {
			return RoomType.getEntranceRoom();
		}
		if (tile == grid.getExit()) {
			return RoomType.getExitRoom();
		}
		return RoomType.getNormalRoom();
	}

	public Room getRoom(Vector2f position) {
		return getRoom(position, false);
	}

	public Room getRoom(Vector2f position, boolean withDoor) {
		return getRoom(position, withDoor, 0.0f);
	}

	public Room getRoom(Vector2f position, float threshold) {
		return getRoom(position, false, threshold);
	}

	public Room getRoom(Vector2f position, boolean withDoor, float threshold) {
		float offset = withDoor ? 4.5f : 4f;
		Vector2f p = position.add(new Vector2f(offset, offset), new Vector2f());
		float x = p.x() % 9;
		float y = p.y() % 9;
		float min = threshold;
		float max = 8f - threshold;
		if (withDoor || (min <= x && x <= max && min <= y && y <= max)) {
			int rx = (int) Math.floor(p.x() / 9);
			int ry = (int) Math.floor(p.y() / 9);
			return getRoom(rx, ry);
		}
		return null;
	}

	public float getHeight(Vector2f position) {
		final float wall_indent = 0.5f;
		final float wall_offset = 0.25f;
		final float top_height = 4.0f;
		final float bottom_height = 1.0f;
		final float room_half_width = 4.5f;

		Room r = getRoom(position, true);

		if (r == null)
			return top_height;

		Vector2f p = position.add(new Vector2f(room_half_width, room_half_width), new Vector2f());

		float x = p.x() % 9;
		float y = p.y() % 9;
		if (x > (wall_offset + wall_indent) && x < (9f - (wall_indent + wall_offset))) {
			if (y > (wall_offset + wall_indent) && y < (9f - (wall_indent + wall_offset))) {
				return bottom_height;
			}
		}
		Vector2f c = new Vector2f(x - room_half_width, y - room_half_width);

		Door.Direction d;
		float v = 0.0f;
		if (Math.abs(c.x()) > Math.abs(c.y())) {
			v = Math.abs(c.y());
			if (c.x() < 0) {
				d = Door.Direction.LEFT;
			} else {
				d = Door.Direction.RIGHT;
			}
		} else {
			v = Math.abs(c.x());
			if (c.y() < 0) {
				d = Door.Direction.TOP;
			} else {
				d = Door.Direction.BOTTOM;
			}
		}

		Door door = r.getDoor(d);
		if (door == null || !door.isPhysicallyOpen())
			return top_height;

		return v <= (1f - wall_offset) ? bottom_height : top_height;

	}

	public IEntity getPlayer() {
		return player;
	}

	public void setPlayer(IEntity player) {
		this.player = player;
		int x = this.entrance.getPosition().x;
		int y = this.entrance.getPosition().y;
		player.setPosition(new Vector3f(9 * x, 1, 9 * y));
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		if (this.getPlayer() != null) {
			Room current;
			if (this.activeRoom != null) {
				current = this.getRoom(new Vector2f(this.getPlayer().getPosition().x(), this.getPlayer().getPosition().z()));
			} else {
				current = this.getRoom(new Vector2f(this.getPlayer().getPosition().x(), this.getPlayer().getPosition().z()), 0.25f);
			}
			if (current != this.activeRoom) {
				if (this.activeRoom != null) {
					this.activeRoom.onExit();
				}
				this.activeRoom = current;
				if (this.activeRoom != null) {
					this.activeRoom.onEntry();
				}
			}
		}
	}
}
