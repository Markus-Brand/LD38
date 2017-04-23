package mbeb.ld38.overworld;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;

/**
 * the entrance to a dungeon
 */
public class DungeonEntrance {

	//<editor-fold desc="Static entrance materials / renderables">
	private static Material mossCobbleMaterial = null;
	private static IRenderable topRenderable = null;
	private static IRenderable bottomRenderable = null;

	private static void initIfNeeded() {
		if (mossCobbleMaterial != null) {
			return;
		}
		mossCobbleMaterial = new Material("material/cobble/moss", 3);

		ObjectLoader loader = new ObjectLoader();
		topRenderable = loader.loadFromFile("overworld/dungeon_entrance_top.obj").withMaterial(mossCobbleMaterial);
		bottomRenderable = loader.loadFromFile("overworld/dungeon_entrance_bottom.obj").withMaterial(mossCobbleMaterial);

	}
	//</editor-fold>

	private final SceneObject topObject;
	private final SceneObject bottomObject;

	public DungeonEntrance(BoneTransformation transform) {
		initIfNeeded();

		topObject = new SceneObject(topRenderable, transform);
		bottomObject = new SceneObject(bottomRenderable, transform);
	}

	public void addTo(SceneObject parent) {
		parent.addSubObject(topObject);
		parent.addSubObject(bottomObject);
	}

	public void setTopVisible(boolean visible) {
		topObject.setVisible(visible);
	}
}
