package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.rendering.renderable.*;

import org.joml.*;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private final LightManager lightManager;
	private final SceneGraphRenderer renderer;
	private final SceneGraphRenderer boundingBoxRenderer;
	private final SceneObject sceneGraphRoot;
	private final ICamera camera;
	private Skybox skybox;
	private final MousePicker picker;

	public Scene(final ICamera cam) {
		this(cam, null);
	}

	public Scene(final ICamera cam, final Skybox skybox) {
		this.camera = cam;
		this.skybox = skybox;
		this.lightManager = new LightManager();
		this.sceneGraphRoot = new SceneObject();
		renderer = new VisibleSceneGraphRenderer(sceneGraphRoot, cam);
		boundingBoxRenderer = new BoundingBoxRenderer(sceneGraphRoot, cam);
		picker = new MousePicker(camera);
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

	public void render(final boolean renderBoundingBoxes) {
		renderer.render();
		picker.searchBBs(sceneGraphRoot, new Matrix4f());
		if (renderBoundingBoxes) {
			boundingBoxRenderer.render();
		}
		if (skybox != null) {
			skybox.render();
		}
	}
}
