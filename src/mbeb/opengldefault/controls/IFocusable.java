package mbeb.opengldefault.controls;

public interface IFocusable {
	default boolean keepFocus() {
		return false;
	}

	default void releaseFocus() {
		Mouse.releaseFocus(this);
	}

	default void releasedFocus() {

	}

	default void gotFocus() {

	}

	default boolean hasFocus() {
		return this.equals(Mouse.getFocus());
	}

	default boolean requestFocus() {
		return Mouse.requestFocus(this);
	}
}
