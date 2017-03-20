package mbeb.opengldefault.gui.menu;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.game.GameStates;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.GUIElement;

public class Menu extends AtlasGUI {

	private GameStates nextGameState = null;

	private Map<GUIElement, GameStates> buttons;

	public Menu(String atlasName, int atlasWidth, int atlasHeight) {
		super(atlasName, atlasWidth, atlasHeight);
		buttons = new HashMap<>();
	}

	public void addButtonElement(GUIElement element, GameStates destination) {
		buttons.put(element, destination);
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
		nextGameState = null;
		if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			for (GUIElement guiElement : elements) {
				if (buttons.containsKey(guiElement) && guiElement.contains(Mouse.getNormalizedDeviceCoordinates())) {
					nextGameState = buttons.get(guiElement);
				}
			}
		}
	}

	public GameStates getNextState() {
		return nextGameState;
	}

	public void resetNextGameState() {
		nextGameState = null;
	}

}
