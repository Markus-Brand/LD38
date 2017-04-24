package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

import mbeb.ld38.HealthBarGUI;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import mbeb.ld38.overworld.*;
import mbeb.lifeforms.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.gl.texture.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.options.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.shapes.Rectangle;

public class OverworldGameState implements GameState {

	private Vector3f port = new Vector3f(0.41f, 2.509f, -7.46f);
	private Vector3f key = new Vector3f(-2.27f, 2.74f, -7.44f);
	private float threshold = 0.75f;
	private boolean leftForDungeon = false;

	private Scene overworldScene;
	private EntityWorld world;
	private OverWorld overworld;

	private ShaderProgram waterShader;

	private float totalTimePassed;

	private Skybox skybox;
	//private FrameBuffer heightMapGenerator;
	//private Texture2D heightMap;

	private Camera topDownViewCamera;

	private ShaderProgram defaultShader;

	//private ShaderProgram displayDepthMap;

	private Player player;

	MonsterEntity goblinEntity;
	PlayerEntity playerEntity;

	@Option(category = "Game")
	@ButtonOption
	public static boolean showBBs = true;

	@Override
	public void init() {

		world = new EntityWorld();

		topDownViewCamera = new PerspectiveCamera();

		skybox = new Skybox("beachbox/beach", "png");
		overworldScene = new Scene(topDownViewCamera, skybox);

		final IRenderable water = new ObjectLoader().loadFromFile("overworld/water.obj");

		waterShader = new ShaderProgram("water.frag", "planet.vert");
		waterShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(waterShader);
		//overworldScene.getSceneGraph().setShader(waterShader);

		defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(defaultShader);
		overworldScene.getSceneGraph().setShader(defaultShader);

		final SceneObject waterObject =
				new SceneObject(water, new BoneTransformation(new Vector3f(), new Quaternionf(), new Vector3f(100)));
		waterObject.setShader(waterShader);

		final ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(animationShader);

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());
		overworld.getSceneObject().addSubObject(waterObject);

		player =
				new Player(100, animationShader, new HeightFromHeightMap(
						Texture.loadBufferedImage("overworldHeight.png"), new Rectangle(new Vector2f(-16),
								new Vector2f(32)), 2f, 1f));


		healthGui = new HealthBarGUI();

		playerEntity = player.spawnNew(new Vector3f(0, 10, 1), 0, overworld.getSceneObject(), healthGui);
		world.add(playerEntity);

		world.add(topDownViewCamera).addBehaviour(0, new TopDownViewBehaviour(playerEntity, 7, 2, 2))
				.setPosition(new Vector3f(3, 4, 5));

		final DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

		final Goblin goblin = new Goblin(playerEntity, animationShader);
		goblinEntity = goblin.spawnNew(new Vector3f(10, 3, 0), 0, overworld.getSceneObject(), healthGui);
		world.add(goblinEntity);

		playerEntity.addTarsched(goblinEntity);

		goblinEntity.showHealthBar(topDownViewCamera);
		playerEntity.showHealthBar(null);
	}

	private HealthBarGUI healthGui;

	@Override
	public void update(final double deltaTime) {
		totalTimePassed += deltaTime;
		overworldScene.update(deltaTime);
		world.update(deltaTime);

		if (goblinEntity.isDead()) {
			overworldScene.getSceneGraph().removeSubObject(goblinEntity.getSceneObject());
			world.remove(goblinEntity);
		}
		healthGui.update(deltaTime);
	}

	@Override
	public void render() {
		waterShader.use();
		skybox.getTexture().bind();
		waterShader.setUniform("skybox", skybox.getTexture());
		waterShader.setUniform("time", totalTimePassed);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		overworldScene.render(showBBs);

		healthGui.render();
	}

	@Override
	public GameStateIdentifier getNextState() {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			return GameStateIdentifier.INTRO;
		} else {
			if (playerEntity.getPosition().distance(key) < threshold) {
				this.leftForDungeon = true;
				return GameStateIdentifier.DUNGEON;
			} else {
				return null;
			}
		}
	}

	@Override
	public void resetNextGameState() {
		//not needed
	}

	@Override
	public void open() {
		if (leftForDungeon) {
			leftForDungeon = false;
			playerEntity.setPosition(port);
		}
		overworldScene.getLightManager().rewriteUBO();
		//GLContext.hideCursor();
	}

}
