package mbeb.opengldefault.gui;

import org.joml.Vector2f;

public class Rectangle {

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

	public Vector2f getSize() {
		return size;
	}

	public Vector2f getPosition() {
		return position;
	}

	public Vector2f getEnd() {
		return position.add(size, new Vector2f());
	}

	public void setSize(Vector2f size) {
		this.size = size;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public boolean contains(Vector2f point) {
		return !(point.x < position.x || point.y < position.y || point.x > position.x + size.x || point.y > position.y
				+ size.y);
	}

	public Rectangle extend(Rectangle other) {
		float startX = Math.min(position.x, other.position.x);
		float startY = Math.min(position.y, other.position.y);
		float endX = Math.max(getEnd().x, other.getEnd().x);
		float endY = Math.max(getEnd().y, other.getEnd().y);

		return new Rectangle(new Vector2f(startX, startY), new Vector2f(endX - startX, endY - startY));
	}
}
