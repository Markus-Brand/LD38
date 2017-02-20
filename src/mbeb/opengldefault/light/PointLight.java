/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbeb.opengldefault.light;

import static org.lwjgl.opengl.GL20.*;

import java.awt.*;

import mbeb.opengldefault.rendering.shader.*;

import org.joml.*;

/**
 * @author Erik
 */
public class PointLight extends Light {

	public static final int DATASIZE_IN_BLOCKS = 3;
	Vector3f position;
	float constant;
	float linear;
	float quadratic;

	public PointLight(final Vector3f color, final Vector3f position, final float constant, final float linear, final float quadratic) {
		super(color);
		this.position = position;
		this.constant = constant;
		this.linear = linear;
		this.quadratic = quadratic;
	}

	public PointLight(final Color color, final Vector3f position, final float reach) {
		super(color);
		this.position = position;
		this.constant = 1.0f;
		this.linear = generateLinearAmount(reach);
		this.quadratic = generateQuadraticAmount(reach);
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
	}

	public void setConstant(final float constant) {
		this.constant = constant;
	}

	public void setLinear(final float lienar) {
		this.linear = lienar;
	}

	public void setQuadratic(final float quadratic) {
		this.quadratic = quadratic;
	}

	public Vector3f getPosition() {
		return position;
	}

	@Override
	public void apply(final Shader shader, final String uniform) {
		glUniform3f(shader.getUniform(uniform + ".position"), position.x, position.y, position.z);
		glUniform1f(shader.getUniform(uniform + ".constant"), constant);
		glUniform1f(shader.getUniform(uniform + ".linear"), linear);
		glUniform1f(shader.getUniform(uniform + ".quadratic"), quadratic);
		super.apply(shader, uniform);
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

}
