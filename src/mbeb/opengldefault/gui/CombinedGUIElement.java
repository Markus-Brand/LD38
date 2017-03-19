package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class CombinedGUIElement extends GUIElement {

	private List<GUIElement> elements;

	public CombinedGUIElement() {
		elements = new ArrayList<GUIElement>();
		setBounding(null);
	}

	public void addGUIElement(GUIElement element) {
		elements.add(element);
		if (getBounding() == null) {
			setBounding(element.getBounding());
		} else {
			System.out.println("Pre :" + getBounding().getPosition() + " " + getBounding().getSize());
			setBounding(getBounding().extend(element.getBounding()));
			System.out.println("Post:" + getBounding().getPosition() + " " + getBounding().getSize());
		}
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

}
