package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;

import mbeb.opengldefault.camera.*;
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

	SceneObject bunnyObj2;

	@Override
	public void init() {
		final String bunnyObjectName = /*/"thinmatrix.dae"/*/"bunny.obj"/**/;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		controlPoints.add(new Vector3f(2, 2, 0));
		controlPoints.add(new Vector3f(0, 2, 2));
		controlPoints.add(new Vector3f(-2, 2, 0));
		controlPoints.add(new Vector3f(0, 2, -2));

		cam = new FirstPersonCamera(new Vector3f(), new Vector3f());//(new BezierCurve(controlPoints, ControlPointInputMode.CameraPointsCircular, true));

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		IRenderable tm = new ObjectLoader().loadFromFileAnim("thinmatrix.dae");
		tm = new TexturedRenderable(tm, new Texture("bunny_2d.png"));
		//IRenderable bunny = new TexturedRenderable(new ObjectLoader().loadFromFile(bunnyObjectName), new Texture("bunny_2d.png"));
		
		Shader phongShader = new Shader("boneAnimation.vert", "phong.frag");
		phongShader.addUniformBlockIndex(1, "Matrices");
		phongShader.use();
		
		bunnyObj2 = new SceneObject(tm, new Matrix4f(), null);
		bunnyObj2.setShader(phongShader);

		bunnyScene.getSceneGraph().addSubObject(bunnyObj2);
		bunnyScene.getSceneGraph().setShader(phongShader);
		
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}

	@Override
	public void update(final double deltaTime) {

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
