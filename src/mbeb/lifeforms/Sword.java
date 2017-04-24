package mbeb.lifeforms;

import java.util.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.materials.*;

public class Sword {

	private static Map<LootType, Material> swordMaterials = new EnumMap<>(LootType.class);
	private static Map<SwordType, IRenderable> swordMeshes = new EnumMap<>(SwordType.class);

	private static IRenderable getSwordMesh(final SwordType swordType) {
		return swordMeshes.computeIfAbsent(swordType, (st) -> {
			final String meshName = "sword/" + st.name() + ".obj";
			return new ObjectLoader().loadFromFile(meshName);
		});
	}

	private static Material getMaterialFor(final LootType type) {
		return swordMaterials.computeIfAbsent(type, (lootType) -> {
			final String materialName = "material/sword/" + lootType.name();
			return new Material(materialName, 1);
		});
	}

	private final float damage;
	private final float range;
	private final float strokeTime;
	private final IRenderable sword;
	private String name;

	public Sword(final float damage, final float range, final float strokeTime, final LootType lootType,
			final SwordType swordType) {
		this.damage = damage;
		this.range = range;
		this.strokeTime = strokeTime;
		this.name = lootType.toString() + " " + swordType.toString();
		sword = getSwordMesh(swordType).withMaterial(getMaterialFor(lootType));
	}

	public SwordEntity spawnNew(final SceneObject parent, final SceneObject playerObject,
			final AnimationStateFacade playerAnimatedRenderable) {
		final SceneObject swordObject = new SceneObject(sword);
		parent.addSubObject(swordObject);
		return new SwordEntity(swordObject, damage, range, strokeTime, playerObject, playerAnimatedRenderable);
	}

	public float getDamage() {
		return damage;
	}

	public float getRange() {
		return range;
	}

	public float getStrokeTime() {
		return strokeTime;
	}

	public String getName() {
		return name;
	}
}
