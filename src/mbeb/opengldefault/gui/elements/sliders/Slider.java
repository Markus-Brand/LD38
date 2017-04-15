package mbeb.opengldefault.gui.elements.sliders;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.shapes.Rectangle;

public abstract class Slider {

	protected float currentValue;
	protected float maxValue;
	protected float minValue;
	protected float step;

	protected float cursorXPos;
	protected float relativeCursorXPos;

	protected String name;

	protected Rectangle bounding;

	public Slider(String name, float initialValue, float min, float max, float step, Rectangle bounding) {
		currentValue = initialValue;
		this.minValue = min;
		this.maxValue = max;
		this.step = step;
		this.name = name;
		this.bounding = bounding;
		this.relativeCursorXPos = (initialValue - min) / (max - min);
		cursorXPos = relativeCursorXPos * bounding.getWidth() + bounding.getPosition().x;

	}

	public void update(double deltaTime) {
		if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			if (bounding.contains(Mouse.getNormalizedDeviceCoordinates())) {
				relativeCursorXPos =
						Math.max(
								Math.min(
										(Mouse.getNormalizedDeviceCoordinates().x - bounding.getPosition().x)
												/ bounding.getWidth(), 1), 0);
				int numSteps = Math.round((maxValue - minValue) / step);
				float stepSize = 1.0f / numSteps;
				int stepNum = Math.round(relativeCursorXPos / stepSize);
				float newValue = stepNum * step + minValue;
				if (newValue != currentValue) {
					cursorXPos = stepSize * stepNum * bounding.getWidth() + bounding.getPosition().x;
					setValue(newValue);
				}
			}
		}
	}

	public void setValue(float currentValue) {
		this.currentValue = currentValue;
		onValueChange();
	}

	public abstract void onValueChange();
}
