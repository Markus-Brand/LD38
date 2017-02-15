package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.*;
import mbeb.opengldefault.rendering.renderable.*;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private SceneGraphRenderer renderer;
	private final SceneObject sceneGraph;
	private ICamera camera;
	private Skybox skybox;

	public Scene(ICamera cam) {
		this(cam, null);
	}

	public Scene(ICamera cam, Skybox skybox) {
		this.camera = cam;
		this.skybox = skybox;
		this.sceneGraph = new SceneObject(null, null, null);
		renderer = new VisibleSceneGraphRenderer(sceneGraph, cam);
	}

	public SceneObject getSceneGraph() {
		return sceneGraph;
	}

	public void update(double deltaTime) {
		camera.update(deltaTime);
		sceneGraph.update(deltaTime);
	}

	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}

	public void render() {
		renderer.render();
		if (skybox != null) {
			skybox.render();
		}
	}
}
