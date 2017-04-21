package mbeb.opengldefault.scene;

import org.joml.*;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.rendering.renderable.*;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private final LightManager lightManager;
	private final SceneGraphRenderer renderer;
	private final SceneGraphRenderer boundingBoxRenderer;
	private final SceneObject sceneGraphRoot;
	private final Camera camera;
	private Skybox skybox;
	private final MousePicker3D picker;

	public Scene(final Camera camera) {
		this(camera, null);
	}

	public Scene(final Camera camera, final Skybox skybox) {
		this.camera = camera;
		this.skybox = skybox;
		this.lightManager = new LightManager();
		this.sceneGraphRoot = new SceneObject();
		renderer = new SceneGraphRenderer(sceneGraphRoot, camera);
		boundingBoxRenderer = new BoundingBoxRenderer(sceneGraphRoot, camera);
		picker = new MousePicker3D(camera);
	}

	public SceneObject getSceneGraph() {
		return sceneGraphRoot;
	}

	public LightManager getLightManager() {
		return lightManager;
	}

	public void update(final double deltaTime) {
		camera.update(deltaTime);
		lightManager.update(deltaTime);
		sceneGraphRoot.update(deltaTime);
		picker.update(deltaTime);
	}

	public void setSkybox(final Skybox skybox) {
		this.skybox = skybox;
	}

	public void render() {
		render(false);
	}

	/**
	 * render the scene completely
	 * @param renderBoundingBoxes true to also display bounding boxes
	 */
	public void render(final boolean renderBoundingBoxes) {
		camera.use();
		renderer.render();
		picker.searchBoundingBoxes(sceneGraphRoot, new Matrix4f());
		if (renderBoundingBoxes) {
			boundingBoxRenderer.render();
		}
		if (skybox != null) {
			skybox.render(camera);
		}
	}
}
