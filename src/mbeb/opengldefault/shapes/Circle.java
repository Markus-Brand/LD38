package mbeb.opengldefault.shapes;

import mbeb.opengldefault.logging.Log;

import org.joml.Vector2f;

public class Circle implements Shape {

	private static final String TAG = "Circle";

	private Vector2f position;

	private float radius;

	public Circle(Vector2f position, float radius) {
		this.position = position;
		this.radius = radius;
	}

	@Override
	public boolean contains(Vector2f point) {
		return point.distanceSquared(position) < radius * radius;
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
		if (contains(other.getStart()) || contains(other.getEnd())) {
			return true;
		}

		Vector2f lineDirection = other.directionVector();
		Vector2f centerToLineStart = other.getStart().sub(position, new Vector2f());

		float a = lineDirection.dot(lineDirection);
		float b = 2 * centerToLineStart.dot(lineDirection);
		float c = centerToLineStart.dot(centerToLineStart) - radius * radius;

		float discriminant = b * b - 4 * a * c;
		if (discriminant >= 0)
		{
			discriminant = (float) Math.sqrt(discriminant);

			float t1 = (-b - discriminant) / (2 * a);
			float t2 = (-b + discriminant) / (2 * a);

			if (t1 >= 0 && t1 <= 1 || t2 >= 0 && t2 <= 1)
			{
				return true;
			}
		}

		return false;
	}

	private boolean intersectsCircle(Circle other) {
		return other.position.distanceSquared(position) < other.radius * other.radius + radius * radius;
	}

	private boolean intersectsRectangle(Rectangle other) {
		return other.intersectsShape(this);
	}

}
