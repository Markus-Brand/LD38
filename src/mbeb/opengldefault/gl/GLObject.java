package mbeb.opengldefault.gl;

import mbeb.opengldefault.logging.Log;

/**
 * Represents an object manages by OpenGL.
 * Each object can be created, bound, unbound and destroyed.
 * 
 * @author Potti, Erik
 * @version 1.0
 */
public abstract class GLObject {
	private static final String TAG = "GLObject";

	/**
	 * The OpenGL handle of this object.
	 * Is null if this object does not exist yet or has already been deleted
	 */
	private Integer glHandle = null;

	/**
	 * @return the gl-handle for this object, or null if it is non-existent
	 */
	public Integer getHandle() {
		return glHandle;
	}

	/**
	 * This returns the same as {@link GLObject#getHandle()},
	 * but generates the object if it does not exist.
	 * 
	 * @return the gl-handle for this object
	 */
	public Integer ensureHandle() {
		ensureExists();
		return getHandle();
	}

	/**
	 * Performs the object type specific generation.
	 *
	 * @return the handle of the generated object or null if generation failed
	 */
	protected abstract Integer glGenerate();

	/**
	 * @return whether this GLObject already exists in the OpenGL context
	 */
	public boolean exists() {
		return this.glHandle != null;
	}

	/**
	 * Tries to generate this object with the OpenGL API,
	 * complaining when the object already has been generated.
	 * 
	 * @return whether the generation succeeded
	 */
	public boolean generate() {
		return this.generate(true);
	}

	/**
	 * Tries to generate this object with the OpenGL API.
	 *
	 * @param errorOnExistence
	 *            whether an error should be generated when this object already exists
	 * @return whether the generation succeeded
	 */
	public boolean generate(boolean errorOnExistence) {
		if (!this.exists()) {
			this.glHandle = this.glGenerate();
			if (!this.exists()) {
				Log.error(TAG, "Object could not be generated.");
			}
			return this.exists();
		} else if (errorOnExistence) {
			Log.error(TAG, "Object has already been generated.");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Ensures the existence of this object in the OpenGL context, generating it if need be.
	 * 
	 * @return whether the object exists, only false if generation failed
	 */
	public boolean ensureExists() {
		return this.generate(false);
	}

	/**
	 * Tries to bind this object to the current context, updating
	 * {@link mbeb.opengldefault.openglcontext.ContextBindings} to match the new state.
	 * 
	 * @return whether the binding succeeded
	 */
	protected abstract boolean glBind();

	/**
	 * Binds this object to the OpenGL context.
	 * 
	 * @return whether the operation succeeded.
	 */
	public boolean bind() {
		if (!this.isBound()) {
			boolean success = this.glBind();
			if (!success) {
				Log.error(TAG, "Could not bind object.");
			}
			return success;
		} else {
			return true;
		}
	}

	/**
	 * Checks with {@link mbeb.opengldefault.openglcontext.ContextBindings} whether this object is currently bound.
	 * 
	 * @return whether this object is currently bound to the context
	 */
	protected abstract boolean isBoundToContext();

	/**
	 * @return whether this object is currently bound to the context
	 */
	public boolean isBound() {
		return this.isBoundToContext();
	}

	/**
	 * Tries to unbind this object from the current context, updating
	 * {@link mbeb.opengldefault.openglcontext.ContextBindings} to match the new state.
	 * 
	 * @return whether the unbinding (of isaac) succeeded
	 */
	protected abstract boolean glUnbind();

	/**
	 * Unbinds this object from the OpenGL context.
	 * 
	 * @return whether the operation succeeded.
	 */
	public boolean unbind() {
		if (this.isBound()) {
			boolean success = this.glUnbind();
			if (!success) {
				Log.error(TAG, "Could not unbind object.");
			}
			return success;
		} else {
			return true;
		}
	}

	/**
	 * Performs the object type specific deletion.
	 *
	 * @return whether the operation succeeded
	 */
	protected abstract boolean glDelete();

	/**
	 * Tries to delete this object with the OpenGL API.
	 * 
	 * @return whether the operation succeeded
	 */
	public boolean delete() {
		if (this.exists()) {
			this.unbind();
			boolean success = this.glDelete();
			if (!success) {
				Log.error(TAG, "Object could not be deleted.");
			}
			return success;
		} else {
			return false;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.exists()) {
			Log.error(TAG, "DELETE ME!");
			this.delete();
		}
	}
}
