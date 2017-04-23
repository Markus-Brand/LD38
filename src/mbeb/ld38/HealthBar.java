package mbeb.ld38;

import org.joml.Vector2f;
import org.joml.Vector3f;

import mbeb.opengldefault.gl.GLContext;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.elements.GUIElement;
import mbeb.opengldefault.logging.Log;

/**
 * a nice healthbar
 */
public class HealthBar {

	public static final Vector2f SIZE = new Vector2f(0.4f, 0.05f).mul(0.5f);

	private static final String TAG = "HealthBar";

	private static AtlasGUI healthGui;
	private static ShaderProgram healthBarShader;
	private static GUIElement bar;

	static {
		healthGui = new AtlasGUI("healthbar.png", 1, 1);
		healthBarShader = new ShaderProgram("healthbar.frag", "gui.vert");
		healthBarShader.compile();
		healthGui.setShader(healthBarShader);



		bar = healthGui.addAtlasGUIElement(0, new Vector2f(), new Vector2f(SIZE.x, GLContext.getAspectRatio() * SIZE.y))
						.setPositionRelativeToScreen(0.01f, 0.99f);

		Log.assertTrue(TAG, healthBarShader.whileBound((ShaderProgram program) ->
				program.setUniform("color1", new Vector3f(0.2f, 0.6f, 1)) >= 0
				&& program.setUniform("color2", new Vector3f(1, 1, 0.4f)) >= 0
				&& program.setUniform("color3", new Vector3f(0.2f)) >= 0
				&& program.setUniform("progress2", 1f) >= 0), "");
	}

	private int maxHealth;
	private int health;
	private int secondHealth;

	public HealthBar(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setSecondHealth(int secondHealth) {
		this.secondHealth = secondHealth;
	}

	public void render() {
		Log.assertTrue(TAG, healthBarShader.whileBound((ShaderProgram program) ->
				program.setUniform("progress1", getFloatProgress()) >= 0
				&& program.setUniform("progress2", getSecondFloatProgress()) >= 0), "");
		healthGui.render();
	}

	public void update(double deltaTime) {
		healthGui.update(deltaTime);
		bar.update(deltaTime);
	}

	private float getFloatProgress() {
		return health / (float) maxHealth;
	}

	private float getSecondFloatProgress() {
		return secondHealth / (float) maxHealth;
	}

	public void setPosition(float x, float y) {
		System.out.println(x + " - " + y);
		bar.setPositionRelativeToScreen(x, y);
	}
}
