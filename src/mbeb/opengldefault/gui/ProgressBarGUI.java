package mbeb.opengldefault.gui;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL33.*;
import mbeb.opengldefault.logging.GLErrors;
import mbeb.opengldefault.rendering.shader.Shader;

/**
 * A Progressbar {@link GUI} that is able to render Progressbars in one drawcall
 *
 * @author Markus
 */
public class ProgressBarGUI extends GUI {

	private static final String TAG = "ProgressBarGUI";

	public ProgressBarGUI() {
		super();
		stride = MAT4_SIZE + VEC4_SIZE;
	}

	@Override
	public void setupVAO() {
		super.setupVAO();
		renderable.bind();
		glEnableVertexAttribArray(7);
		GLErrors.checkForError(TAG, "glEnableVertexAttribArray");
		glVertexAttribPointer(7, 4, GL_FLOAT, false, stride, 4 * VEC4_SIZE);
		GLErrors.checkForError(TAG, "glVertexAttribPointer");

		glVertexAttribDivisor(7, 1);
		GLErrors.checkForError(TAG, "glVertexAttribDivisor");
		renderable.unbind();
	}

	@Override
	public void render(Shader shader) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GLErrors.checkForError(TAG, "glBlendFunc");
		super.render(shader);
		glDisable(GL_BLEND);
	}

}
