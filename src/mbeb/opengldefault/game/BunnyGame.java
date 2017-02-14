package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;
import mbeb.opengldefault.animation.AnimatedRenderable;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import org.joml.Matrix4f;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.glDisable;

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
		String bunnyObjectName = /**/"oneBone.dae"/*/"stick2.dae"/**/;
		ArrayList<Vector3f> controlPoints = new ArrayList<>();

		controlPoints.add(new Vector3f(2, 2, 0));
		controlPoints.add(new Vector3f(0, 2, 2));
		controlPoints.add(new Vector3f(-2, 2, 0));
		controlPoints.add(new Vector3f(0, 2, -2));

		cam = new FirstPersonCamera(new Vector3f(), new Vector3f());//(new BezierCurve(controlPoints, ControlPointInputMode.CameraPointsCircular, true));

		Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		IRenderable bunny = new ObjectLoader().loadFromFileAnim(bunnyObjectName);
		System.gc();
		
		Shader debugShader = new Shader("boneAnimation.vert", "debugging.frag");
		debugShader.addUniformBlockIndex(1, "Matrices");
		debugShader.use();
		
		bunnyObj2 = new SceneObject(bunny, new Matrix4f(), null);
		bunnyObj2.setShader(debugShader);

		//cubeObj.getTransformation().scale(0.3f);
		bunnyScene.getSceneGraph().addSubObject(bunnyObj2);

		bunnyScene.getSceneGraph().setShader(debugShader);
		
		glDisable(GL_CULL_FACE);
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
