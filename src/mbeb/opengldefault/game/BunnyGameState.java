package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.*;
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
import java.util.List;
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
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.*;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

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
	
	Entity spotLightEntity, ple;
	
	EntityWorld entityWorld;

	@Override
	public void init() {
		timePassed = 0;
		curve = generateRandomBezier();
		BezierCurve curve2 = generateRandomBezier();
		
		camera = new Camera(OpenGLContext.getAspectRatio());
		final Skybox skybox = new Skybox("skybox/mountain");
		bunnyScene = new Scene(camera, skybox);
		
		AnimatedMesh playerAnimMesh = new ObjectLoader().loadFromFileAnim("player.fbx");
		playerAnimMesh.setTransform(MeshFlip);
		Texture bunnyTexture = new Texture("player.png");
		Texture lampTexture = new Texture("lamp.png");
		playerAnimMesh.getSkeleton().printRecursive("");
		
		final AnimatedMesh bunnyAnimMesh = new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		bunnyAnimMesh.setTransform(MeshFlip);
		System.out.println();
		bunnyAnimMesh.getSkeleton().printRecursive("");
		
		final ShaderProgram curveShader = new ShaderProgram("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(UBOManager.MATRICES);
		curveShader.setDrawMode(GL_LINES);
		
		final ShaderProgram animatedShader = new ShaderProgram("boneAnimation.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(animatedShader);
		animatedShader.addUniformBlockIndex(UBOManager.MATRICES);
		
		final ShaderProgram stillShader = new ShaderProgram("basic.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(stillShader);
		stillShader.addUniformBlockIndex(UBOManager.MATRICES);
		
		final IRenderable boxRenderable = new ObjectLoader().loadFromFile("box.obj");
		SceneObject box = new SceneObject(new TexturedRenderable(boxRenderable, bunnyTexture));
		box.setShader(stillShader);
		
		final IRenderable lampRenderable = new ObjectLoader().loadFromFile("lamp.obj");
		SceneObject lamp = new SceneObject(new TexturedRenderable(lampRenderable, lampTexture));
		lamp.setShader(stillShader);
		
		animPlayer = new AnimationStateFacade(playerAnimMesh);
		
		playerObj = new SceneObject(new TexturedRenderable(animPlayer, bunnyTexture));
		playerObj.setShader(animatedShader);
		
		entityWorld = new EntityWorld();
		
		entityWorld.add(camera).addBehaviour(1, new PlayerControlBehaviour());
		
		IEntity lastBunnyEntity = createBunnyChain(bunnyScene.getSceneGraph(), entityWorld, bunnyTexture, bunnyAnimMesh, curve);
		createBunnyChain(bunnyScene.getSceneGraph(), entityWorld, bunnyTexture, bunnyAnimMesh, curve2);
		
		entityWorld.add(lamp).addBehaviour(1,
				new BoneTrackingBehaviour(playerObj, animPlayer.getRenderable(), "Hand.L", new Vector3f(0, 0.5f, 0))
						.fixedDirection());
		
		
		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);
		SceneObject curveObj2 = new SceneObject(new BezierCurveRenderable(curve2));
		curveObj2.setShader(curveShader);
		
		//bunnyScene.getSceneGraph().addSubObject(playerObj);
		bunnyScene.getSceneGraph().addSubObject(curveObj);
		//bunnyScene.getSceneGraph().addSubObject(box);
		//bunnyScene.getSceneGraph().addSubObject(lamp);
		
		bunnyScene.getSceneGraph().setShader(animatedShader);
		
		
		//a light on the hand
		PointLight pl = new PointLight(new Color(0, 245, 5), new Vector3f(), 75);
		ple = new PointLightEntity(pl);
		bunnyScene.getLightManager().addLight(pl);
		ple.addBehaviour(1, new ParentBehaviour(lamp, new Vector3f(0, -1.5f, 0)));
		
		//pl = new PointLight(Color.GREEN, new Vector3f(0, 10, 0), 1000);
		//bunnyScene.getLightManager().addLight(pl);
		DirectionalLight dl = new DirectionalLight(new Vector3f(1, 0.9f, 0.5f), new Vector3f(-0.1f, -1f, 0));
		bunnyScene.getLightManager().addLight(dl);
		
		sl = new SpotLight(Color.ORANGE, new Vector3f(0, -0.25f, 0), new Vector3f(0, 1, 0), 5, 10, 1000);
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
		
		
		for (AnimationStateFacade facade: animBunnyList) {
			facade.registerAnimation("ohr1", "OhrenFlackern1", 4);
			facade.registerAnimation("ohr2", "OhrenFlackern2", 4);
			facade.registerAnimation("party", "HeadBang", 4);
		}
		
	}
	
	private BezierCurve generateRandomBezier() {
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();
		final Random random = new Random();
		for (int i = 0; i < 10; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		return new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);
	}
	
	/**
	 * creates a list of following bunnys
	 * @param bunnyParent the SceneOIbject to add the list to
	 * @param world the entityWorld to animate the bunnies in
	 * @param bunnyTexture the texture to apply
	 * @param renderable the renderable to display
	 * @param curve the curve to follow
	 * @return last bunny
	 */
	private IEntity createBunnyChain(SceneObject bunnyParent, EntityWorld world, Texture bunnyTexture, AnimatedMesh renderable, BezierCurve curve) {
		AnimationStateFacade mainBunnyFacade = new AnimationStateFacade(renderable);
		animBunnyList.add(mainBunnyFacade);
		final SceneObject mainBunnyObj = new SceneObject(new TexturedRenderable(mainBunnyFacade, bunnyTexture));
		bunnyParent.addSubObject(mainBunnyObj);
		
		SceneObject toFollowObject = mainBunnyObj;
		IEntity toFollow = world.add(toFollowObject).addBehaviour(1, new BezierBehaviour(curve, 3));
		
		int bunnyCount = 100;
		for (int b = 0; b < bunnyCount; b++) {
			AnimationStateFacade followingBunnyFacade = new AnimationStateFacade(renderable);
			animBunnyList.add(followingBunnyFacade);
			SceneObject followerObject = new SceneObject(new TexturedRenderable(followingBunnyFacade, bunnyTexture));
			bunnyParent.addSubObject(followerObject);
			toFollow = world.add(followerObject)
					           .addBehaviour(1, new SmoothFollowingBehaviour(toFollow, 1f));
			//.addBehaviour(1, new FollowingBehaviour(toFollow, 0.5f).limited(5))
			//.addBehaviour(2, new FollowingBehaviour(toFollow, 7f));
			toFollowObject = followerObject;
		}
		return toFollow;
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
		for (AnimationStateFacade facade: animBunnyList) {
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
		ple.update(deltaTime);
		spotLightEntity.update(deltaTime);
		bunnyScene.update(deltaTime);
		
		if (KeyBoard.isKeyDown(GLFW_KEY_ESCAPE)) {
			nextGameState = GameStateIdentifier.MAIN_MENU;
		}
	}
	
	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");
		
		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");
		
		bunnyScene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB)); //bunnyScene.render(); to render without BoundingBoxes
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
