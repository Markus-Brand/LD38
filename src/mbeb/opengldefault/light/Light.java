package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL20.*;

import java.awt.*;

import mbeb.opengldefault.rendering.shader.*;

import org.joml.*;

/**
 * @author Erik + Merlin + Markus :)
 */

public abstract class Light {

	protected Vector3f color;
	private boolean dirty = true;

	public Light(final Vector3f color) {
		this.color = color;
	}

	public Light(final Color color) {
		this.color = new Vector3f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
	}

	public void setColor(final Vector3f color) {
		this.color = color;
	}

	public void apply(final Shader shader, final String uniform) {
		glUniform3f(shader.getUniform(uniform + ".color"), color.x, color.y, color.z);
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (obj instanceof Light) {
	// return id == ((Light) obj).id;
	// }
	// return false;
	// }

	public abstract float[] getData();

	public boolean isDirty() {
		return this.dirty;
	}

	public void setDirty() {
		this.dirty = true;
	}

	public void setClean() {
		this.dirty = false;
	}
}
