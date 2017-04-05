package mbeb.opengldefault.light;

import java.awt.*;

import mbeb.opengldefault.scene.entities.DirectionalLightEntity;
import mbeb.opengldefault.scene.entities.IEntity;
import org.joml.*;

import mbeb.opengldefault.logging.*;

/**
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class DirectionalLight extends Light {
	/** Class Name Tag */
	private static final String TAG = "DirectionalLight";

	/** light shining direction */
	protected Vector3f direction;

	/**
	 * creates a light shining with a <i>color</i> in a <i>direction</i>
	 *
	 * @param color
	 *            with rgb values normally in [0,1]^3
	 * @param direction
	 *            as Vector3f, not zero vector
	 */
	public DirectionalLight(final Vector3f color, final Vector3f direction) {
		super(color);
		setDirection(direction);
	}

	/**
	 * creates a light shining with a <i>color</i> in a <i>direction</i>
	 *
	 * @param color
	 *            with rgb values usually in [0,255]^3
	 * @param direction
	 *            as Vector3f
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	public DirectionalLight(final Color color, final Vector3f direction) {
		this(vectorFromColor(color), direction);
	}

	/**
	 * @param direction
	 *            as Vector3f
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	public void setDirection(final Vector3f direction) {
		Log.assertTrue(TAG, direction.length() != 0, "Nullvector Exterminated");
		this.direction = direction;
		setDirty();
	}

	/**
	 * @return my shining direction as Vector3f
	 */
	public Vector3f getDirection() {
		return this.direction;
	}

	/**
	 * contains 2 Blocks (4 32bit floats each)
	 * <list>
	 * <li>the direction (3 components + 1 buffer)</li>
	 * <li>the color (3 components + 1 buffer)</li>
	 * </list>
	 * <br>
	 * if changes occur -> {@link DirectionalLightTypeManager}
	 */
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
	public IEntity asNewEntity() {
		return new DirectionalLightEntity(this);
	}
}
