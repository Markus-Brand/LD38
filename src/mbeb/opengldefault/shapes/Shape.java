package mbeb.opengldefault.shapes;

import org.joml.Vector2f;

public interface Shape {
	public boolean contains(Vector2f point);

	public boolean intersectsShape(Shape other);
}
