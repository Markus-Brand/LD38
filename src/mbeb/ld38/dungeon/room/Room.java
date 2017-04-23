package mbeb.ld38.dungeon.room;

import mbeb.opengldefault.scene.SceneObject;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Room extends SceneObject {

	private SceneObject baseContainer;
	private SceneObject slotContainer;
	Map<Door.Direction, Door> doors;
	private boolean open = false;

	Room() {
		baseContainer = new SceneObject();
		this.addSubObject(baseContainer);
		slotContainer = new SceneObject();
		this.addSubObject(slotContainer);
		doors = new EnumMap<>(Door.Direction.class);
	}

	void addBaseObjects(List<SceneObject> baseObjects) {
		for (SceneObject baseObject : baseObjects) {
			baseContainer.addSubObject(baseObject);
		}
	}

	void addSlotObject(SceneObject object) {
		slotContainer.addSubObject(object);
		if(object instanceof Door) {
			System.out.println("DOOR");
			this.doors.put(((Door)object).getDirection(), (Door)object);
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
		if(this.doors.containsKey(direction)) {
			this.slotContainer.removeSubObject(this.doors.get(direction));
			this.doors.put(direction, neighbour.getDoor(direction.getOpposite()));
		}
	}

}
