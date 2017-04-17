package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Random;

import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.light.Light;
import mbeb.opengldefault.light.PointLight;
import mbeb.opengldefault.light.SpotLight;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.BezierCurveRenderable;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.shader.ShaderProgram;
import mbeb.opengldefault.rendering.shader.UBOManager;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.BezierBehaviour;
import mbeb.opengldefault.scene.behaviour.BoneTrackingBehaviour;
import mbeb.opengldefault.scene.behaviour.FollowingBehaviour;
import mbeb.opengldefault.scene.behaviour.ParentBehaviour;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.PointLightEntity;
import mbeb.opengldefault.scene.entities.SceneEntity;
import mbeb.opengldefault.scene.entities.SpotLightEntity;
import mbeb.opengldefault.options.ButtonOption;
import mbeb.opengldefault.options.Option;
import mbeb.opengldefault.options.SliderOption;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class BunnyGameState implements GameState {

	@ButtonOption
	@Option(category = "Game")
	public static boolean showFPS = true;

	@ButtonOption
	@Option(category = "Game")
	public static boolean showBBs = true;

	private static final String TAG = "BunnyGameState";

	GameStateIdentifier nextGameState;

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1);

	private float timePassed;

	private TextGUIElement fps;

	private ShaderProgram guiShader;

	protected ICamera camera;
	Scene bunnyScene;
	//PointLight pl;
	//DirectionalLight dl;
	SpotLight sl;

	ShaderProgram curveShader;

	ArrayList<Light> lights = new ArrayList<>();

	BezierCurve curve;

	AnimationStateFacade animBunny, animPlayer;

	SceneObject playerObj, bunny0, bunny1, bunny2, bunny3, bunny4, curveObj;

	Entity mainBunny, followingBunny1, followingBunny2, followingBunny3, followingBunny4, camEntity, playerEntity,
			spotLightEntity, ple, lampEntity;

	private TextGUI textGUI;

	@Option(category = "Game")
	@SliderOption(min = 3, max = 300, step = 1)
	public static int bezierCurveSize = 10;

	@Override
	public void init() {
		timePassed = 0;

		camera = new Camera(OpenGLContext.getAspectRatio());

		camEntity = new CameraEntity(camera);

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(camera, skybox);

		AnimatedMesh playerAnim = new ObjectLoader().loadFromFileAnim("player.fbx");
		playerAnim.setTransform(MeshFlip);
		Texture bunnyTexture = new Texture("player.png");
		Texture lampTexture = new Texture("lamp.png");
		playerAnim.getSkeleton().printRecursive("");

		final AnimatedMesh bunnyAnim = new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		bunnyAnim.setTransform(MeshFlip);
		System.out.println();
		bunnyAnim.getSkeleton().printRecursive("");

		curveShader = new ShaderProgram("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(UBOManager.MATRICES);
		curveShader.setDrawMode(GL_LINES);

		final ShaderProgram animatedShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(animatedShader);
		animatedShader.addUniformBlockIndex(UBOManager.MATRICES);

		final ShaderProgram stationaryShader = new ShaderProgram("basic.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(stationaryShader);
		stationaryShader.addUniformBlockIndex(UBOManager.MATRICES);

		final IRenderable boxRenderable = new ObjectLoader().loadFromFile("box.obj");
		SceneObject box = new SceneObject(new TexturedRenderable(boxRenderable, bunnyTexture));
		box.setShader(stationaryShader);

		final IRenderable lampRenderable = new ObjectLoader().loadFromFile("lamp.obj");
		SceneObject lamp = new SceneObject(new TexturedRenderable(lampRenderable, lampTexture));
		lamp.setShader(stationaryShader);

		animPlayer = new AnimationStateFacade(playerAnim);
		animBunny = new AnimationStateFacade(bunnyAnim);

		playerObj = new SceneObject(new TexturedRenderable(animPlayer, bunnyTexture));
		bunny0 = new SceneObject(new TexturedRenderable(animBunny, bunnyTexture));
		bunny1 = new SceneObject(new TexturedRenderable(animBunny, bunnyTexture));
		bunny2 = new SceneObject(new TexturedRenderable(animBunny, bunnyTexture));
		bunny3 = new SceneObject(new TexturedRenderable(animBunny, bunnyTexture));
		bunny4 = new SceneObject(new TexturedRenderable(animBunny, bunnyTexture));

		playerEntity = new SceneEntity(playerObj);
		mainBunny = new SceneEntity(bunny0);
		followingBunny1 = new SceneEntity(bunny1);
		followingBunny2 = new SceneEntity(bunny2);
		followingBunny3 = new SceneEntity(bunny3);
		followingBunny4 = new SceneEntity(bunny4);
		lampEntity = new SceneEntity(lamp);

		mainBunny.addBehaviour(1, new BezierBehaviour(curve, 4));

		followingBunny1.addBehaviour(1, new FollowingBehaviour(mainBunny, 3f).limited(5));
		followingBunny1.addBehaviour(2, new FollowingBehaviour(mainBunny, 7.6f));

		followingBunny2.addBehaviour(1, new FollowingBehaviour(followingBunny1, 3f).limited(5));
		followingBunny2.addBehaviour(2, new FollowingBehaviour(followingBunny1, 7.6f));

		followingBunny3.addBehaviour(1, new FollowingBehaviour(followingBunny2, 3f).limited(5));
		followingBunny3.addBehaviour(2, new FollowingBehaviour(followingBunny2, 7.6f));

		followingBunny4.addBehaviour(1, new FollowingBehaviour(followingBunny3, 3f).limited(5));
		followingBunny4.addBehaviour(2, new FollowingBehaviour(followingBunny3, 7.6f));

		camEntity.addBehaviour(1, new PlayerControlBehaviour());

		bunnyScene.getSceneGraph().addSubObject(playerObj);
		bunnyScene.getSceneGraph().addSubObject(bunny0);
		bunnyScene.getSceneGraph().addSubObject(bunny1);
		bunnyScene.getSceneGraph().addSubObject(bunny2);
		bunnyScene.getSceneGraph().addSubObject(bunny3);
		bunnyScene.getSceneGraph().addSubObject(bunny4);
		bunnyScene.getSceneGraph().addSubObject(box);
		bunnyScene.getSceneGraph().addSubObject(lamp);

		bunnyScene.getSceneGraph().setShader(animatedShader);

		//a light on the hand
		PointLight pl = new PointLight(new Color(240, 245, 255), new Vector3f(), 50);
		ple = new PointLightEntity(pl);
		bunnyScene.getLightManager().addLight(pl);
		ple.addBehaviour(1, new ParentBehaviour(lamp, new Vector3f(0, -0.5f, 0)));

		lampEntity.addBehaviour(1, new BoneTrackingBehaviour(playerObj, animPlayer.getRenderable(), "Hand.L",
				new Vector3f(0, 0.5f, 0)).fixedDirection());

		//pl = new PointLight(Color.GREEN, new Vector3f(0, 10, 0), 1000);
		//bunnyScene.getLightManager().addLight(pl);
		DirectionalLight dl = new DirectionalLight(new Vector3f(1, 0.9f, 0.5f), new Vector3f(-0.1f, -1f, 0));
		bunnyScene.getLightManager().addLight(dl);

		sl = new SpotLight(Color.ORANGE, new Vector3f(0, -0.25f, 0), new Vector3f(0, 1, 0), 5, 10, 1000);
		bunnyScene.getLightManager().addLight(sl);
		spotLightEntity = new SpotLightEntity(sl);
		spotLightEntity.addBehaviour(1, new FollowingBehaviour(followingBunny3, 3f).limited(5));
		spotLightEntity.addBehaviour(9001, new FollowingBehaviour(followingBunny3, 7.6f));

		glEnable(GL_CULL_FACE);
		GLErrors.checkForError(TAG, "glEnable");
		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");

		animPlayer.registerAnimation("jogging", "running", 25, 2, 0.5);
		animPlayer.registerAnimation("hat", "wave", 10, 0.5, 0.5);

		animBunny.registerAnimation("ohr1", "OhrenFlackern1", 4);
		animBunny.registerAnimation("ohr2", "OhrenFlackern2", 4);
		animBunny.registerAnimation("party", "HeadBang", 4);

		textGUI = new TextGUI(new Font("Comic Sans MS", Font.PLAIN, 128));
		guiShader = new ShaderProgram("gui.vert", "gui.frag");
		textGUI.setShader(guiShader);
		fps = textGUI.addText("0", new Vector2f(), 0.03f);
		fps.setColor(Color.ORANGE);
		fps.setPositionRelativeToScreen(0, 0);

		generateBezierCurve();
	}

	private void generateBezierCurve() {
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		final Random random = new Random();
		for (int i = 0; i < bezierCurveSize; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);
		mainBunny.getBehaviours().clear();
		mainBunny.addBehaviour(1, new BezierBehaviour(curve, 4));
		bunnyScene.getSceneGraph().removeSubObject(curveObj);
		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);
		bunnyScene.getSceneGraph().addSubObject(curveObj);
	}

	@Override
	public void update(final double deltaTime) {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			nextGameState = GameStateIdentifier.MAIN_MENU;
		}
		if (curve.getControlPoints().size() != bezierCurveSize * 3 + 1) {
			generateBezierCurve();
		}

		fps.setText("FPS: " + (int) (1 / deltaTime));
		//fps.setPositionRelativeToScreen(0, 0);
		textGUI.update(deltaTime);

		timePassed += deltaTime;

		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			animPlayer.slideSpeed("jogging", 50, deltaTime, 5);
		} else {
			animPlayer.slideSpeed("jogging", 25, deltaTime, 5);
		}
		animPlayer.ensureRunning("jogging", KeyBoard.isKeyDown(GLFW_KEY_Q));
		animPlayer.ensureRunning("hat", KeyBoard.isKeyDown(GLFW_KEY_E), false);

		if (timePassed > 3) {
			animBunny.ensureRunning("ohr1");
		}
		if (timePassed > 6) {
			animBunny.ensureRunning("ohr2");
		}
		if (timePassed > 9) {
			animBunny.ensureRunning("party");
		}

		mainBunny.update(deltaTime);
		followingBunny1.update(deltaTime);
		followingBunny2.update(deltaTime);
		followingBunny3.update(deltaTime);
		followingBunny4.update(deltaTime);
		camEntity.update(deltaTime);
		lampEntity.update(deltaTime);

		//pl.setColor(new Color((float) java.lang.Math.sin(timepassed) / 2 + 0.5f, (float) 1.0, (float) java.lang.Math.cos(timepassed) / 2 + 0.5f));
		//pl.setPosition(new Vector3f((float) java.lang.Math.sin(timepassed) * 5, 10, (float) java.lang.Math.cos(timepassed) * 5));
		ple.update(deltaTime);
		spotLightEntity.update(deltaTime);
		bunnyScene.update(deltaTime);
		textGUI.update(deltaTime);
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		bunnyScene.render(showBBs); //bunnyScene.render(); to render without BoundingBoxes
		if (showFPS) {
			textGUI.render();
		}
	}

	@Override
	public void clear() {
		TextureCache.clearCache();
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
		OpenGLContext.hideCursor();
	}

}
