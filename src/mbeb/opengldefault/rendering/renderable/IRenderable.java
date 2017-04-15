package mbeb.opengldefault.rendering.renderable;

import mbeb.opengldefault.animation.Pose;
import org.joml.*;

import mbeb.opengldefault.gl.shader.*;
import mbeb.opengldefault.scene.*;

/**
 * something that can be rendered into a scene (is an IRenderableHolder of itself)
 */
public interface IRenderable extends IRenderableHolder{

	@Override
	default IRenderable getRenderable() {
		return this;
	}

	void render(ShaderProgram shader);

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
