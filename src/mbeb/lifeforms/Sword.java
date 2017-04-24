package mbeb.lifeforms;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;

public class Sword {
	private float damage;
	private float range;
	private float strokeTime;
	private IRenderable sword;
	private Material material;

	public Sword(float damage, float range, float strokeTime) {
		this.damage = damage;
		this.range = range;
		this.strokeTime = strokeTime;
		material = new Material("material/samurai", 1);
		sword = new ObjectLoader().loadFromFile("sword.obj").withMaterial(material);
	}

	public SwordEntity spawnNew(final SceneObject parent, SceneObject playerObject,
			AnimationStateFacade playerAnimatedRenderable) {
		final SceneObject swordObject = new SceneObject(sword);
		parent.addSubObject(swordObject);
		return new SwordEntity(swordObject, damage, range, strokeTime, playerObject, playerAnimatedRenderable);
	}

	public float getDamage() {
		return damage;
	}
}
