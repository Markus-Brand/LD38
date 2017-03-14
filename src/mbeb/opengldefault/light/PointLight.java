package mbeb.opengldefault.light;

import java.awt.*;

import org.joml.*;

/**
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class PointLight extends Light implements LimitedLight {
	/** Class Name Tag */
	private static final String TAG = "PointLight";

	/** light source position */
	protected Vector3f position;
	/** constant attenuation factor */
	protected float constant;
	/** linear attenuation factor */
	protected float linear;
	/** quadratic attenuation factor */
	protected float quadratic;

	/**
	 * creates an omnidirectional light (like a sun) at <i>position</i> shining in <i>color</i>
	 *
	 * @param color
	 *            with rgb values usually in [0,1]^3
	 * @param position
	 *            as Vector3f
	 */
	private PointLight(final Vector3f color, final Vector3f position) {
		super(color);
		setPosition(position);
	}

	/**
	 * creates an omnidirectional light (like a sun) at <i>position</i> shining in <i>color</i> with explicit naming of the attenuation factors <i>constant</i>, <i>linear</i> and <i>quadratic</i>
	 *
	 * @param color
	 *            with rgb values usually in [0,1]^3
	 * @param position
	 *            as Vector3f
	 * @param constant
	 *            attenuation factor
	 * @param linear
	 *            attenuation factor
	 * @param quadratic
	 *            attenuation factor
	 */
	public PointLight(final Vector3f color, final Vector3f position, final float constant, final float linear, final float quadratic) {
		this(color, position);
		setConstant(constant);
		setLinear(linear);
		setQuadratic(quadratic);
	}

	/**
	 * creates an omnidirectional light (like a sun) at <i>position</i> shining in <i>color</i> with explicit naming of the attenuation factors <i>constant</i>, <i>linear</i> and <i>quadratic</i>
	 *
	 * @param color
	 *            with rgb values usually in [0,255]^3
	 * @param position
	 *            as Vector3f
	 * @param constant
	 *            attenuation factor
	 * @param linear
	 *            attenuation factor
	 * @param quadratic
	 *            attenuation factor
	 */
	public PointLight(final Color color, final Vector3f position, final float constant, final float linear, final float quadratic) {
		this(vectorFromColor(color), position, constant, linear, quadratic);
	}

	/**
	 * creates an omnidirectional light (like a sun) at <i>position</i> shining in <i>color</i> with a limited <i>reach</i> (without explicit naming of the attenuation factors <i>constant</i>,
	 * <i>linear</i> and <i>quadratic</i>)
	 *
	 * @param color
	 *            with rgb values usually in [0,1]^3
	 * @param position
	 *            as Vector3f
	 * @param reach
	 *            the reach of the light source
	 */
	public PointLight(final Vector3f color, final Vector3f position, final float reach) {
		this(color, position);
		setConstant(1.0f);
		setLinear(generateLinearAmount(reach));
		setQuadratic(generateQuadraticAmount(reach));
	}

	/**
	 * creates an omnidirectional light (like a sun) at <i>position</i> shining in <i>color</i> with a limited <i>reach</i> (without explicit naming of the attenuation factors <i>constant</i>,
	 * <i>linear</i> and <i>quadratic</i>)
	 *
	 * @param color
	 *            with rgb values usually in [0,255]^3
	 * @param position
	 *            as Vector3f
	 * @param reach
	 *            the reach of the light source
	 */
	public PointLight(final Color color, final Vector3f position, final float reach) {
		this(vectorFromColor(color), position, reach);
	}

	/**
	 * @param position
	 *            my new position as Vector3f
	 */
	public void setPosition(final Vector3f position) {
		this.position = position;
		setDirty();
	}

	/**
	 * @param constant
	 *            new constant attenuation factor
	 */
	public void setConstant(final float constant) {
		this.constant = constant;
		setDirty();
	}

	/**
	 * @param linear
	 *            new linear attenuation factor
	 */
	public void setLinear(final float linear) {
		this.linear = linear;
		setDirty();
	}

	/**
	 * @param quadratic
	 *            new quadratic attenuation factor
	 */
	public void setQuadratic(final float quadratic) {
		this.quadratic = quadratic;
		setDirty();
	}

	/**
	 * @return my position as Vector3f
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @return my <i>constant</i> attenuation factor
	 */
	public float getConstant() {
		return constant;
	}

	/**
	 * @return my <i>linear</i> attenuation factor
	 */
	public float getLinear() {
		return linear;
	}

	/**
	 * @return my <i>quadratic</i> attenuation factor
	 */
	public float getQuadratic() {
		return quadratic;
	}

	/**
	 * @param reach
	 *            new reach of the lightsource
	 */
	//TODO maybe also implement get reach
	public void setReach(final float reach) {
		setConstant(1.0f);
		setLinear(generateLinearAmount(reach));
		setQuadratic(generateQuadraticAmount(reach));
	}

	/**
	 * contains 3 Blocks (4 32bit floats each)
	 * <list>
	 * <li>the position (3 components + 1 buffer)</li>
	 * <li>the color (3 components + 1 buffer)</li>
	 * <li>the 3 attenuation factors constant, linear and quadratic</li>
	 * </list>
	 * <br>
	 * if changes occur -> {@link PointLightTypeManager}
	 */
	@Override
	public float[] getData() {
		final float[] data = new float[12];

		data[0] = position.x;
		data[1] = position.y;
		data[2] = position.z;

		data[4] = color.x;
		data[5] = color.y;
		data[6] = color.z;
		data[7] = constant;

		data[8] = linear;

		data[9] = quadratic;

		return data;
	}
}
