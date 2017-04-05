package mbeb.opengldefault.game;

import mbeb.opengldefault.animation.BoneTransformation;
import mbeb.opengldefault.camera.Camera;
import mbeb.opengldefault.camera.ICamera;
import mbeb.opengldefault.controls.KeyBoard;
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
import mbeb.opengldefault.scene.behaviour.AutoRotateBehaviour;
import mbeb.opengldefault.scene.behaviour.CombinedBehaviour;
import mbeb.opengldefault.scene.behaviour.IBehaviour;
import mbeb.opengldefault.scene.behaviour.PlayerControlBehaviour;
import mbeb.opengldefault.scene.entities.EntityWorld;
import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.Vector3f;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

/**
 * Particles that attract each other
 */
public class PhysicsSimulationState implements GameState {
	
	private Scene scene;
	private EntityWorld bunnys;
	private EntityWorld others;
	private TexturedRenderable bunny;
	
	@Override
	public void init() {
		ICamera camera = new Camera(OpenGLContext.getAspectRatio());
		Skybox sky = new Skybox("skybox/mountain");
		scene = new Scene(camera, sky);
		
		ObjectLoader loader = new ObjectLoader();
		
		IRenderable bunnyRaw = loader.loadFromFile("ico.obj");
		Texture tex = new Texture("blueRed.png");
		bunny = new TexturedRenderable(bunnyRaw, tex);
		
		bunnys = new EntityWorld();
		others = new EntityWorld();
		
		others.add(camera).addBehaviour(1, new PlayerControlBehaviour());
		
		ShaderProgram defaultShader = new ShaderProgram("basic.vert", "basic.frag");
		defaultShader.addUniformBlockIndex(UBOManager.MATRICES);
		scene.getSceneGraph().setShader(defaultShader);
		scene.getLightManager().addShader(defaultShader);

		for (int i = 0; i < 200; i++) {
			addABunny();
		}
	}
	
	private void addABunny() {
		SceneObject newBunny = new SceneObject(bunny, getStartTransform().and(new BoneTransformation(null, null, new Vector3f(0.05f))));
		scene.getSceneGraph().addSubObject(newBunny);
		IBehaviour autoRotate = new AutoRotateBehaviour();
		bunnys.add(newBunny).addBehaviour(1, new CombinedBehaviour(autoRotate, new IBehaviour() {
			@Override
			public boolean triggers(IEntity entity) {
				return true;
			}

			final float speedDecrease = 0f;
			final float forceFactor = 0.01f;
			private Vector3f speed = new Vector3f();
			
			@Override
			public void update(double deltaTime, IEntity entity) {
				float deltaTimef = (float) deltaTime;
				Vector3f currentPosition = entity.getPosition();
				Vector3f force = new Vector3f();
				bunnys.forEachEntity((IEntity other) -> {
					if (other != entity) {

						Vector3f localForce = other.getPosition().sub(currentPosition, new Vector3f());
						float distance = localForce.length();
						if (distance != 0) {
							localForce.normalize();
							localForce.div(Math.max(distance * distance, 1));
							force.add(localForce);
						}
					}
				});
				force.mul(forceFactor);

				speed = speed.mul(1 - (speedDecrease * deltaTimef)).add(force.mul(deltaTimef));

				entity.setPosition(currentPosition.add(speed.mul(deltaTimef, new Vector3f())));
			}
		}/**/));
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
		bunnys.update(deltaTime);
		others.update(deltaTime);

		centerAll(bunnys);
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
