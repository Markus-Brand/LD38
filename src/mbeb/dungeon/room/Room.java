package mbeb.dungeon.room;

import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.sound.SoundEnvironment;

import java.util.LinkedList;
import java.util.List;

public class Room extends SceneObject {

	SceneObject baseContainer;
	SceneObject slotContainer;
	List<Door> doors;
	private boolean open = false;

	Room() {
		baseContainer = new SceneObject();
		this.addSubObject(baseContainer);
		slotContainer = new SceneObject();
		this.addSubObject(slotContainer);
		doors = new LinkedList<>();
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
			this.doors.add((Door)object);
		}
	}

	public void open() {
		for (Door door : doors) {
			door.open();
		}
		open = true;
	}

	public void close() {
		for (Door door : doors) {
			door.close();
		}
		open = false;
	}

	public void forceOpen() {
		for (Door door : doors) {
			door.forceOpen();
		}
		open = true;
	}

	public void forceClose() {
		for (Door door : doors) {
			door.forceClose();
		}
		open = false;
	}

	public boolean isOpen() {
		return open;
	}
}
