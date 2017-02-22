package mbeb.opengldefault.light;

import java.awt.*;

import org.joml.*;

/**
 * @author Erik + Merlin + Markus :)
 */
public class DirectionalLight extends Light {

	public static final int DATASIZE_IN_BLOCKS = 2;
	private Vector3f direction;

	public DirectionalLight(final Vector3f color, final Vector3f direction) {
		super(color);
		setDirection(direction);
	}

	public DirectionalLight(final Color color, final Vector3f direction) {
		this(vectorFromColor(color), direction);
	}

	public void setDirection(final Vector3f direction) {
		this.direction = direction;
		setDirty();
	}

	@Override
	public float[] getData() {
		final float[] data = new float[8]; //always 4-float / 16-Byte blocks
		//block1
		data[0] = direction.x;
		data[1] = direction.y;
		data[2] = direction.z;

		//block2
		data[4] = color.x;
		data[5] = color.y;
		data[6] = color.z;

		return data;
	}

	@Override
	public int getBlockSize() {
		return DATASIZE_IN_BLOCKS;
	}
}
