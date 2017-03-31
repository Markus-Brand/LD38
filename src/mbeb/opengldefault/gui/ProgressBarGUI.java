package mbeb.opengldefault.gui;

import static org.lwjgl.opengl.GL11.*;
import mbeb.opengldefault.constants.Constants;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.ShaderProgram;

/**
 * A Progressbar {@link GUI} that is able to render Progressbars in one drawcall
 *
 * @author Markus
 */
public class ProgressBarGUI extends GUI {

	/**
	 * Class Tag
	 */
	private static final String TAG = "ProgressBarGUI";

	public ProgressBarGUI() {
		super();
		stride = Constants.MAT4_COMPONENTS + Constants.VEC4_COMPONENTS + Constants.VEC4_COMPONENTS;
	}

	@Override
	public void render(ShaderProgram shader) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GLErrors.checkForError(TAG, "glBlendFunc");
		super.render(shader);
		glDisable(GL_BLEND);
	}

}
