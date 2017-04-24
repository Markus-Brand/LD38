package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetBoolean;

import java.awt.*;

import mbeb.ld38.SharedData;
import mbeb.lifeforms.Goblin;
import mbeb.opengldefault.gl.GLContext;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import mbeb.ld38.dungeon.DungeonLevel;
import mbeb.ld38.dungeon.room.RoomType;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.behaviour.TopDownViewBehaviour;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;

public class DungeonGameState implements GameState {

	private static final String TAG = "DungeonGameState";

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1)
			.rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	private Scene scene;
	private DungeonLevel level;
	private EntityWorld world;
	private Goblin goblin;

	private SharedData shared;

	public DungeonGameState(SharedData data) {
		this.shared = data;
	}

	@Override
	public void init() {
		Camera camera = new PerspectiveCamera();
		Skybox skybox = new Skybox("darkbox/db");

		scene = new Scene(camera, skybox);
		world = new EntityWorld();

		//shaders
		ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(defaultShader);
		scene.getSceneGraph().setShader(defaultShader);
		ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(animationShader);

		//textures;

		goblin = new Goblin(shared.playerEntity, animationShader);

		RoomType.initializeRoomTypes();

		level = new DungeonLevel(scene.getLightManager(), goblin, shared.healthBarGUI, camera);
		level.setEnemySpawns(0.2f, 0.2f, 0.2f, 0.2f, 0.2f);
		level.generate(3, 4);

		scene.getSceneGraph().addSubObject(level);

		IEntity cameraEntity = new CameraEntity(camera);
		camera.setEye(new Vector3f(0, 20, 0));
		cameraEntity.setDirection(new Vector3f(0, -1, 0));



		shared.playerEntity.setHeightSource(level);

		world.add(camera).addBehaviour(0, new TopDownViewBehaviour(shared.playerEntity, 8, 1.5f, 2))
				.setPosition(new Vector3f(3, 4, 5));

		//light
		DirectionalLight sun = new DirectionalLight(new Color(8, 7, 6), new Vector3f(0, -1, 0));
		scene.getLightManager().addLight(sun);
		world.add(shared.playerEntity);

		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");

	}

	@Override
	public void update(double deltaTime) {
		if (KeyBoard.pullKeyDown(GLFW_KEY_T)) {
			if (this.level.getActiveRoom() != null) {
				if (this.level.getActiveRoom().isOpen()) {
					this.level.getActiveRoom().close();
				} else {
					this.level.getActiveRoom().open();
				}
			}
		}
		shared.healthBarGUI.update(deltaTime);
		scene.update(deltaTime);
		world.update(deltaTime);
	}

	@Override
	public void render() {
		scene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB));
		shared.healthBarGUI.render();
	}

	@Override
	public void clear() {
	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.pullKeyDown(GLFW_KEY_ESCAPE) ? GameStateIdentifier.OVERWORLD : null;
	}

	@Override
	public boolean isActive() {
		return !KeyBoard.isKeyDown(GLFW_KEY_ESCAPE);
	}

	@Override
	public void resetNextGameState() {
		//nothing
	}

	@Override
	public void open() {
		this.shared.playerEntity.addTo(scene.getSceneGraph(), scene.getLightManager());
		this.level.setPlayer(this.shared.playerEntity);
		GLContext.hideCursor();
		scene.getLightManager().rewriteUBO();
	}
}
