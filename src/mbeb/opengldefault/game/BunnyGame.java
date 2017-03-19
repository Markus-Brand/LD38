package mbeb.opengldefault.game;

import java.util.ArrayList;
import java.util.Random;

import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimationStateFacade;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.opengl.GL11.*;

import java.awt.*;

import mbeb.opengldefault.scene.behaviour.*;
import org.joml.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.rendering.textures.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.SceneEntity;

import org.lwjgl.glfw.GLFW;

/**
 * Object to characterize a whole game
 */
public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1);

	private float timePassed;

	protected ICamera camera;
	Scene bunnyScene;
	PointLight pl;
	DirectionalLight dl;
	SpotLight sl;
	ArrayList<Light> lights = new ArrayList<>();

	BezierCurve curve;

	AnimationStateFacade animBunny, animPlayer;

	SceneObject playerObj, bunny0, bunny1, bunny2, bunny3, bunny4, curveObj;

	Entity mainBunny, followingBunny1, followingBunny2, followingBunny3, followingBunny4, camEntity, playerEntity;

	@Override
	public void init() {
		timePassed = 0;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();
		final Random random = new Random();
		for (int i = 0; i < 10; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);

		camera = new Camera(getContext().getAspectRatio());
		camEntity = new CameraEntity(camera);
		final Skybox skybox = new Skybox("skybox/mountain");
		bunnyScene = new Scene(camera, skybox);

		AnimatedMesh playerAnim = new ObjectLoader().loadFromFileAnim("player.fbx");
		playerAnim.setTransform(MeshFlip);
		Texture bunnyTexture = new Texture("bunny_2d.png");
		playerAnim.getSkeleton().printRecursive("");

		final AnimatedMesh bunnyAnim = new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		bunnyAnim.setTransform(MeshFlip);
		System.out.println();
		bunnyAnim.getSkeleton().printRecursive("");

		final Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(UBOManager.MATRICES);
		curveShader.setDrawMode(GL_LINES);

		final Shader defaultShader = new Shader("boneAnimation.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(defaultShader);
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);

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

		mainBunny.addBehaviour(1, new BezierBehaviour(curve, 4));

		followingBunny1.addBehaviour(1, new FollowingBehaviour(mainBunny, 3f).limited(5));
		followingBunny1.addBehaviour(2, new FollowingBehaviour(mainBunny, 7.6f));

		followingBunny2.addBehaviour(1, new FollowingBehaviour(followingBunny1, 3f).limited(5));
		followingBunny2.addBehaviour(2, new FollowingBehaviour(followingBunny1, 7.6f));

		followingBunny3.addBehaviour(1, new FollowingBehaviour(followingBunny2, 3f).limited(5));
		followingBunny3.addBehaviour(2, new FollowingBehaviour(followingBunny2, 7.6f));

		followingBunny4.addBehaviour(1, new FollowingBehaviour(followingBunny3, 3f).limited(5));
		followingBunny4.addBehaviour(2, new FollowingBehaviour(followingBunny3, 7.6f));

		camEntity.addBehaviour(1, new CombinedBehaviour(
				new BoneTrackingBehaviour(playerObj, animPlayer.getRenderable(), "Hand.L", new Vector3f(0, 0.5f, 0)),
				new PitchYawMouseBehaviour()));

		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);

		bunnyScene.getSceneGraph().addSubObject(playerObj);
		bunnyScene.getSceneGraph().addSubObject(bunny0);
		bunnyScene.getSceneGraph().addSubObject(bunny1);
		bunnyScene.getSceneGraph().addSubObject(bunny2);
		bunnyScene.getSceneGraph().addSubObject(bunny3);
		bunnyScene.getSceneGraph().addSubObject(bunny4);
		bunnyScene.getSceneGraph().addSubObject(curveObj);

		bunnyScene.getSceneGraph().setShader(defaultShader);

		pl = new PointLight(Color.GREEN, new Vector3f(0, 10, 0), 50);
		bunnyScene.getLightManager().addLight(pl);

		dl = new DirectionalLight(new Vector3f(1.0f, 0.7f, 0.2f).mul(0.2f), new Vector3f(0.2f, -1f, 0));
		bunnyScene.getLightManager().addLight(dl);

		sl = new SpotLight(Color.ORANGE, new Vector3f(0, -0.25f, 0), new Vector3f(0, 1, 0), 10, 5, 1);
		//bunnyScene.getLightManager().addLight(sl);

		glEnable(GL_CULL_FACE);
		GLErrors.checkForError(TAG, "glEnable");
		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");

		animPlayer.registerAnimation("jogging", "running", 25, 2, 0.5);
		animPlayer.registerAnimation("hat", "wave", 10, 0.5, 0.5);

		animBunny.registerAnimation("ohr1", "OhrenFlackern1", 4);
		animBunny.registerAnimation("ohr2", "OhrenFlackern2", 4);
		animBunny.registerAnimation("party", "HeadBang", 4);
	}

	@Override
	public void update(final double deltaTime) {

		timePassed += deltaTime;
		
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			animPlayer.slideSpeed("jogging", 50, deltaTime * 5);
		} else {
			animPlayer.slideSpeed("jogging", 25, deltaTime * 5);
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

		pl.setColor(new Color((float) java.lang.Math.sin(timePassed) / 2 + 0.5f, (float) 1.0, (float) java.lang.Math.cos(timePassed) / 2 + 0.5f));
		pl.setPosition(new Vector3f((float) java.lang.Math.sin(timePassed) * 5, 10, (float) java.lang.Math.cos(timePassed) * 5));

		bunnyScene.update(deltaTime);
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, getContext().getFramebufferWidth(), getContext().getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		bunnyScene.render(true); //bunnyScene.render(); to render without BoundingBoxes
	}

	@Override
	public void clear() {
		TextureCache.clearCache();
	}

}
