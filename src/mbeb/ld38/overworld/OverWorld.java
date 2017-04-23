package mbeb.ld38.overworld;

import java.util.ArrayList;
import java.util.List;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.materials.Material;

/**
 * The world that the player can move in (contains the environment)
 */
public class OverWorld {

	private static final List<Vector3f> palmPositions = new ArrayList<Vector3f>() {
		{
			add(new Vector3f(0.5f, -13, 0));
			add(new Vector3f(-4.6f, -9, 0.3f));
			add(new Vector3f(-4.2f, -2.76f, 0.53f));
			add(new Vector3f(-8.1f, 1, 0.6f));
			add(new Vector3f(-8.8f, 6.3f, 0.35f));
			add(new Vector3f(0.9f, 7.9f, 0.6f));
			add(new Vector3f(8.6f, 2.5f, 0.76f));
			add(new Vector3f(4.4f, 1.2f, 0.85f));
			add(new Vector3f(-0.1f, -0.1f, 0.54f));
		}
	};

	private EntityWorld environment = new EntityWorld();

	private SceneObject parent;

	public OverWorld() {
		init();
	}

	private void init() {
		parent = new SceneObject();
		Material landMaterial = new Material("material/beach", 1);
		Material mossCobbleMaterial = new Material("material/cobble/moss", 3);

		ObjectLoader loader = new ObjectLoader();
		IRenderable island = loader.loadFromFile("overworld/island.obj").withMaterial(landMaterial);
		IRenderable entrance = loader.loadFromFile("overworld/entrance.obj").withMaterial(mossCobbleMaterial);

		parent.addSubObject(island);

		parent.addSubObject(new SceneObject(entrance, new BoneTransformation(new Vector3f(-2.74f, 0.65f, -7.43f))));

		//add palms
		int number = 0;
		for (Vector3f position : palmPositions) {
			float rotationAngle = number * 290f;
			Quaternionf rotation = new Quaternionf(new AxisAngle4f(rotationAngle, new Vector3f(0, 1, 0)));
			float scale = 0.7f + rotationAngle / 17 % 0.5f;

			BoneTransformation transform =
					new BoneTransformation(new Vector3f(position.x, position.z, position.y), rotation, new Vector3f(
							scale));

			new PalmTree(transform).addTo(parent);
			number++;
		}


		Material testMaterial = new Material("material/stonewall/seam", 1);
		IRenderable test = loader.loadFromFile("dungeon/corridor/floor/+x.obj");
		test = test.withMaterial(testMaterial);

		parent.addSubObject(test);

	}

	public SceneObject getSceneObject() {
		return parent;
	}
}
