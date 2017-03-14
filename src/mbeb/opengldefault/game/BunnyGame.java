package mbeb.opengldefault.game;

import java.util.ArrayList;
import java.util.Random;

import mbeb.opengldefault.animation.AnimatedMesh;
import mbeb.opengldefault.animation.AnimatedRenderable;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.opengl.GL11.*;


import mbeb.opengldefault.animation.Animator;
import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.openglcontext.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.rendering.textures.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.BezierBehaviour;
import mbeb.opengldefault.scene.behaviour.FollowingBehaviour;
import mbeb.opengldefault.scene.behaviour.LimitedDistanceBehaviour;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.SceneEntity;

import org.joml.*;

/**
 * Object to characterize a whole game
 */
public class BunnyGame extends Game {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";
	
	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1);
	
	private float timePassed;

	protected ICamera cam;
	Scene bunnyScene;

	BezierCurve curve;

	AnimatedRenderable animBunny0, animBunny1, animBunny2, animBunny3, animBunny4;

	SceneObject bunny0, bunny1, bunny2, bunny3, bunny4, curveObj;

	Entity mainBunny, followingBunny1, followingBunny2, followingBunny3, followingBunny4, camEntity;

	boolean waving = false, running = false;

	Animator runningAnim, waveAnim;

	@Override
	public void init() {
		timePassed = 0;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);

		cam = new Camera(getContext().getAspectRatio());

		camEntity = new CameraEntity(cam);

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		AnimatedMesh bunnyAnim = new ObjectLoader().loadFromFileAnim("player.fbx");
		bunnyAnim.setTransform(MeshFlip);
		Texture bunnyTexture = new Texture("bunny_2d.png");

		final Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(1, "Matrices");
		curveShader.setDrawMode(GL_LINES);

		final Shader defaultShader = new Shader("boneAnimation.vert", "phong.frag");
		defaultShader.addUniformBlockIndex(1, "Matrices");

		animBunny0 = new AnimatedRenderable(bunnyAnim);
		animBunny1 = new AnimatedRenderable(bunnyAnim);
		animBunny2 = new AnimatedRenderable(bunnyAnim);
		animBunny3 = new AnimatedRenderable(bunnyAnim);
		animBunny4 = new AnimatedRenderable(bunnyAnim);

		bunny0 = new SceneObject(new TexturedRenderable(animBunny0, bunnyTexture));
		bunny1 = new SceneObject(new TexturedRenderable(animBunny1, bunnyTexture));
		bunny2 = new SceneObject(new TexturedRenderable(animBunny2, bunnyTexture));
		bunny3 = new SceneObject(new TexturedRenderable(animBunny3, bunnyTexture));
		bunny4 = new SceneObject(new TexturedRenderable(animBunny4, bunnyTexture));
		mainBunny = new SceneEntity(bunny0);
		followingBunny1 = new SceneEntity(bunny1);
		followingBunny2 = new SceneEntity(bunny2);
		followingBunny3 = new SceneEntity(bunny3);
		followingBunny4 = new SceneEntity(bunny4);

		mainBunny.addBehaviour(1, new BezierBehaviour(curve, 4));

		followingBunny1.addBehaviour(1, new LimitedDistanceBehaviour(new FollowingBehaviour(mainBunny, 3f), 5));
		followingBunny1.addBehaviour(2, new FollowingBehaviour(mainBunny, 7.6f));

		followingBunny2.addBehaviour(1, new LimitedDistanceBehaviour(new FollowingBehaviour(followingBunny1, 3f), 5));
		followingBunny2.addBehaviour(2, new FollowingBehaviour(followingBunny1, 7.6f));

		followingBunny3.addBehaviour(1, new LimitedDistanceBehaviour(new FollowingBehaviour(followingBunny2, 3f), 5));
		followingBunny3.addBehaviour(2, new FollowingBehaviour(followingBunny2, 7.6f));

		followingBunny4.addBehaviour(1, new LimitedDistanceBehaviour(new FollowingBehaviour(followingBunny3, 3f), 5));
		followingBunny4.addBehaviour(2, new FollowingBehaviour(followingBunny3, 7.6f));

		camEntity.addBehaviour(1, new PlayerControlBehaviour());

		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);

		bunnyScene.getSceneGraph().addSubObject(bunny0);
		bunnyScene.getSceneGraph().addSubObject(bunny1);
		bunnyScene.getSceneGraph().addSubObject(bunny2);
		bunnyScene.getSceneGraph().addSubObject(bunny3);
		bunnyScene.getSceneGraph().addSubObject(bunny4);
		bunnyScene.getSceneGraph().addSubObject(curveObj);

		bunnyScene.getSceneGraph().setShader(defaultShader);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		runningAnim = new Animator(bunnyAnim.getAnimationByName("running"), 25, 3 * 25, 3 * 25);
		waveAnim = new Animator(bunnyAnim.getAnimationByName("wave"), 10, 1 * 10 , 1 * 10);

	}

	@Override
	public void update(final double deltaTime) {
		timePassed += deltaTime;

		if (KeyBoard.isKeyDown(GLFW_KEY_Q)) {
			if (!waving) {
				waving = true;
				animBunny4.playAnimation(new Animator(waveAnim));
			}
		} else {
			if (waving) {
				waving = false;
				animBunny4.stopAnimationsOf(waveAnim.getAnimation());
			}
		}
		if (KeyBoard.isKeyDown(GLFW_KEY_E)) {
			if (!running) {
				running = true;
				animBunny4.playAnimation(new Animator(runningAnim));
			}
		} else {
			if (running) {
				running = false;
				animBunny4.stopAnimationsOf(runningAnim.getAnimation());
			}
		}

		mainBunny.update(deltaTime);
		followingBunny1.update(deltaTime);
		followingBunny2.update(deltaTime);
		followingBunny3.update(deltaTime);
		//followingBunny4.update(deltaTime);
		camEntity.update(deltaTime);

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
