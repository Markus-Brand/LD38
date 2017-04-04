package mbeb.opengldefault.gui.elements;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

/**
 * A {@link GUIElement} that holds a List of {@link GUIElement}s. Good for managing text, for example
 *
 * @author Markus
 */
public class CombinedGUIElement extends GUIElement {

	/**
	 * The List of {@link GUIElement}s that are managed by this CombinedGUIElement
	 */
	private List<GUIElement> elements;

	public CombinedGUIElement() {
		resetElements();
	}

	/**
	 * Adds a {@link GUIElement} and updates this elements Bounding
	 *
	 * @param element
	 *            new {@link GUIElement}
	 */
	public void addGUIElement(GUIElement element) {
		elements.add(element);
		if (getBounding() == null) {
			setBounding(element.getBounding());
		} else {
			setBounding(getBounding().extend(element.getBounding()));
		}
		setDirty();
	}

	/**
	 * Updates the position of this element and off all of the {@link GUIElement}s in the elements list
	 */
	@Override
	public void setPosition(Vector2f position) {
		Vector2f delta = position.sub(getPosition(), new Vector2f());
		for (GUIElement element : elements) {
			element.setPosition(element.getPosition().add(delta, new Vector2f()));
		}
		super.setPosition(position);
	}

	@Override
	public void update(double deltaTime) {
		for (GUIElement element : elements) {
			element.update(deltaTime);
		}
	}

	@Override
	public int writeToBuffer(FloatBuffer buffer, int offset) {
		int totalOffset = 0;
		for (GUIElement element : elements) {
			totalOffset += element.writeToBuffer(buffer, offset + totalOffset);
		}
		return totalOffset;
	}

	/**
	 * Getter for the elements saved in this CombinedGUIElement
	 *
	 * @return the List of GUIElements
	 */
	public List<GUIElement> getElements() {
		return elements;
	}

	@Override
	public boolean isDirty() {
		if (super.isDirty()) {
			return true;
		}
		return elements.stream().anyMatch(element -> element.isDirty());
	}

	@Override
	public void setLutRow(float lutRow) {
		super.setLutRow(lutRow);
		for (GUIElement guiElement : elements) {
			guiElement.setLutRow(lutRow);
		}
	}

	@Override
	public void useTexture() {
		super.useTexture();
		if (elements != null) {
			for (GUIElement guiElement : elements) {
				guiElement.useTexture();
			}
		}
	}

	/**
	 * Resets the Elements and the bounding. Useful for changing text, for example.
	 */
	public void resetElements() {
		elements = new ArrayList<>();
		setBounding(null);
		setDirty();
	}

	@Override
	public int getNumElements() {
		return elements.size();
	}

}
