package mbeb.ld38.dungeon.room;

import java.util.*;
import java.util.function.Consumer;

import mbeb.ld38.dungeon.DungeonLevel;
import mbeb.lifeforms.MonsterEntity;
import mbeb.opengldefault.scene.SceneObject;

public class Room extends SceneObject {

	private SceneObject baseContainer;
	private SceneObject slotContainer;
	private Map<Door.Direction, Door> doors;
	private Map<String, SceneObject> slotObjects;
	private boolean visited = false;
	private Collection<MonsterEntity> enemies;

	private DungeonLevel.Point position;
	private boolean open = true;
	private Consumer<Room> entryListener;
	private Consumer<Room> exitListener;

	Room() {
		baseContainer = new SceneObject();
		this.addSubObject(baseContainer);
		slotContainer = new SceneObject();
		this.addSubObject(slotContainer);
		doors = new EnumMap<>(Door.Direction.class);
		slotObjects = new HashMap<>();
		enemies = new HashSet<>();
	}

	void addBaseObjects(List<SceneObject> baseObjects) {
		for (SceneObject baseObject : baseObjects) {
			baseContainer.addSubObject(baseObject);
		}
	}

	void addSlotObject(String key, SceneObject object) {
		slotContainer.addSubObject(object);
		this.slotObjects.put(key, object);
		if (object instanceof Door) {
			this.doors.put(((Door) object).getDirection(), (Door) object);
		}
	}

	public void open() {
		doors.values().forEach(Door::open);
		open = true;
	}

	public void close() {
		doors.values().forEach(Door::close);
		open = false;
	}

	public void forceOpen() {
		doors.values().forEach(Door::forceOpen);
		open = true;
	}

	public void forceClose() {
		doors.values().forEach(Door::forceClose);
		open = false;
	}

	public boolean isOpen() {
		return open;
	}

	public Door getDoor(Door.Direction key) {
		return doors.get(key);
	}

	public void registerNeighbour(Door.Direction direction, Room neighbour) {
		if (this.doors.containsKey(direction)) {
			this.slotContainer.removeSubObject(this.doors.get(direction));
			this.doors.put(direction, neighbour.getDoor(direction.getOpposite()));
		}
	}

	public void setEntryListener(Consumer<Room> entryListener) {
		this.entryListener = entryListener;
	}

	public void setExitListener(Consumer<Room> exitListener) {
		this.exitListener = exitListener;
	}

	public void onEntry() {
		if (this.entryListener != null) {
			this.entryListener.accept(this);
		}
	}

	public void onExit() {
		if (this.exitListener != null) {
			this.exitListener.accept(this);
		}
	}

	public DungeonLevel.Point getPosition() {
		return position;
	}

	public void setPosition(DungeonLevel.Point position) {
		this.position = position;
	}

	public boolean wasVisited() {
		return visited;
	}

	public void setVisited() {
		this.visited = true;
	}

	public Collection<MonsterEntity> getEnemies() {
		return enemies;
	}

	public SceneObject getSlotObject(String key) {
		return slotObjects.get(key);
	}
}
