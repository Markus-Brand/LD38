package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import mbeb.opengldefault.animation.AnimatedRenderable;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.curves.*;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.logging.*;
import mbeb.opengldefault.openglcontext.*;
import mbeb.opengldefault.rendering.io.*;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.rendering.textures.*;
import mbeb.opengldefault.scene.*;
import mbeb.opengldefault.scene.behaviour.BezierBehaviour;
import mbeb.opengldefault.scene.behaviour.EscapingBehaviour;
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

	private float timePassed;

	protected ICamera cam;
	Scene bunnyScene;

	SceneObject bunnySceneObject;
	
	Entity camEntity, bunnyEntity;

	@Override
	public void init() {
		cam = new Camera();

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(cam, skybox);

		final IRenderable bunnyRenderable = new ObjectLoader().loadFromFile("bunny.obj");
		IRenderable bunny = new TexturedRenderable(bunnyRenderable, new Texture("bunny_2d.png"));
		
		Shader phongShader = new Shader("basic.vert", "phong.frag");
		phongShader.addUniformBlockIndex(1, "Matrices");
		//phongShader.use();
		
		bunnySceneObject = new SceneObject(bunny, new Matrix4f().translate(1, 2, 3));
		bunnySceneObject.setShader(phongShader);

		bunnyScene.getSceneGraph().addSubObject(bunnySceneObject);
		bunnyScene.getSceneGraph().setShader(phongShader);
		
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		camEntity = new CameraEntity(cam);
		camEntity.addBehaviour(1, new PlayerControlBehaviour());
		
		bunnyEntity = new SceneEntity(bunnySceneObject);
		bunnyEntity.addBehaviour(1, new LimitedDistanceBehaviour(new EscapingBehaviour(camEntity, 1), 5));
		
		/*new Thread() {

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
		}.start();/**/
	}

	@Override
	public void update(final double deltaTime) {
		timePassed += deltaTime;
		
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getWidth(), OpenGLContext.getHeight());
		GLErrors.checkForError(TAG, "glViewport");

		bunnyScene.update(deltaTime);
		camEntity.update(deltaTime);
		bunnyEntity.update(deltaTime);
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
