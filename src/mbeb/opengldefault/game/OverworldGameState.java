package mbeb.opengldefault.game;

import java.awt.*;

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
								new Vector2f(32)))));
		world.add(topDownViewCamera).addBehaviour(0, new TopDownViewBehaviour(playerEntity));

		overworld = new OverWorld();
		overworldScene.getSceneGraph().addSubObject(overworld.getSceneObject());
		overworld.getSceneObject().addSubObject(player);
		overworld.getSceneObject().addSubObject(waterObject);

		DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0.2f, -1, 0).normalize());
		overworldScene.getLightManager().addLight(sun);

		playerAnimatedRenderable.registerAnimation("Jogging", "Jogging", 32);
		SceneObject swordObject = new SceneObject(sword);
		overworldScene.getSceneGraph().addSubObject(swordObject);
		world.add(swordObject).addBehaviour(0,
				new BoneTrackingBehaviour(player, playerAnimatedRenderable.getAnimatedRenderable(), "Item.Right"));

		//generateHeightMap();
	}

	ShaderProgram depthShader;

	/*private void generateHeightMap() {
		Camera camera = new OrthographicCamera(32, 1, 1, 400f);
		camera.setEye(new Vector3f(0, 4f, 0));
		camera.setCenter(new Vector3f(0, 0, 0));
		camera.setUp(new Vector3f(1, 0, 0));

		overworldScene.setCamera(camera);
		int textureSize = 2048;
		heightMap = new Texture2D(textureSize, textureSize, InternalFormat.DEPTH);
		heightMap.whileBound((Texture t) -> t.setInterpolates(false));
		heightMapGenerator = new FrameBuffer();

		heightMapGenerator.ensureExists();
		heightMapGenerator.bind();
		heightMap.bind();
		heightMapGenerator.attach(Attachment.DEPTH, heightMap);
		heightMapGenerator.bind();
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		heightMapGenerator.unbind();

		depthShader = new ShaderProgram("depth.vert", "depth.frag");
		depthShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		overworldScene.getSceneGraph().setShader(depthShader);

		glViewport(0, 0, textureSize, textureSize);
		heightMapGenerator.bind();
		glClear(GL_DEPTH_BUFFER_BIT);
		depthShader.use();
		overworldScene.render();

		heightMapGenerator.unbind();
		depthShader.delete();

		displayDepthMap = new ShaderProgram("rect.vert", "rect.frag");
		displayDepthMap.use();
		displayDepthMap.setUniform("u_texture", heightMap);
		overworldScene.setCamera(topDownViewCamera);

		glDrawBuffer(GL_BACK);
		glReadBuffer(GL_BACK);
		overworldScene.getSceneGraph().setShader(defaultShader);
	}*/

	@Override
	public void update(double deltaTime) {
		totalTimePassed += deltaTime;
		playerAnimatedRenderable.ensureRunning("Jogging");
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

		/*displayDepthMap.use();
		IRenderable plane = StaticMeshes.getScreenAlignedQuad();
		plane.render(displayDepthMap);*/
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
