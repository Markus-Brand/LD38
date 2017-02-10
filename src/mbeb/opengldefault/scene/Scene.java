package mbeb.opengldefault.scene;

import mbeb.opengldefault.camera.Camera;

/**
 * A scene is an object in which objects live and the camera moves. There should
 * be always one active scene that gets rendered.
 */
public class Scene {

	private final SceneObject sceneGraph;
	private Camera cam;

	public Scene(Camera cam) {
		this.cam = cam;
		this.sceneGraph = new SceneObject(null, null, null);
	}

	public SceneObject getSceneGraph() {
		return sceneGraph;
	}
	
	public void render() {
		cam.update();
		new VisibleSceneGraphRenderer(sceneGraph, cam).render();
	}
}
