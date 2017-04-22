package mbeb.ld38.overworld;

import mbeb.opengldefault.animation.BoneTransformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;

/**
 * One of the trees at the overworld
 */
public class PalmTree {

	//<editor-fold desc="Static palm materials / renderables">
	private static Material barkMaterial = null;
	private static Material leafMaterial = null;
	private static IRenderable trunkRenderable = null;
	private static IRenderable leavesRenderable = null;

	private static void initIfNeeded() {
		if (barkMaterial != null) {
			return;
		}
		barkMaterial = new Material("material/palmTree", 1);
		leafMaterial = new Material("material/palmLeaf", 1);

		ObjectLoader loader = new ObjectLoader();
		trunkRenderable = loader.loadFromFile("overworld/palmTrunk.obj").withMaterial(barkMaterial);
		leavesRenderable = loader.loadFromFile("overworld/palmLeaves.obj").withMaterial(leafMaterial);

	}
	//</editor-fold>

	private final Vector3f position;
	private final SceneObject trunkObject;
	private final SceneObject leavesObject;

	public PalmTree(Vector3f position) {
		this(position, null);
	}
	public PalmTree(Vector3f position, Quaternionf rotation) {
		initIfNeeded();
		this.position = position;

		BoneTransformation pos = new BoneTransformation(position, rotation);
		trunkObject = new SceneObject(trunkRenderable, pos);
		leavesObject = new SceneObject(leavesRenderable, pos);
	}

	public void addTo(SceneObject parent) {
		parent.addSubObject(trunkObject);
		parent.addSubObject(leavesObject);
	}
}
