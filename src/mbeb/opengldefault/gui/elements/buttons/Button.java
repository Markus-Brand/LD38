package mbeb.opengldefault.gui.elements.buttons;

import mbeb.opengldefault.shapes.Rectangle;

/**
 * A Button is a AbstractButton that only keeps pressed while the User keeps the Mouse pressed
 *
 * @author Markus
 */
public abstract class Button extends AbstractButton {

	public Button(Rectangle bounding, boolean initialState) {
		super(bounding, initialState);
	}

	@Override
	public void wasPressed() {
		setPressed(true);
	}

	@Override
	public void wasReleased() {
		setPressed(false);
	}
}
