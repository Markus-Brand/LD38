package mbeb.lifeforms;

import org.joml.*;
import org.lwjgl.glfw.*;

import mbeb.ld38.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;

public class PlayerEntity extends LifeformEntity {

	private final AnimationStateFacade animator;

	private SwordEntity swordEntity;

	private final float playerSpeed = 4f;

	private final Inventory inventory;

	private final WalkOnHeightMapBehaviour heightWalk;

	public PlayerEntity(final float radius, final SceneObject sceneObject, final AnimationStateFacade animator, final float healthpoints, final IHeightSource heightSource,
			final HealthBarGUI healthGui) {
		super(sceneObject, healthpoints, radius, healthGui);
		this.animator = animator;
		heightWalk = new WalkOnHeightMapBehaviour(heightSource, playerSpeed);
		addBehaviour(0, new CombinedBehaviour(new SamuraiPlayerBehaviour(), heightWalk));
		setHealthBarOffset(new Vector3f(0, 2, 0));
		inventory = new Inventory();

		inventory.addSword(new Sword(10, 1, 1.5f, LootType.Wood, SwordType.LONG_SWORD));
		inventory.addSword(new Sword(5, 1, 0.1f, LootType.Diamond, SwordType.LONG_SWORD));

		setSword(inventory.getSelectedSword());
	}

	@Override
	protected float getHealthBarSize() {
		return 1.5f;
	}

	@Override
	public void update(final double deltaTime) {
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

	public void setSword(final Sword sword) {
		setSwordEntity(sword.spawnNew(getSceneObject().getParent(), getSceneObject(), animator));
	}

	public void setSwordEntity(final SwordEntity swordEntity) {
		if (this.swordEntity != null) {
			for (final LifeformEntity tarsched : this.swordEntity.getTarscheds().keySet()) {
				swordEntity.addTarsched(tarsched);
			}
			this.swordEntity.getSceneObject().removeSelf();
		}
		this.swordEntity = swordEntity;
	}

	public void addTo(final SceneObject so) {
		this.getSceneObject().removeSelf();
		so.addSubObject(this.getSceneObject());
		this.swordEntity.getSceneObject().removeSelf();
		so.addSubObject(this.swordEntity.getSceneObject());
	}

	public SwordEntity getSword() {
		return swordEntity;
	}

	public void addTarsched(final LifeformEntity tarsched) {
		swordEntity.addTarsched(tarsched);
	}

	public void setHeightSource(final IHeightSource heightSource) {
		heightWalk.setHeightSource(heightSource);
	}
}
