package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.ICamera;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private SceneGraphRenderer renderer;
	private final SceneObject sceneGraph;
	private ICamera cam;

	public Scene(ICamera cam) {
		this.cam = cam;
		this.sceneGraph = new SceneObject(null, null, null);
		renderer = new VisibleSceneGraphRenderer(sceneGraph, cam);
	}

	public SceneObject getSceneGraph() {
		return sceneGraph;
	}

	public void update(double deltaTime) {
		cam.update(deltaTime);
	}

	public void render() {
		renderer.render();
	}
}
