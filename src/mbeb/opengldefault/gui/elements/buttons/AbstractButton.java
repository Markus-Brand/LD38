package mbeb.opengldefault.gui.elements.buttons;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.IFocusable;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.shapes.Rectangle;

/**
 * An abstract class that is used to define the logic of a Button
 *
 * @author Markus
 */
public abstract class AbstractButton implements IFocusable {

	/**
	 * Is this button currently pressed
	 */
	protected boolean isPressed;

	protected Rectangle bounding;

	private boolean releasedMouseSinceLastButtonPress;

	public AbstractButton(Rectangle bounding, boolean initialState) {
		this.isPressed = initialState;
		this.bounding = bounding;
		releasedMouseSinceLastButtonPress = true;
	}

	@Override
	public boolean keepFocus() {
		return !releasedMouseSinceLastButtonPress;
	}

	public void update(double deltaTime) {
		if (releasedMouseSinceLastButtonPress) {
			if (bounding.contains(Mouse.getNormalizedDeviceCoordinates())) {
				requestFocus();
				if (hasFocus()) {
					if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
						wasPressed();
						releasedMouseSinceLastButtonPress = false;
					}
				}
			} else {
				releaseFocus();
			}
		} else {
			if (!Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
				wasReleased();
				releasedMouseSinceLastButtonPress = true;
			}
		}
	}

	public abstract void wasReleased();

	public abstract void wasPressed();

	public abstract void onButtonChanged();

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		boolean changed = this.isPressed != isPressed;
		this.isPressed = isPressed;
		if (changed) {
			onButtonChanged();
		}
	}
}
