/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbeb.opengldefault.light;

import java.awt.*;

import org.joml.*;

/**
 * @author Erik + Merlin + Markus :)
 */
public class PointLight extends Light {

	public static final int DATASIZE_IN_BLOCKS = 3;
	Vector3f position;
	float constant;
	float linear;
	float quadratic;

	public PointLight(final Vector3f color, final Vector3f position, final float constant, final float linear, final float quadratic) {
		super(color);
		setPosition(position);
		setConstant(constant);
		setLinear(linear);
		setQuadratic(quadratic);
	}

	public PointLight(final Color color, final Vector3f position, final float constant, final float linear, final float quadratic) {
		this(vectorFromColor(color), position, constant, linear, quadratic);
	}

	public PointLight(final Vector3f color, final Vector3f position, final float reach) {
		this(color, position, 1.0f, generateLinearAmount(reach), generateQuadraticAmount(reach));
	}

	public PointLight(final Color color, final Vector3f position, final float reach) {
		this(vectorFromColor(color), position, reach);
	}

	/**
	 * generates an approximation of this tables inear amount column:
	 * http://www.learnopengl.com/#!Lighting/Light-casters based on this
	 * function restorer: http://www.arndt-bruenner.de/mathe/scripts/regrnl.htm
	 *
	 * @param distance
	 *            the desired reach distance of the light
	 * @return the linear part for attenuation calculation
	 */
	private static float generateLinearAmount(final float distance) {
		return (float) (4.767566446388858 / distance);
	}

	/**
	 * generates an approximation of this tables quadratic amount column:
	 * http://www.learnopengl.com/#!Lighting/Light-casters based on this
	 * function restorer: http://www.arndt-bruenner.de/mathe/scripts/regrnl.htm
	 *
	 * @param distance
	 *            the desired distance for the light
	 * @return the quadratic amount for attenuation calculation
	 */
	private static float generateQuadraticAmount(final float distance) {
		return (float) (0.0361492d / distance + 48.572348116d / (distance * distance) + 280d / (distance * distance * distance));
	}

	public void setPosition(final Vector3f position) {
		this.position = position;
		setDirty();
	}

	public void setConstant(final float constant) {
		this.constant = constant;
		setDirty();
	}

	public void setLinear(final float linear) {
		this.linear = linear;
		setDirty();
	}

	public void setQuadratic(final float quadratic) {
		this.quadratic = quadratic;
		setDirty();
	}

	public Vector3f getPosition() {
		return position;
	}

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

	@Override
	public int getBlockSize() {
		return DATASIZE_IN_BLOCKS;
	}
}
