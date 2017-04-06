package mbeb.opengldefault.game;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.light.DirectionalLight;
import mbeb.opengldefault.light.PointLight;
import mbeb.opengldefault.openglcontext.OpenGLContext;
import mbeb.opengldefault.rendering.io.ObjectLoader;
import mbeb.opengldefault.rendering.renderable.IRenderable;
import mbeb.opengldefault.rendering.renderable.Skybox;
import mbeb.opengldefault.rendering.renderable.TexturedRenderable;
import mbeb.opengldefault.rendering.shader.ShaderProgram;
import mbeb.opengldefault.rendering.shader.UBOManager;
import mbeb.opengldefault.rendering.textures.Texture;
import mbeb.opengldefault.rendering.textures.TextureCache;
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
	private EntityWorld bunnys;
	private EntityWorld others;
	private TexturedRenderable particle;
	private TexturedRenderable greenParticle;
	private GravitationBehaviour gravitation;
	
	@Override
	public void init() {
		ICamera camera = new Camera(OpenGLContext.getAspectRatio());
		camera.setPosition(new Vector3f(1, 1, 1));
		Skybox sky = new Skybox("spacebox/s", "png");
		scene = new Scene(camera, sky);
		
		ObjectLoader loader = new ObjectLoader();
		
		IRenderable bunnyRaw = loader.loadFromFile("ico.obj");
		Texture blueRed = new Texture("blueRedLight.png");
		Texture green = new Texture("green.png");
		particle = new TexturedRenderable(bunnyRaw, blueRed);
		greenParticle = new TexturedRenderable(bunnyRaw, green);
		
		bunnys = new EntityWorld();
		others = new EntityWorld();
		gravitation = new GravitationBehaviour(bunnys, 0.01f, 0.01f);

		/*new PlayerControlBehaviour().fixedLocation()/*/
		//bunnys.add(camera).addBehaviour(1, gravitation);
		others.add(camera).addBehaviour(1, new PlayerControlBehaviour());
		
		ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);
		scene.getSceneGraph().setShader(defaultShader);
		scene.getLightManager().addShader(defaultShader);

		DirectionalLight sun = new DirectionalLight(new Vector3f(0.1f), new Vector3f(0, -1, 0.2f).normalize());
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
		SceneObject newBunny = new SceneObject(particle, getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.05f))));
		scene.getSceneGraph().addSubObject(newBunny);
		bunnys.add(newBunny).addBehaviour(1, gravitation);
	}

	private void addALight() {
		SceneObject newBunny = new SceneObject(greenParticle, getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.02f))));
		scene.getSceneGraph().addSubObject(newBunny);
		bunnys.add(newBunny).addBehaviour(1, gravitation);

		PointLight light = new PointLight(randomColor(), randomVec(), 3);
		scene.getLightManager().addLight(light);
		bunnys.add(light).addBehaviour(1, new ParentBehaviour(newBunny));
	}

	private Vector3f randomColor() {
		return new Vector3f(0, 1, 0);
		/*Random r = new Random();
		Vector3f color = new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat());
		color.normalize();
		return color;/**/
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
			bunnys.update(deltaTime);
		}
		others.update(deltaTime);

		//centerAll(bunnys);
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
		TextureCache.clearCache();
	}
	
	@Override
	public GameStateIdentifier getNextState() {
		return KeyBoard.isKeyDown(GLFW_KEY_ESCAPE) ? GameStateIdentifier.MAIN_MENU : null;
	}
	
	@Override
	public void resetNextGameState() {
	
	}
	
	@Override
	public void open() {
	
	}
}
