package mbeb.opengldefault.gl;

import java.util.function.Function;

import mbeb.opengldefault.logging.Log;

/**
 * Represents an object managed by OpenGL.
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
	 * The current transaction level.
	 * This stores how many transactions are currently in progress.
	 */
	private int transactionLevel = 0;
	/**
	 * Whether the texture is currently bound temporarily, for the purposes of a transaction.
	 */
	private boolean temporaryBinding = false;

	/**
	 * @return the gl-handle for this object, or null if it is non-existent
	 */
	public final Integer getHandle() {
		return glHandle;
	}

	/**
	 * This returns the same as {@link GLObject#getHandle()},
	 * but generates the object if it does not exist.
	 *
	 * @return the gl-handle for this object
	 */
	public final Integer ensureHandle() {
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
	public final boolean exists() {
		return this.glHandle != null;
	}

	/**
	 * Tries to generate this object with the OpenGL API,
	 * complaining when the object already has been generated.
	 *
	 * @return whether the generation succeeded
	 */
	public final boolean generate() {
		return this.generate(true);
	}

	/**
	 * Tries to generate this object with the OpenGL API.
	 *
	 * @param errorOnExistence
	 *            whether an error should be generated when this object already exists
	 * @return whether the generation succeeded
	 */
	public final boolean generate(boolean errorOnExistence) {
		if (!this.exists()) {
			this.glHandle = this.glGenerate();
			if (!this.exists()) {
				Log.error(TAG, "Object could not be generated.");
			}
			return this.exists();
		} else if (errorOnExistence) {
			Log.error(TAG, "Object has already been generated.");
		}
		return !errorOnExistence;
	}

	/**
	 * Ensures the existence of this object in the OpenGL context, generating it if need be.
	 *
	 * @return whether the object exists, only false if generation failed
	 */
	public final boolean ensureExists() {
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
	public final boolean bind() {
		boolean success = true;
		if (!this.isBound()) {
			success = this.glBind();
			if (!success) {
				Log.error(TAG, "Could not bind object.");
			}
		}
		return success;
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
	public final boolean isBound() {
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
	public final boolean unbind() {
		boolean success = true;
		if (this.isBound()) {
			success = this.glUnbind();
			if (!success) {
				Log.error(TAG, "Could not unbind object.");
			}
		}
		return success;
	}

	//<editor-fold desc="Transactions">
	/**
	 * Tries to ensure this object is the one being edited.
	 * This is called if a starting transaction found the object already bound.
	 *
	 * @return whether the operation succeeded
	 */
	protected boolean glBeginTransaction() {
		return true;
	}

	/**
	 * Ensures this texture is bound after this method has been called.
	 * If it is bound, it changes the active texture unit to the unit it is bound to.
	 * If in is not bound, it starts a temporary binding and binds the object.
	 * This makes sure any glTex* calls affect this texture.
	 *
	 * @return whether the operation succeeded
	 */
	protected final boolean beginTransaction() {
		this.ensureExists();
		if (this.isBound()) {
			boolean success = this.glBeginTransaction();
			if (success) {
				this.transactionLevel++;
			}
			return success;
		} else {
			boolean success = this.bind();
			this.temporaryBinding = success;
			if (success) {
				this.transactionLevel++;
			}
			return success;
		}
	}

	/**
	 * Releases a temporary binding created by {@link #beginTransaction()}.
	 *
	 * @return whether the operation succeeded
	 */
	protected final boolean finishTransaction() {
		if (this.transactionLevel > 0) {
			this.transactionLevel--;
			if (this.temporaryBinding && this.transactionLevel == 0) {
				boolean success = this.unbind();
				this.temporaryBinding = !success;
				return success;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	/**
	 * @param actor
	 *            a function to execute while this GLObject is guaranteed to be bound
	 * @return whether the operation succeeded
	 */
	@SuppressWarnings("unchecked") //Because <this.class> is not (yet) a thing
	public final <T extends GLObject> boolean whileBound(Function<T, Boolean> actor) {
		if (this.beginTransaction()) {
			boolean success = actor.apply((T) this);
			if (!this.finishTransaction()) {
				Log.error(TAG, "Could not finish transaction.");
			}
			return success;
		} else {
			return false;
		}
	}
	//</editor-fold>

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
	public final boolean delete() {
		if (this.exists()) {
			this.unbind();
			boolean success = this.glDelete();
			if (success) {
				this.glHandle = null;
			} else {
				Log.error(TAG, "Object could not be deleted.");
			}
			return success;
		} else {
			return false;
		}
	}

	@Override
	protected final void finalize() throws Throwable {
		if (this.exists()) {
			Log.error(TAG, "DELETE ME!");
			this.delete();
		}
	}
}
