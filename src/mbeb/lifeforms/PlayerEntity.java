package mbeb.lifeforms;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.*;
import org.lwjgl.glfw.*;

import mbeb.ld38.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.sound.*;

public class PlayerEntity extends LifeformEntity {

	private final AnimationStateFacade animator;

	private SwordEntity swordEntity;
	private boolean justDied = false;
	private final SceneEntity lampEntity;
	private final PointLightEntity lightEntity;

	private final float playerSpeed = 4f;

	private final Inventory inventory;

	private final WalkOnHeightMapBehaviour heightWalk;

	public SoundSource craftingSoundSource;
	public SoundSource swordChangeSoundSource;
	public SoundSource walkingSoundSource;
	private Sound walkingSandSound;
	private Sound walkingStoneSound;

	public PlayerEntity(final float radius, final SceneObject sceneObject, final AnimationStateFacade animator, final float healthpoints, final IHeightSource heightSource,
			final HealthBarGUI healthGui, final SoundEnvironment soundEnvironment) {
		super(sceneObject, healthpoints, radius, healthGui, soundEnvironment);
		this.animator = animator;
		heightWalk = new WalkOnHeightMapBehaviour(heightSource, playerSpeed);
		addBehaviour(0, new CombinedBehaviour(new SamuraiPlayerBehaviour(), heightWalk));
		setHealthBarOffset(new Vector3f(0, 2, 0));
		inventory = new Inventory();

		inventory.addSword(new Sword(10, 1, 1.5f, LootType.Wood, SwordType.LONG_SWORD));
		inventory.addSword(new Sword(5, 1, 0.1f, LootType.Diamond, SwordType.LONG_SWORD));

		setSword(inventory.getSelectedSword());

		final SceneObject lampObject = new SceneObject(Player.lampRenderable, new BoneTransformation(null, null, new Vector3f(0.3f)));

		final PointLight light = new PointLight(new Vector3f(1f, 0.5f, 0.2f), new Vector3f(), 15f);
		lightEntity = (PointLightEntity) light.asEntity().addBehaviour(0, new ParentBehaviour(lampObject, new Vector3f(0, 2, 0)));
		lampEntity = (SceneEntity) lampObject.asNewEntity().addBehaviour(0, new BoneTrackingBehaviour(sceneObject, animator.getAnimatedRenderable(), "Hand.Left").fixedDirection());
		initSound(soundEnvironment);
	}

	@Override
	protected void initSound(final SoundEnvironment soundEnvironment) {
		final Sound craftingSound = soundEnvironment.createSound("crafting");
		this.craftingSoundSource = soundEnvironment.createSoundSource(false, true);
		craftingSoundSource.setSound(craftingSound);
		craftingSoundSource.asNewEntity().attachTo(this);

		walkingSandSound = soundEnvironment.createSound("walking_sand");
		walkingStoneSound = soundEnvironment.createSound("walking_stone");
		this.walkingSoundSource = soundEnvironment.createSoundSource(true, true);
		walkingSoundSource.setSound(walkingSandSound);
		walkingSoundSource.asNewEntity().attachTo(this);

		final Sound swordSound = soundEnvironment.createSound("change_sword");
		this.swordChangeSoundSource = soundEnvironment.createSoundSource(false, true);
		swordChangeSoundSource.setSound(swordSound);
		swordChangeSoundSource.asNewEntity().attachTo(this);
	}

	@Override
	public String getAttackSound() {
		return "player_attack";
	}

	@Override
	public String getHurtSound() {
		return "player_damage";
	}

	@Override
	public String getDieSound() {
		return "player_die";
	}

	@Override
	protected float getHealthBarSize() {
		return 1.5f;
	}

	@Override
	public void update(final double deltaTime) {
		super.update(deltaTime);
		swordEntity.update(deltaTime);
		lampEntity.update(deltaTime);
		lightEntity.update(deltaTime);

		if (KeyBoard.pullKeyDown(GLFW.GLFW_KEY_TAB)) {
			swordChangeSoundSource.play();
			inventory.switchSword();
			setSword(inventory.getSelectedSword());
		}

		if (KeyBoard.isKeyDown(GLFW_KEY_W) || KeyBoard.isKeyDown(GLFW_KEY_S)) {
			walkingSoundSource.play();
		} else {
			walkingSoundSource.pause();
		}
	}

	public void setStoneWalkingSound(final boolean stoneNotSand) {
		if (stoneNotSand) {
			walkingSoundSource.setSound(walkingStoneSound);
		} else {
			walkingSoundSource.setSound(walkingSandSound);
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

	@Override
	public boolean deletesBar() {
		return false;
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

	public void addTo(final SceneObject so, final LightManager newLightManager) {
		this.getSceneObject().removeSelf();
		so.addSubObject(this.getSceneObject());

		this.swordEntity.getSceneObject().removeSelf();
		so.addSubObject(this.swordEntity.getSceneObject());

		this.lampEntity.getSceneObject().removeSelf();
		so.addSubObject(this.lampEntity.getSceneObject());

		final PointLight oldLight = lightEntity.getLight();
		final PointLight newLight = new PointLight(oldLight);
		oldLight.remove();
		newLightManager.addLight(newLight);
		lightEntity.setPointlight(newLight);
	}

	public boolean isJustDied() {
		return justDied;
	}

	public void setJustDied(boolean justDied) {
		this.justDied = justDied;
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

	public Inventory getInventory() {
		return inventory;
	}
}
