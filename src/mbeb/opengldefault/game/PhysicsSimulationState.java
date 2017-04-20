package mbeb.opengldefault.game;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.light.PointLight;
import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.shader.UBOManager;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.Vector3f;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glEnable;

/**
 * Particles that attract each other
 */
public class PhysicsSimulationState implements GameState {
	
	private Scene scene;
	private EntityWorld particles;
	private EntityWorld others;
	private TexturedRenderable particle;
	private TexturedRenderable greenParticle;
	private GravitationBehaviour gravitation;
	
	@Override
	public void init() {
		ICamera camera = new Camera(GLContext.getAspectRatio());
		camera.setPosition(new Vector3f(1, 1, 1));
		Skybox sky = new Skybox("spacebox/s", "png");
		scene = new Scene(camera, sky);
		
		ObjectLoader loader = new ObjectLoader();
		
		IRenderable bunnyRaw = loader.loadFromFile("ico.obj");
		Texture2D blueRed = TexturedRenderable.loadModelTexture("blueRedLight.png");
		Texture2D green = TexturedRenderable.loadModelTexture("green.png");
		particle = new TexturedRenderable(bunnyRaw, blueRed);
		greenParticle = new TexturedRenderable(bunnyRaw, green);
		
		particles = new EntityWorld();
		others = new EntityWorld();
		gravitation = new GravitationBehaviour(particles, 0.01f, 0.01f);

		/*new PlayerControlBehaviour().fixedLocation()/*/
		//particles.add(camera).addBehaviour(1, gravitation);
		others.add(camera).addBehaviour(1, new PlayerControlBehaviour());
		
		ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);
		scene.getSceneGraph().setShader(defaultShader);
		scene.getLightManager().addShader(defaultShader);

		DirectionalLight sun = new DirectionalLight(new Vector3f(0.2f), new Vector3f(0, -1, 0.2f).normalize());
		scene.getLightManager().addLight(sun);

		glEnable(GL_CULL_FACE);

		for (int i = 0; i < 100; i++) {
			addAParticle();
		}
		for (int i = 0; i < 5; i++) {
			addALight();
		}
	}

	private void addAParticle() {
		SceneObject newParticle = new SceneObject(particle, getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.05f))));
		scene.getSceneGraph().addSubObject(newParticle);
		particles.add(newParticle).addBehaviour(1, gravitation);
	}

	private void addALight() {
		//here are materials missing: emission on these should be at max
		SceneObject newLightObject = new SceneObject(greenParticle, getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.02f))));
		scene.getSceneGraph().addSubObject(newLightObject);
		particles.add(newLightObject).addBehaviour(1, gravitation);

		PointLight newLight = new PointLight(generateLightColor(), randomVec(), 3);
		scene.getLightManager().addLight(newLight);
		particles.add(newLight).addBehaviour(1, new ParentBehaviour(newLightObject));
	}

	private Vector3f generateLightColor() {
		return new Vector3f(0, 1, 0);
	}
	
	private BoneTransformation getStartTransform() {
		return new BoneTransformation(randomVec());
	}

	private Vector3f randomVec() {
		Random r = new Random();
		return new Vector3f(r.nextFloat() * 6 - 3, r.nextFloat() * 6 - 3, r.nextFloat() * 6 - 3);
	}
	
	@Override
	public void update(double deltaTime) {
		scene.update(deltaTime);
		if (KeyBoard.isKeyDown(GLFW_KEY_SPACE)) {
			particles.update(deltaTime);
		}
		others.update(deltaTime);

		centerAll(particles);
	}

	private void centerAll(EntityWorld entities) {
		Vector3f average = new Vector3f();
		entities.forEachEntity((IEntity e) -> {
			average.add(e.getPosition());
		});
		average.div(entities.entityCount());
		entities.forEachEntity((IEntity e) -> {
			e.setPosition(e.getPosition().sub(average));
		});
	}

	@Override
	public void render() {
		scene.render(KeyBoard.isKeyDown(GLFW_KEY_TAB));
	}
	
	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW_KEY_ESCAPE) ? GameStateIdentifier.MAIN_MENU : null;
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
