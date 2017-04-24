package mbeb.ld38;

import mbeb.opengldefault.gl.buffer.GLBufferWriter;
import mbeb.opengldefault.gui.elements.GUIElement;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Created by erik on 24.04.17.
 */
public class HealthBarGUIElement extends GUIElement {
	
	private Vector3f color1;
	private float progress1;
	private Vector3f color2;
	private float progress2;
	private Vector3f color3;
	
	public HealthBarGUIElement(float size, Vector3f color1, Vector3f color2, Vector3f color3) {
		super(new Vector2f(0.5f), new Vector2f(0.4f, 0.05f).mul(size));
		this.color1 = color1;
		this.color2 = color2;
		this.color3 = color3;
		progress1 = 1;
		progress2 = 1;
	}
	
	/**
	 * @param progress1 from 0 to 1
	 */
	public void setHealth(float progress1) {
		this.progress1 = progress1;
		setDirty();
	}
	
	@Override
	public void update(double deltaTime) {
		
		float dist = progress2 - progress1;
		dist *= (float) deltaTime;
		progress2 -= dist;
		
		setDirty();
	}
	
	@Override
	public void writeTo(GLBufferWriter writer) {
		super.writeTo(writer);
		writer.write(color1).write(progress1).write(color2).write(progress2).write(color3).write(0f);
	}
}
