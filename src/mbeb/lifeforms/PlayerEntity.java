package mbeb.lifeforms;

import mbeb.ld38.HealthBarGUI;
import mbeb.ld38.dungeon.DungeonLevel;
import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.CombinedBehaviour;
import mbeb.opengldefault.scene.behaviour.IHeightSource;
import mbeb.opengldefault.scene.behaviour.SamuraiPlayerBehaviour;
import mbeb.opengldefault.scene.behaviour.WalkOnHeightMapBehaviour;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class PlayerEntity extends LifeformEntity {

	private AnimationStateFacade animator;

	private SwordEntity swordEntity;

	private float playerSpeed = 4f;

	private Inventory inventory;

	private final WalkOnHeightMapBehaviour heightWalk;

	public PlayerEntity(float radius, final SceneObject sceneObject, AnimationStateFacade animator,
						final float healthpoints, IHeightSource heightSource, final HealthBarGUI healthGui) {
		super(sceneObject, healthpoints, radius, healthGui);
		this.animator = animator;
		heightWalk = new WalkOnHeightMapBehaviour(heightSource, playerSpeed);
		addBehaviour(0, new CombinedBehaviour(
				new SamuraiPlayerBehaviour(),
				heightWalk));
		setHealthBarOffset(new Vector3f(0, 2, 0));
		inventory = new Inventory();

		inventory.addSword(new Sword(10, 1, 1.5f, LootType.Wood));
		inventory.addSword(new Sword(5, 1, 0.1f, LootType.Diamond));

		setSword(inventory.getSelectedSword());
	}

	@Override
	protected float getHealthBarSize() {
		return 1.5f;
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		swordEntity.update(deltaTime);

		if (KeyBoard.pullKeyDown(GLFW.GLFW_KEY_TAB)) {
			inventory.switchSword();
			System.out.println(inventory.getSelectedSword().getDamage());
			setSword(inventory.getSelectedSword());
		}
	}

	public void startStroke() {
		if (!swordEntity.isStriking()) {
			swordEntity.startStriking();
			getAnimator().setDuration("Pierce", swordEntity.getStrokeTime());
			getAnimator().ensureRunning("Pierce", true, false);
			getAnimator().ensureRunning("Pierce", false, false);
		}
	}

	public AnimationStateFacade getAnimator() {
		return animator;
	}

	public void setSword(Sword sword) {
		setSwordEntity(sword.spawnNew(getSceneObject().getParent(), getSceneObject(), animator));
	}

	public void setSwordEntity(SwordEntity swordEntity) {
		if (this.swordEntity != null) {
			for (LifeformEntity tarsched : this.swordEntity.getTarscheds().keySet()) {
				swordEntity.addTarsched(tarsched);
			}
			this.swordEntity.getSceneObject().removeSelf();
		}
		this.swordEntity = swordEntity;
	}

	public void addTo(SceneObject so) {
		this.getSceneObject().removeSelf();
		so.addSubObject(this.getSceneObject());
	}

	public SwordEntity getSword() {
		return swordEntity;
	}

	public void addTarsched(LifeformEntity tarsched) {
		swordEntity.addTarsched(tarsched);
	}

	public void setHeightSource(IHeightSource heightSource) {
		heightWalk.setHeightSource(heightSource);
	}
}
