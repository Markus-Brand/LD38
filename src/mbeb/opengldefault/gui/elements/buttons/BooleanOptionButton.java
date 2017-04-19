package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;
import java.lang.reflect.Field;

import org.joml.Vector2f;

import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.shapes.Rectangle;

/**
 * A Class that adds a Button in the Options for changing a boolean type field
 *
 * @author Markus
 */
public class BooleanOptionButton extends Switch {

	/** the option field to set */
	private Field option;

	/** the button guiElement, not including the text */
	private AtlasGUIElement buttonAtlas;

	/** the TextGUIElement on the button */
	private TextGUIElement buttonText;

	public BooleanOptionButton(Rectangle bounding, Field option, boolean initialState) {
		super(bounding, initialState);
		this.option = option;
	}

	/**
	 * Show the guiElements of this button in the guis
	 *
	 * @param atlasGUI
	 * @param textGUI
	 */
	public void show(AtlasGUI atlasGUI, TextGUI textGUI) {
		buttonAtlas =
				atlasGUI.addAtlasGUIElement(2 + (isPressed ? 1 : 0), new Vector2f(),
						new Vector2f(bounding.getWidth()));
		buttonAtlas.setPositionRelativeTo(bounding, 0.5f, 0.5f);
		buttonAtlas.setColor(new Color(30, 30, 30), 0);
		buttonAtlas.setColor(Color.DARK_GRAY, 200);
		buttonAtlas.setColor(Color.LIGHT_GRAY, 220);
		buttonAtlas.setColor(Color.GRAY, 255);

		buttonText = textGUI.addText(option.getName(), new Vector2f(), bounding.getHeight() / 2);
		buttonText.setColor(Color.BLACK);
		onButtonChanged();
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
	}

	@Override
	public void gotFocus() {
		buttonAtlas.setColor(new Color(0.8f, 0.9f, 0.8f), 220);
	}

	@Override
	public void releasedFocus() {
		buttonAtlas.setColor(Color.LIGHT_GRAY, 220);
	}

	@Override
	public void onButtonChanged() {
		if (buttonAtlas != null && buttonText != null) {
			if (isPressed) {
				buttonAtlas.setAtlasIndex(3);
				buttonText.setPositionRelativeTo(bounding, 0.5f, 0.35f);
			} else {
				buttonAtlas.setAtlasIndex(2);
				buttonText.setPositionRelativeTo(bounding, 0.5f, 0.65f);
			}
		}

		try {
			option.set(null, isPressed());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
