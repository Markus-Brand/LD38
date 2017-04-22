package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.behaviour.FlyingKeyboardBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;

public class IntroGameState implements GameState {

	private static final String TAG = "IntroGameState";

	private float progress;

	private Scene introScene;

	private EntityWorld entities;

	private Camera camera;

	@Override
	public void init() {
		progress = 0;
		camera = new PerspectiveCamera();
		introScene = new Scene(camera, new Skybox("skybox/mountain"));
		entities = new EntityWorld();
		entities.add(camera).addBehaviour(1, new FlyingKeyboardBehaviour());
	}

	@Override
	public void update(double deltaTime) {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			progress = 1;
		}
		introScene.update(deltaTime);
		entities.update(deltaTime);
		camera.update(deltaTime);
		//progress += deltaTime;
	}

	@Override
	public void render() {
		glClearColor(0, 0, 0, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		introScene.render();
	}

	@Override
	public GameStateIdentifier getNextState() {
		return progress >= 1 ? GameStateIdentifier.OVERWORLD : null;
	}

	@Override
	public void resetNextGameState() {
		// TODO Auto-generated method stub

	}

	@Override
	public void open() {
		introScene.getLightManager().rewriteUBO();
		progress = 0;
	}

}
