package mbeb.opengldefault.gui;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * A GUI Element that gets rendered in a {@link GUI}
 *
 * @author Markus
 */
public abstract class GUIElement {

	private Rectangle bounding;

	/**
	 * Has data changed?
	 */
	private boolean dirty;

	public GUIElement(Vector2f position, Vector2f size) {
		bounding = new Rectangle(position, size);
		dirty = true;
	}

	public GUIElement(Vector2f size) {
		this(new Vector2f(), size);
	}

	public GUIElement() {
		this(new Vector2f(1));
	}

	/**
	 * Sets the position of the GUI Element relative to the screen
	 *
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to screen.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to screen.
	 * @return this
	 */
	public GUIElement setPositionRelativeToScreen(float relativeX, float relativeY) {
		return setPositionRelativeTo(new Vector2f(-1), new Vector2f(2), relativeX, relativeY);
	}

	/**
	 * Sets the position of the GUIElement relative to another GUI Element
	 *
	 * @param element
	 *            the parent GUIElement
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to parent.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to parent.
	 * @return this
	 */
	public GUIElement setPositionRelativeTo(GUIElement element, float relativeX, float relativeY) {
		return setPositionRelativeTo(element.getPosition(), element.getSize(), relativeX, relativeY);
	}

	/**
	 * Sets the position of the GUIElement relative to a BoundingBox
	 *
	 * @param boundingStart
	 *            bounding box start
	 * @param boundingSize
	 *            bounding box size
	 * @param relativeX
	 *            float value normally in range [0, 1] for x position relative to a BoundingBox.
	 * @param relativeY
	 *            float value normally in range [0, 1] for y position relative to a BoundingBox.
	 * @return this
	 */
	public GUIElement setPositionRelativeTo(Vector2f boundingStart, Vector2f boundingSize, float relativeX,
			float relativeY) {
		Vector2f maxPosition = boundingStart.add(boundingSize.sub(bounding.getSize(), new Vector2f()), new Vector2f());
		setPosition(boundingStart.lerp(maxPosition, new Vector2f(relativeX, relativeY), new Vector2f()));
		return this;
	}

	public Vector2f getPosition() {
		return bounding.getPosition();
	}

	public void setPosition(Vector2f position) {
		if (bounding == null) {
			bounding = new Rectangle(new Vector2f(), new Vector2f());
		}
		bounding.setPosition(position);
	}

	public Vector2f getSize() {
		return bounding.getSize();
	}

	public void setSize(Vector2f size) {
		if (bounding == null) {
			bounding = new Rectangle(new Vector2f(), new Vector2f());
		}
		bounding.setSize(size);
	}

	/**
	 * Calculates Model Matrix based on the {@link #position} and {@link #size}
	 *
	 * @return
	 */
	protected Matrix4f getModelMatrix() {
		return new Matrix4f().
				translate(new Vector3f(getPosition().x, getPosition().y, 0)).
				scale(new Vector3f(getSize().x, getSize().y, 1));
	}

	/**
	 * Writes the model matrix and potentially other instanced gui data to the buffer
	 *
	 * @param buffer
	 *            buffer handle for the GL_ARRAY_BUFFER from the {@link GUI}
	 * @param offset
	 *            offset of the data within the buffer
	 */
	public int writeToBuffer(FloatBuffer buffer, int offset) {
		getModelMatrix().get(offset, buffer);
		return 16;
	}

	/**
	 * Updates the GUIElement
	 *
	 * @param deltaTime
	 */
	public abstract void update(double deltaTime);

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty() {
		this.dirty = true;
	}

	public void setClean() {
		this.dirty = false;
	}

	public boolean contains(Vector2f point) {
		return bounding.contains(point);
	}

	public Rectangle getBounding() {
		return bounding;
	}

	public void setBounding(Rectangle bounding) {
		this.bounding = bounding;
	}
}
