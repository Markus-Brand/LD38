package mbeb.ld38.dungeon;

import java.lang.Math;
import java.util.*;
import java.util.function.*;

import mbeb.opengldefault.gui.elements.TextGUIElement;
import org.joml.*;

import mbeb.ld38.*;
import mbeb.ld38.dungeon.room.*;
import mbeb.lifeforms.*;
import mbeb.mazes.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.sound.*;

public class DungeonLevel extends SceneObject implements IHeightSource {

	public Room getActiveRoom() {
		return activeRoom;
	}

	public class Point {
		int x, y;

		public Point(final int x, final int y) {
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
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			final Point point = (Point) o;

			if (x != point.x) {
				return false;
			}
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
		private final float[] table;

		public Table(final float ... table) {
			this.table = table;
		}

		public int getValue(final float v) {
			float acc = 0.0f;
			int i;
			for (i = 0; i < table.length && acc <= v; i++) {
				acc += table[i];
			}
			return i - 1;
		}
	}

	private Map<Point, Room> rooms;
	private Room activeRoom;
	private Room entrance;
	private Room exit;
	private PlayerEntity player;
	private Table enemySpawns;
	private final LightManager manager;
	private final TextGUIElement infoBox;
	private final Goblin enemy;
	private final Chest chest;
	private final HealthBarGUI gui;
	private final Camera camera;
	private final SoundEnvironment soundEnvironment;
	private final EnumMap<LootType, Table> lootSpawns;
	private Consumer<DungeonLevel> finishListener;

	public DungeonLevel(final LightManager manager, final Goblin enemy, final HealthBarGUI gui, final Camera camera,
			final Chest chest, final TextGUIElement infoBox, final SoundEnvironment soundEnvironment) {

		super();
		this.manager = manager;
		this.enemy = enemy;
		this.gui = gui;
		this.soundEnvironment = soundEnvironment;
		this.camera = camera;
		this.chest = chest;
		this.lootSpawns = new EnumMap<>(LootType.class);
		this.infoBox = infoBox;
		for (final LootType lootType : LootType.values()) {
			this.setLootSpawn(lootType, 1.0f);
		}
		this.enemySpawns = new Table(0.25f, 0.5f, 0.25f);
	}

