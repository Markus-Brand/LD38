package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import mbeb.opengldefault.camera.BezierCamera;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

/**
 * Object to characterize a whole game
 */
public class BunnyGame implements IGame {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	protected Camera cam;

	TexturedRenderable bunny;

	public BunnyGame() {
	}

	@Override
	public void init() {
		bunny = new TexturedRenderable(new ObjectLoader().loadFromFile("bunny.obj"), new Texture("bunny_2d.png"));
		ArrayList<Vector3f> controlPoints = new ArrayList<>();

		controlPoints.add(new Vector3f(1, 1, 0));
		controlPoints.add(new Vector3f(0, 1, 1));
		controlPoints.add(new Vector3f(-1, 1, 0));
		controlPoints.add(new Vector3f(0, 1, -1));

		cam = new BezierCamera(new BezierCurve(controlPoints, ControlPointInputMode.CameraPointsCircular, true));

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

		cam.update(deltaTime);

		render();

	}

	@Override
	public void render() {
		Vector3f pos = cam.getPosition();
		//System.out.println(pos.x + " " + pos.y + " " + pos.z);
		bunny.getShader().use();
		GL20.glUniform3f(bunny.getShader().getUniform("viewPos"), pos.x, pos.y, pos.z);
		GLErrors.checkForError(TAG, "glUniform3f");
		Matrix4f model = new Matrix4f();
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		GL20.glUniformMatrix4fv(bunny.getShader().getUniform("model"), false, model.get(buffer));
		GLErrors.checkForError(TAG, "glUniformMatrix4fv");
		bunny.render();
	}

	@Override
	public void clear() {
		TextureCache.clearCache();
	}

}
