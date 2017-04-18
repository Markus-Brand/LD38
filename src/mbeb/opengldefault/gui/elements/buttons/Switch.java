package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.gui.elements.GUIElement;

public abstract class Switch extends AbstractButton {

	public Switch(GUIElement element, boolean initialState, Color normalColor,
			Color pressedColor, Color hoveringColor) {
		super(element, initialState, normalColor, pressedColor, hoveringColor);
	}

	@Override
	public void update(double deltaTime) {
		selected = false;
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
		super.update(deltaTime);
	}
}
