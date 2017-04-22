package mbeb.dungeon.room;

import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.sound.SoundEnvironment;

import java.util.List;

public class Room extends SceneObject {

	SceneObject baseContainer;
	SceneObject slotContainer;

	Room() {
		baseContainer = new SceneObject();
		this.addSubObject(baseContainer);
		slotContainer = new SceneObject();
		this.addSubObject(slotContainer);
	}

	void addBaseObjects(List<SceneObject> baseObjects) {
		for (SceneObject baseObject : baseObjects) {
			baseContainer.addSubObject(baseObject);
		}
	}

	void addSlotObject(SceneObject object) {
		slotContainer.addSubObject(object);
	}

}
