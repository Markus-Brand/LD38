package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import mbeb.ld38.dungeon.DungeonLevel;
import mbeb.ld38.dungeon.room.RoomType;
import mbeb.opengldefault.scene.entities.SceneEntity;
import org.joml.Vector3f;

import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.IEntity;

public class DungeonGameState implements GameState {

	private static final String TAG = "DungeonGameState";

	private Scene scene;
	private DungeonLevel level;
	private List<IEntity> entities = new ArrayList<>();

	@Override
	public void init() {
		Camera camera = new PerspectiveCamera();
		Skybox skybox = new Skybox("skybox/mountain");

		scene = new Scene(camera, skybox);

		//shaders
		ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		scene.getLightManager().addShader(defaultShader);
		scene.getSceneGraph().setShader(defaultShader);

		//textures

		RoomType.initializeRoomTypes();


		level = new DungeonLevel(3, 3);
		scene.getSceneGraph().addSubObject(level);

		IEntity cameraEntity = new CameraEntity(camera);
		camera.setEye(new Vector3f(0, 20, 0));
		cameraEntity.setDirection(new Vector3f(0, -1, 0));
		cameraEntity.addBehaviour(2, new PlayerControlBehaviour(0.0f, 0.0f, 0.01f, 10.0f));
		entities.add(cameraEntity);

		//light
		DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0, -1, 0));
		scene.getLightManager().addLight(sun);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");
	}

	@Override
	public void update(double deltaTime) {
		if(KeyBoard.pullKeyDown(GLFW_KEY_T)) {
			if(this.level.getActiveRoom().isOpen()){
				this.level.getActiveRoom().close();
			}else{
				this.level.getActiveRoom().open();
			}
		}
		scene.update(deltaTime);
		for (IEntity entity : entities) {
			entity.update(deltaTime);
		}
	}

	@Override
	public void render() {
		scene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB));
	}

	@Override
	public void clear() {
	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.pullKeyDown(GLFW_KEY_ESCAPE) ? GameStateIdentifier.EXIT : null;
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
		scene.getLightManager().rewriteUBO();
	}
}
