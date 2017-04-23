package mbeb.opengldefault.shapes;

import mbeb.opengldefault.logging.Log;

import org.joml.Vector2f;

/**
 * A class that represents a Rectangle
 *
 * @author Markus
 */
public class Rectangle implements Shape {

	private static final String TAG = "Rectangle";

	/**
	 * position of the downLeft corner
	 */
	private Vector2f position;

	/**
	 * Size of the GUI Element
	 */
	private Vector2f size;

	public Rectangle(Vector2f position, Vector2f size) {
		this.position = position;
		this.size = size;
	}

	/**
	 * Copy constructor
	 *
	 * @param bounding
	 */
	public Rectangle(Rectangle reference) {
		this.position = new Vector2f(reference.getPosition());
		this.size = new Vector2f(reference.getSize());
	}

	/**
	 * Getter for the Rectangles size
	 *
	 * @return the Rectangles size
	 */
	public Vector2f getSize() {
		return size;
	}

	/**
	 * Getter for the Rectangles start position
	 *
	 * @return the Rectangles start position
	 */
	public Vector2f getPosition() {
		return position;
	}

	/**
	 * Getter for the Rectangles end position
	 *
	 * @return the Rectangles end position
	 */
	public Vector2f getEnd() {
		return position.add(size, new Vector2f());
	}

	/**
	 * Setter for the Rectangles size
	 *
	 * @param size
	 *            the new size
	 */
	public void setSize(Vector2f size) {
		this.size = size;
	}

	/**
	 * Setter for the Rectangles position
	 *
	 * @param position
	 *            the new position
	 */
	@Override
	public void setPosition(Vector2f position) {
		this.position = position;
	}

	/**
	 * Tests if the Rectange contains a given point
	 *
	 * @param point
	 *            input point
	 * @return true if he point is within the Rectange
	 */
	@Override
	public boolean contains(Vector2f point) {
		return !(point.x < position.x || point.y < position.y || point.x > position.x + size.x || point.y > position.y
				+ size.y);
	}

	/**
	 * Calculates a Rectangle that contains both this and a reference Rectangle
	 *
	 * @param other
	 *            the reference Rectangle
	 * @return the combined Rectangle
	 */
	public Rectangle extend(Rectangle other) {
		float startX = Math.min(position.x, other.position.x);
		float startY = Math.min(position.y, other.position.y);
		float endX = Math.max(getEnd().x, other.getEnd().x);
		float endY = Math.max(getEnd().y, other.getEnd().y);

		return new Rectangle(new Vector2f(startX, startY), new Vector2f(endX - startX, endY - startY));
	}

	public float getWidth() {
		return getSize().x;
	}

	public float getHeight() {
		return getSize().y;
	}

	/**
	 * Sets the position of this Rectangle realtive to another Rectangle
	 *
	 * @param bounding
	 *            the referenced Rectangle
	 * @param relativeX
	 *            normally range 0 - 1.
	 *            0 -> Our left border is set to the referenced Rectangles left border
	 *            1 -> Our right border is set to the referenced Rectangles right border
	 * @param relativeY
	 *            normally range 0 - 1.
	 *            0 -> Our lower border is set to the referenced Rectangles lower border
	 *            1 -> Our upper border is set to the referenced Rectangles upper border
	 */
	public void setPositionRelativeTo(Rectangle bounding, float relativeX, float relativeY) {
		Vector2f maxPosition = bounding.position.add(bounding.size.sub(getSize(), new Vector2f()), new Vector2f());
		setPosition(bounding.position.lerp(maxPosition, new Vector2f(relativeX, relativeY), new Vector2f()));
	}

	@Override
	public boolean intersectsShape(Shape other) {
		if (other instanceof Rectangle) {
			return intersectsRectangle((Rectangle) other);
		} else if (other instanceof Circle) {
			return intersectsCircle((Circle) other);
		} else if (other instanceof Line) {
			return intersectsLine((Line) other);
		} else {
			Log.error(TAG, "Intersections with Shape not supported");
			return false;
		}
	}

	private boolean intersectsLine(Line other) {
		Log.error(TAG, "Intersections with Line not supported");
		return false;
	}

	private boolean intersectsCircle(Circle other) {
		Log.error(TAG, "Intersections with Circle not supported");
		return false;
	}

	private boolean intersectsRectangle(Rectangle other) {
		Log.error(TAG, "Intersections with Rectangle not supported");
		return false;
	}

}
