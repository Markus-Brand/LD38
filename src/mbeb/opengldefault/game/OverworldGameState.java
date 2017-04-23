package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.lang.Math;

import org.joml.*;
import org.lwjgl.glfw.*;

import mbeb.ld38.overworld.*;
import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.*;
import mbeb.opengldefault.gl.*;
import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.gl.texture.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.options.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;
import mbeb.opengldefault.scene.materials.*;
import mbeb.opengldefault.shapes.Rectangle;

public class OverworldGameState implements GameState {

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1).rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

	private Scene overworldScene;
	private EntityWorld world;
	private OverWorld overworld;
	AnimationStateFacade playerAnimatedRenderable;

	private SceneObject player;

	private ShaderProgram waterShader;

	private float totalTimePassed;

	private Skybox skybox;
	//private FrameBuffer heightMapGenerator;
	//private Texture2D heightMap;

	private Camera topDownViewCamera;

	private ShaderProgram defaultShader;

	//private ShaderProgram displayDepthMap;

	@Option(category = "Game")
	@ButtonOption
	public static boolean showBBs = true;

	@Override
	public void init() {

		world = new EntityWorld();

		topDownViewCamera = new PerspectiveCamera();

		skybox = new Skybox("beachbox/beach", "png");
		overworldScene = new Scene(topDownViewCamera, skybox);

		final Material samuraiMaterial = new Material("material/samurai", 1);
		final AnimatedMesh samuraiMesh = new ObjectLoader().loadFromFileAnim("samurai.fbx");
		samuraiMesh.setTransform(MeshFlip);
		samuraiMesh.getSkeleton().printRecursive("");
		playerAnimatedRenderable = new AnimationStateFacade(samuraiMesh, samuraiMaterial);

		final IRenderable water = new ObjectLoader().loadFromFile("overworld/water.obj");
		final IRenderable sword = new ObjectLoader().loadFromFile("sword.obj").withMaterial(samuraiMaterial);
		//final IRenderable playerRenderable = new ObjectLoader().loadFromFile("bunny.obj").withMaterial(new Material("material/beach", 1));

		waterShader = new ShaderProgram("water.frag", "planet.vert");
		waterShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(waterShader);
		//overworldScene.getSceneGraph().setShader(waterShader);

		defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(defaultShader);
		overworldScene.getSceneGraph().setShader(defaultShader);

		final ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(animationShader);

		final SceneObject waterObject = new SceneObject(water, new BoneTransformation(new Vector3f(), new Quaternionf(), new Vector3f(100)));
		waterObject.setShader(waterShader);

		player = new SceneObject(playerAnimatedRenderable, new BoneTransformation(new Vector3f(1, 10, 0)));
		player.setShader(animationShader);

		final IEntity playerEntity = world.add(player).addBehaviour(0,
				new WalkOnHeightMapBehaviour(new HeightFromHeightMap(Texture.loadBufferedImage("overworldHeight.png"), new Rectangle(new Vector2f(-16), new Vector2f(32)), 2f, 1f), 4f));
		world.add(topDownViewCamera).addBehaviour(0, new TopDownViewBehaviour(playerEntity, 7, 2, 2)).setPosition(new Vector3f(3, 4, 5));

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());
		overworld.getSceneObject().addSubObject(player);
		overworld.getSceneObject().addSubObject(waterObject);

		final DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

		playerAnimatedRenderable.registerAnimation("Jogging", "Jogging", 32);
		playerAnimatedRenderable.registerAnimation("Pierce", "Pierce", 32, 0.1f, 0.1f, 1.1f);
		final SceneObject swordObject = new SceneObject(sword);
		overworldScene.getSceneGraph().addSubObject(swordObject);
		world.add(swordObject).addBehaviour(0, new BoneTrackingBehaviour(player, playerAnimatedRenderable.getAnimatedRenderable(), "Item.Right"));

		world.update(0.0001f);

		//final Monster monster = new Monster(123456, 123456, 12345, 1234, 123, playerRenderable, null);
		//monster.spawnNew(new Vector3f(3), 0, overworld.getSceneObject());
	}

	@Override
	public void update(final double deltaTime) {
		totalTimePassed += deltaTime;
		playerAnimatedRenderable.ensureRunning("Jogging");
		playerAnimatedRenderable.ensureRunning("Pierce", KeyBoard.isKeyDown(GLFW.GLFW_KEY_Q), false);
		overworldScene.update(deltaTime);
		world.update(deltaTime);
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
	}

	@Override
	public GameStateIdentifier getNextState() {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			return GameStateIdentifier.INTRO;
		} else {
			if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_K)) {
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
		overworldScene.getLightManager().rewriteUBO();
		GLContext.hideCursor();
	}

}
