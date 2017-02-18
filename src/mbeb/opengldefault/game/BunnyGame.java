package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

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
import mbeb.opengldefault.scene.behaviour.LimitedDistanceFollowingBehaviour;
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

	private float timePassed;

	protected ICamera cam;
	Scene bunnyScene;

	BezierCurve curve;

	SceneObject cubeObj, bunnyObj, cowboy, curveObj;

	Entity followed, follower, camEntity;

	@Override
	public void init() {
		timePassed = 0;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		controlPoints.add(new Vector3f(20, 5, 0));
		controlPoints.add(new Vector3f(0, -10, 20));
		controlPoints.add(new Vector3f(-20, 10, 0));
		controlPoints.add(new Vector3f(0, 15, -20));
		controlPoints.add(new Vector3f(0, 0, 0));

		curve = new BezierCurve(controlPoints, ControlPointInputMode.CameraPointsCircular, true);

		cam = new FirstPersonCamera(new Vector3f(), new Vector3f());

		camEntity = new CameraEntity(cam);

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		IRenderable tm =
				new TexturedRenderable(new ObjectLoader().loadFromFile("thinmatrix.dae"), new Texture("bunny_2d.png"));
		IRenderable bunny =
				new TexturedRenderable(new ObjectLoader().loadFromFile("bunny.obj"), new Texture("bunny_2d.png"));
		IRenderable cube = new TexturedRenderable(new ObjectLoader().loadFromFile("cube.obj"), new Texture("AO.png"));

		Shader bonePhongShader = new Shader("boneAnimation.vert", "phong.frag");
		bonePhongShader.addUniformBlockIndex(1, "Matrices");

		Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(1, "Matrices");
		curveShader.setDrawMode(GL_LINES);

		final Shader defaultShader = new Shader("basic.vert", "phong.frag");
		defaultShader.addUniformBlockIndex(1, "Matrices");

		bunnyObj = new SceneObject(bunny, new Matrix4f().translate(1, 1, 1), null);
		followed = new SceneEntity(bunnyObj);
		followed.addBehaviour(1, new BezierBehaviour(curve, 10));

		cowboy = new SceneObject(tm, new Matrix4f().translate(10, 0, 0), null);
		follower = new SceneEntity(cowboy);
		follower.addBehaviour(1, new LimitedDistanceFollowingBehaviour(followed, 7, 5));
		follower.addBehaviour(2, new FollowingBehaviour(followed, 1f));
		camEntity.addBehaviour(1, new BezierBehaviour(curve, 8f));

		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);

		cubeObj = new SceneObject(cube);
		cubeObj.addSubObject(bunnyObj);

		bunnyScene.getSceneGraph().addSubObject(cowboy);
		bunnyScene.getSceneGraph().addSubObject(cubeObj);
		bunnyScene.getSceneGraph().addSubObject(curveObj);

		bunnyScene.getSceneGraph().setShader(defaultShader);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void update(final double deltaTime) {
		timePassed += deltaTime;

		followed.update(deltaTime);
		follower.update(deltaTime);
		camEntity.update(deltaTime);

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getWidth(), OpenGLContext.getHeight());
		GLErrors.checkForError(TAG, "glViewport");

		bunnyScene.update(deltaTime);
	}

	@Override
	public void render() {
		bunnyScene.render();
	}

	@Override
	public void clear() {
		TextureCache.clearCache();
	}

}
