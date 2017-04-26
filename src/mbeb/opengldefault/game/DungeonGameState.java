package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.lang.Math;
import java.util.Collection;
import java.util.HashSet;

import org.joml.*;

import mbeb.ld38.*;
import mbeb.ld38.dungeon.*;
import mbeb.ld38.dungeon.room.*;
import mbeb.lifeforms.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.gl.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.gui.*;
import mbeb.opengldefault.gui.elements.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

public class DungeonGameState implements GameState {

	private static final String TAG = "DungeonGameState";

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1)
			.rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	private Scene scene;
	private DungeonLevel level;
	private EntityWorld world;
	private Goblin goblin;
	private Camera camera;
	private Chest chest;
	private TextGUI infoGUI;
	private TextGUIElement infoBox;
	private boolean exitDungeon = false;
	private int size = 2;

	private final SharedData shared;

	public DungeonGameState(final SharedData data) {
		this.shared = data;
	}

	@Override
	public void init() {
		camera = new PerspectiveCamera();
		final Skybox skybox = new Skybox("darkbox/db");

		scene = new Scene(camera, skybox);
		world = new EntityWorld();

		//shaders
		final ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(defaultShader);
		scene.getSceneGraph().setShader(defaultShader);
		final ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(animationShader);

		//textures;

		goblin = new Goblin(shared.playerEntity, animationShader);

		RoomType.initializeRoomTypes(shared.soundEnvironment);
		chest = new Chest(animationShader, shared.playerEntity);

		infoGUI = new TextGUI(new Font("Comic Sans MS", 0, 64));
		infoBox = infoGUI.addText(" ", new Vector2f());
		infoBox.setPositionRelativeToScreen(new Vector2f(0.01f, 0.01f));
		infoBox.setColor(Color.WHITE);

		level =
				new DungeonLevel(scene.getLightManager(), goblin, shared.healthBarGUI, camera, chest, infoBox,
						shared.soundEnvironment);

		level.setEnemySpawns(0.2f, 0.2f, 0.2f, 0.2f, 0.2f);
		level.generate(size, size);
		level.setFinishListener(dungeonLevel -> {
			if (!exitDungeon) {
				this.exitDungeon = true;
				this.shared.playerEntity.resetHealth();
				this.shared.playerEntity.setLastClearedLevel(size - 1);
				this.size++;
			}
		});

		scene.getSceneGraph().addSubObject(level);

		final IEntity cameraEntity = new CameraEntity(camera);
		camera.setEye(new Vector3f(0, 20, 0));
		cameraEntity.setDirection(new Vector3f(0, -1, 0));

		shared.playerEntity.setHeightSource(level);
		shared.playerEntity.setDeathListener(lifeformEntity -> {
			shared.playerEntity.revive();
			shared.playerEntity.setJustDied(true);
			shared.playerEntity.getInventory().looseXPercentofYerShit(0.5f);
			this.exitDungeon = true;
		});

		world.add(camera).addBehaviour(0, new TopDownViewBehaviour(shared.playerEntity, 8, 1.5f, 2))
				.setPosition(new Vector3f(3, 4, 5));

		//light
		final DirectionalLight sun = new DirectionalLight(new Color(8, 7, 6), new Vector3f(0, -1, 0));
		scene.getLightManager().addLight(sun);
		world.add(shared.playerEntity);

		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");

	}

	@Override
	public void update(final double deltaTime) {
		shared.soundEnvironment.makeCurrent();

		shared.healthBarGUI.update(deltaTime);
		infoGUI.update(deltaTime);
		scene.update(deltaTime);
		world.update(deltaTime);
	}

	@Override
	public void render() {
		scene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB));
		shared.healthBarGUI.render();
		infoGUI.render();
	}

	@Override
	public void clear() {
	}

	@Override
	public GameStateIdentifier getNextState() {
		if (exitDungeon) {
			Collection<MonsterEntity> enemies = new HashSet<>(this.level.getActiveRoom().getEnemies());
			this.level.getActiveRoom().getEnemies().clear();
			enemies.forEach(monsterEntity -> {
				monsterEntity.setDeathListener(null);
				monsterEntity.damage(100000.0f);
			});
			return GameStateIdentifier.OVERWORLD;
		} else {
			return null;
		}
	}

	@Override
	public void resetNextGameState() {
		//nothing
	}

	@Override
	public void open() {
		shared.soundEnvironment.makeCurrent();
		if (this.exitDungeon) {
			this.level.removeSelf();

			this.level =
					new DungeonLevel(scene.getLightManager(), goblin, shared.healthBarGUI, camera, chest, infoBox,
							shared.soundEnvironment);

			this.level.generate(size, size);
			this.scene.getSceneGraph().addSubObject(this.level);
			level.setFinishListener(dungeonLevel -> {
				if (!exitDungeon) {
					this.exitDungeon = true;
					this.shared.playerEntity.resetHealth();
					this.shared.playerEntity.setLastClearedLevel(size - 1);
					this.size++;
				}
			});
		}
		this.exitDungeon = false;
		this.shared.playerEntity.addTo(scene.getSceneGraph(), scene.getLightManager());
		shared.playerEntity.setStoneWalkingSound(true);
		this.level.setPlayer(this.shared.playerEntity);
		GLContext.hideCursor();
		scene.getLightManager().rewriteUBO();
	}

	public int getDungeonLevel() {
		return size - 1;
	}
}
