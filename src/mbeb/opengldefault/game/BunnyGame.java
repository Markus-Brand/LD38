package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import mbeb.opengldefault.camera.FirstPersonCamera;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.Transformation;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.glDisable;

/**
 * Object to characterize a whole game
 */
public class BunnyGame implements IGame {
	/** Class Name Tag */
	private static final String TAG = "BunnyGame";

	Scene bunnyScene;

	SceneObject cubeObj, bunnyObj, bunnyObj2;

	@Override
	public void init() {
		bunnyScene = new Scene(new FirstPersonCamera(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1)));

		IRenderable bunny = new TexturedRenderable(new ObjectLoader().loadFromFile("bunny.obj"), new Texture("bunny_2d.png"));
		IRenderable cube = new TexturedRenderable(new ObjectLoader().loadFromFile("cube.obj"), new Texture("bunny_2d.png"));

		cubeObj = new SceneObject(cube, null, null);
		bunnyObj = new SceneObject(bunny, Transformation.fromPosition(new Vector3f(1, 1, 1)), null);
		bunnyObj2 = new SceneObject(bunny, Transformation.fromPosition(new Vector3f(1, -1, 1)), null);

		cubeObj.addSubObject(bunnyObj);
		bunnyScene.getSceneGraph().addSubObject(bunnyObj2);
		bunnyScene.getSceneGraph().addSubObject(cubeObj);

		Shader defaultShader = new Shader("basic.vert", "phong.frag");
		defaultShader.addUniformBlockIndex(1, "Matrices");
		bunnyScene.getSceneGraph().setShader(defaultShader);

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

		cubeObj.getTransformation().asMatrix().rotate((float) deltaTime, new Vector3f(1, 1, 0));
		bunnyObj.getTransformation().asMatrix().rotate((float) deltaTime * 3, new Vector3f(0, 1, 0));

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
