package mbeb.opengldefault.gui.elements.sliders;

import java.awt.Color;
import java.lang.reflect.Field;

import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.TextGUI;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.gui.elements.CombinedGUIElement;
import mbeb.opengldefault.gui.elements.TextGUIElement;
import mbeb.opengldefault.shapes.Rectangle;

import org.joml.Vector2f;

public class OptionSlider extends Slider {

	protected TextGUIElement value;
	protected CombinedGUIElement sliderBar;
	protected AtlasGUIElement cursor;
	protected Field option;

	private static float thickness = 0.005f;

	public OptionSlider(Field option, float initialValue, float min, float max, float step, Rectangle bounding) {
		super(option.getName(), initialValue, min, max, step, bounding);

		this.option = option;
	}

	public void showSliderBar(AtlasGUI atlasGUI) {
		atlasGUI.addAtlasGUIElement(
				15,
				new Vector2f(0, (bounding.getHeight() - thickness) / 2.0f).add(bounding
						.getPosition()),
				new Vector2f(bounding.getWidth(), thickness)).setColor(Color.WHITE);;
	}

	public void showValue(TextGUI textGUI) {
		value = textGUI.addText(" ", new Vector2f(), bounding.getHeight() / 2);
		onValueChange();
	}

	public void showCursor(AtlasGUI atlasGUI) {
		cursor =
				atlasGUI.addAtlasGUIElement(4, new Vector2f(), new Vector2f(0.02f,
						0.04f));
		cursor.setPosition(new Vector2f(cursorXPos, bounding.getPosition().y + (bounding.getHeight() - thickness) / 2
				- cursor.getSize().y));
		onValueChange();
	}

	@Override
	public void update(double deltaTime) {
		super.update(deltaTime);
	}

	@Override
	public void onValueChange() {
		if (value != null) {
			value.setColor(Color.GREEN);
			System.out.println(value.getSize());
			value.setPositionRelativeTo(bounding, 0.5f, 1);
		}
		if (cursor != null) {
			cursor.setColor(Color.RED);
			//cursor.setPositionRelativeTo(bounding, relativeCursorXPos, 0.5f);
			cursor.setPosition(new Vector2f(cursorXPos - cursor.getSize().x / 2, cursor.getPosition().y));
		}
	}

}
