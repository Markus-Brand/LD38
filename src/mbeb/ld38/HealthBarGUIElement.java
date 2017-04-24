package mbeb.ld38;

import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gui.elements.AtlasGUIElement;
import mbeb.opengldefault.gui.elements.GUIElement;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Created by erik on 24.04.17.
 */
public class HealthBarGUIElement extends AtlasGUIElement {

	private Vector3f color1;
	private Vector3f color2;
	private Vector3f color3;

	private float oldHealth;
	private float shadowDuration = 1.5f;
	private float shadowProgress;

	private float health;
	private float shadowHealth;
	
	public HealthBarGUIElement(float size, Vector3f color1, Vector3f color2, Vector3f color3) {
		super(0, 1, 1, new Vector2f(0.5f), new Vector2f(0.4f, 0.05f).mul(size));
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		setHealth(1);
		setHealth(1);
	}
	
	/**
	 * @param health from 0 to 1
	 */
	public void setHealth(float health) {
		oldHealth = this.shadowHealth;
		shadowProgress = 0;
		this.health = health;
		setDirty();
	}
	
	@Override
	public void update(double deltaTime) {
		if (Math.abs(oldHealth - health) < 0.0001f) {
			return;
		}
		shadowProgress += (float) deltaTime;

		if (shadowProgress > shadowDuration) {
			shadowProgress = 0;
			oldHealth = health;
		}


		//3x^2 - 2x^3


		float x = shadowProgress / shadowDuration;

		//float smooth = x * x * (3 - 2 * x);
		float smooth = x*x*x*(x*(x*6 - 15) + 10);

		shadowHealth = oldHealth - (oldHealth - health) * smooth;

		setDirty();
	}
	
	@Override
	public void writeTo(GLBufferWriter writer) {
		super.writeTo(writer);
		writer.write(new Vector4f(color1, health)).write(new Vector4f(color2, shadowHealth)).write(new Vector4f(color3, 0));
		//writer.write(color1).write(health).write(color2).write(shadowHealth).write(color3).write(0f);
	}
}
