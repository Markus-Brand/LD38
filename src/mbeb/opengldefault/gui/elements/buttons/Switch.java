package mbeb.opengldefault.gui.elements.buttons;

import mbeb.opengldefault.shapes.Rectangle;

/**
 * A Switch is a AbstractButton that changes its state every time it is clicked
 *
 * @author Markus
 */
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
