package mbeb.opengldefault.scene.behaviour;

import java.awt.image.BufferedImage;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import mbeb.opengldefault.controls.KeyBoard;
import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.shapes.Rectangle;

public class WalkOnHeightMapBehaviour extends PitchYawMouseBehaviour {

	private BufferedImage heightMap;
	private Rectangle heightMapBounding;
	private static final Vector3f worldUp = new Vector3f(0, 1, 0);
	private HeightSource heightSource;

	public WalkOnHeightMapBehaviour(HeightSource heightSource, Rectangle heightMapBounding) {
		this.heightSource = heightSource;
		this.heightMapBounding = heightMapBounding;
	}

	@Override
	public void update(double deltaTime, IEntity entity) {
		super.update(deltaTime, entity);

		Vector3f position = entity.getPosition();
		Vector3f direction = entity.getDirection();
		final Vector3f delta = new Vector3f();

		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_A)) {
			worldUp.cross(direction, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_D)) {
			direction.cross(worldUp, delta);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_W)) {
			delta.add(direction);
		}
		if (KeyBoard.isKeyDown(GLFW.GLFW_KEY_S)) {
			delta.sub(direction);
		}
		if (delta.length() == 0) {
			return;
		}

		Vector3f newPosition = delta.add(position);

		Vector2f oldRelativeMapPosition =
				new Vector2f(position.x, position.z).sub(heightMapBounding.getPosition()).mul(
						new Vector2f(1 / heightMapBounding.getWidth(), 1 / heightMapBounding.getHeight()));

		Vector2f newRelativeMapPosition =
				new Vector2f(newPosition.x, newPosition.z).sub(heightMapBounding.getPosition()).mul(
						new Vector2f(1 / heightMapBounding.getWidth(), 1 / heightMapBounding.getHeight()));

		float oldHeight = getHeight(oldRelativeMapPosition);
		float newHeight = getHeight(newRelativeMapPosition);

		entity.setPosition(new Vector3f(newPosition.x, newHeight, newPosition.z));

	}

	private float getHeight(Vector2f oldRelativeMapPosition) {
		float realX = oldRelativeMapPosition.x * heightMap.getWidth();
		float realY = oldRelativeMapPosition.y * heightMap.getHeight();
		int sampleX = (int) realX;
		int sampleY = (int) realY;
		float topLeft = sampleHeightAt(sampleX, sampleY);
		float topRight = sampleHeightAt(sampleX + 1, sampleY);
		float bottomLeft = sampleHeightAt(sampleX, sampleY + 1);
		float bottomRight = sampleHeightAt(sampleX + 1, sampleY + 1);

		float relativeX = realX - sampleX;
		float relativeY = realY - sampleY;

		float top = topRight * relativeX + topLeft * (1.0f - relativeX);
		float bottom = bottomRight * relativeX + bottomLeft * (1.0f - relativeX);
		float middle = bottom * relativeY + top * (1.0f - relativeY);

		return middle;
	}

	private float sampleHeightAt(int x, int y) {
		if (x < 0 || y < 0 || x >= heightMap.getWidth() || y >= heightMap.getHeight()) {
			return 0;
		}
		return (heightMap.getRGB(x, y) >> 16 & 0xFF) / 255f * 2f + 1f;
	}

	@Override
	protected void updateDirection(IEntity entity) {
		Vector3f direction = new Vector3f();

		direction.x = (float) Math.cos(yaw);
		direction.y = 0;
		direction.z = (float) Math.sin(yaw);

		direction.normalize();

		entity.setDirection(direction);
	}

}
