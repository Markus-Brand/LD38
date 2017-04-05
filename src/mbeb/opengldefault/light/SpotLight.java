package mbeb.opengldefault.light;

import java.awt.*;

import mbeb.opengldefault.scene.entities.IEntity;
import mbeb.opengldefault.scene.entities.SpotLightEntity;
import org.joml.*;

import mbeb.opengldefault.logging.*;

/**
 * @author Merlin (and Erik and Markus but if something is wrong blame him and only him) :D
 */
public class SpotLight extends Light implements LimitedLight {
	/** Class Name Tag */
	private static final String TAG = "SpotLight";

	/** light source position */
	protected Vector3f position;
	/** light source direction */
	protected Vector3f direction;
	/** inner border angle of the light cone */
	protected float innerCutoff;
	/** Outer Rim angle of the light cone */
	protected float outerCutoff;
	/** constant attenuation factor */
	protected float constant;
	/** linear attenuation factor */
	protected float linear;
	/** quadratic attenuation factor */
	protected float quadratic;

	/**
	 * creates a straight, <i>color</i> colored lightcone located at <i>position</i> "shining" in <i>direction</i>
	 * light intensity is interpolated between innerCutoff and outerCutoff
	 *
	 * @param color
	 *            with rgb values usually in [0,1]^3
	 * @param position
	 *            as Vector3f
	 * @param direction
	 *            as Vector3f
	 * @param innerCutoff
	 *            as angle in degrees
	 * @param outerCutoff
	 *            as angle in degrees
	 * @param constant
	 *            attenuation factor
	 * @param linear
	 *            attenuation factor
	 * @param quadratic
	 *            attenuation factor
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	private SpotLight(final Vector3f color, final Vector3f position, final Vector3f direction, final float innerCutoff, final float outerCutoff) {
		super(color);
		setPosition(position);
		setDirection(direction);
		setInnerCutoff(innerCutoff);
		setOuterCutoff(outerCutoff);
	}

	/**
	 * creates a straight, <i>color</i> colored lightcone located at <i>position</i> "shining" in <i>direction</i> with explicit naming of the attenuation factors <i>constant</i>, <i>linear</i> and
	 * <i>quadratic</i>
	 * light intensity is interpolated between innerCutoff and outerCutoff
	 *
	 * @param color
	 *            with rgb values usually in [0,1]^3
	 * @param position
	 *            as Vector3f
	 * @param direction
	 *            as Vector3f
	 * @param innerCutoff
	 *            as angle in degrees
	 * @param outerCutoff
	 *            as angle in degrees
	 * @param constant
	 *            attenuation factor
	 * @param linear
	 *            attenuation factor
	 * @param quadratic
	 *            attenuation factor
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	public SpotLight(final Vector3f color, final Vector3f position, final Vector3f direction, final float innerCutoff, final float outerCutoff, final float constant, final float linear,
			final float quadratic) {
		this(color, position, direction, innerCutoff, outerCutoff);
		setConstant(constant);
		setLinear(linear);
		setQuadratic(quadratic);
	}

	/**
	 * creates a straight, <i>color</i> colored lightcone located at <i>position</i> "shining" in <i>direction</i> with explicit naming of the attenuation factors <i>constant</i>, <i>linear</i> and
	 * <i>quadratic</i>
	 * light intensity is interpolated between innerCutoff and outerCutoff
	 *
	 * @param color
	 *            with rgb values usually in [0,255]^3
	 * @param position
	 *            as Vector3f
	 * @param direction
	 *            as Vector3f
	 * @param innerCutoff
	 *            as angle in degrees
	 * @param outerCutoff
	 *            as angle in degrees
	 * @param constant
	 *            attenuation factor
	 * @param linear
	 *            attenuation factor
	 * @param quadratic
	 *            attenuation factor
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	public SpotLight(final Color color, final Vector3f position, final Vector3f direction, final float innerCutoff, final float outerCutoff, final float constant, final float linear,
			final float quadratic) {
		this(vectorFromColor(color), position, direction, innerCutoff, outerCutoff, constant, linear, quadratic);
	}

	/**
	 * creates a straight, <i>color</i> colored lightcone located at <i>position</i> "shining" in <i>direction</i> with a limited <i>reach</i> (without explicit naming of the attenuation factors
	 * <i>constant</i>, <i>linear</i> and <i>quadratic</i>)
	 * light intensity is interpolated between innerCutoff and outerCutoff
	 *
	 * @param color
	 *            with rgb values usually in [0,1]^3
	 * @param position
	 *            as Vector3f
	 * @param direction
	 *            as Vector3f
	 * @param innerCutoff
	 *            as angle in degrees
	 * @param outerCutoff
	 *            as angle in degrees
	 * @param reach
	 *            the reach of the light source
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	public SpotLight(final Vector3f color, final Vector3f position, final Vector3f direction, final float innerCutoff, final float outerCutoff, final float reach) {
		this(color, position, direction, innerCutoff, outerCutoff);
		setReach(reach);
	}

	/**
	 * creates a straight, <i>color</i> colored lightcone located at <i>position</i> "shining" in <i>direction</i> with a limited <i>reach</i> (without explicit naming of the attenuation factors
	 * <i>constant</i>, <i>linear</i> and <i>quadratic</i>)
	 * light intensity is interpolated between innerCutoff and outerCutoff
	 *
	 * @param color
	 *            with rgb values usually in [0,255]^3
	 * @param position
	 *            as Vector3f
	 * @param direction
	 *            as Vector3f
	 * @param innerCutoff
	 *            as angle in degrees
	 * @param outerCutoff
	 *            as angle in degrees
	 * @param reach
	 *            the reach of the light source
	 * @throws AssertionError
	 *             if direction is zero vector
	 */
	public SpotLight(final Color color, final Vector3f position, final Vector3f direction, final float innerCutoff, final float outerCutoff, final float reach) {
		this(vectorFromColor(color), position, direction, innerCutoff, outerCutoff, reach);
	}

