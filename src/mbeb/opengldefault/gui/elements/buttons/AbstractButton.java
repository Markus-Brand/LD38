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

	/**
	 * The bounding of the Button in screen space coordinates (-1 - 1)
	 */
	protected Rectangle bounding;

	/**
	 * Was the Mouse released since the last button press
	 */
	private boolean releasedMouseSinceLastButtonPress;

	/**
	 * Constructor for a AbstreactButton
	 * 
	 * @param bounding
	 *            The bounding of the Button in screen space coordinates (-1 - 1)
	 * @param initialState
	 *            initial value of the button
	 */
	public AbstractButton(Rectangle bounding, boolean initialState) {
		this.isPressed = initialState;
		this.bounding = bounding;
		releasedMouseSinceLastButtonPress = true;
	}

	@Override
	public boolean keepFocus() {
		return !releasedMouseSinceLastButtonPress;
	}

	/**
	 * Updates the AbstractButton
	 * 
	 * @param deltaTime
	 *            time since the last update
	 */
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

	/**
	 * Action if the Button was released
	 */
	public abstract void wasReleased();

	/**
	 * Action if ther Button was pressed
	 */
	public abstract void wasPressed();

	/**
	 * Action if the buttonState changed
	 */
	public abstract void onButtonChanged();

	/**
	 * Getter for the button state
	 * 
	 * @return true if the button is pressed
	 */
	public boolean isPressed() {
		return isPressed;
	}

	/**
	 * Setter for the button state
	 * 
	 * @param isPressed
	 *            the new button state. true -> pressed
	 */
	public void setPressed(boolean isPressed) {
		boolean changed = this.isPressed != isPressed;
		this.isPressed = isPressed;
		if (changed) {
			onButtonChanged();
		}
	}
}
