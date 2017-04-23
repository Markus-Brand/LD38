package mbeb.opengldefault.shapes;

import mbeb.opengldefault.logging.Log;

import org.joml.Vector2f;

public class Line implements Shape {

	private static final String TAG = "Line";

	private Vector2f start;
	private Vector2f end;

	public Line(Vector2f start, Vector2f end) {
		this.setStart(start);
		this.setEnd(end);
	}

	@Override
	public boolean contains(Vector2f point) {
		return false;
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
		return other.intersectsShape(this);
	}

	private boolean intersectsRectangle(Rectangle other) {
		return other.intersectsShape(this);
	}

	/**
	 * @return the start
	 */
	public Vector2f getStart() {
		return start;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(Vector2f start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Vector2f getEnd() {
		return end;
	}

	/**
	 * @param end
	 *            the end to set
	 */
	public void setEnd(Vector2f end) {
		this.end = end;
	}

	public Vector2f directionVector() {
		return getEnd().sub(getStart(), new Vector2f());
	}

	@Override
	public void setPosition(Vector2f position) {
		this.setStart(position);
	}
}
