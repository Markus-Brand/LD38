package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.animation.Pose;
import mbeb.opengldefault.rendering.shader.*;
import mbeb.opengldefault.scene.*;
import org.joml.Matrix4f;

/**
 * something that can be rendered into a scene
 */
public interface IRenderable {
	void render(Shader shader);

	BoundingBox getBoundingBox();

	default void update(double deltaTime) {
	}

	/**
	 * a static transformation on this Renderable
	 *
	 * @return the static transformation on this Renderable
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
