package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.gui.elements.GUIElement;

public abstract class Button {

	protected boolean selected;

	protected boolean isPressed;

	private boolean keepsTriggered;

	protected GUIElement referencedElement;

	private static float minClickTime = 0.2f;

	private float timeSinceLastPress = 0;

	protected Color normalColor;
	protected Color pressedColor;
	protected Color hoveringColor;

	public Button(GUIElement element, boolean keepsTriggered, boolean initialState, Color normalColor,
			Color pressedColor, Color hoveringColor) {
		this.setPressed(initialState);
		this.keepsTriggered = keepsTriggered;
		referencedElement = element;
		this.pressedColor = pressedColor;
		this.normalColor = normalColor;
		this.hoveringColor = hoveringColor;
	}

	public void update(double deltaTime) {
		selected = false;
		if (keepsTriggered) {
			if (timeSinceLastPress < minClickTime) {
				timeSinceLastPress += deltaTime;
			} else {
				if (referencedElement.contains(Mouse.getNormalizedDeviceCoordinates())) {
					selected = true;
					if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
						setPressed(!isPressed());
						onButtonPress();
						timeSinceLastPress = 0;
					}
				}
			}
		} else {
			boolean newState = false;
			if (referencedElement.contains(Mouse.getNormalizedDeviceCoordinates())) {
				selected = true;
				newState = Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1);
				if (newState && !isPressed()) {
					onButtonPress();
				}
			}
			setPressed(newState);
		}
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
		Button.minClickTime = minClickTime;
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
}
