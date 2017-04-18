package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.gui.elements.GUIElement;

public abstract class Button extends AbstractButton {

	public Button(GUIElement element, boolean initialState, Color normalColor,
			Color pressedColor, Color hoveringColor) {
		super(element, initialState, normalColor, pressedColor, hoveringColor);
	}

	@Override
	public void update(double deltaTime) {
		selected = false;
		boolean newState = false;
		if (referencedElement.contains(Mouse.getNormalizedDeviceCoordinates())) {
			selected = true;
			newState = Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1);
			if (newState && !isPressed()) {
				onButtonPress();
			}
		}
		setPressed(newState);
		super.update(deltaTime);
	}
}
