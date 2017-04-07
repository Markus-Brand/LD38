package mbeb.opengldefault.gl.texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

import mbeb.opengldefault.gl.GLObject;

/**
 * Represents any texture created with OpenGL.
 * 
 * @author Potti
 * @version 1.0
 */
public abstract class Texture extends GLObject {

	public enum Type {
		/**
		 * A two dimensional texture.
		 */
		TEXTURE_2D(GL_TEXTURE_2D),
		/**
		 * An array of two dimensional textures.
		 */
		TEXTURE_2D_ARRAY(GL_TEXTURE_2D_ARRAY),
		/**
		 * A three dimensional texture.
		 */
		TEXTURE_3D(GL_TEXTURE_3D),
		/**
		 * A texture that can be sampled in all directions.
		 */
		TEXTURE_CUBE_MAP(GL_TEXTURE_CUBE_MAP);

		private int glEnum;

		Type(int glEnum) {
			this.glEnum = glEnum;
		}

		public int getGlEnum() {
			return glEnum;
		}
	}

}
