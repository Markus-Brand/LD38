package mbeb.opengldefault.game;

import java.awt.*;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import mbeb.ld38.overworld.OverWorld;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.behaviour.TopDownViewBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.materials.Material;

public class OverworldGameState implements GameState {

	private Scene overworldScene;
	private EntityWorld world;
	private OverWorld overworld;

	private SceneObject player;

	private ShaderProgram waterShader;

	private float totalTimePassed;

	private Skybox skybox;

	@Override
	public void init() {
		world = new EntityWorld();
		Camera camera = new PerspectiveCamera();

		skybox = new Skybox("beachbox/beach", "png");
		overworldScene = new Scene(camera, skybox);

		final IRenderable playerRenderable =
				new ObjectLoader().loadFromFile("bunny.obj").withMaterial(new Material("material/beach", 1));
		IRenderable water = new ObjectLoader().loadFromFile("overworld/water.obj");

		waterShader = new ShaderProgram("water.frag", "planet.vert");
		waterShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(waterShader);
		overworldScene.getSceneGraph().setShader(waterShader);

		SceneObject waterObject =
				new SceneObject(water, new BoneTransformation(new Vector3f(), new Quaternionf(), new Vector3f(100)));
		waterObject.setShader(waterShader);

		player = new SceneObject(playerRenderable, new BoneTransformation(new Vector3f(0, 10, 0)));

		IEntity playerEntity = world.add(player).addBehaviour(0, new PlayerControlBehaviour());
		world.add(camera).addBehaviour(0, new TopDownViewBehaviour(playerEntity));

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());
		overworld.getSceneObject().addSubObject(player);
		overworld.getSceneObject().addSubObject(waterObject);

		ShaderProgram defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(defaultShader);
		overworldScene.getSceneGraph().setShader(defaultShader);

		DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

	}

	@Override
	public void update(double deltaTime) {
		totalTimePassed += deltaTime;
		overworldScene.update(deltaTime);
		world.update(deltaTime);
	}

	@Override
	public void render() {
		waterShader.use();
		skybox.getTexture().bind();
		waterShader.setUniform("skybox", skybox.getTexture());
		waterShader.setUniform("time", totalTimePassed);

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
