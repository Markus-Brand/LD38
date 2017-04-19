package mbeb.opengldefault.gui.elements.buttons;

import mbeb.opengldefault.shapes.Rectangle;

public abstract class Switch extends AbstractButton {

	public Switch(Rectangle bounding, boolean initialState) {
		super(bounding, initialState);
	}

	@Override
	public void wasPressed() {
		setPressed(!isPressed());
	}

	@Override
	public void wasReleased() {

	}
}
