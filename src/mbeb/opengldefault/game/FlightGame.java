package mbeb.opengldefault.game;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.curves.BezierCurve;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.*;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.shader.UBOManager;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.CameraEntity;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.SceneEntity;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Another test game, where you fly a box around
 */
public class FlightGame implements GameState {
	
	private static final String TAG = "FlightGame";
	
	private Scene scene;
	private List<IEntity> entities = new ArrayList<>();
	
	@Override
	public void init() {
		Camera camera = new Camera(GLContext.getAspectRatio());
		Skybox skybox = new Skybox("skybox/mountain");
		
		scene = new Scene(camera, skybox);
		
		//shaders
		ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);
		scene.getLightManager().addShader(defaultShader);
		scene.getSceneGraph().setShader(defaultShader);
		
		ShaderProgram bezierShader = new ShaderProgram("bezier.vert", "bezier.frag", "bezier.geom");
		bezierShader.setDrawMode(ShaderProgram.DrawMode.LINES);
		bezierShader.addUniformBlockIndex(UBOManager.MATRICES);
		
		//textures
		Texture2D tex = TexturedRenderable.loadModelTexture("player.png");
		
		//meshes
		final ObjectLoader loader = new ObjectLoader();
		IRenderable boxRenderable = loader.loadFromFile("longBox.obj");
		TexturedRenderable boxTextured = new TexturedRenderable(boxRenderable, tex);
		IRenderable bunnyRenderable = loader.loadFromFile("bunny.obj");
		TexturedRenderable bunnyTextured = new TexturedRenderable(bunnyRenderable, tex);
		
		//bezier
		List<Vector3f> controlPoints = new ArrayList<>();
		float curveHeight = 1.05f;
		controlPoints.add(new Vector3f(0, curveHeight, 0));
		controlPoints.add(new Vector3f(2.5f, curveHeight, 1.4f));
		controlPoints.add(new Vector3f(2.5f, curveHeight, -1.4f));
		controlPoints.add(new Vector3f(0, curveHeight, 0));
		controlPoints.add(new Vector3f(-2.5f, curveHeight, 1.4f));
		controlPoints.add(new Vector3f(-2.5f, curveHeight, -1.4f));
		BezierCurve curve = new BezierCurve(controlPoints, BezierCurve.ControlPointInputMode.CAMERAPOINTSCIRCULAR, true);
		
		//scene Objects
		SceneObject flightObject = new SceneObject(boxTextured);
		scene.getSceneGraph().addSubObject(flightObject);
		
		SceneObject bezierObject = new SceneObject(new BezierCurveRenderable(curve));
		bezierObject.setShader(bezierShader);
		flightObject.addSubObject(bezierObject);
		
		SceneObject bunnyObject = new SceneObject(bunnyTextured, new BoneTransformation(null, null, new Vector3f(0.5f)));
		flightObject.addSubObject(bunnyObject);
		
		//entity system
		IEntity bunnyEntity = new SceneEntity(bunnyObject);
		bunnyEntity.addBehaviour(1, new BezierBehaviour(bezierObject, 3));
		entities.add(bunnyEntity);
		
		IEntity flightEntity = new SceneEntity(flightObject);
		flightEntity.addBehaviour(1, new PlayerControlBehaviour());
		entities.add(flightEntity);
		
		IEntity cameraEntity = new CameraEntity(camera);
		ReferenceEntityBehaviour lookAt = new FollowingBehaviour(flightEntity, 0.01f);
		cameraEntity.addBehaviour(1, new CombinedBehaviour(
				lookAt.limited(6).fixedLocation(), new EscapingBehaviour(flightEntity, 1).limited(6).fixedDirection()));
		cameraEntity.addBehaviour(2, lookAt.limited(8));
		cameraEntity.addBehaviour(3, new FollowingBehaviour(flightEntity, 2));
		entities.add(cameraEntity);
		
		//light
		DirectionalLight sun = new DirectionalLight(Color.WHITE, new Vector3f(0, -1, 0));
		scene.getLightManager().addLight(sun);
		
		
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		GLErrors.checkForError(TAG, "glEnable");
	}
	
	@Override
	public void update(double deltaTime) {
		scene.update(deltaTime);
		for (IEntity entity : entities) {
			entity.update(deltaTime);
		}
	}
	
	@Override
	public void render() {
		glClearColor(0.05f, 0.075f, 0.075f, 1);
		GLErrors.checkForError(TAG, "glClearColor");
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GLErrors.checkForError(TAG, "glClear");
		
		glViewport(0, 0, GLContext.getFramebufferWidth(), GLContext.getFramebufferHeight());
		GLErrors.checkForError(TAG, "glViewport");
		
		scene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB));
	}
	
	@Override
	public void clear() {
	}
	
	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.pullKeyDown(GLFW_KEY_ESCAPE) ? GameStateIdentifier.MAIN_MENU : null;
	}

	@Override
	public boolean isActive() {
		return !KeyBoard.isKeyDown(GLFW_KEY_ESCAPE);
	}

	@Override
	public void resetNextGameState() {
		//nothing
	}
	
	@Override
	public void open() {
		//nothing
	}
}
