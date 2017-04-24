package mbeb.lifeforms;

import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.materials.Material;

import java.util.EnumMap;
import java.util.Map;

public class Sword {

	private static IRenderable swordMesh = null;
	private static Map<LootType, Material> swordMaterials = new EnumMap<>(LootType.class);

	private static IRenderable getSwordMesh() {
		if (swordMesh == null) {
			swordMesh = new ObjectLoader().loadFromFile("sword.obj");
		}
		return swordMesh;
	}

	private static Material getMaterialFor(LootType type) {
		return swordMaterials.computeIfAbsent(type, (lootType) -> {
			String materialName = "material/sword/" + lootType.name();
			return new Material(materialName, 1);
		});
	}


	private float damage;
	private float range;
	private float strokeTime;
	private IRenderable sword;

	public Sword(float damage, float range, float strokeTime, LootType type) {
		this.damage = damage;
		this.range = range;
		this.strokeTime = strokeTime;
		sword = getSwordMesh().withMaterial(getMaterialFor(type));
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
