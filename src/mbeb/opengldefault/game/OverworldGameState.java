package mbeb.opengldefault.game;

import java.awt.*;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import mbeb.ld38.overworld.OverWorld;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;

public class OverworldGameState implements GameState {

	private Scene overworldScene;
	private EntityWorld world = new EntityWorld();
	private OverWorld overworld;

	@Override
	public void init() {
		Camera camera = new PerspectiveCamera();

		Skybox sky = new Skybox("beachbox/beach", "png");
		overworldScene = new Scene(camera, sky);

		world.add(camera).addBehaviour(0, new PlayerControlBehaviour());

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());

		ShaderProgram defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(defaultShader);
		overworldScene.getSceneGraph().setShader(defaultShader);

		DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

	}

	@Override
	public void update(double deltaTime) {
		overworldScene.update(deltaTime);
		world.update(deltaTime);
	}

	@Override
	public void render() {
		overworldScene.render();
	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) ? GameStateIdentifier.INTRO : null;
	}

	@Override
	public void resetNextGameState() {
		//not needed
	}

	@Override
	public void open() {
		overworldScene.getLightManager().rewriteUBO();
	}

}
