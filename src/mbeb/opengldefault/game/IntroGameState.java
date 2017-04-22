package mbeb.opengldefault.game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.PerspectiveCamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.curves.BezierCurve.ControlPointInputMode;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.GUIElement;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.BezierCurveRenderable;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.BezierBehaviour;
import mbeb.opengldefault.scene.behaviour.CombinedBehaviour;
import mbeb.opengldefault.scene.behaviour.LookAtBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.materials.Material;

public class IntroGameState implements GameState {

	private static final String TAG = "IntroGameState";

	private float progress;

	private Scene introScene;

	private EntityWorld entities;

	private Camera camera;

	private SceneObject world, island;

	private ShaderProgram worldShader;
	private ShaderProgram curveShader;
	private ShaderProgram islandShader;

	private Skybox skyBox;

	private float totalTimePassed;

	private BezierCurve curve;

	private BezierBehaviour bezierBehaviour;

	private TextGUI textGUI;

	private AtlasGUI menuGUI;

	private ShaderProgram guiShader;

	private TextGUIElement fps, buttonGame;

	private GUIElement buttonExit, buttonOptions;

	private GameStateIdentifier nextGameState = null;

	private boolean starting;

	@Override
	public void init() {
		progress = 0;
		totalTimePassed = 0;

		List<Vector3f> controlPoints = new ArrayList<Vector3f>();

		//controlPoints.add(new Vector3f(1.02f, 0, 0))
		controlPoints.add(new Vector3f(1, -2, 0));
		controlPoints.add(new Vector3f(1.01f, -1.2f, 0));
		controlPoints.add(new Vector3f(1.05f, -0.8f, 0));
		controlPoints.add(new Vector3f(1.1f, -0.4f, 0));
		controlPoints.add(new Vector3f(1.15f, -0.1f, 0));
		controlPoints.add(new Vector3f(1.03f, -0.001f, 0));
		controlPoints.add(new Vector3f(1.02f, 0, 0));
		curve = new BezierCurve(controlPoints, ControlPointInputMode.CAMERAPOINTS);

		skyBox = new Skybox("spacebox/s", "png");
		camera = new PerspectiveCamera();
		camera.setNear(0.001f);
		introScene = new Scene(camera, skyBox);

		introScene.getLightManager()
				.addLight(new DirectionalLight(Color.WHITE, new Vector3f(-1, -0.4f, 0).normalize()));

		worldShader = new ShaderProgram("planet.frag", "planet.vert");
		worldShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		introScene.getLightManager().addShader(worldShader);

		curveShader = new ShaderProgram("bezier.vert", "bezier.frag", "bezier.geom");
		curveShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		curveShader.setDrawMode(ShaderProgram.DrawMode.LINES);

		islandShader = new ShaderProgram("basic.vert", "basic.frag");
		islandShader.addUniformBlockIndex(Camera.UBO_NAME, Camera.UBO_INDEX);
		introScene.getLightManager().addShader(islandShader);

		final IRenderable worldRenderable = new ObjectLoader().loadFromFile("planet.obj");

		final IRenderable islandRenderable =
				new ObjectLoader().loadFromFile("island.obj").withMaterial(new Material("material/beach", 1));

		world = new SceneObject(worldRenderable);
		world.setShader(worldShader);

		island = new SceneObject(islandRenderable);
		island.setTransformation(new BoneTransformation(new Vector3f(0.9999f, 0, 0), new Quaternionf(new AxisAngle4f(
				(float) Math.PI / 2f,
				new Vector3f(0, 0, -1))), new Vector3f(0.001f)));
		island.setShader(islandShader);

		introScene.getSceneGraph().addSubObject(world);
		introScene.getSceneGraph().addSubObject(island);

		SceneObject curveObj = new SceneObject(new BezierCurveRenderable(curve));
		curveObj.setShader(curveShader);
		//introScene.getSceneGraph().addSubObject(curveObj);

		entities = new EntityWorld();
		bezierBehaviour = new BezierBehaviour(curveObj, 1.9f);
		entities.add(camera).addBehaviour(0,
				new CombinedBehaviour(
						bezierBehaviour,
						new LookAtBehaviour(island.asEntity())));
		GL11.glDisable(GL_CULL_FACE);

		menuGUI = new AtlasGUI("menu.png", 4, 4);
		guiShader = new ShaderProgram("gui.vert", "gui.frag");
		textGUI = new TextGUI(new Font("Comic Sans MS", Font.PLAIN, 128));

		textGUI.setShader(guiShader);
		menuGUI.setShader(guiShader);

		fps = textGUI.addText("0", new Vector2f(), 0.03f);
		fps.setPositionRelativeToScreen(0, 0);
		fps.setColor(Color.ORANGE);

		buttonGame =
				(TextGUIElement) textGUI.addText("Start Game", new Vector2f(), 0.2f).setPositionRelativeToScreen(0.5f,
						0.4f);
		buttonOptions = textGUI.addText("Options", new Vector2f(), 0.2f).setPositionRelativeToScreen(0.5f, 0.6f);

		buttonExit =
				menuGUI.addAtlasGUIElement(0, new Vector2f(), new Vector2f(0.1f, GLContext.getAspectRatio() * 0.1f))
						.setPositionRelativeToScreen(0.01f, 0.99f);
		entities.update(0.0000001f);
	}

	@Override
	public void update(double deltaTime) {
		progress = bezierBehaviour.getTotalProgress();
		totalTimePassed += deltaTime;

		worldShader.use();
		skyBox.getTexture().bind();
		worldShader.setUniform("skybox", skyBox.getTexture());
		worldShader.setUniform("time", totalTimePassed);

		introScene.update(deltaTime);
		if (starting) {
			entities.update(deltaTime);
		} else {
			if (KeyBoard.isKeyDown(GLFW_KEY_ESCAPE)) {
				nextGameState = GameStateIdentifier.EXIT;
			}
		}
		camera.update(deltaTime);

		fps.setText("FPS: " + (int) (1 / deltaTime));
		menuGUI.update(deltaTime);
		textGUI.update(deltaTime);

		if (buttonGame.selected()) {
			buttonGame.setColor(Color.RED);
			if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
				starting = true;
			}
		} else {
			buttonGame.setColor(Color.GREEN);
		}

		if (buttonOptions.selected()) {
			buttonOptions.setColor(Color.RED);
			if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
				nextGameState = GameStateIdentifier.OPTIONS;
			}
		} else {
			buttonOptions.setColor(Color.GREEN);
		}

		if (buttonExit.selected()) {
			buttonExit.setColor(Color.RED);
			if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
				nextGameState = GameStateIdentifier.EXIT;
			}
		} else {
			buttonExit.setColor(Color.GREEN);
		}
	}

	@Override
	public void render() {
		introScene.render();
		if (!starting) {
			menuGUI.render();
			textGUI.render();
		}
	}

	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW.GLFW_KEY_ESCAPE) && starting || progress >= 1 ? GameStateIdentifier.OVERWORLD
				: nextGameState;
	}

	@Override
	public void resetNextGameState() {
		nextGameState = null;
	}

	@Override
	public void open() {
		System.out.println(true);
		starting = false;
		GLContext.showCursor();
		introScene.getLightManager().rewriteUBO();
		bezierBehaviour.resetProgress();
		progress = 0;
		entities.update(0.0000001f);
	}

}
