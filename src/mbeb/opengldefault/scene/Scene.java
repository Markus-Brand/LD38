package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.rendering.renderable.*;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private SceneGraphRenderer renderer;
	private SceneGraphRenderer boundingBoxRenderer;
	private final SceneObject sceneGraph;
	private ICamera camera;
	private Skybox skybox;
	private MousePicker picker;

	public Scene(ICamera cam) {
		this(cam, null);
	}

	public Scene(ICamera cam, Skybox skybox) {
		this.camera = cam;
		this.skybox = skybox;
		this.sceneGraph = new SceneObject();
		renderer = new VisibleSceneGraphRenderer(sceneGraph, cam);
		boundingBoxRenderer = new BoundingBoxRenderer(sceneGraph, cam);
		picker = new MousePicker(camera);
	}

	public SceneObject getSceneGraph() {
		return sceneGraph;
	}

	public void update(double deltaTime) {
		camera.update(deltaTime);
		sceneGraph.update(deltaTime);
		picker.update(deltaTime);
	}

	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}

	public void render() {
		render(false);
	}

	public void render(boolean renderBoundingBoxes) {
		renderer.render();
		if (renderBoundingBoxes) {
			boundingBoxRenderer.render();
		}
		if (skybox != null) {
			skybox.render();
		}
		picker.searchBBs(sceneGraph);
	}
}
