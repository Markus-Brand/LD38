package mbeb.opengldefault.game;

import java.util.ArrayList;
import java.util.Random;
import mbeb.opengldefault.animation.AnimatedRenderable;
import static org.lwjgl.opengl.GL11.*;


import mbeb.opengldefault.camera.*;
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
public class BunnyGame implements IGame {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";
	
	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1);
	
	private float timePassed;

	protected ICamera cam;
	Scene bunnyScene;

	BezierCurve curve;

	SceneObject bunny0, bunny1, bunny2, bunny3, bunny4, curveObj;

	Entity mainBunny, followingBunny1, followingBunny2, followingBunny3, followingBunny4, camEntity;

	@Override
	public void init() {
		timePassed = 0;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);

		cam = new Camera();

		camEntity = new CameraEntity(cam);

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		AnimatedRenderable bunnyAnim = new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		bunnyAnim.getAnimatedMesh().setTransform(MeshFlip);
		IRenderable bunnyTextured = new TexturedRenderable(bunnyAnim, new Texture("bunny_2d.png"));

		final Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(1, "Matrices");
		curveShader.setDrawMode(GL_LINES);

		final Shader defaultShader = new Shader("boneAnimation.vert", "phong.frag");
		defaultShader.addUniformBlockIndex(1, "Matrices");

		bunny0 = new SceneObject(bunnyTextured);
		bunny1 = new SceneObject(bunnyTextured);
		bunny2 = new SceneObject(bunnyTextured);
		bunny3 = new SceneObject(bunnyTextured);
		bunny4 = new SceneObject(bunnyTextured);
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
		
		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyAnim.playAnimation("OhrenFlackern1", 4);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyAnim.playAnimation("HeadBang", 4);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyAnim.playAnimation("OhrenFlackern2", 4);
			}
		}.start();/**/
	}

	@Override
	public void update(final double deltaTime) {
		timePassed += deltaTime;

		mainBunny.update(deltaTime);
		followingBunny1.update(deltaTime);
		followingBunny2.update(deltaTime);
		followingBunny3.update(deltaTime);
		//followingBunny4.update(deltaTime);
		camEntity.update(deltaTime);

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		bunnyScene.update(deltaTime);
	}

	@Override
	public void render() {
		bunnyScene.render(true); //bunnyScene.render(); to render without BoundingBoxes
	}

	@Override
	public void clear() {
		TextureCache.clearCache();
	}

}