	public void generate(final int width, final int height) {
		rooms = new HashMap<>();
		final MazeGrid grid = MazeBuilder.make4Maze(width, height, 0.11f);
		this.addSubObject(new SceneObject(RoomType.getCORNER(), new BoneTransformation(null, new Quaternionf(
				new AxisAngle4f((float) Math.PI / -2, 0, 1, 0)))));
		for (int x = 0; x < width; x++) {
			final float o = determineOffset(x, 0);
			this.addSubObject(new SceneObject(RoomType.getSEGMENT(), new BoneTransformation(new Vector3f(9 * x + o, o,
					o), new Quaternionf(new AxisAngle4f((float) Math.PI / -2, 0, 1, 0)))));
		}
		for (int y = 0; y < height; y++) {
			final float o = determineOffset(0, y);
			this.addSubObject(
					new SceneObject(RoomType.getSEGMENT(), new BoneTransformation(new Vector3f(o, o, 9 * y + o),
							new Quaternionf(new AxisAngle4f((float) Math.PI, 0, 1, 0)), new Vector3f(-1f, 1, 1))));
		}

		final Function<Room, Boolean> markVisited = room -> {
			final boolean visited = room.wasVisited();
			if (!visited) {
				room.setVisited();
			}
			return !visited;
		};

		final Random random = new Random();

		final Consumer<Room> normalRoom =
				room -> {
					if (markVisited.apply(room)) {
						final int count = this.getEnemySpawns().getValue(random.nextFloat());
						for (int i = 0; i < count; i++) {
							final MonsterEntity e =
									enemy.spawnNew(new Vector3f(random.nextFloat() * 6 - 3, 1,
											random.nextFloat() * 6 - 3), 0.0f, room, gui, soundEnvironment);
							e.showHealthBar(camera);
							player.addTarsched(e);
							room.getEnemies().add(e);
							e.setDeathListener(lifeformEntity -> {
								room.getEnemies().remove(e);
								if (room.getEnemies().isEmpty()) {
									room.setChest(chest
											.spawnNew(
													new Vector3f(0.0f, 0.0f, 0.0f),
													0.0f,
													room,
													chestEntity -> {
														final EnumMap<LootType, Integer> counts =
																new EnumMap<>(LootType.class);
														for (final LootType lootType : LootType.values()) {
															final int itemCount =
																	this.getLootSpawn(lootType).getValue(
																			random.nextFloat());
															if (itemCount > 0) {
																counts.put(lootType, itemCount);
																player.getInventory().addLoot(lootType, itemCount);
															}
														}
														StringBuilder b = new StringBuilder("You found ");
														if (counts.isEmpty()) {
															b.append("nothing");
														} else {
															int rem = counts.size();
															boolean first = true;
															for (Map.Entry<LootType, Integer> entry : counts.entrySet()) {
																if (first) {
																	first = false;
																} else if (rem == 1) {
																	b.append(" and ");
																} else {
																	b.append(", ");
																}
																rem--;
																b.append(entry.getValue());
																b.append(' ');
																b.append(entry.getKey().toString());
															}
														}
														b.append('.');
														infoBox.setText(b.toString());
													}, soundEnvironment));
									room.open();
								}
							});
						}
						if (count > 0) {
							room.close();
						}
					}
				};

		final Consumer<Room> exitRoom = room -> {
			if (markVisited.apply(room)) {
				room.getSlotObject("exit").setVisible(true);
			}
		};

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				final RoomParameter p = new RoomParameter();
				p.set(RoomParameter.Type.RIGHT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.RIGHT));
				p.set(RoomParameter.Type.LEFT_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.LEFT));
				p.set(RoomParameter.Type.TOP_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.TOP));
				p.set(RoomParameter.Type.BOTTOM_NEIGHBOUR, grid.getTile(x, y).hasNeighbour(Door.Direction.BOTTOM));
				final float o = determineOffset(x, y);
				final Vector3f pos = new Vector3f(9 * x + o, o, 9 * y + o);
				final Room room =
						determineRoomType(null, grid, grid.getTile(x, y)).construct(p, manager, pos, soundEnvironment);
				if (x > 0) {
					room.registerNeighbour(Door.Direction.LEFT, getRoom(x - 1, y));
				}
				if (y > 0) {
					room.registerNeighbour(Door.Direction.TOP, getRoom(x, y - 1));

				}
				room.setTransformation(new BoneTransformation(pos));
				final Point pt = new Point(x, y);
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

	public void setEnemySpawns(final float ... spawns) {
		this.enemySpawns = new Table(spawns);
	}

	public Table getLootSpawn(final LootType key) {
		return lootSpawns.get(key);
	}

	public Table setLootSpawn(final LootType key, final float ... spawns) {
		return lootSpawns.put(key, new Table(spawns));
	}

	public Room getRoom(final int x, final int y) {
		return rooms.get(new Point(x, y));
	}

	private float determineOffset(final int x, final int y) {
		return -0.0025f + 0.00125f * (x % 2 + y % 2 * 2);
	}

	private RoomType determineRoomType(final Random r, final MazeGrid grid, final MazeTile tile) {
		if (tile == grid.getEntrance()) {
			return RoomType.getEntranceRoom();
		}
		if (tile == grid.getExit()) {
			return RoomType.getExitRoom();
		}
		return RoomType.getNormalRoom();
	}

	public Room getRoom(final Vector2f position) {
		return getRoom(position, false);
	}

	public Room getRoom(final Vector2f position, final boolean withDoor) {
		return getRoom(position, withDoor, 0.0f);
	}

	public Room getRoom(final Vector2f position, final float threshold) {
		return getRoom(position, false, threshold);
	}

	public Room getRoom(final Vector2f position, final boolean withDoor, final float threshold) {
		final float offset = withDoor ? 4.5f : 4f;
		final Vector2f p = position.add(new Vector2f(offset, offset), new Vector2f());
		final float x = p.x() % 9;
		final float y = p.y() % 9;
		final float min = threshold;
		final float max = 8f - threshold;
		if (withDoor || min <= x && x <= max && min <= y && y <= max) {
			final int rx = (int) Math.floor(p.x() / 9);
			final int ry = (int) Math.floor(p.y() / 9);
			return getRoom(rx, ry);
		}
		return null;
	}

	@Override
	public float getHeight(final Vector2f position) {
		final float wall_indent = 0.5f;
		final float wall_offset = 0.25f;
		final float top_height = 4.0f;
		final float bottom_height = 1.0f;
		final float room_half_width = 4.5f;

		final Room r = getRoom(position, true);

		if (r == null) {
			return top_height;
		}

		final Vector2f p = position.add(new Vector2f(room_half_width, room_half_width), new Vector2f());

		final float x = p.x() % 9;
		final float y = p.y() % 9;
		if (x > wall_offset + wall_indent && x < 9f - (wall_indent + wall_offset)) {
			if (y > wall_offset + wall_indent && y < 9f - (wall_indent + wall_offset)) {
				return bottom_height;
			}
		}
		final Vector2f c = new Vector2f(x - room_half_width, y - room_half_width);

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

		final Door door = r.getDoor(d);
		if (door == null || !door.isPhysicallyOpen()) {
			return top_height;
		}

		return v <= 1f - wall_offset ? bottom_height : top_height;

	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public void setPlayer(final PlayerEntity player) {
		this.player = player;
		player.setHeightSource(this);
		final int x = this.entrance.getPosition().x;
		final int y = this.entrance.getPosition().y;
		player.setPosition(new Vector3f(9 * x, 1, 9 * y));
	}

	public Consumer<DungeonLevel> getFinishListener() {
		return finishListener;
	}

	public void setFinishListener(Consumer<DungeonLevel> finishListener) {
		this.finishListener = finishListener;
	}

	@Override
	public void update(final double deltaTime) {
		super.update(deltaTime);
		if (this.getPlayer() != null) {
			Room current;
			if (this.activeRoom != null) {
				current =
						this.getRoom(new Vector2f(this.getPlayer().getPosition().x(), this.getPlayer().getPosition()
								.z()));
			} else {
				current =
						this.getRoom(new Vector2f(this.getPlayer().getPosition().x(), this.getPlayer().getPosition()
								.z()), 0.25f);
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
			if (this.activeRoom == this.exit) {
				float rx = this.getPlayer().getPosition().x() - (this.activeRoom.getPosition().getX() * 9);
				float ry = this.getPlayer().getPosition().z() - (this.activeRoom.getPosition().getY() * 9);
				if (((rx * rx) + (ry * ry)) < 2.0f){
					if(this.finishListener != null){
						this.finishListener.accept(this);
					}
				}
			}
		}
	}
}
