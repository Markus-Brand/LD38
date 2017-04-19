package mbeb.opengldefault.gui.elements.sliders;

import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.IFocusable;
import mbeb.opengldefault.controls.Mouse;
import mbeb.opengldefault.shapes.Rectangle;

/**
 * Abstract class containing all the logic needed for a slider
 *
 * @author Markus
 */
public abstract class Slider implements IFocusable {

	/**
	 * current value of the silder
	 */
	protected float currentValue;
	/**
	 * max slider value
	 */
	protected float maxValue;
	/**
	 * min slider value
	 */
	protected float minValue;
	/**
	 * size of a step on the slider
	 */
	protected float step;

	/**
	 * Bounding of the slider
	 */
	protected Rectangle bounding;

	/**
	 * x Position of the cursor on screen
	 */
	protected float cursorXPos;

	/**
	 * Name of the slider
	 */
	protected String name;

	public Slider(String name, float initialValue, float min, float max, float step, Rectangle bounding) {
		currentValue = initialValue;
		this.minValue = min;
		this.maxValue = max;
		this.step = step;
		this.name = name;
		this.bounding = bounding;
		cursorXPos = (initialValue - min) / (max - min) * bounding.getWidth() + bounding.getPosition().x;

	}

	/**
	 * Keep focused while Mouse is pressed to allow dragging the slider even if the mouse is not in the bounding any
	 * more
	 */
	@Override
	public boolean keepFocus() {
		return Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1);
	}

	/**
	 * Update the slider
	 * 
	 * @param deltaTime
	 */
	public void update(double deltaTime) {
		if (Mouse.isDown(GLFW.GLFW_MOUSE_BUTTON_1)) {
			if (bounding.contains(Mouse.getNormalizedDeviceCoordinates())) {
				requestFocus();
			}

			if (hasFocus()) {
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
		} else {
			releaseFocus();
		}
	}

	/**
	 * used for the finding the best value for a relative mouse pos
	 * 
	 * @param relativeMousePos
	 *            position on the slider between 0 and 1
	 * @return
	 */
	private float getNearestValue(float relativeMousePos) {
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

	/**
	 * Setter for the value, that will also call onValueChange
	 * 
	 * @param currentValue
	 */
	public void setValue(float currentValue) {
		this.currentValue = currentValue;
		onValueChange();
	}

	/**
	 * Abstract method that is called when the value has changed
	 */
	public abstract void onValueChange();

	/**
	 * Get current value as String
	 * 
	 * @return current value as String
	 */
	public String getValueString() {
		return format(currentValue);
	}

	/**
	 * Get min value as String
	 * 
	 * @return min value as String
	 */
	public String getMinValueString() {
		return format(minValue);
	}

	/**
	 * Get max value as String
	 * 
	 * @return max value as String
	 */
	public String getMaxValueString() {
		return format(maxValue);
	}

	/**
	 * How to format the values to be displayed
	 * 
	 * @param in
	 *            the input that will be formatted
	 * @return the value as a String
	 */
	public String format(float in) {
		return String.valueOf(in);
	}
}
