package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.materials.Material;

public class IntroGameState implements GameState {

	private static final String TAG = "IntroGameState";

	private float progress;

	private Scene introScene;

	private EntityWorld entities;

	private Camera camera;

	private SceneObject world;

	private ShaderProgram worldShader;

	@Override
	public void init() {
		progress = 0;

		Skybox skyBox = new Skybox("skybox/mountain");
		camera = new PerspectiveCamera();
		introScene = new Scene(camera, skyBox);

		worldShader = new ShaderProgram("basic.frag", "basic.vert");
		introScene.getLightManager().addShader(worldShader);
		worldShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);

		Material metalbox = new Material("material/metalbox", 4);

		final IRenderable worldRenderable = new ObjectLoader().loadFromFile("planet.obj").withMaterial(metalbox);

		world = new SceneObject(worldRenderable);
		world.setShader(worldShader);

		introScene.getSceneGraph().addSubObject(world);

		entities = new EntityWorld();
		entities.add(camera).addBehaviour(1, new PlayerControlBehaviour());
		GL11.glDisable(GL_CULL_FACE);
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
