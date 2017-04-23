package mbeb.opengldefault.game;

import java.awt.*;

import mbeb.opengldefault.gl.GLContext;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL11.*;
import mbeb.ld38.overworld.OverWorld;
import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.texture.Texture;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.options.ButtonOption;
import mbeb.opengldefault.options.Option;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.BoneTrackingBehaviour;
import mbeb.opengldefault.scene.behaviour.TopDownViewBehaviour;
import mbeb.opengldefault.scene.behaviour.WalkOnHeightMapBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.materials.Material;
import mbeb.opengldefault.shapes.Rectangle;

public class OverworldGameState implements GameState {

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1)
			.rotate(new AxisAngle4f((float) Math.PI / 2, 0, 0, 1));

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

		Material samuraiMaterial = new Material("material/samurai", 1);
		AnimatedMesh samuraiMesh = new ObjectLoader().loadFromFileAnim("samurai.fbx");
		samuraiMesh.setTransform(MeshFlip);
		samuraiMesh.getSkeleton().printRecursive("");
		playerAnimatedRenderable = new AnimationStateFacade(samuraiMesh, samuraiMaterial);

		IRenderable water = new ObjectLoader().loadFromFile("overworld/water.obj");
		IRenderable sword = new ObjectLoader().loadFromFile("sword.obj").withMaterial(samuraiMaterial);

		waterShader = new ShaderProgram("water.frag", "planet.vert");
		waterShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(waterShader);
		//overworldScene.getSceneGraph().setShader(waterShader);

		defaultShader = new ShaderProgram("basic.frag", "basic.vert");
		defaultShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(defaultShader);
		overworldScene.getSceneGraph().setShader(defaultShader);

		ShaderProgram animationShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		animationShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getLightManager().addShader(animationShader);

		SceneObject waterObject =
				new SceneObject(water, new BoneTransformation(new Vector3f(), new Quaternionf(), new Vector3f(100)));
		waterObject.setShader(waterShader);

		player = new SceneObject(playerAnimatedRenderable, new BoneTransformation(new Vector3f(0, 10, 0)));
		player.setShader(animationShader);

		IEntity playerEntity =
				world.add(player).addBehaviour(
						0,
						new WalkOnHeightMapBehaviour(new HeightFromHeightMap(Texture
								.loadBufferedImage("overworldHeight.png"), new Rectangle(new Vector2f(-16),
								new Vector2f(32)), 2f, 1f), 4f));
		world.add(topDownViewCamera).addBehaviour(0, new TopDownViewBehaviour(playerEntity, 7, 2, 2))
				.setPosition(new Vector3f(3, 4, 5));

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());
		overworld.getSceneObject().addSubObject(player);
		overworld.getSceneObject().addSubObject(waterObject);

		DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

		playerAnimatedRenderable.registerAnimation("Jogging", "Jogging", 32);
		playerAnimatedRenderable.registerAnimation("Pierce", "Pierce", 32, 0.1f, 0.1f, 1.1f);
		SceneObject swordObject = new SceneObject(sword);
		overworldScene.getSceneGraph().addSubObject(swordObject);
		world.add(swordObject).addBehaviour(0,
				new BoneTrackingBehaviour(player, playerAnimatedRenderable.getAnimatedRenderable(), "Item.Right"));

		world.update(0.0001f);
	}

	@Override
	public void update(double deltaTime) {
		totalTimePassed += deltaTime;
		playerAnimatedRenderable.ensureRunning("Jogging");
		playerAnimatedRenderable.ensureRunning("Pierce", KeyBoard.isKeyDown(GLFW.GLFW_KEY_SPACE), false);
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
		return KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) ? GameStateIdentifier.INTRO : null;
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
