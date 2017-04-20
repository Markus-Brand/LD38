package mbeb.opengldefault.controls;

/**
 * An object that can have the Mouse Focus
 * 
 * @author Markus
 */
public interface IFocusable {
	/**
	 * Does the object want to keep the focus, if another object asks for the focus
	 * 
	 * @return true, if the object wants to keep focus
	 */
	default boolean keepFocus() {
		return false;
	}

	/**
	 * Unregister this object from the Mouse focus
	 */
	default void releaseFocus() {
		Mouse.releaseFocus(this);
	}

	/**
	 * Called when the object just lost the focus
	 */
	default void releasedFocus() {

	}

	/**
	 * Called when the object just got the focus
	 */
	default void gotFocus() {

	}

	/**
	 * Does the object currently have the Mouse focus
	 * 
	 * @return true if the object has the Mouse focus
	 */
	default boolean hasFocus() {
		return this.equals(Mouse.getFocus());
	}

	/**
	 * Requests the focus from the Mouse.
	 * Will succeed if the Mouse has no focus or the current focus is willing to release its focus
	 * 
	 * @return true if the object was able to get the focus
	 */
	default boolean requestFocus() {
		return Mouse.requestFocus(this);
	}
}
