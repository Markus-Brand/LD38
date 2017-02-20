package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.light.*;
import mbeb.opengldefault.rendering.renderable.*;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private final SceneGraphRenderer renderer;
	private final SceneObject sceneGraph;
	private final ICamera camera;
	private Skybox skybox;
	private final LightManager lightManager;

	public Scene(final ICamera cam) {
		this(cam, null);
	}

	public Scene(final ICamera cam, final Skybox skybox) {
		this.camera = cam;
		this.skybox = skybox;
		this.lightManager = new LightManager();
		this.sceneGraph = new SceneObject();
		renderer = new VisibleSceneGraphRenderer(sceneGraph, cam);
	}

	public SceneObject getSceneGraph() {
		return sceneGraph;
	}

	public LightManager getLightManager() {
		return lightManager;
	}

	public void update(final double deltaTime) {
		camera.update(deltaTime);
		sceneGraph.update(deltaTime);
	}

	public void setSkybox(final Skybox skybox) {
		this.skybox = skybox;
	}

	public void render() {
		renderer.render();
		if (skybox != null) {
			skybox.render();
		}
	}
}
