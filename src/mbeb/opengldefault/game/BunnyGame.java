package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.awt.*;
import java.util.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.curves.*;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.openglcontext.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.rendering.textures.*;
import mbeb.opengldefault.scene.*;

import org.joml.*;

/**
 * Object to characterize a whole game
 */
public class BunnyGame implements IGame {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	protected ICamera cam;
	Scene bunnyScene;
	PointLight pl;
	DirectionalLight dl;
	SpotLight sl;
	double timepassed = 0;
	ArrayList<Light> lights = new ArrayList<>();

	BezierCurve curve;

	SceneObject cubeObj, bunnyObj, cowboy, curveObj;

	@Override
	public void init() {
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		controlPoints.add(new Vector3f(20, 5, 0));
		controlPoints.add(new Vector3f(0, -10, 20));
		controlPoints.add(new Vector3f(-20, 10, 0));
		controlPoints.add(new Vector3f(0, 15, -20));
		controlPoints.add(new Vector3f(0, 0, 0));

		curve = new BezierCurve(controlPoints, ControlPointInputMode.CameraPointsCircular, true);

		cam = new FirstPersonCamera(new Vector3f(), new Vector3f());
		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		final IRenderable tm = new TexturedRenderable(new ObjectLoader().loadFromFileAnim("thinmatrix.dae"), new Texture("bunny_2d.png"));
		final IRenderable bunny = new TexturedRenderable(new ObjectLoader().loadFromFile("bunny.obj"), new Texture("bunny_2d.png"));
		final IRenderable cube = new TexturedRenderable(new ObjectLoader().loadFromFile("cube.obj"), new Texture("AO.png"));

		//final Shader bonePhongShader = new Shader("boneAnimation.vert", "phong.frag");
		//bonePhongShader.addUniformBlockIndex(1, "Matrices");

		final Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(UBOManager.MATRICES);
		curveShader.setDrawMode(GL_LINES);

		final Shader defaultShader = new Shader("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);
		bunnyScene.getLightManager().addShader(defaultShader);

		pl = new PointLight(new Vector3f(0, 1, 0), new Vector3f(0, 10, 0), 300);
		bunnyScene.getLightManager().addLight(pl);
		dl = new DirectionalLight(new Vector3f(0, 1, 0), new Vector3f(0, 1, 0));
		bunnyScene.getLightManager().addLight(dl);
		sl = new SpotLight(new Vector3f(1, 0, 0), new Vector3f(10, 0, 0), new Vector3f(-1, 0, 0), 1, 2, 500);
		bunnyScene.getLightManager().addLight(sl);

		//cowboy = new SceneObject(tm, new Matrix4f(), null);
		//cowboy.setShader(bonePhongShader);

		cubeObj = new SceneObject(cube);

		bunnyObj = new SceneObject(bunny, new Matrix4f().translate(1, 1, 1), null);

		curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);

		cubeObj.addSubObject(bunnyObj);

		//bunnyScene.getSceneGraph().addSubObject(cowboy);
		bunnyScene.getSceneGraph().addSubObject(cubeObj);
		bunnyScene.getSceneGraph().addSubObject(curveObj);

		bunnyScene.getSceneGraph().setShader(defaultShader);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void update(final double deltaTime) {
		timepassed += deltaTime;
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getWidth(), OpenGLContext.getHeight());
		GLErrors.checkForError(TAG, "glViewport");

		pl.setColor(new Color((float) java.lang.Math.sin(timepassed) / 2 + 0.5f, (float) 1.0, (float) java.lang.Math.cos(timepassed) / 2 + 0.5f));
		pl.setPosition(new Vector3f((float) java.lang.Math.sin(timepassed) * 5, 10, (float) java.lang.Math.cos(timepassed) * 5));

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
