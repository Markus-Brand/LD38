package mbeb.opengldefault.game;

import java.awt.image.BufferedImage;

import org.joml.Vector2f;

import mbeb.opengldefault.scene.behaviour.IHeightSource;
import mbeb.opengldefault.shapes.Rectangle;

public class HeightFromHeightMap implements IHeightSource {

	private BufferedImage heightMap;
	private Rectangle heightMapBounding;

	private float minLevel;
	private float height;

	public HeightFromHeightMap(BufferedImage heightMap, Rectangle heightMapBounding, float minLevel, float height) {
		this.heightMap = heightMap;
		this.heightMapBounding = heightMapBounding;
		this.minLevel = minLevel;
		this.height = height;
	}

	@Override
	public float getHeight(Vector2f worldSpaceCoordinate) {

		Vector2f oldRelativeMapPosition =
				new Vector2f(worldSpaceCoordinate.x, worldSpaceCoordinate.y).sub(heightMapBounding.getPosition()).mul(
						new Vector2f(1 / heightMapBounding.getWidth(), 1 / heightMapBounding.getHeight()));

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

		return topLeft;
	}

	private float sampleHeightAt(int x, int y) {
		if (x < 0 || y < 0 || x >= heightMap.getWidth() || y >= heightMap.getHeight()) {
			return 0;
		}
		return (heightMap.getRGB(x, heightMap.getHeight() - y - 1) >> 16 & 0xFF) / 255f * height + minLevel;
	}

}
