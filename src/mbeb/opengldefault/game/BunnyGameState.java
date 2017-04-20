package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.glfw.GLFW.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.shader.UBOManager;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.light.Light;
import mbeb.opengldefault.light.SpotLight;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.BezierCurveRenderable;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.MaterialRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.BezierBehaviour;
import mbeb.opengldefault.scene.behaviour.FollowingBehaviour;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.behaviour.SmoothFollowingBehaviour;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.SpotLightEntity;
import mbeb.opengldefault.scene.materials.Material;

import org.joml.*;

public class BunnyGameState implements GameState {

	private static final String TAG = "BunnyGameState";

	GameStateIdentifier nextGameState;

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1);

	private float timePassed;

	protected ICamera camera;
	Scene bunnyScene;
	//PointLight pl;
	//DirectionalLight dl;
	SpotLight sl;
	ArrayList<Light> lights = new ArrayList<>();

	BezierCurve curve;

	AnimationStateFacade animPlayer;

	List<AnimationStateFacade> animBunnyList = new ArrayList<>();

	SceneObject playerObj, curveObj;

	private TextGUI textGUI;

	Entity spotLightEntity, ple;

	EntityWorld entityWorld;

	TextGUIElement fps;

	@Override
	public void init() {
		timePassed = 0;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();
		final Random random = new Random();
		for (int i = 0; i < 10; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);

		camera = new Camera(GLContext.getAspectRatio());
		final Skybox skybox = new Skybox("skybox/mountain");
		bunnyScene = new Scene(camera, skybox);

		Material playerMaterial = new Material("material/player", 2);
		//Material lampMaterial = new Material("material/lamp", 3);
		Material bunnyMaterial = new Material("material/bunny", 4);
		Material metalbox = new Material("material/metalbox", 4);

		AnimatedMesh playerAnimMesh = new ObjectLoader().loadFromFileAnim("player.fbx");
		playerAnimMesh.setTransform(MeshFlip);
		playerAnimMesh.getSkeleton().printRecursive("");

		final AnimatedMesh bunnyAnimMesh = new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		bunnyAnimMesh.setTransform(MeshFlip);
		System.out.println();
		bunnyAnimMesh.getSkeleton().printRecursive("");

		final IRenderable containerRenderable = new ObjectLoader().loadFromFile("cube.obj").withMaterial(metalbox);

		final ShaderProgram curveShader = new ShaderProgram("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(UBOManager.MATRICES);
		curveShader.setDrawMode(ShaderProgram.DrawMode.LINES);

		final ShaderProgram animatedShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(animatedShader);
		animatedShader.addUniformBlockIndex(UBOManager.MATRICES);

		final ShaderProgram stillShader = new ShaderProgram("basic.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(stillShader);
		stillShader.addUniformBlockIndex(UBOManager.MATRICES);

		//final IRenderable boxRenderable = new ObjectLoader().loadFromFile("box.obj");

		//SceneObject box = new SceneObject(new MaterialRenderable(boxRenderable, bunnyMaterial));
		//box.setShader(stillShader);

		//final IRenderable lampRenderable = new ObjectLoader().loadFromFile("lamp.obj").withMaterial(lampMaterial);
		//SceneObject lamp = new SceneObject(lampRenderable);
		//lamp.setShader(stillShader);

		SceneObject myContainer = new SceneObject(containerRenderable, new BoneTransformation(new Vector3f(5, 0, 0)));
		myContainer.setShader(stillShader);
		bunnyScene.getSceneGraph().addSubObject(myContainer);

		animPlayer = new AnimationStateFacade(playerAnimMesh, playerMaterial);

		playerObj = new SceneObject(animPlayer);
		playerObj.setShader(animatedShader);

		entityWorld = new EntityWorld();

		entityWorld.add(camera).addBehaviour(1, new PlayerControlBehaviour());

		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);
		
		IEntity lastBunnyEntity = createBunnyChain(bunnyScene.getSceneGraph(), entityWorld, bunnyMaterial, bunnyAnimMesh, curveObj);

		//entityWorld.add(lamp).addBehaviour(1,
		//		new BoneTrackingBehaviour(playerObj, animPlayer.getAnimatedRenderable(), "Hand.L", new Vector3f(0, 0.5f, 0))
		//				.fixedDirection());

		bunnyScene.getSceneGraph().addSubObject(curveObj);
		bunnyScene.getSceneGraph().setShader(animatedShader);

		DirectionalLight dl = new DirectionalLight(new Vector3f(1, 0.99f, 0.9f), new Vector3f(-0.1f, -1f, 0));
		bunnyScene.getLightManager().addLight(dl);

		sl = new SpotLight(new Vector3f(1, 1, 0.9f), new Vector3f(0, -0.25f, 0), new Vector3f(0, 1, 0), 5, 10, 1000);
		bunnyScene.getLightManager().addLight(sl);
		spotLightEntity = new SpotLightEntity(sl);
		spotLightEntity.addBehaviour(1, new FollowingBehaviour(lastBunnyEntity, 3f).limited(5));
		spotLightEntity.addBehaviour(9001, new FollowingBehaviour(lastBunnyEntity, 7.6f));

		glEnable(GL_CULL_FACE);
		GLErrors.checkForError(TAG, "glEnable");
		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");

		animPlayer.registerAnimation("jogging", "running", 25, 2, 0.5);
		animPlayer.registerAnimation("hat", "wave", 10, 0.5, 0.5);

		textGUI = new TextGUI(new Font("Comic Sans MS", Font.PLAIN, 128));
		ShaderProgram guiShader = new ShaderProgram("gui.vert", "gui.frag");
		textGUI.setShader(guiShader);
		fps = textGUI.addText("0", new Vector2f(), 0.03f);
		fps.setColor(Color.ORANGE);
		fps.setPositionRelativeToScreen(0, 0);
		for (AnimationStateFacade facade : animBunnyList) {
			facade.registerAnimation("ohr1", "OhrenFlackern1", 4);
			facade.registerAnimation("ohr2", "OhrenFlackern2", 4);
			facade.registerAnimation("party", "HeadBang", 4);
		}

	}

	/**
	 * creates a list of following bunnys
	 *
	 * @param bunnyParent
	 *            the SceneObject to add the list to
	 * @param world
	 *            the entityWorld to animate the bunnies in
	 * @param bunnyMaterial
	 *            the material to apply
	 * @param renderable
	 *            the renderable to display
	 * @param curve
	 *            the curve to follow
	 * @return last bunny
	 */
	private IEntity createBunnyChain(SceneObject bunnyParent, EntityWorld world, Material bunnyMaterial, AnimatedMesh renderable, SceneObject curve) {
		AnimationStateFacade mainBunnyFacade = new AnimationStateFacade(renderable, bunnyMaterial);
		animBunnyList.add(mainBunnyFacade);
		final SceneObject mainBunnyObj = new SceneObject(mainBunnyFacade);
		bunnyParent.addSubObject(mainBunnyObj);

		SceneObject toFollowObject = mainBunnyObj;
		IEntity toFollow = world.add(toFollowObject).addBehaviour(1, new BezierBehaviour(curve, 3.0f));

		int bunnyCount = 100;
		for (int b = 0; b < bunnyCount; b++) {
			AnimationStateFacade followingBunnyFacade = new AnimationStateFacade(renderable, bunnyMaterial);
			animBunnyList.add(followingBunnyFacade);
			SceneObject followerObject = new SceneObject(followingBunnyFacade, createStartMatrix());
			bunnyParent.addSubObject(followerObject);
			toFollow = world.add(followerObject).addBehaviour(1, new SmoothFollowingBehaviour(toFollow, 1f));
			//.addBehaviour(1, new FollowingBehaviour(toFollow, 0.5f).limited(5))
			//.addBehaviour(2, new FollowingBehaviour(toFollow, 7f));
			toFollowObject = followerObject;
		}
		return toFollow;
	}

	private BoneTransformation createStartMatrix() {
		Random r = new Random();
		return new BoneTransformation(new Vector3f(r.nextFloat() * 6 - 3, r.nextFloat() * 6 - 3, r.nextFloat() * 6 - 3));
	}

	@Override
	public void update(final double deltaTime) {

		timePassed += deltaTime;

		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			animPlayer.slideSpeed("jogging", 50, deltaTime, 5);
		} else {
			animPlayer.slideSpeed("jogging", 25, deltaTime, 5);
		}
		animPlayer.ensureRunning("jogging", KeyBoard.isKeyDown(GLFW_KEY_Q));
		animPlayer.ensureRunning("hat", KeyBoard.isKeyDown(GLFW_KEY_E), false);

		int i = 0;
		for (AnimationStateFacade facade : animBunnyList) {
			if (timePassed > 3 + i * 0.01) {
				facade.ensureRunning("ohr1");
			}
			if (timePassed > 6 + i * 0.01) {
				facade.ensureRunning("ohr2");
			}
			if (timePassed > 9 + i * 0.01) {
				facade.ensureRunning("party");
			}
			i++;
		}

		entityWorld.update(deltaTime);

		//pl.setColor(new Color((float) java.lang.Math.sin(timepassed) / 2 + 0.5f, (float) 1.0, (float) java.lang.Math.cos(timepassed) / 2 + 0.5f));
		//pl.setPosition(new Vector3f((float) java.lang.Math.sin(timepassed) * 5, 10, (float) java.lang.Math.cos(timepassed) * 5));
		//ple.update(deltaTime);
		spotLightEntity.update(deltaTime);
		bunnyScene.update(deltaTime);

		if (KeyBoard.isKeyDown(GLFW_KEY_ESCAPE)) {
			KeyBoard.releaseAll();
			nextGameState = GameStateIdentifier.MAIN_MENU;
		}
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, GLContext.getFramebufferWidth(), GLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");
		bunnyScene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB));
		textGUI.render();
	}

	@Override
	public void clear() {

	}

	@Override
	public GameStateIdentifier getNextState() {
		return nextGameState;
	}

	@Override
	public void resetNextGameState() {
		nextGameState = null;
	}

	@Override
	public void open() {
		GLContext.hideCursor();
		bunnyScene.getLightManager().rewriteUBO();
	}

}
