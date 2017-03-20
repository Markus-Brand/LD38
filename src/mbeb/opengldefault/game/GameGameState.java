package mbeb.opengldefault.game;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import mbeb.opengldefault.animation.AnimatedRenderable;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUIElement;
import mbeb.opengldefault.light.Light;
import mbeb.opengldefault.light.SpotLight;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.BezierCurveRenderable;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.shader.Shader;
import mbeb.opengldefault.rendering.shader.UBOManager;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.BezierBehaviour;
import mbeb.opengldefault.scene.behaviour.FollowingBehaviour;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.Entity;
import mbeb.opengldefault.scene.entities.SceneEntity;
import mbeb.opengldefault.scene.entities.SpotLightEntity;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class GameGameState implements GameState {

	private static final String TAG = "GameGameState";

	GameStates nextGameState;

	private static final Matrix4f MeshFlip = new Matrix4f(1, 0, 0, 0, 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1);

	private float timePassed;

	private TextGUIElement fps;

	private Shader guiShader;

	protected ICamera camera;
	Scene bunnyScene;
	//PointLight pl;
	//DirectionalLight dl;
	SpotLight sl;

	double timepassed = 0;
	ArrayList<Light> lights = new ArrayList<>();

	BezierCurve curve;

	SceneObject bunny0, bunny1, bunny2, bunny3, bunny4, curveObj;

	Entity mainBunny, followingBunny1, followingBunny2, followingBunny3, followingBunny4, camEntity, spotLightEntity;

	private AtlasGUI textGUI;

	@Override
	public void init() {
		timePassed = 0;
		final ArrayList<Vector3f> controlPoints = new ArrayList<>();

		final Random random = new Random();
		for (int i = 0; i < 10; i++) {
			controlPoints.add(new Vector3f(random.nextInt(51) - 25, random.nextInt(51) - 25, random.nextInt(51) - 25));
		}
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);

		camera = new Camera(OpenGLContext.getAspectRatio());

		camEntity = new CameraEntity(camera);

		final Skybox skybox = new Skybox("skybox/mountain");

		bunnyScene = new Scene(camera, skybox);

		final AnimatedRenderable bunnyAnim = new ObjectLoader().loadFromFileAnim("ohrenFlackern.fbx");
		bunnyAnim.getAnimatedMesh().setTransform(MeshFlip);
		final IRenderable bunnyTextured = new TexturedRenderable(bunnyAnim, new Texture("bunny_2d.png"));

		final Shader curveShader = new Shader("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(UBOManager.MATRICES);
		curveShader.setDrawMode(GL_LINES);

		final Shader defaultShader = new Shader("boneAnimation.vert", "basic.frag");
		bunnyScene.getLightManager().addShader(defaultShader);
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);

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

		followingBunny1.addBehaviour(1, new FollowingBehaviour(mainBunny, 3f).limited(5));
		followingBunny1.addBehaviour(2, new FollowingBehaviour(mainBunny, 7.6f));

		followingBunny2.addBehaviour(1, new FollowingBehaviour(followingBunny1, 3f).limited(5));
		followingBunny2.addBehaviour(2, new FollowingBehaviour(followingBunny1, 7.6f));

		followingBunny3.addBehaviour(1, new FollowingBehaviour(followingBunny2, 3f).limited(5));
		followingBunny3.addBehaviour(2, new FollowingBehaviour(followingBunny2, 7.6f));

		followingBunny4.addBehaviour(1, new FollowingBehaviour(followingBunny3, 3f).limited(5));
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

		//pl = new PointLight(Color.GREEN, new Vector3f(0, 10, 0), 1000);
		//bunnyScene.getLightManager().addLight(pl);
		//dl = new DirectionalLight(Color.GREEN, new Vector3f(1, 0.5f, 0));
		//bunnyScene.getLightManager().addLight(dl);

		sl = new SpotLight(Color.ORANGE, new Vector3f(0, -0.25f, 0), new Vector3f(0, 1, 0), 5, 10, 1000);
		bunnyScene.getLightManager().addLight(sl);
		spotLightEntity = new SpotLightEntity(sl);
		spotLightEntity.addBehaviour(1, new FollowingBehaviour(followingBunny3, 3f).limited(5));
		spotLightEntity.addBehaviour(9001, new FollowingBehaviour(followingBunny3, 7.6f));

		textGUI = new AtlasGUI("font.png", 32, 16);
		guiShader = new Shader("gui.vert", "gui.frag");
		fps = textGUI.addText("0", new Vector2f(), 0.01f);
		fps.setPositionRelativeToScreen(0, 0);

		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch(final InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyAnim.playAnimation("OhrenFlackern1", 4);
				try {
					Thread.sleep(3000);
				} catch(final InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyAnim.playAnimation("HeadBang", 4);
				try {
					Thread.sleep(3000);
				} catch(final InterruptedException ex) {
					ex.printStackTrace();
				}
				bunnyAnim.playAnimation("OhrenFlackern2", 4);
			}
		}.start();/**/
	}

	@Override
	public void update(final double deltaTime) {
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			nextGameState = GameStates.MAIN_MENU;
			KeyBoard.releaseAll();
		}

		fps.setText("FPS: " + (int) (1 / deltaTime));
		textGUI.update(deltaTime);

		timePassed += deltaTime;

		mainBunny.update(deltaTime);
		followingBunny1.update(deltaTime);
		followingBunny2.update(deltaTime);
		followingBunny3.update(deltaTime);
		//followingBunny4.update(deltaTime);
		camEntity.update(deltaTime);

		//pl.setColor(new Color((float) java.lang.Math.sin(timepassed) / 2 + 0.5f, (float) 1.0, (float) java.lang.Math.cos(timepassed) / 2 + 0.5f));
		//pl.setPosition(new Vector3f((float) java.lang.Math.sin(timepassed) * 5, 10, (float) java.lang.Math.cos(timepassed) * 5));
		spotLightEntity.update(deltaTime);
		bunnyScene.update(deltaTime);
		textGUI.update(deltaTime);
	}

	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");

		glViewport(0, 0, OpenGLContext.getFramebufferWidth(), OpenGLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");

		bunnyScene.render(true); //bunnyScene.render(); to render without BoundingBoxes
		guiShader.use();
		textGUI.render(guiShader);
	}

	@Override
	public void clear() {
		TextureCache.clearCache();
	}

	@Override
	public GameStates getNextState() {
		return nextGameState;
	}

	@Override
	public void resetNextGameState() {
		nextGameState = null;
	}

	@Override
	public void open() {
		OpenGLContext.hideCursor();
	}

}
