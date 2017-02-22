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
public class SpotLight extends Light {

	public static final int DATASIZE_IN_BLOCKS = 4;
	Vector3f position, direction;
	float cutOff, outerCutOff;
	float constant;
	float linear;
	float quadratic;

	public SpotLight(final Vector3f color, final Vector3f position, final Vector3f direction, final float cutOff, final float outerCutOff, final float constant, final float linear,
			final float quadratic) {
		super(color);
		this.position = position;
		this.direction = direction;
		this.cutOff = cutOff;
		this.outerCutOff = outerCutOff;
		this.constant = constant;
		this.linear = linear;
		this.quadratic = quadratic;
	}

	public SpotLight(final Color color, final Vector3f position, final Vector3f direction, final float cutOff, final float outerCutOff, final float constant, final float linear, final float quadratic) {
		this(vectorFromColor(color), position, direction, cutOff, outerCutOff, constant, linear, quadratic);
	}

	public SpotLight(final Vector3f color, final Vector3f position, final Vector3f direction, final float cutOff, final float outerCutOff, final float reach) {
		this(color, position, direction, cutOff, outerCutOff, 1.0f, generateLinearAmount(reach), generateQuadraticAmount(reach));
	}

	public SpotLight(final Color color, final Vector3f position, final Vector3f direction, final float cutOff, final float outerCutOff, final float reach) {
		this(vectorFromColor(color), position, direction, cutOff, outerCutOff, 1.0f, generateLinearAmount(reach), generateQuadraticAmount(reach));
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

	public void setDirection(final Vector3f direction) {
		this.direction = direction;
		setDirty();
	}

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

		data[11] = (float) java.lang.Math.cos(java.lang.Math.toRadians(cutOff));

		data[12] = (float) java.lang.Math.cos(java.lang.Math.toRadians(outerCutOff));

		data[13] = constant;

		data[14] = linear;

		data[15] = quadratic;

		return data;
	}

	@Override
	public int getBlockSize() {
		return DATASIZE_IN_BLOCKS;
	}

}
