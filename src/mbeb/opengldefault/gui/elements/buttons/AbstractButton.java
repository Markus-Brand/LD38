package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;

import mbeb.opengldefault.gui.elements.GUIElement;

/**
 * An abstract class that is used to define the logic of a Button
 *
 * @author Markus
 */
public abstract class AbstractButton {

	protected boolean selected;

	protected boolean isPressed;

	protected GUIElement referencedElement;

	protected static float minClickTime = 0.2f;

	protected float timeSinceLastPress = 0;

	protected Color normalColor;
	protected Color pressedColor;
	protected Color hoveringColor;

	public AbstractButton(GUIElement element, boolean initialState, Color normalColor,
			Color pressedColor, Color hoveringColor) {
		this.setPressed(initialState);
		referencedElement = element;
		this.pressedColor = pressedColor;
		this.normalColor = normalColor;
		this.hoveringColor = hoveringColor;
	}

	public void update(double deltaTime) {
		setColor();
		referencedElement.update(deltaTime);
	}

	protected void setColor() {
		if (selected) {
			referencedElement.setColor(hoveringColor);
		} else {
			if (isPressed) {
				referencedElement.setColor(pressedColor);
			} else {
				referencedElement.setColor(normalColor);
			}
		}
	}

	public abstract void onButtonPress();

	public static void setMinClickTime(float minClickTime) {
		AbstractButton.minClickTime = minClickTime;
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
}
