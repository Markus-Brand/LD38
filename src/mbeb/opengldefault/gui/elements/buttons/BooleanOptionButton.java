package mbeb.opengldefault.gui.elements.buttons;

import java.awt.Color;
import java.lang.reflect.Field;

import org.joml.Vector2f;

import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.gui.elements.CombinedGUIElement;
import mbeb.opengldefault.gui.elements.TextGUIElement;

public class BooleanOptionButton extends Switch {

	private Field option;

	private CombinedGUIElement button;

	private AtlasGUIElement buttonAtlas;

	private TextGUIElement buttonText;

	public BooleanOptionButton(TextGUIElement text, Field option, boolean initialState, AtlasGUI atlasGUI) {
		super(text, initialState, Color.LIGHT_GRAY, Color.GRAY, new Color(160, 160, 255));
		this.option = option;

		button = new CombinedGUIElement();

		this.buttonText = text;
		button.addGUIElement(text);

		buttonAtlas =
				atlasGUI.addAtlasGUIElement(2 + (initialState ? 1 : 0), text.getPosition(),
						new Vector2f(buttonText.getSize().y * 4f));
		buttonAtlas.setPositionRelativeTo(buttonText, 0.5f, 0.5f);
		buttonAtlas.setColor(new Color(30, 30, 30), 0);
		buttonAtlas.setColor(Color.DARK_GRAY, 200);
		buttonAtlas.setColor(normalColor, 220);
		buttonAtlas.setColor(Color.GRAY, 255);
		button.addGUIElement(buttonAtlas);

	}

	@Override
	protected void setColor() {
		buttonText.setColor(Color.BLACK);
		if (selected) {
			buttonAtlas.setColor(hoveringColor, 220);
		} else {
			buttonAtlas.setColor(normalColor, 220);
		}

		if (isPressed) {
			buttonAtlas.setAtlasIndex(3);
			buttonText.setPositionRelativeTo(buttonAtlas, 0.5f, 0.45f);
		} else {
			buttonAtlas.setAtlasIndex(2);
			buttonText.setPositionRelativeTo(buttonAtlas, 0.5f, 0.55f);
		}
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);

	}

	@Override
	public void onButtonPress() {
		try {
			option.set(null, isPressed());
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
