package mbeb.opengldefault.gl.vao;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.GLObject;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.openglcontext.ContextBindings;
import mbeb.opengldefault.rendering.io.DataFragment;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * A VAO
 */
public class VertexArray extends GLObject {

	private static final String TAG = "VertexArray";
	
	/**
	 * an openGL-AttribPointer: one rule on how to understand VBO data
	 */
	public static class AttributePointer {

		/**
		 * The type of data for an AttribPointer
		 */
		public enum Type {
			INT(GL_INT), FLOAT(GL_FLOAT);

			private int glType;

			Type(int glType) {
				this.glType = glType;
			}

			/**
			 * @return the OpenGL enum representing this data type
			 */
			public int getGlType() {
				return glType;
			}
		}

		private int id = -1;
		private int size;
		private Type type;
		private boolean normalized;
		private int stride;
		private int offset;
		
		public AttributePointer(int size, Type type, boolean normalized, int stride, int offset) {
			this.size = size;
			this.type = type;
			this.normalized = normalized;
			this.stride = stride;
			this.offset = offset;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		/**
		 * tell openGL my own parameters
		 */
		public void sync() {
			if (type == Type.INT) {
				glVertexAttribIPointer(id, size, type.getGlType(), stride, offset);
				GLErrors.checkForError(TAG, "glVertexAttribIPointer");
			} else {
				glVertexAttribPointer(id, size, type.getGlType(), normalized, stride, offset);
				GLErrors.checkForError(TAG, "glVertexAttribPointer", true);
			}
		}

		/**
		 * set the divisor for this attribute
		 * @param divisor
		 */
		public void divisor(int divisor) {
			glVertexAttribDivisor(id, divisor);
			GLErrors.checkForError(TAG, "glVertexAttribDivisor");
		}

		/**
		 * mark this attribute for "instanced rendering": it is only updated once per mesh
		 */
		public void instanced() {
			this.divisor(1);
		}
	}
	
	private List<AttributePointer> attributePointers = new ArrayList<>();

	@Override
	protected Integer glGenerate() {
		int handle = glGenVertexArrays();
		return GLErrors.checkForError(TAG, "glGenVertexArrays") ? null : handle;
	}

	@Override
	protected boolean glBind() {
		glBindVertexArray(this.ensureHandle());
		boolean success = !GLErrors.checkForError(TAG, "glBindVertexArray");
		if (success) {
			ContextBindings.bind(this);
		}
		return success;
	}

	@Override
	protected boolean isBoundToContext() {
		return ContextBindings.isBound(this);
	}

	@Override
	protected boolean glUnbind() {
		glBindVertexArray(0);
		boolean success = !GLErrors.checkForError(TAG, "glBindVertexArray");
		if (success) {
			ContextBindings.unbindVAO();
		}
		return success;
	}

	@Override
	protected boolean glDelete() {
		glDeleteVertexArrays(getHandle());
		return !GLErrors.checkForError(TAG, "glDeleteVertexArrays");
	}
	
	/**
	 * set all the attribPointers according to a given DataFormat
	 * @param dataFormat
	 */
	public void attribPointers(DataFragment[] dataFormat) {
		int stride = 0;
		for (DataFragment dataFragemt : dataFormat) {
			stride += Constants.FLOAT_SIZE * dataFragemt.size();
		}
		
		int offset = 0;
		for (DataFragment dataFragment : dataFormat) {
			AttributePointer.Type type = dataFragment.isFloat() ? AttributePointer.Type.FLOAT : AttributePointer.Type.INT;
			this.attribPointer(dataFragment.size(), type, stride, offset);
			offset += dataFragment.size() * Constants.FLOAT_SIZE;
		}
	}
	
	/**
	 * add and enable a new AttribPointer
	 * @param size
	 * @param type
	 * @param stride
	 * @param offset
	 */
	public void attribPointer(int size, AttributePointer.Type type, int stride, int offset) {
		attribPointer(size, type, false, stride, offset);
	}
	
	/**
	 * Add and enable a new AttribPointer
	 * @param size
	 * @param type
	 * @param normalized
	 * @param stride
	 * @param offset
	 */
	public void attribPointer(int size, AttributePointer.Type type, boolean normalized, int stride, int offset) {
		attribPointer(new AttributePointer(size, type, normalized, stride, offset));
	}
	
	/**
	 * add a nd enable an AttribPointer
	 * @param pointer
	 */
	public void attribPointer(AttributePointer pointer) {
		attributePointers.add(pointer);
		pointer.setId(attributePointers.size() - 1);
		syncPointer(pointer);
	}

	/**
	 * upload the AttribPointer-data to the GPU and enable the pointer
	 *
	 * @param pointer the pointer to enable
	 */
	private void syncPointer(AttributePointer pointer) {
		pointer.sync();
		glEnableVertexAttribArray(pointer.getId());
		GLErrors.checkForError(TAG, "glEnableVertexAttribArray");
	}

	/**
	 * @return the last {@link AttributePointer} in the list of Pointers for this VAO
	 */
	public AttributePointer getLastPointer() {
		return attributePointers.get(attributePointers.size() - 1);
	}

	/**
	 * removes the last x pointers (does not sync that removal to ogl)
	 * @param amount
	 */
	public void clearPointers(int amount) {
		for (int pointer = 0; pointer < amount; pointer++) {
			attributePointers.remove(attributePointers.size() - 1);
		}
	}

	/**
	 * removes pointers until there are only x left (does not sync that removal to ogl)
	 * @param maximumAmount
	 */
	public void trimPointers(int maximumAmount) {
		int toRemove = this.attributePointers.size() - maximumAmount;
		if (toRemove > 0) {
			clearPointers(toRemove);
		}
	}
}
