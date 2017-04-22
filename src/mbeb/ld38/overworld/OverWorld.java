package mbeb.ld38.overworld;

import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.ParentBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.materials.ColorMaterial;
import mbeb.opengldefault.scene.materials.Material;
import org.joml.Vector3f;

import java.awt.*;

/**
 * The world that the player can move in (contains the environment)
 */
public class OverWorld {

	private EntityWorld environment = new EntityWorld();

	private SceneObject parent;

	public OverWorld() {
		init();
	}

	private void init() {
		//Material water = new ColorMaterial(Color.BLUE);
		//Material land = new Material("material/beach", 1);
		parent = new SceneObject();

		new PalmTree(new Vector3f()).addTo(parent);

	}

	public SceneObject getSceneObject() {
		return parent;
	}
}