	/**
	 * @return my shining direction as Vector3f
	 */
	public Vector3f getDirection() {
		return direction;
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
	 * @return the inner border angle of my light cone (in Degrees)
	 */
	public float getInnerCutoff() {
		return innerCutoff;
	}

	/**
	 * @param innerCutoff
	 *            new inner border angle of my light cone (in Degrees)
	 */
	public void setInnerCutoff(final float innerCutoff) {
		this.innerCutoff = innerCutoff;
		setDirty();
	}

	/**
	 * @return the Outer Rim angle of my light cone (in Degrees)
	 */
	public float getOuterCutoff() {
		return outerCutoff;
	}

	/**
	 * @param outerCutoff
	 *            new Outer Rim angle of my light cone (in Degrees)
	 */
	public void setOuterCutoff(final float outerCutoff) {
		this.outerCutoff = outerCutoff;
		setDirty();
	}

	/**
	 * @return my <i>constant</i> attenuation factor
	 */
	public float getConstant() {
		return constant;
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
	 * @return my <i>linear</i> attenuation factor
	 */
	public float getLinear() {
		return linear;
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
	 * @return my <i>quadratic</i> attenuation factor
	 */
	public float getQuadratic() {
		return quadratic;
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
	 * @param position
	 *            my new position as Vector3f
	 */
	public void setPosition(final Vector3f position) {
		this.position = position;
		setDirty();
	}

	/**
	 * @param reach
	 *            new reach of the light source
	 */
	//TODO maybe also implement get reach
	public void setReach(final float reach) {
		setConstant(1.0f);
		setLinear(generateLinearAmount(reach));
		setQuadratic(generateQuadraticAmount(reach));
	}

	/**
	 * contains 4 Blocks (4 32bit floats each)
	 * <list>
	 * <li>the position (3 components + 1 buffer)</li>
	 * <li>the direction (3 components + 1 buffer)</li>
	 * <li>the color (3 components)</li>
	 * <li><b>cos</b>(cutoff), <b>cos</b>(outerCutoff) and the 3 attenuation factors constant, linear and quadratic</li>
	 * </list>
	 * <br>
	 * if changes occur -> {@link SpotLightTypeManager}
	 */
	@Override
	public float[] getData() {
		final float[] data = new float[16];

		data[0] = position.x;
		data[1] = position.y;
		data[2] = position.z;

		data[4] = direction.x;
		data[5] = direction.y;
		data[6] = direction.z;

		data[8] = color.x;
		data[9] = color.y;
		data[10] = color.z;

		data[11] = (float) java.lang.Math.cos(java.lang.Math.toRadians(innerCutoff));

		data[12] = (float) java.lang.Math.cos(java.lang.Math.toRadians(outerCutoff));

		data[13] = constant;

		data[14] = linear;

		data[15] = quadratic;

		return data;
	}
	
	@Override
	public IEntity asNewEntity() {
		return new SpotLightEntity(this);
	}
}
