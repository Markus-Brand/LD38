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
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gl.shader.UBOManager;
import mbeb.opengldefault.gl.texture.Texture2D;
import mbeb.opengldefault.scene.Scene;
import mbeb.opengldefault.scene.SceneObject;
import mbeb.opengldefault.scene.behaviour.*;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.materials.ColorMaterial;
import mbeb.opengldefault.scene.materials.Material;
import org.joml.Vector3f;

import java.awt.*;
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
	private IRenderable particle;
	private IRenderable greenParticle;
	private GravitationBehaviour gravitation;
	
	@Override
	public void init() {
		ICamera camera = new Camera(GLContext.getAspectRatio());
		camera.setPosition(new Vector3f(1, 1, 1));
		Skybox sky = new Skybox("spacebox/s", "png");
		scene = new Scene(camera, sky);
		
		ObjectLoader loader = new ObjectLoader();
		
		IRenderable icoSphere = loader.loadFromFile("ico.obj");
		Material blueRed = new Material("material/blueRedLight", 2);
		particle = icoSphere.withMaterial(blueRed);
		
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
		for (int i = 0; i < 20; i++) {
			addALight(icoSphere, generateLightColor(i));
		}
	}

	private void addAParticle() {
		SceneObject newParticle = new SceneObject(particle, getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.05f))));
		scene.getSceneGraph().addSubObject(newParticle);
		particles.add(newParticle).addBehaviour(1, gravitation);
	}

	private void addALight(IRenderable lightParticle, Color color) {
		Material material = new ColorMaterial(color, Color.BLACK, color);
		
		//here are materials missing: emission on these should be at max
		SceneObject newLightObject = new SceneObject(lightParticle.withMaterial(material), getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.02f))));
		scene.getSceneGraph().addSubObject(newLightObject);
		particles.add(newLightObject).addBehaviour(1, gravitation);

		PointLight newLight = new PointLight(color, randomVec(), 6);
		scene.getLightManager().addLight(newLight);
		particles.add(newLight).addBehaviour(1, new ParentBehaviour(newLightObject));
	}

	private Color generateLightColor(int num) {
		int maxColors = 3;
		switch (num % maxColors) {
			case 0:
				return Color.GREEN;
			case 1:
				return Color.RED;
			case 2:
				return Color.BLUE;
			case 3:
				return Color.CYAN;
			case 4:
				return Color.MAGENTA;
			case 5:
				return Color.YELLOW;
		}
		
		return Color.WHITE;
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
	public void clear() {
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
