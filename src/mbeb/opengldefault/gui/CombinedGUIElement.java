package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class CombinedGUIElement extends GUIElement {

	private List<GUIElement> elements;

	public CombinedGUIElement() {
		resetElements();
	}

	public void addGUIElement(GUIElement element) {
		elements.add(element);
		if (getBounding() == null) {
			setBounding(element.getBounding());
		} else {
			setBounding(getBounding().extend(element.getBounding()));
		}
		setDirty();
	}

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

	public List<GUIElement> getElements() {
		return elements;
	}

	@Override
	public boolean isDirty() {
		if (super.isDirty()) {
			return true;
		}
		for (GUIElement element : elements) {
			if (element.isDirty()) {
				return true;
			}
		}
		return false;
	}

	public void resetElements() {
		elements = new ArrayList<>();
		setBounding(null);
		setDirty();
	}

}
