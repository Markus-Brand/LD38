package mbeb.opengldefault.gui.elements.sliders;

import java.lang.reflect.Field;

import mbeb.opengldefault.shapes.Rectangle;

public class IntegerOptionSlider extends OptionSlider {

	public IntegerOptionSlider(Field option, float initialValue, float min, float max, float step, Rectangle bounding) {
		super(option, initialValue, min, max, step, bounding);
	}

	@Override
	public void onValueChange() {
		int newOptionValue = (int) currentValue;
		value.setText(option.getName() + ": " + newOptionValue);
		try {
			option.setInt(null, newOptionValue);
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		super.onValueChange();
	}

}
