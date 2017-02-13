package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.BezierCurveRenderable;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.Transformation;

import org.joml.Vector3f;

/**
 * Object to characterize a whole game
 */
public class BunnyGame implements IGame {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	protected ICamera cam;
	Scene bunnyScene;

	BezierCurve curve;

	SceneObject cubeObj, bunnyObj, bunnyObj2, curveObj;

	@Override
	public void init() {
		ArrayList<Vector3f> controlPoints = new ArrayList<>();

		controlPoints.add(new Vector3f(20, 5, 0));
		controlPoints.add(new Vector3f(0, -10, 20));
		controlPoints.add(new Vector3f(-20, 10, 0));
		controlPoints.add(new Vector3f(0, 15, -20));

		curve = new BezierCurve(controlPoints, ControlPointInputMode.CameraPointsCircular, true);

		cam = new FirstPersonCamera(new Vector3f(), new Vector3f(1, 0, 0));

		Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		IRenderable bunny = new TexturedRenderable(new ObjectLoader().loadFromFile("bunny.obj"), new Texture("bunny_2d.png"));
		IRenderable cube = new TexturedRenderable(new ObjectLoader().loadFromFile("cube.obj"), new Texture("bunny_2d.png"));

		cubeObj = new SceneObject(cube, null, null);
		bunnyObj = new SceneObject(bunny, Transformation.fromPosition(new Vector3f(1, 1, 1)), null);
		bunnyObj2 = new SceneObject(bunny, Transformation.fromPosition(new Vector3f(1, -1, 1)), null);
		curveObj = new SceneObject(new BezierCurveRenderable(curve), null, null);

		Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(1, "Matrices");
		curveShader.setDrawMode(GL_LINES);

		curveObj.setShader(curveShader);

		cubeObj.addSubObject(bunnyObj);
		bunnyScene.getSceneGraph().addSubObject(bunnyObj2);
		bunnyScene.getSceneGraph().addSubObject(cubeObj);
		bunnyScene.getSceneGraph().addSubObject(curveObj);

		Shader defaultShader = new Shader("basic.vert", "phong.frag");
		defaultShader.addUniformBlockIndex(1, "Matrices");
		bunnyScene.getSceneGraph().setShader(defaultShader);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void update(double deltaTime) {

		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getWidth(), OpenGLContext.getHeight());
		GLErrors.checkForError(TAG, "glViewport");

		cubeObj.getTransformation().asMatrix().rotate((float) deltaTime, new Vector3f(1, 1, 0));
		bunnyObj.getTransformation().asMatrix().rotate((float) deltaTime * 3, new Vector3f(0, 1, 0));

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
