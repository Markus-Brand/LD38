package mbeb.opengldefault.rendering.renderable;

import org.joml.*;

import mbeb.opengldefault.animation.*;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * something that can be rendered into a scene
 */
public interface IRenderable {

	/**
	 * render the Renderable with the given shader
	 * @param shader
	 */
	void render(ShaderProgram shader);

	BoundingBox getBoundingBox();

	default void update(double deltaTime) {
	}

	/**
	 * a static transformation on this Renderable
	 * 
	 * @return
	 */
	default Matrix4f getTransform() {
		return new Matrix4f();
	}

	default boolean hasAnimations() {
		return getCurrentPose() != null;
	}

	default Pose getCurrentPose() {
		return null;
	}
}
