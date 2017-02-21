package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mbeb.opengldefault.animation.AnimatedRenderable;

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

	SceneObject cowboy;

	@Override
	public void init() {
		cam = new FirstPersonCamera(new Vector3f(), new Vector3f());

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		final AnimatedRenderable bunnyRenderable = (AnimatedRenderable)new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		IRenderable bunny = new TexturedRenderable(bunnyRenderable, new Texture("bunny_2d.png"));
		
		Shader phongShader = new Shader("boneAnimation.vert", "phong.frag");
		phongShader.addUniformBlockIndex(1, "Matrices");
		phongShader.use();
		
		cowboy = new SceneObject(bunny, new Matrix4f(), null);
		cowboy.setShader(phongShader);

		bunnyScene.getSceneGraph().addSubObject(cowboy);
		bunnyScene.getSceneGraph().setShader(phongShader);
		
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyRenderable.playAnimation("OhrenFlackern1", true, true);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyRenderable.playAnimation("HeadBang", true, true);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyRenderable.playAnimation("OhrenFlackern2", true, true);
			}
		}.start();
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
