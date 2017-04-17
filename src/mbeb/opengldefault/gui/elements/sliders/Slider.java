package mbeb.opengldefault.gui.elements.sliders;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.shapes.Rectangle;

/**
 * Abstract class containing all the logic needed for a slider
 * 
 * @author Markus
 */
public abstract class Slider {

	protected float currentValue;
	protected float maxValue;
	protected float minValue;
	protected float step;

	protected float cursorXPos;

	protected String name;

	protected Rectangle bounding;

	public Slider(String name, float initialValue, float min, float max, float step, Rectangle bounding) {
		currentValue = initialValue;
		this.minValue = min;
		this.maxValue = max;
		this.step = step;
		this.name = name;
		this.bounding = bounding;
		cursorXPos = (initialValue - min) / (max - min) * bounding.getWidth() + bounding.getPosition().x;

	}

	public void update(double deltaTime) {
		if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			if (bounding.contains(Mouse.getNormalizedDeviceCoordinates())) {
				float realMouseXPos = (Mouse.getNormalizedDeviceCoordinates().x - bounding.getPosition().x)
						/ bounding.getWidth();
				float normalizedMouseXPos = Math.max(Math.min(realMouseXPos, 1), 0);
				float newValue = getNearestValue(normalizedMouseXPos);
				if (newValue != currentValue) {
					cursorXPos =
							(newValue - minValue) / (maxValue - minValue) * bounding.getWidth()
									+ bounding.getPosition().x;
					setValue(newValue);
				}
			}
		}
	}

	public float getNearestValue(float relativeMousePos) {
		float mouseValue = relativeMousePos * (maxValue - minValue);
		float lowerValue = Math.max(mouseValue - mouseValue % step + minValue, minValue);
		float higherValue = Math.min(mouseValue - mouseValue % step + step + minValue, maxValue);
		float optimalValue;
		if ((lowerValue + higherValue) / 2 > mouseValue + minValue) {
			optimalValue = lowerValue;
		} else {
			optimalValue = higherValue;
		}
		return optimalValue;
	}

	public void setValue(float currentValue) {
		this.currentValue = currentValue;
		onValueChange();
	}

	public abstract void onValueChange();
}
