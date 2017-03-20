package mbeb.opengldefault.gui.menu;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.game.GameStates;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.GUIElement;

/**
 * A {@link AtlasGUI} that represents a Menu by using a mapping from GUIElements to GameStates that represents buttons
 * 
 * @author Markus
 */
public class Menu extends AtlasGUI {

	/**
	 * The next GameState that will be called if this gameState isn't active any more. null if the gameState is active
	 */
	private GameStates nextGameState = null;

	/**
	 * Map for GUIElements that represent buttons to their resulting new GameState if pressed
	 */
	private Map<GUIElement, GameStates> buttons;

	public Menu(String atlasName, int atlasWidth, int atlasHeight) {
		super(atlasName, atlasWidth, atlasHeight);
		buttons = new HashMap<>();
	}

	/**
	 * Adds a Entry to the buttons Map
	 *
	 * @param element
	 *            triggering element representing the button
	 * @param destination
	 *            Resulting GameState
	 */
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

	/**
	 * Getter for the nextGameState parameter
	 *
	 * @return the nextGameState parameter
	 */
	public GameStates getNextState() {
		return nextGameState;
	}

	/**
	 * Resets the nextGameState parameter to null
	 */
	public void resetNextGameState() {
		nextGameState = null;
	}

}
