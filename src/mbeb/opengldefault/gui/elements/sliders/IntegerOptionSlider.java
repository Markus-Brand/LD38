package mbeb.opengldefault.gui.elements.sliders;

import java.lang.reflect.Field;

import mbeb.opengldefault.shapes.Rectangle;

/**
 * A class that can modify a Field of type int
 * 
 * @author Markus
 */
public class IntegerOptionSlider extends OptionSlider {

	public IntegerOptionSlider(Field option, float initialValue, float min, float max, float step, Rectangle bounding) {
		super(option, initialValue, min, max, step, bounding);
	}

	@Override
	public void onValueChange() {
		try {
			option.setInt(null, (int) currentValue);
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		super.onValueChange();
	}

	@Override
	public String format(float in) {
		return String.valueOf((int) in);
	}
}
