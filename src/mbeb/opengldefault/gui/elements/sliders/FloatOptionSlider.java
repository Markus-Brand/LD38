package mbeb.opengldefault.gui.elements.sliders;

import java.lang.reflect.Field;

import mbeb.opengldefault.shapes.Rectangle;

/**
 * A class that can modify a Field of type float
 * 
 * @author Markus
 */
public class FloatOptionSlider extends OptionSlider {

	public FloatOptionSlider(Field option, float initialValue, float min, float max, float step, Rectangle bounding) {
		super(option, initialValue, min, max, step, bounding);
	}

	@Override
	public void onValueChange() {
		try {
			option.setFloat(null, currentValue);
		} catch(IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		super.onValueChange();
	}

}
