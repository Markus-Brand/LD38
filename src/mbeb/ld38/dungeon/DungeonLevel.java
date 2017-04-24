package mbeb.ld38.dungeon;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import mbeb.lifeforms.LootType;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import mbeb.ld38.HealthBarGUI;
import mbeb.ld38.dungeon.room.Door;
import mbeb.ld38.dungeon.room.Room;
import mbeb.ld38.dungeon.room.RoomParameter;
import mbeb.ld38.dungeon.room.RoomType;
import mbeb.lifeforms.Goblin;
import mbeb.lifeforms.MonsterEntity;
import mbeb.lifeforms.PlayerEntity;
import mbeb.mazes.MazeBuilder;
import mbeb.mazes.MazeGrid;
import mbeb.mazes.MazeTile;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.light.LightManager;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.IHeightSource;
import sun.tools.jconsole.Tab;

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

	public class Table {
		private float[] table;

		public Table(float... table) {
			this.table = table;
		}

		public int getValue(float v) {
			float acc = 0.0f;
			int i;
			for (i = 0; i < table.length && acc <= v; i++) {
				acc += table[i];
			}
			return i-1;
		}
	}

	private Map<Point, Room> rooms;
	private Room activeRoom;
	private Room entrance;
	private Room exit;
	private PlayerEntity player;
	private Table enemySpawns;
	private LightManager manager;
	private Goblin enemy;
	private HealthBarGUI gui;
	private Camera camera;
	private EnumMap<LootType, Table> lootSpawns;

	public DungeonLevel(LightManager manager, Goblin enemy, HealthBarGUI gui, Camera camera) {
		super();
		this.manager = manager;
		this.enemy = enemy;
		this.gui = gui;
		this.camera = camera;
		this.lootSpawns = new EnumMap<>(LootType.class);
		this.enemySpawns = new Table(0.25f, 0.5f, 0.25f);
	}

	public void generate(int width, int height) {
		rooms = new HashMap<>();
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

		Function<Room, Boolean> markVisited = room -> {
			boolean visited = room.wasVisited();
			if (!visited) {
				room.setVisited();
			}
			return !visited;
		};

		Random random = new Random();

		Consumer<Room> normalRoom = room -> {
			if (markVisited.apply(room)) {
				int count = this.getEnemySpawns().getValue(random.nextFloat());
				for (int i = 0; i < count; i++) {
					MonsterEntity e = enemy.spawnNew(new Vector3f(random.nextFloat() * 6 - 3, 1, random.nextFloat() * 6 - 3), 0.0f, room, gui);
					e.showHealthBar(camera);
					player.addTarsched(e);
					room.getEnemies().add(e);
					e.setDeathListener(lifeformEntity -> {
						room.getEnemies().remove(e);
						if (room.getEnemies().isEmpty()) {
							room.open();
						}
					});
				}
				if (count > 0) {
					room.close();
				}
			}
		};

		Consumer<Room> exitRoom = room -> {
			if (markVisited.apply(room)) {
				room.getSlotObject("exit").setVisible(true);
			}
		};

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				RoomParameter p = new RoomParameter();
				p.set(RoomParameter.Type.RIGHT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.RIGHT));
				p.set(RoomParameter.Type.LEFT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.LEFT));
				p.set(RoomParameter.Type.TOP_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.TOP));
				p.set(RoomParameter.Type.BOTTOM_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.BOTTOM));
				float o = determineOffset(x, y);
				Vector3f pos = new Vector3f(9 * x + o, o, 9 * y + o);
				Room room = determineRoomType(null, grid, grid.getTile(x, y)).construct(p, manager, pos);
				if (x > 0) {
					room.registerNeighbour(Door.Direction.LEFT, getRoom(x - 1, y));
				}
				if (y > 0) {
					room.registerNeighbour(Door.Direction.TOP, getRoom(x, y - 1));

				}
				room.setTransformation(new BoneTransformation(pos));
				Point pt = new Point(x, y);
				room.setPosition(pt);
				this.addSubObject(room);
				this.rooms.put(pt, room);
				if (grid.getTile(x, y) == grid.getEntrance()) {
					this.entrance = room;
				} else if (grid.getTile(x, y) == grid.getExit()) {
					this.exit = room;
					room.setEntryListener(exitRoom);
				} else {
					room.setEntryListener(normalRoom);
				}
			}
		}
	}

	public Table getEnemySpawns() {
		return enemySpawns;
	}

	public void setEnemySpawns(float... spawns) {
		this.enemySpawns = new Table(spawns);
	}

	public Table getLootSpawn(LootType key) {
		return lootSpawns.get(key);
	}

	public Table setLootSpawn(LootType key, float... spawns) {
		return lootSpawns.put(key, new Table(spawns));
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

	public PlayerEntity getPlayer() {
		return player;
	}



	public void setPlayer(PlayerEntity player) {
		this.player = player;
		player.setHeightSource(this);
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
