package mbeb.ld38;

import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.gl.shader.ShaderProgram;
import mbeb.opengldefault.gui.AtlasGUI;
import mbeb.opengldefault.gui.GUI;
import mbeb.opengldefault.logging.GLErrors;

import static mbeb.opengldefault.constants.Constants.MAT4_COMPONENTS;
import static mbeb.opengldefault.constants.Constants.VEC3_COMPONENTS;
import static mbeb.opengldefault.constants.Constants.VEC4_COMPONENTS;
import static org.lwjgl.opengl.GL11.*;

/**
 * A ProgressBar {@link GUI} that is able to render ProgressBars in one drawcall
 *
 * @author Markus
 */
public class HealthBarGUI extends AtlasGUI {

	public HealthBarGUI() {
		super("healthbar.png", 1, 1);
		//Store a Matrix, the progress vector and the lut vector
		stride += VEC4_COMPONENTS * 3;
		
		ShaderProgram healthBarShader = new ShaderProgram("healthbar.frag", "healthbar.vert");
		healthBarShader.compile();
		
		setShader(healthBarShader);
	}

}
